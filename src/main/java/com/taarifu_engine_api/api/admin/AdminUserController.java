package com.taarifu_engine_api.api.admin;

import com.taarifu_engine_api.modules.common.domain.util.PageResponseWrapper;
import com.taarifu_engine_api.modules.common.domain.util.ResponseWrapper;
import com.taarifu_engine_api.modules.notification.domain.dto.EmailRequestDto;
import com.taarifu_engine_api.modules.notification.domain.dto.EmailResponseDto;
import com.taarifu_engine_api.modules.notification.domain.enums.EmailStatus;
import com.taarifu_engine_api.modules.notification.domain.enums.EmailType;
import com.taarifu_engine_api.modules.notification.service.EmailService;
import com.taarifu_engine_api.modules.userandrole.domain.dto.AdminUserResponseDto;
import com.taarifu_engine_api.modules.userandrole.domain.dto.AdminUserSummaryDto;
import com.taarifu_engine_api.modules.userandrole.domain.dto.ChangePasswordDto;
import com.taarifu_engine_api.modules.userandrole.domain.dto.CreateAdminUserDto;
import com.taarifu_engine_api.modules.userandrole.domain.dto.PasswordValidationDto;
import com.taarifu_engine_api.modules.userandrole.domain.dto.ResetPasswordDto;
import com.taarifu_engine_api.modules.userandrole.domain.dto.UpdateAdminUserDto;
import com.taarifu_engine_api.modules.userandrole.service.AdminUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for admin user management operations.
 * Provides endpoints specifically for managing admin users (UserType.ADMIN).
 * All endpoints require admin authentication and authorization.
 */
