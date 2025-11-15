package com.taarifu_engine_api.modules.userandrole.service;

import com.taarifu_engine_api.modules.common.domain.util.PasswordGenerator;
import com.taarifu_engine_api.modules.common.domain.util.PasswordStrengthCalculator;
import com.taarifu_engine_api.modules.common.exception.ApiException;
import com.taarifu_engine_api.modules.notification.domain.enums.EmailType;
import com.taarifu_engine_api.modules.notification.service.EmailService;
import com.taarifu_engine_api.modules.notification.service.SmsService;
import com.taarifu_engine_api.modules.notification.domain.enums.SmsType;
import com.taarifu_engine_api.modules.notification.util.PhoneNumberUtil;
import com.taarifu_engine_api.modules.userandrole.domain.dto.AdminUserResponseDto;
import com.taarifu_engine_api.modules.userandrole.domain.dto.AdminUserSummaryDto;
import com.taarifu_engine_api.modules.userandrole.domain.dto.ChangePasswordDto;
import com.taarifu_engine_api.modules.userandrole.domain.dto.CreateAdminUserDto;
import com.taarifu_engine_api.modules.userandrole.domain.dto.PasswordValidationDto;
import com.taarifu_engine_api.modules.userandrole.domain.dto.ResetPasswordDto;
import com.taarifu_engine_api.modules.userandrole.domain.dto.UpdateAdminUserDto;
import com.taarifu_engine_api.modules.userandrole.domain.entity.User;
import com.taarifu_engine_api.modules.userandrole.domain.enums.UserStatus;
import com.taarifu_engine_api.modules.userandrole.domain.enums.UserType;
import com.taarifu_engine_api.modules.userandrole.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service implementation for admin user management operations.
 * Provides methods specifically for managing admin users (UserType.ADMIN).
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AdminUserServiceImpl implements AdminUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final SmsService smsService;
    private final com.taarifu_engine_api.modules.auth.service.AuthService authService;

    @Value("${ROOT_ADMIN_USERNAME:rootadmin}")
    private String rootAdminUsername;

    @Override
    public AdminUserResponseDto createAdminUser(CreateAdminUserDto createAdminUserDto) {
        log.info("Creating admin user with username: {}", createAdminUserDto.getUsername());

        // Generate a secure password for admin user
        String password = PasswordGenerator.generateTemporaryAdminPassword();
        boolean passwordGenerated = true;
        log.info("Generated secure password for admin user: {}", createAdminUserDto.getUsername());

        // Check if username already exists
        if (userRepository.existsByUsername(createAdminUserDto.getUsername())) {
            throw new ApiException("Username already exists", HttpStatus.CONFLICT);
        }

        // Check if email already exists
        if (userRepository.existsByEmail(createAdminUserDto.getEmail())) {
            throw new ApiException("Email already exists", HttpStatus.CONFLICT);
        }

        // Create new admin user
        User adminUser = new User();
        adminUser.setUsername(createAdminUserDto.getUsername());
        adminUser.setEmail(createAdminUserDto.getEmail());
        adminUser.setUserType(UserType.ADMIN);
        adminUser.setStatus(UserStatus.ACTIVE); // Admin users are active by default
        adminUser.setRequirePasswordChange(createAdminUserDto.getRequirePasswordChange());

        // Validate and format phone number if provided
        if (createAdminUserDto.getPhoneNumber() != null && 
            !createAdminUserDto.getPhoneNumber().trim().isEmpty()) {
            try {
                String formattedPhone = PhoneNumberUtil.formatToInternational(
                    createAdminUserDto.getPhoneNumber()
                );
                adminUser.setPhoneNumber(formattedPhone);
            } catch (ApiException e) {
                log.error("Invalid phone number format: {}", createAdminUserDto.getPhoneNumber(), e);
                throw new ApiException("Invalid phone number format: " + e.getMessage(), 
                                     HttpStatus.BAD_REQUEST);
            }
        }

        // Hash password and set strength
        String hashedPassword = passwordEncoder.encode(password);
        adminUser.setPasswordHash(hashedPassword);
        adminUser.setPasswordStrength(PasswordStrengthCalculator.calculatePasswordStrength(password));

        // Ensure ULID is generated
        adminUser.ensureUid();

        // Save admin user
        User savedAdminUser = userRepository.save(adminUser);

        // Send welcome SMS if phone number is provided
        if (savedAdminUser.getPhoneNumber() != null && 
            !savedAdminUser.getPhoneNumber().trim().isEmpty()) {
            try {
                sendAdminUserCreatedSms(savedAdminUser, password);
            } catch (Exception e) {
                log.error("Failed to send welcome SMS to admin user: {}", 
                         savedAdminUser.getPhoneNumber(), e);
                // Don't throw - SMS failure shouldn't break admin creation
            }
        }

        // Send admin user created notification email
        sendAdminUserCreatedEmail(savedAdminUser, password, passwordGenerated);

        log.info("Admin user created successfully with ID: {}", savedAdminUser.getId());
        return convertToResponseDto(savedAdminUser);
    }

    @Override
    public AdminUserResponseDto updateAdminUser(Long userId, UpdateAdminUserDto updateAdminUserDto) {
        log.info("Updating admin user with ID: {}", userId);

        User adminUser = findAdminUserById(userId);
        return updateAdminUserInternal(adminUser, updateAdminUserDto);
    }

    @Override
    public AdminUserResponseDto updateAdminUserByUid(String uid, UpdateAdminUserDto updateAdminUserDto) {
        log.info("Updating admin user with UID: {}", uid);

        User adminUser = findAdminUserByUid(uid);
        return updateAdminUserInternal(adminUser, updateAdminUserDto);
    }

    private AdminUserResponseDto updateAdminUserInternal(User adminUser, UpdateAdminUserDto updateAdminUserDto) {
        // Prevent rootadmin modification
        validateNotRootAdmin(adminUser, "modified");

        boolean updated = false;

        // Update username if provided
        if (StringUtils.hasText(updateAdminUserDto.getUsername()) && 
            !updateAdminUserDto.getUsername().equals(adminUser.getUsername())) {
            
            if (userRepository.existsByUsername(updateAdminUserDto.getUsername())) {
                throw new ApiException("Username already exists", HttpStatus.CONFLICT);
            }
            adminUser.setUsername(updateAdminUserDto.getUsername());
            updated = true;
        }

        // Update email if provided
        if (StringUtils.hasText(updateAdminUserDto.getEmail()) && 
            !updateAdminUserDto.getEmail().equals(adminUser.getEmail())) {
            
            if (userRepository.existsByEmail(updateAdminUserDto.getEmail())) {
                throw new ApiException("Email already exists", HttpStatus.CONFLICT);
            }
            adminUser.setEmail(updateAdminUserDto.getEmail());
            updated = true;
        }

        // Update phone number if provided
        if (updateAdminUserDto.getPhoneNumber() != null) {
            if (updateAdminUserDto.getPhoneNumber().trim().isEmpty()) {
                // Clear phone number if empty string provided
                adminUser.setPhoneNumber(null);
                updated = true;
            } else {
                try {
                    String formattedPhone = PhoneNumberUtil.formatToInternational(
                        updateAdminUserDto.getPhoneNumber()
                    );
                    if (!formattedPhone.equals(adminUser.getPhoneNumber())) {
                        adminUser.setPhoneNumber(formattedPhone);
                        updated = true;
                    }
                } catch (ApiException e) {
                    log.error("Invalid phone number format: {}", 
                             updateAdminUserDto.getPhoneNumber(), e);
                    throw new ApiException("Invalid phone number format: " + e.getMessage(), 
                                         HttpStatus.BAD_REQUEST);
                }
            }
        }

        // Update require password change if provided
        if (updateAdminUserDto.getRequirePasswordChange() != null) {
            adminUser.setRequirePasswordChange(updateAdminUserDto.getRequirePasswordChange());
            updated = true;
        }

        if (updated) {
            try {
                User savedAdminUser = userRepository.save(adminUser);
                
                // Send admin user updated notification email
                sendAdminUserUpdatedEmail(savedAdminUser);
                
                log.info("Admin user updated successfully with ID: {}", savedAdminUser.getId());
                return convertToResponseDto(savedAdminUser);
            } catch (ObjectOptimisticLockingFailureException e) {
                log.warn("Concurrent update detected for admin user with ID: {}", adminUser.getId());
                throw new ApiException(
                    "User was modified by another process. Please refresh and try again.",
                    HttpStatus.CONFLICT
                );
            }
        } else {
            log.info("No changes detected for admin user with ID: {}", adminUser.getId());
            return convertToResponseDto(adminUser);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public AdminUserResponseDto getAdminUserById(Long userId) {
        log.info("Retrieving admin user with ID: {}", userId);
        User adminUser = findAdminUserById(userId);
        return convertToResponseDto(adminUser);
    }

    @Override
    @Transactional(readOnly = true)
    public AdminUserResponseDto getAdminUserByUid(String uid) {
        log.info("Retrieving admin user with UID: {}", uid);
        User adminUser = findAdminUserByUid(uid);
        return convertToResponseDto(adminUser);
    }

    @Override
    @Transactional(readOnly = true)
    public AdminUserResponseDto getAdminUserByUsername(String username) {
        log.info("Retrieving admin user with username: {}", username);
        User adminUser = findAdminUserByUsername(username);
        return convertToResponseDto(adminUser);
    }

    @Override
    @Transactional(readOnly = true)
    public AdminUserResponseDto getAdminUserByEmail(String email) {
        log.info("Retrieving admin user with email: {}", email);
        User adminUser = findAdminUserByEmail(email);
        return convertToResponseDto(adminUser);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AdminUserResponseDto> getAllAdminUsers(Pageable pageable) {
        log.info("Retrieving all admin users with pagination");
        Page<User> adminUsers = userRepository.findByUserTypeExcludingDeleted(UserType.ADMIN, pageable);
        return adminUsers.map(this::convertToResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AdminUserResponseDto> getAdminUsersByStatus(UserStatus status, Pageable pageable) {
        log.info("Retrieving admin users with status: {} and pagination", status);
        Page<User> adminUsers = userRepository.findByUserTypeAndStatusExcludingDeleted(
            UserType.ADMIN, status, pageable);
        return adminUsers.map(this::convertToResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AdminUserResponseDto> searchAdminUsers(String query, Pageable pageable) {
        log.info("Searching admin users with query: {}", query);
        
        // Validate query is not empty or whitespace
        if (query == null || query.trim().isEmpty()) {
            throw new ApiException("Search query parameter 'q' is required", HttpStatus.BAD_REQUEST);
        }
        
        String searchTerm = query.trim();
        Page<User> adminUsers = userRepository.searchAdminUsersByQuery(UserType.ADMIN, searchTerm, pageable);
        
        return adminUsers.map(this::convertToResponseDto);
    }

    @Override
    public AdminUserResponseDto deactivateAdminUser(Long userId) {
        log.info("Deactivating admin user with ID: {}", userId);
        User adminUser = findAdminUserById(userId);
        
        // Prevent rootadmin deactivation
        validateNotRootAdmin(adminUser, "deactivated");
        
        // Prevent admin from deactivating their own account
        validateNotSelfOperation(adminUser, "deactivate");
        
        adminUser.setStatus(UserStatus.INACTIVE);
        User savedAdminUser = userRepository.save(adminUser);
        log.info("Admin user deactivated successfully with ID: {}", savedAdminUser.getId());
        return convertToResponseDto(savedAdminUser);
    }

    @Override
    public AdminUserResponseDto deactivateAdminUserByUid(String uid) {
        log.info("Deactivating admin user with UID: {}", uid);
        User adminUser = findAdminUserByUid(uid);
        
        // Prevent rootadmin deactivation
        validateNotRootAdmin(adminUser, "deactivated");
        
        // Prevent admin from deactivating their own account
        validateNotSelfOperation(adminUser, "deactivate");
        
        adminUser.setStatus(UserStatus.INACTIVE);
        User savedAdminUser = userRepository.save(adminUser);
        
        // Send admin user deactivated notification email
        sendAdminUserDeactivatedEmail(savedAdminUser);
        
        log.info("Admin user deactivated successfully with UID: {}", savedAdminUser.getUid());
        return convertToResponseDto(savedAdminUser);
    }

    @Override
    public AdminUserResponseDto activateAdminUser(Long userId) {
        log.info("Activating admin user with ID: {}", userId);
        User adminUser = findAdminUserById(userId);
        
        // Prevent rootadmin modification (though activation shouldn't be needed for rootadmin)
        validateNotRootAdmin(adminUser, "modified");
        
        // Prevent admin from activating their own account
        validateNotSelfOperation(adminUser, "activate");
        
        adminUser.setStatus(UserStatus.ACTIVE);
        User savedAdminUser = userRepository.save(adminUser);
        log.info("Admin user activated successfully with ID: {}", savedAdminUser.getId());
        return convertToResponseDto(savedAdminUser);
    }

    @Override
    public AdminUserResponseDto activateAdminUserByUid(String uid) {
        log.info("Activating admin user with UID: {}", uid);
        User adminUser = findAdminUserByUid(uid);
        
        // Prevent rootadmin modification (though activation shouldn't be needed for rootadmin)
        validateNotRootAdmin(adminUser, "modified");
        
        // Prevent admin from activating their own account
        validateNotSelfOperation(adminUser, "activate");
        
        adminUser.setStatus(UserStatus.ACTIVE);
        User savedAdminUser = userRepository.save(adminUser);
        
        // Send admin user activated notification email
        sendAdminUserActivatedEmail(savedAdminUser);
        
        log.info("Admin user activated successfully with UID: {}", savedAdminUser.getUid());
        return convertToResponseDto(savedAdminUser);
    }

    @Override
    public AdminUserResponseDto suspendAdminUser(Long userId) {
        log.info("Suspending admin user with ID: {}", userId);
        User adminUser = findAdminUserById(userId);
        
        // Prevent rootadmin suspension
        validateNotRootAdmin(adminUser, "suspended");
        
        // Prevent admin from suspending their own account
        validateNotSelfOperation(adminUser, "suspend");
        
        adminUser.setStatus(UserStatus.SUSPENDED);
        User savedAdminUser = userRepository.save(adminUser);
        log.info("Admin user suspended successfully with ID: {}", savedAdminUser.getId());
        return convertToResponseDto(savedAdminUser);
    }

    @Override
    public AdminUserResponseDto suspendAdminUserByUid(String uid) {
        log.info("Suspending admin user with UID: {}", uid);
        User adminUser = findAdminUserByUid(uid);
        
        // Prevent rootadmin suspension
        validateNotRootAdmin(adminUser, "suspended");
        
        // Prevent admin from suspending their own account
        validateNotSelfOperation(adminUser, "suspend");
        
        adminUser.setStatus(UserStatus.SUSPENDED);
        User savedAdminUser = userRepository.save(adminUser);
        
        // Send admin user suspended notification email
        sendAdminUserSuspendedEmail(savedAdminUser);
        
        log.info("Admin user suspended successfully with UID: {}", savedAdminUser.getUid());
        return convertToResponseDto(savedAdminUser);
    }

    @Override
    @Transactional(readOnly = true)
    public long getAdminUserCount() {
        return userRepository.countByUserTypeExcludingDeleted(UserType.ADMIN);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isUsernameAvailable(String username) {
        return !userRepository.existsByUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isEmailAvailable(String email) {
        return !userRepository.existsByEmail(email);
    }

    // Helper methods

    private User findAdminUserById(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new ApiException("Admin user not found with ID: " + userId, HttpStatus.NOT_FOUND);
        }
        User user = userOpt.get();
        if (user.getUserType() != UserType.ADMIN) {
            throw new ApiException("User is not an admin user", HttpStatus.FORBIDDEN);
        }
        return user;
    }

    private User findAdminUserByUid(String uid) {
        Optional<User> userOpt = userRepository.findByUidExcludingDeleted(uid);
        if (userOpt.isEmpty()) {
            throw new ApiException("Admin user not found with UID: " + uid, HttpStatus.NOT_FOUND);
        }
        User user = userOpt.get();
        if (user.getUserType() != UserType.ADMIN) {
            throw new ApiException("User is not an admin user", HttpStatus.FORBIDDEN);
        }
        return user;
    }

    private User findAdminUserByUsername(String username) {
        Optional<User> userOpt = userRepository.findByUsernameExcludingDeleted(username);
        if (userOpt.isEmpty()) {
            throw new ApiException("Admin user not found with username: " + username, HttpStatus.NOT_FOUND);
        }
        User user = userOpt.get();
        if (user.getUserType() != UserType.ADMIN) {
            throw new ApiException("User is not an admin user", HttpStatus.FORBIDDEN);
        }
        return user;
    }

    private User findAdminUserByEmail(String email) {
        Optional<User> userOpt = userRepository.findByEmailExcludingDeleted(email);
        if (userOpt.isEmpty()) {
            throw new ApiException("Admin user not found with email: " + email, HttpStatus.NOT_FOUND);
        }
        User user = userOpt.get();
        if (user.getUserType() != UserType.ADMIN) {
            throw new ApiException("User is not an admin user", HttpStatus.FORBIDDEN);
        }
        return user;
    }

    private AdminUserResponseDto convertToResponseDto(User user) {
        AdminUserResponseDto responseDto = new AdminUserResponseDto();
        responseDto.setId(user.getId());
        responseDto.setUid(user.getUid());
        responseDto.setUsername(user.getUsername());
        responseDto.setEmail(user.getEmail());
        responseDto.setPhoneNumber(user.getPhoneNumber());
        responseDto.setStatus(user.getStatus());
        responseDto.setRequirePasswordChange(user.getRequirePasswordChange());
        responseDto.setCreatedAt(user.getCreatedAt());
        responseDto.setUpdatedAt(user.getUpdatedAt());
        responseDto.setLastLoginAt(user.getLastLoginAt());
        responseDto.setIsActive(user.isActive());
        
        // Set new fields
        responseDto.setEmailVerified(user.isEmailVerified());
        responseDto.setEmailVerifiedAt(user.getEmailVerifiedAt());
        responseDto.setFailedLoginAttempts(user.getFailedLoginAttempts());
        responseDto.setAccountLockedUntil(user.getAccountLockedUntil());
        responseDto.setPasswordChangedAt(user.getPasswordChangedAt());
        responseDto.setPasswordExpiresAt(user.getPasswordExpiresAt());
        responseDto.setDeleted(user.isDeleted());
        responseDto.setDeletedAt(user.getDeletedAt());
        responseDto.setCreatedBy(user.getCreatedBy());
        responseDto.setUpdatedBy(user.getUpdatedBy());
        responseDto.setVersion(user.getVersion());
        
        return responseDto;
    }

    // Email notification methods

    /**
     * Sends admin user created notification email.
     */
    private void sendAdminUserCreatedEmail(User adminUser, String password, boolean passwordGenerated) {
        try {
            // Prepare template variables
            Map<String, Object> templateVariables = Map.of(
                "username", adminUser.getUsername(),
                "email", adminUser.getEmail(),
                "password", password,
                "passwordGenerated", passwordGenerated,
                "requirePasswordChange", adminUser.getRequirePasswordChange()
            );
            
            // Use template with variables including the password
            emailService.sendTemplateEmailAsync(
                adminUser.getEmail(),
                "Admin Account Created - Taarifu Engine",
                "admin_user_created",
                templateVariables,
                EmailType.ADMIN_USER_CREATED
            );
            
            log.info("Admin user created email sent to: {} with {} password", 
                    adminUser.getEmail(), passwordGenerated ? "generated" : "provided");
        } catch (Exception e) {
            log.error("Failed to send admin user created email to: {}", adminUser.getEmail(), e);
        }
    }

    /**
     * Sends admin user created notification SMS.
     */
    private void sendAdminUserCreatedSms(User adminUser, String password) {
        String message = String.format(
            "Welcome to Taarifu Admin Portal! Username: %s. Password: %s. Please change your password after first login.",
            adminUser.getUsername(), password
        );
        smsService.sendSimpleSmsAsync(
            adminUser.getPhoneNumber(), 
            message, 
            SmsType.ADMIN_USER_CREATED
        );
        log.info("Admin user created SMS sent to: {}", adminUser.getPhoneNumber());
    }

    /**
     * Sends admin user updated notification email.
     */
    private void sendAdminUserUpdatedEmail(User adminUser) {
        try {
            emailService.sendTemplateEmailAsync(
                adminUser.getEmail(),
                "Admin Account Updated - Taarifu Engine",
                "admin_user_updated",
                Map.of(
                    "username", adminUser.getUsername(),
                    "email", adminUser.getEmail()
                ),
                EmailType.ADMIN_USER_UPDATED
            );
            
            log.info("Admin user updated email sent to: {}", adminUser.getEmail());
        } catch (Exception e) {
            log.error("Failed to send admin user updated email to: {}", adminUser.getEmail(), e);
        }
    }

    /**
     * Sends admin user activated notification email.
     */
    private void sendAdminUserActivatedEmail(User adminUser) {
        try {
            emailService.sendTemplateEmailAsync(
                adminUser.getEmail(),
                "Admin Account Activated - Taarifu Engine",
                "admin_user_activated",
                Map.of(
                    "username", adminUser.getUsername(),
                    "email", adminUser.getEmail()
                ),
                EmailType.ADMIN_USER_ACTIVATED
            );
            
            log.info("Admin user activated email sent to: {}", adminUser.getEmail());
        } catch (Exception e) {
            log.error("Failed to send admin user activated email to: {}", adminUser.getEmail(), e);
        }
    }

    /**
     * Sends admin user deactivated notification email.
     */
    private void sendAdminUserDeactivatedEmail(User adminUser) {
        try {
            emailService.sendTemplateEmailAsync(
                adminUser.getEmail(),
                "Admin Account Deactivated - Taarifu Engine",
                "admin_user_deactivated",
                Map.of(
                    "username", adminUser.getUsername(),
                    "email", adminUser.getEmail()
                ),
                EmailType.ADMIN_USER_DEACTIVATED
            );
            
            log.info("Admin user deactivated email sent to: {}", adminUser.getEmail());
        } catch (Exception e) {
            log.error("Failed to send admin user deactivated email to: {}", adminUser.getEmail(), e);
        }
    }

    /**
     * Sends admin user suspended notification email.
     */
    private void sendAdminUserSuspendedEmail(User adminUser) {
        try {
            emailService.sendTemplateEmailAsync(
                adminUser.getEmail(),
                "Admin Account Suspended - Taarifu Engine",
                "admin_user_suspended",
                Map.of(
                    "username", adminUser.getUsername(),
                    "email", adminUser.getEmail()
                ),
                EmailType.ADMIN_USER_SUSPENDED
            );
            
            log.info("Admin user suspended email sent to: {}", adminUser.getEmail());
        } catch (Exception e) {
            log.error("Failed to send admin user suspended email to: {}", adminUser.getEmail(), e);
        }
    }

    @Override
    public AdminUserResponseDto changePasswordByUid(String uid, ChangePasswordDto changePasswordDto) {
        log.info("Changing password for admin user with UID: {}", uid);

        // Find admin user by UID
        User adminUser = userRepository.findByUidAndUserType(uid, UserType.ADMIN)
            .orElseThrow(() -> new ApiException("Admin user not found with UID: " + uid, HttpStatus.NOT_FOUND));

        // Verify current password
        if (!passwordEncoder.matches(changePasswordDto.getCurrentPassword(), adminUser.getPasswordHash())) {
            throw new ApiException("Current password is incorrect", HttpStatus.BAD_REQUEST);
        }

        // Validate new password confirmation
        if (!changePasswordDto.getNewPassword().equals(changePasswordDto.getConfirmPassword())) {
            throw new ApiException("New password and confirmation do not match", HttpStatus.BAD_REQUEST);
        }

        // Validate new password strength for admin users
        if (!PasswordStrengthCalculator.meetsAdminPasswordRequirements(changePasswordDto.getNewPassword())) {
            throw new ApiException(
                "New password does not meet strength requirements. " + 
                PasswordStrengthCalculator.getAdminPasswordRequirements(),
                HttpStatus.BAD_REQUEST
            );
        }

        // Check if new password is different from current
        if (passwordEncoder.matches(changePasswordDto.getNewPassword(), adminUser.getPasswordHash())) {
            throw new ApiException("New password must be different from current password", HttpStatus.BAD_REQUEST);
        }

        // Hash new password and update strength
        String hashedPassword = passwordEncoder.encode(changePasswordDto.getNewPassword());
        adminUser.setPasswordHash(hashedPassword);
        adminUser.setPasswordStrength(PasswordStrengthCalculator.calculatePasswordStrength(changePasswordDto.getNewPassword()));
        adminUser.setRequirePasswordChange(false); // Password change completed
        
        // Update password tracking fields
        adminUser.setPasswordChangedAt(java.time.LocalDateTime.now());
        adminUser.updatePasswordExpiry(); // Sets passwordExpiresAt based on passwordExpiryDays

        // Save updated admin user
        User updatedAdminUser = userRepository.save(adminUser);

        // Send password changed notification email
        sendPasswordChangedEmail(updatedAdminUser);

        log.info("Password changed successfully for admin user with UID: {}", uid);
        return convertToResponseDto(updatedAdminUser);
    }

    @Override
    public AdminUserResponseDto resetPasswordByUid(String uid, ResetPasswordDto resetPasswordDto) {
        log.info("Resetting password for admin user with UID: {}", uid);

        // Find admin user by UID
        User adminUser = userRepository.findByUidAndUserType(uid, UserType.ADMIN)
            .orElseThrow(() -> new ApiException("Admin user not found with UID: " + uid, HttpStatus.NOT_FOUND));

        // Prevent rootadmin password reset
        validateNotRootAdmin(adminUser, "modified");

        // Prevent admin from resetting their own password
        validateNotSelfOperation(adminUser, "reset password for");

        // Validate new password strength for admin users
        if (!PasswordStrengthCalculator.meetsAdminPasswordRequirements(resetPasswordDto.getNewPassword())) {
            throw new ApiException(
                "New password does not meet strength requirements. " + 
                PasswordStrengthCalculator.getAdminPasswordRequirements(),
                HttpStatus.BAD_REQUEST
            );
        }

        // Hash new password and update strength
        String hashedPassword = passwordEncoder.encode(resetPasswordDto.getNewPassword());
        adminUser.setPasswordHash(hashedPassword);
        adminUser.setPasswordStrength(PasswordStrengthCalculator.calculatePasswordStrength(resetPasswordDto.getNewPassword()));
        adminUser.setRequirePasswordChange(resetPasswordDto.getRequirePasswordChange());
        
        // Update password tracking fields
        adminUser.setPasswordChangedAt(java.time.LocalDateTime.now());
        adminUser.updatePasswordExpiry(); // Sets passwordExpiresAt based on passwordExpiryDays

        // Save updated admin user
        User updatedAdminUser = userRepository.save(adminUser);

        // Send password reset notification email if requested
        if (resetPasswordDto.getSendEmailNotification()) {
            sendPasswordResetEmail(updatedAdminUser, resetPasswordDto.getNewPassword());
        }

        // Send password reset notification SMS if requested and user has phone number
        if (resetPasswordDto.getSendSmsNotification() && 
            StringUtils.hasText(updatedAdminUser.getPhoneNumber())) {
            sendPasswordResetSms(updatedAdminUser, resetPasswordDto.getNewPassword());
        }

        log.info("Password reset successfully for admin user with UID: {}", uid);
        return convertToResponseDto(updatedAdminUser);
    }

    @Override
    public AdminUserResponseDto generateNewPasswordByUid(String uid) {
        log.info("Generating new password for admin user with UID: {}", uid);

        // Find admin user by UID
        User adminUser = userRepository.findByUidAndUserType(uid, UserType.ADMIN)
            .orElseThrow(() -> new ApiException("Admin user not found with UID: " + uid, HttpStatus.NOT_FOUND));

        // Prevent rootadmin password reset
        validateNotRootAdmin(adminUser, "modified");

        // Prevent admin from generating password for themselves
        validateNotSelfOperation(adminUser, "generate password for");

        // Generate a secure password for admin user
        String newPassword = PasswordGenerator.generateTemporaryAdminPassword();
        log.info("Generated new secure password for admin user: {}", adminUser.getUsername());

        // Hash new password and update strength
        String hashedPassword = passwordEncoder.encode(newPassword);
        adminUser.setPasswordHash(hashedPassword);
        adminUser.setPasswordStrength(PasswordStrengthCalculator.calculatePasswordStrength(newPassword));
        adminUser.setRequirePasswordChange(true); // Require password change on next login
        
        // Update password tracking fields
        adminUser.setPasswordChangedAt(java.time.LocalDateTime.now());
        adminUser.updatePasswordExpiry(); // Sets passwordExpiresAt based on passwordExpiryDays

        // Save updated admin user
        User updatedAdminUser = userRepository.save(adminUser);

        // Send new password via email
        sendNewPasswordEmail(updatedAdminUser, newPassword);

        log.info("New password generated and sent successfully for admin user with UID: {}", uid);
        return convertToResponseDto(updatedAdminUser);
    }

    /**
     * Sends password changed notification email.
     */
    private void sendPasswordChangedEmail(User adminUser) {
        try {
            emailService.sendTemplateEmailAsync(
                adminUser.getEmail(),
                "Password Changed - Taarifu Engine",
                "password_changed",
                Map.of(
                    "username", adminUser.getUsername(),
                    "email", adminUser.getEmail()
                ),
                EmailType.PASSWORD_CHANGED
            );
            
            log.info("Password changed email sent to: {}", adminUser.getEmail());
        } catch (Exception e) {
            log.error("Failed to send password changed email to: {}", adminUser.getEmail(), e);
        }
    }

    /**
     * Sends password reset notification email.
     */
    private void sendPasswordResetEmail(User adminUser, String newPassword) {
        try {
            String subject = "Password Reset - Taarifu Engine";
            
            String emailBody = String.format("""
                Hello %s,
                
                Your admin account password has been reset by an administrator.
                
                Your New Password: %s
                
                SECURITY NOTICE:
                - Please log in immediately and change this password
                - Do not share this password with anyone
                - Use a strong, unique password for your account
                
                %s
                
                If you have any questions or need assistance, please contact the system administrator.
                
                Best regards,
                Taarifu Team
                
                This is an automated message. Please do not reply to this email.
                """, 
                adminUser.getUsername(),
                newPassword,
                adminUser.getRequirePasswordChange() 
                    ? "Important: You will be required to change your password on your first login."
                    : "You can now log in with this password."
            );
            
            emailService.sendSimpleEmailAsync(
                adminUser.getEmail(),
                subject,
                emailBody,
                EmailType.PASSWORD_RESET
            );
            
            log.info("Password reset email sent to: {}", adminUser.getEmail());
        } catch (Exception e) {
            log.error("Failed to send password reset email to: {}", adminUser.getEmail(), e);
        }
    }

    /**
     * Sends password reset notification SMS.
     */
    private void sendPasswordResetSms(User adminUser, String newPassword) {
        try {
            String message = String.format(
                "Your Taarifu Admin Portal password has been reset. Username: %s. New Password: %s. %s",
                adminUser.getUsername(),
                newPassword,
                adminUser.getRequirePasswordChange() 
                    ? "Please change your password after first login." 
                    : "You can now log in with this password."
            );
            
            smsService.sendSimpleSmsAsync(
                adminUser.getPhoneNumber(),
                message,
                SmsType.PASSWORD_RESET
            );
            
            log.info("Password reset SMS sent to: {}", adminUser.getPhoneNumber());
        } catch (Exception e) {
            log.error("Failed to send password reset SMS to: {}", adminUser.getPhoneNumber(), e);
            // Don't throw exception - SMS failure shouldn't break the flow
        }
    }

    /**
     * Sends new password notification email.
     */
    private void sendNewPasswordEmail(User adminUser, String newPassword) {
        try {
            emailService.sendTemplateEmailAsync(
                adminUser.getEmail(),
                "New Password Generated - Taarifu Engine",
                "new_password_generated",
                Map.of(
                    "username", adminUser.getUsername(),
                    "email", adminUser.getEmail(),
                    "password", newPassword,
                    "requirePasswordChange", adminUser.getRequirePasswordChange()
                ),
                EmailType.NEW_PASSWORD_GENERATED
            );
            
            log.info("New password email sent to: {}", adminUser.getEmail());
        } catch (Exception e) {
            log.error("Failed to send new password email to: {}", adminUser.getEmail(), e);
        }
    }

    // Frontend-Friendly Method Implementations

    @Override
    public Page<AdminUserSummaryDto> getAdminUserSummaries(Pageable pageable) {
        log.info("Getting admin user summaries with pagination: {}", pageable);
        
        Page<User> adminUsers = userRepository.findByUserTypeExcludingDeleted(UserType.ADMIN, pageable);
        
        return adminUsers.map(this::convertToSummaryDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AdminUserSummaryDto> getAdminUserSummariesByStatus(UserStatus status, Pageable pageable) {
        log.info("Getting admin user summaries with status: {} and pagination: {}", status, pageable);
        Page<User> adminUsers = userRepository.findByUserTypeAndStatusExcludingDeleted(
            UserType.ADMIN, status, pageable);
        return adminUsers.map(this::convertToSummaryDto);
    }

    @Override
    public AdminUserSummaryDto getAdminUserSummaryByUid(String uid) {
        log.info("Getting admin user summary with UID: {}", uid);
        
        User adminUser = findAdminUserByUid(uid);
        return convertToSummaryDto(adminUser);
    }

    @Override
    public PasswordValidationDto validatePassword(String password, boolean isAdminPassword) {
        log.debug("Validating password for admin user");
        
        PasswordValidationDto validation = new PasswordValidationDto();
        
        // Calculate password strength
        validation.setStrength(PasswordStrengthCalculator.calculatePasswordStrength(password));
        
        // Validate based on requirements
        if (isAdminPassword) {
            validation.setIsValid(PasswordStrengthCalculator.meetsAdminPasswordRequirements(password));
            validation.setMessages(List.of(
                "Password must be at least 12 characters long",
                "Password must contain uppercase letters",
                "Password must contain lowercase letters", 
                "Password must contain numbers",
                "Password must contain special characters"
            ));
        } else {
            // For non-admin passwords, use basic validation
            boolean isValid = password != null && password.length() >= 8;
            validation.setIsValid(isValid);
            validation.setMessages(List.of(
                "Password must be at least 8 characters long",
                "Password should contain a mix of letters and numbers"
            ));
        }
        
        return validation;
    }

    /**
     * Converts a User entity to AdminUserSummaryDto.
     */
    private AdminUserSummaryDto convertToSummaryDto(User user) {
        AdminUserSummaryDto summaryDto = new AdminUserSummaryDto();
        summaryDto.setUid(user.getUid());
        summaryDto.setUsername(user.getUsername());
        summaryDto.setEmail(user.getEmail());
        summaryDto.setStatus(user.getStatus());
        summaryDto.setRequirePasswordChange(user.getRequirePasswordChange());
        summaryDto.setCreatedAt(user.getCreatedAt());
        return summaryDto;
    }

    @Override
    public AdminUserResponseDto softDeleteUser(String uid, String deletedByUid) {
        log.info("Soft deleting admin user with UID: {}", uid);
        
        User adminUser = findAdminUserByUid(uid);
        
        // Prevent rootadmin deletion
        validateNotRootAdmin(adminUser, "deleted");
        
        // Check if already deleted
        if (adminUser.isDeleted()) {
            throw new ApiException("Admin user is already deleted", HttpStatus.BAD_REQUEST);
        }
        
        adminUser.softDelete(deletedByUid);
        User savedAdminUser = userRepository.save(adminUser);
        
        log.info("Admin user soft deleted successfully with UID: {}", uid);
        return convertToResponseDto(savedAdminUser);
    }

    @Override
    public AdminUserResponseDto restoreUser(String uid) {
        log.info("Restoring admin user with UID: {}", uid);
        
        // Use findByUid to include soft-deleted users for restore
        Optional<User> userOpt = userRepository.findByUid(uid);
        if (userOpt.isEmpty()) {
            throw new ApiException("Admin user not found with UID: " + uid, HttpStatus.NOT_FOUND);
        }
        
        User user = userOpt.get();
        if (user.getUserType() != UserType.ADMIN) {
            throw new ApiException("User is not an admin user", HttpStatus.FORBIDDEN);
        }
        
        if (!user.isDeleted()) {
            throw new ApiException("Admin user is not deleted", HttpStatus.BAD_REQUEST);
        }
        
        user.restore();
        User savedAdminUser = userRepository.save(user);
        
        log.info("Admin user restored successfully with UID: {}", uid);
        return convertToResponseDto(savedAdminUser);
    }

    @Override
    public AdminUserResponseDto lockUserAccount(String uid, Integer lockoutMinutes) {
        log.info("Locking admin user account with UID: {}", uid);
        
        User adminUser = findAdminUserByUid(uid);
        
        // Prevent rootadmin account lock
        validateNotRootAdmin(adminUser, "locked");
        
        int minutes = (lockoutMinutes != null && lockoutMinutes > 0) ? lockoutMinutes : 30;
        adminUser.lockAccount(minutes);
        
        User savedAdminUser = userRepository.save(adminUser);
        
        log.info("Admin user account locked for {} minutes with UID: {}", minutes, uid);
        return convertToResponseDto(savedAdminUser);
    }

    @Override
    public AdminUserResponseDto unlockUserAccount(String uid) {
        log.info("Unlocking admin user account with UID: {}", uid);
        
        User adminUser = findAdminUserByUid(uid);
        
        adminUser.unlockAccount();
        
        User savedAdminUser = userRepository.save(adminUser);
        
        log.info("Admin user account unlocked with UID: {}", uid);
        return convertToResponseDto(savedAdminUser);
    }

    @Override
    public com.taarifu_engine_api.modules.auth.domain.dto.ForgotPasswordResponseDto resendVerificationEmail(String uid) {
        log.info("Resending verification email for admin user with UID: {}", uid);
        
        User adminUser = findAdminUserByUid(uid);
        
        // Use AuthService to resend verification
        return authService.resendEmailVerification(adminUser.getEmail());
    }

    /**
     * Gets the current authenticated user from SecurityContext
     */
    private User getCurrentAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ApiException("No authenticated user found", HttpStatus.UNAUTHORIZED);
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof User user) {
            return user;
        } else {
            throw new ApiException("Invalid user principal type", HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * Checks if a user is the root admin user
     */
    private boolean isRootAdmin(User user) {
        return user != null && rootAdminUsername.equalsIgnoreCase(user.getUsername());
    }

    /**
     * Validates that the target user is not rootadmin (immutable)
     */
    private void validateNotRootAdmin(User user, String operation) {
        if (isRootAdmin(user)) {
            throw new ApiException(
                String.format("Root admin user cannot be %s. Root admin is immutable.", operation),
                HttpStatus.FORBIDDEN
            );
        }
    }

    /**
     * Validates that the current user is not trying to perform an operation on themselves
     */
    private void validateNotSelfOperation(User targetUser, String operation) {
        User currentUser = getCurrentAuthenticatedUser();
        if (currentUser.getUid().equals(targetUser.getUid())) {
            throw new ApiException(
                String.format("You cannot %s your own account", operation),
                HttpStatus.FORBIDDEN
            );
        }
    }
}
