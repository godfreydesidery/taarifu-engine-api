package com.taarifu_engine_api.modules.userandrole.service;

import com.taarifu_engine_api.modules.common.domain.util.PasswordGenerator;
import com.taarifu_engine_api.modules.common.domain.util.PasswordStrengthCalculator;
import com.taarifu_engine_api.modules.common.exception.ApiException;
import com.taarifu_engine_api.modules.notification.domain.enums.EmailType;
import com.taarifu_engine_api.modules.notification.service.EmailService;
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

        // Hash password and set strength
        String hashedPassword = passwordEncoder.encode(password);
        adminUser.setPasswordHash(hashedPassword);
        adminUser.setPasswordStrength(PasswordStrengthCalculator.calculatePasswordStrength(password));
        
        // TEMPORARY: Store raw password for testing (remove in production)
        adminUser.setRawPassword(password);

        // Ensure ULID is generated
        adminUser.ensureUid();

        // Save admin user
        User savedAdminUser = userRepository.save(adminUser);

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


        // Update require password change if provided
        if (updateAdminUserDto.getRequirePasswordChange() != null) {
            adminUser.setRequirePasswordChange(updateAdminUserDto.getRequirePasswordChange());
            updated = true;
        }

        if (updated) {
            User savedAdminUser = userRepository.save(adminUser);
            
            // Send admin user updated notification email
            sendAdminUserUpdatedEmail(savedAdminUser);
            
            log.info("Admin user updated successfully with ID: {}", savedAdminUser.getId());
            return convertToResponseDto(savedAdminUser);
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
        Page<User> adminUsers = userRepository.findByUserType(UserType.ADMIN, pageable);
        return adminUsers.map(this::convertToResponseDto);
    }

    @Override
    public AdminUserResponseDto deactivateAdminUser(Long userId) {
        log.info("Deactivating admin user with ID: {}", userId);
        User adminUser = findAdminUserById(userId);
        adminUser.setStatus(UserStatus.INACTIVE);
        User savedAdminUser = userRepository.save(adminUser);
        log.info("Admin user deactivated successfully with ID: {}", savedAdminUser.getId());
        return convertToResponseDto(savedAdminUser);
    }

    @Override
    public AdminUserResponseDto deactivateAdminUserByUid(String uid) {
        log.info("Deactivating admin user with UID: {}", uid);
        User adminUser = findAdminUserByUid(uid);
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
        adminUser.setStatus(UserStatus.ACTIVE);
        User savedAdminUser = userRepository.save(adminUser);
        log.info("Admin user activated successfully with ID: {}", savedAdminUser.getId());
        return convertToResponseDto(savedAdminUser);
    }

    @Override
    public AdminUserResponseDto activateAdminUserByUid(String uid) {
        log.info("Activating admin user with UID: {}", uid);
        User adminUser = findAdminUserByUid(uid);
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
        adminUser.setStatus(UserStatus.SUSPENDED);
        User savedAdminUser = userRepository.save(adminUser);
        log.info("Admin user suspended successfully with ID: {}", savedAdminUser.getId());
        return convertToResponseDto(savedAdminUser);
    }

    @Override
    public AdminUserResponseDto suspendAdminUserByUid(String uid) {
        log.info("Suspending admin user with UID: {}", uid);
        User adminUser = findAdminUserByUid(uid);
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
        return userRepository.countByUserType(UserType.ADMIN);
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
        Optional<User> userOpt = userRepository.findByUid(uid);
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
        Optional<User> userOpt = userRepository.findByUsername(username);
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
        Optional<User> userOpt = userRepository.findByEmail(email);
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
        responseDto.setStatus(user.getStatus());
        responseDto.setRequirePasswordChange(user.getRequirePasswordChange());
        responseDto.setCreatedAt(user.getCreatedAt());
        responseDto.setUpdatedAt(user.getUpdatedAt());
        responseDto.setLastLoginAt(user.getLastLoginAt());
        responseDto.setIsActive(user.isActive());
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
        
        // TEMPORARY: Update raw password for testing (remove in production)
        adminUser.setRawPassword(changePasswordDto.getNewPassword());

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
        
        // TEMPORARY: Update raw password for testing (remove in production)
        adminUser.setRawPassword(resetPasswordDto.getNewPassword());

        // Save updated admin user
        User updatedAdminUser = userRepository.save(adminUser);

        // Send password reset notification email if requested
        if (resetPasswordDto.getSendEmailNotification()) {
            sendPasswordResetEmail(updatedAdminUser, resetPasswordDto.getNewPassword());
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

        // Generate a secure password for admin user
        String newPassword = PasswordGenerator.generateTemporaryAdminPassword();
        log.info("Generated new secure password for admin user: {}", adminUser.getUsername());

        // Hash new password and update strength
        String hashedPassword = passwordEncoder.encode(newPassword);
        adminUser.setPasswordHash(hashedPassword);
        adminUser.setPasswordStrength(PasswordStrengthCalculator.calculatePasswordStrength(newPassword));
        adminUser.setRequirePasswordChange(true); // Require password change on next login
        
        // TEMPORARY: Update raw password for testing (remove in production)
        adminUser.setRawPassword(newPassword);

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
            emailService.sendTemplateEmailAsync(
                adminUser.getEmail(),
                "Password Reset - Taarifu Engine",
                "password_reset",
                Map.of(
                    "username", adminUser.getUsername(),
                    "email", adminUser.getEmail(),
                    "password", newPassword,
                    "requirePasswordChange", adminUser.getRequirePasswordChange()
                ),
                EmailType.PASSWORD_RESET
            );
            
            log.info("Password reset email sent to: {}", adminUser.getEmail());
        } catch (Exception e) {
            log.error("Failed to send password reset email to: {}", adminUser.getEmail(), e);
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
        
        Page<User> adminUsers = userRepository.findByUserType(UserType.ADMIN, pageable);
        
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
}