@RestController
@RequestMapping("/admin/v1/users")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final AdminUserService adminUserService;
    private final EmailService emailService;

    /**
     * Creates a new admin user.
     *
     * @param createAdminUserDto the admin user creation data
     * @return the created admin user response
     */
    @PostMapping("/admin-users")
    public ResponseEntity<ResponseWrapper<AdminUserResponseDto>> createAdminUser(
            @Valid @RequestBody CreateAdminUserDto createAdminUserDto) {
        
        log.info("Creating admin user with username: {}", createAdminUserDto.getUsername());
        
        AdminUserResponseDto adminUser = adminUserService.createAdminUser(createAdminUserDto);
        
        ResponseWrapper<AdminUserResponseDto> response = new ResponseWrapper<>(
            true,
            HttpStatus.CREATED.value(),
            "Admin user created successfully",
            adminUser
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Updates an existing admin user by UID.
     *
     * @param uid the UID of the admin user to update
     * @param updateAdminUserDto the admin user update data
     * @return the updated admin user response
     */
    @PutMapping("/admin-users/uid/{uid}")
    public ResponseEntity<ResponseWrapper<AdminUserResponseDto>> updateAdminUser(
            @PathVariable String uid,
            @Valid @RequestBody UpdateAdminUserDto updateAdminUserDto) {
        
        log.info("Updating admin user with UID: {}", uid);
        
        AdminUserResponseDto adminUser = adminUserService.updateAdminUserByUid(uid, updateAdminUserDto);
        
        ResponseWrapper<AdminUserResponseDto> response = new ResponseWrapper<>(
            true,
            HttpStatus.OK.value(),
            "Admin user updated successfully",
            adminUser
        );
        
        return ResponseEntity.ok(response);
    }



    /**
     * Retrieves an admin user by UID.
     *
     * @param uid the UID of the admin user
     * @return the admin user response
     */
    @GetMapping("/admin-users/uid/{uid}")
    public ResponseEntity<ResponseWrapper<AdminUserResponseDto>> getAdminUserByUid(@PathVariable String uid) {
        log.info("Retrieving admin user with UID: {}", uid);
        
        AdminUserResponseDto adminUser = adminUserService.getAdminUserByUid(uid);
        
        ResponseWrapper<AdminUserResponseDto> response = new ResponseWrapper<>(
            true,
            HttpStatus.OK.value(),
            "Admin user retrieved successfully",
            adminUser
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves an admin user by username.
     *
     * @param username the username of the admin user
     * @return the admin user response
     */
    @GetMapping("/admin-users/username/{username}")
    public ResponseEntity<ResponseWrapper<AdminUserResponseDto>> getAdminUserByUsername(@PathVariable String username) {
        log.info("Retrieving admin user with username: {}", username);
        
        AdminUserResponseDto adminUser = adminUserService.getAdminUserByUsername(username);
        
        ResponseWrapper<AdminUserResponseDto> response = new ResponseWrapper<>(
            true,
            HttpStatus.OK.value(),
            "Admin user retrieved successfully",
            adminUser
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves an admin user by email.
     *
     * @param email the email of the admin user
     * @return the admin user response
     */
    @GetMapping("/admin-users/email/{email}")
    public ResponseEntity<ResponseWrapper<AdminUserResponseDto>> getAdminUserByEmail(@PathVariable String email) {
        log.info("Retrieving admin user with email: {}", email);
        
        AdminUserResponseDto adminUser = adminUserService.getAdminUserByEmail(email);
        
        ResponseWrapper<AdminUserResponseDto> response = new ResponseWrapper<>(
            true,
            HttpStatus.OK.value(),
            "Admin user retrieved successfully",
            adminUser
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves all admin users with pagination.
     *
     * @param pageable pagination parameters
     * @return a page of admin user responses
     */
    @GetMapping("/admin-users")
    public ResponseEntity<PageResponseWrapper<AdminUserResponseDto>> getAllAdminUsers(
            @PageableDefault(size = 20) Pageable pageable) {
        
        log.info("Retrieving all admin users with pagination");
        
        Page<AdminUserResponseDto> adminUsers = adminUserService.getAllAdminUsers(pageable);
        
        PageResponseWrapper<AdminUserResponseDto> response = PageResponseWrapper.fromPage(
            adminUsers,
            "Admin users retrieved successfully"
        );
        
        return ResponseEntity.ok(response);
    }


    /**
     * Deactivates an admin user by UID.
     *
     * @param uid the UID of the admin user to deactivate
     * @return the deactivated admin user response
     */
    @PutMapping("/admin-users/uid/{uid}/deactivate")
    public ResponseEntity<ResponseWrapper<AdminUserResponseDto>> deactivateAdminUser(@PathVariable String uid) {
        log.info("Deactivating admin user with UID: {}", uid);
        
        AdminUserResponseDto adminUser = adminUserService.deactivateAdminUserByUid(uid);
        
        ResponseWrapper<AdminUserResponseDto> response = new ResponseWrapper<>(
            true,
            HttpStatus.OK.value(),
            "Admin user deactivated successfully",
            adminUser
        );
        
        return ResponseEntity.ok(response);
    }


    /**
     * Activates an admin user by UID.
     *
     * @param uid the UID of the admin user to activate
     * @return the activated admin user response
     */
    @PutMapping("/admin-users/uid/{uid}/activate")
    public ResponseEntity<ResponseWrapper<AdminUserResponseDto>> activateAdminUser(@PathVariable String uid) {
        log.info("Activating admin user with UID: {}", uid);
        
        AdminUserResponseDto adminUser = adminUserService.activateAdminUserByUid(uid);
        
        ResponseWrapper<AdminUserResponseDto> response = new ResponseWrapper<>(
            true,
            HttpStatus.OK.value(),
            "Admin user activated successfully",
            adminUser
        );
        
        return ResponseEntity.ok(response);
    }


    /**
     * Suspends an admin user by UID.
     *
     * @param uid the UID of the admin user to suspend
     * @return the suspended admin user response
     */
    @PutMapping("/admin-users/uid/{uid}/suspend")
    public ResponseEntity<ResponseWrapper<AdminUserResponseDto>> suspendAdminUser(@PathVariable String uid) {
        log.info("Suspending admin user with UID: {}", uid);
        
        AdminUserResponseDto adminUser = adminUserService.suspendAdminUserByUid(uid);
        
        ResponseWrapper<AdminUserResponseDto> response = new ResponseWrapper<>(
            true,
            HttpStatus.OK.value(),
            "Admin user suspended successfully",
            adminUser
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * Gets the count of admin users.
     *
     * @return the total number of admin users
     */
    @GetMapping("/admin-users/count")
    public ResponseEntity<ResponseWrapper<Long>> getAdminUserCount() {
        log.info("Retrieving admin user count");
        
        long count = adminUserService.getAdminUserCount();
        
        ResponseWrapper<Long> response = new ResponseWrapper<>(
            true,
            HttpStatus.OK.value(),
            "Admin user count retrieved successfully",
            count
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * Checks if a username is available for admin users.
     *
     * @param username the username to check
     * @return true if available, false otherwise
     */
    @GetMapping("/admin-users/check-username/{username}")
    public ResponseEntity<ResponseWrapper<Boolean>> isUsernameAvailable(@PathVariable String username) {
        log.info("Checking username availability: {}", username);
        
        boolean available = adminUserService.isUsernameAvailable(username);
        
        ResponseWrapper<Boolean> response = new ResponseWrapper<>(
            true,
            HttpStatus.OK.value(),
            "Username availability checked successfully",
            available
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * Checks if an email is available for admin users.
     *
     * @param email the email to check
     * @return true if available, false otherwise
     */
    @GetMapping("/admin-users/check-email/{email}")
    public ResponseEntity<ResponseWrapper<Boolean>> isEmailAvailable(@PathVariable String email) {
        log.info("Checking email availability: {}", email);
        
        boolean available = adminUserService.isEmailAvailable(email);
        
        ResponseWrapper<Boolean> response = new ResponseWrapper<>(
            true,
            HttpStatus.OK.value(),
            "Email availability checked successfully",
            available
        );
        
        return ResponseEntity.ok(response);
    }

    // Password Management Endpoints

    /**
     * Changes the password for an admin user.
     * Requires current password verification.
     */
    @PutMapping("/admin-users/uid/{uid}/change-password")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseWrapper<AdminUserResponseDto>> changePassword(
            @PathVariable String uid,
            @Valid @RequestBody ChangePasswordDto changePasswordDto) {
        log.info("Changing password for admin user with UID: {}", uid);
        AdminUserResponseDto adminUser = adminUserService.changePasswordByUid(uid, changePasswordDto);
        
        ResponseWrapper<AdminUserResponseDto> response = new ResponseWrapper<>(
            true,
            HttpStatus.OK.value(),
            "Password changed successfully",
            adminUser
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * Resets the password for an admin user.
     * Admin-only operation that bypasses current password verification.
     */
    @PutMapping("/admin-users/uid/{uid}/reset-password")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseWrapper<AdminUserResponseDto>> resetPassword(
            @PathVariable String uid,
            @Valid @RequestBody ResetPasswordDto resetPasswordDto) {
        log.info("Resetting password for admin user with UID: {}", uid);
        AdminUserResponseDto adminUser = adminUserService.resetPasswordByUid(uid, resetPasswordDto);
        
        ResponseWrapper<AdminUserResponseDto> response = new ResponseWrapper<>(
            true,
            HttpStatus.OK.value(),
            "Password reset successfully",
            adminUser
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * Generates a new temporary password for an admin user.
     * Sends the new password via email.
     */
    @PostMapping("/admin-users/uid/{uid}/generate-password")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseWrapper<AdminUserResponseDto>> generateNewPassword(
            @PathVariable String uid) {
        log.info("Generating new password for admin user with UID: {}", uid);
        AdminUserResponseDto adminUser = adminUserService.generateNewPasswordByUid(uid);
        
        ResponseWrapper<AdminUserResponseDto> response = new ResponseWrapper<>(
            true,
            HttpStatus.OK.value(),
            "New password generated and sent via email",
            adminUser
        );
        
        return ResponseEntity.ok(response);
    }

    // Frontend-Friendly Endpoints

    /**
     * Gets a paginated list of admin users in summary format.
     * Optimized for frontend lists and tables.
     */
    @GetMapping("/admin-users/summaries")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageResponseWrapper<AdminUserSummaryDto>> getAdminUserSummaries(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        log.info("Getting admin user summaries - page: {}, size: {}, sort: {} {}", page, size, sortBy, sortDir);
        
        Page<AdminUserSummaryDto> adminUsers = adminUserService.getAdminUserSummaries(
            PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortDir), sortBy))
        );
        
        PageResponseWrapper<AdminUserSummaryDto> response = PageResponseWrapper.fromPage(
            adminUsers,
            "Admin user summaries retrieved successfully"
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * Gets a single admin user summary by UID.
     * Optimized for frontend quick views and dropdowns.
     */
    @GetMapping("/admin-users/uid/{uid}/summary")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseWrapper<AdminUserSummaryDto>> getAdminUserSummary(
            @PathVariable String uid) {
        log.info("Getting admin user summary with UID: {}", uid);
        AdminUserSummaryDto adminUser = adminUserService.getAdminUserSummaryByUid(uid);
        
        ResponseWrapper<AdminUserSummaryDto> response = new ResponseWrapper<>(
            true,
            HttpStatus.OK.value(),
            "Admin user summary retrieved successfully",
            adminUser
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * Validates a password and returns detailed feedback.
     * Used for real-time password strength validation in frontend.
     */
    @PostMapping("/admin-users/validate-password")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseWrapper<PasswordValidationDto>> validatePassword(
            @RequestParam String password,
            @RequestParam(defaultValue = "true") boolean isAdminPassword) {
        
        log.info("Validating password for admin user");
        PasswordValidationDto validation = adminUserService.validatePassword(password, isAdminPassword);
        
        ResponseWrapper<PasswordValidationDto> response = new ResponseWrapper<>(
            true,
            HttpStatus.OK.value(),
            "Password validation completed",
            validation
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * Test endpoint to verify email functionality.
     * This endpoint sends a test email to verify SMTP configuration.
     */
    @PostMapping("/admin-users/test-email")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseWrapper<String>> testEmail(
            @RequestParam String email) {
        
        log.info("Testing email functionality for: {}", email);
        
        try {
            // Create a simple test email
            EmailRequestDto testEmail = new EmailRequestDto();
            testEmail.setTo(email);
            testEmail.setSubject("Test Email - Taarifu Engine");
            testEmail.setBody("<h1>Test Email</h1><p>This is a test email to verify SMTP configuration.</p>");
            testEmail.setHtml(true);
            testEmail.setEmailType(EmailType.NOTIFICATION);
            
            // Send email synchronously for testing
            EmailResponseDto response = emailService.sendEmailSync(testEmail);
            
            if (response.getStatus() == EmailStatus.SENT) {
                return ResponseEntity.ok(new ResponseWrapper<>(
                    true,
                    HttpStatus.OK.value(),
                    "Test email sent successfully",
                    "Email sent to: " + email
                ));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseWrapper<>(
                    false,
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Failed to send test email: " + response.getErrorMessage(),
                    null
                ));
            }
            
        } catch (Exception e) {
            log.error("Error testing email functionality", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseWrapper<>(
                false,
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Error testing email: " + e.getMessage(),
                null
            ));
        }
    }

    /**
     * Simple test endpoint to send a plain text email.
     * This helps diagnose if the issue is with HTML emails or SMTP delivery.
     */
    @PostMapping("/admin-users/test-simple-email")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseWrapper<String>> testSimpleEmail(
            @RequestParam String email) {
        
        log.info("Testing simple email functionality for: {}", email);
        
        try {
            // Create a simple plain text email
            EmailRequestDto testEmail = new EmailRequestDto();
            testEmail.setTo(email);
            testEmail.setSubject("Simple Test Email - Taarifu Engine");
            testEmail.setBody("This is a simple test email to verify SMTP delivery. If you receive this, the email system is working correctly.");
            testEmail.setHtml(false); // Plain text email
            testEmail.setEmailType(EmailType.NOTIFICATION);
            
            // Send email synchronously for testing
            EmailResponseDto response = emailService.sendEmailSync(testEmail);
            
            if (response.getStatus() == EmailStatus.SENT) {
                return ResponseEntity.ok(new ResponseWrapper<>(
                    true,
                    HttpStatus.OK.value(),
                    "Simple test email sent successfully",
                    "Plain text email sent to: " + email
                ));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseWrapper<>(
                    false,
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Failed to send simple test email: " + response.getErrorMessage(),
                    null
                ));
            }
            
        } catch (Exception e) {
            log.error("Error testing simple email functionality", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseWrapper<>(
                false,
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Error testing simple email: " + e.getMessage(),
                null
            ));
        }
    }
}
