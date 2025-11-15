package com.taarifu_engine_api.modules.userandrole.service;

import com.taarifu_engine_api.modules.userandrole.domain.dto.AdminUserResponseDto;
import com.taarifu_engine_api.modules.userandrole.domain.dto.AdminUserSummaryDto;
import com.taarifu_engine_api.modules.userandrole.domain.dto.ChangePasswordDto;
import com.taarifu_engine_api.modules.userandrole.domain.dto.CreateAdminUserDto;
import com.taarifu_engine_api.modules.userandrole.domain.dto.PasswordValidationDto;
import com.taarifu_engine_api.modules.userandrole.domain.dto.ResetPasswordDto;
import com.taarifu_engine_api.modules.userandrole.domain.dto.UpdateAdminUserDto;
import com.taarifu_engine_api.modules.userandrole.domain.enums.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service interface for admin user management operations.
 * Provides methods specifically for managing admin users (UserType.ADMIN).
 */
public interface AdminUserService {

    /**
     * Creates a new admin user.
     *
     * @param createAdminUserDto the admin user creation data
     * @return the created admin user response
     * @throws com.taarifu_engine_api.modules.common.exception.ApiException if validation fails or user already exists
     */
    AdminUserResponseDto createAdminUser(CreateAdminUserDto createAdminUserDto);

    /**
     * Updates an existing admin user.
     *
     * @param userId the ID of the admin user to update
     * @param updateAdminUserDto the admin user update data
     * @return the updated admin user response
     * @throws com.taarifu_engine_api.modules.common.exception.ApiException if user not found or validation fails
     */
    AdminUserResponseDto updateAdminUser(Long userId, UpdateAdminUserDto updateAdminUserDto);

    /**
     * Updates an existing admin user by UID.
     *
     * @param uid the UID of the admin user to update
     * @param updateAdminUserDto the admin user update data
     * @return the updated admin user response
     * @throws com.taarifu_engine_api.modules.common.exception.ApiException if user not found or validation fails
     */
    AdminUserResponseDto updateAdminUserByUid(String uid, UpdateAdminUserDto updateAdminUserDto);

    /**
     * Retrieves an admin user by ID.
     *
     * @param userId the ID of the admin user
     * @return the admin user response
     * @throws com.taarifu_engine_api.modules.common.exception.ApiException if user not found or not an admin
     */
    AdminUserResponseDto getAdminUserById(Long userId);

    /**
     * Retrieves an admin user by UID.
     *
     * @param uid the UID of the admin user
     * @return the admin user response
     * @throws com.taarifu_engine_api.modules.common.exception.ApiException if user not found or not an admin
     */
    AdminUserResponseDto getAdminUserByUid(String uid);

    /**
     * Retrieves an admin user by username.
     *
     * @param username the username of the admin user
     * @return the admin user response
     * @throws com.taarifu_engine_api.modules.common.exception.ApiException if user not found or not an admin
     */
    AdminUserResponseDto getAdminUserByUsername(String username);

    /**
     * Retrieves an admin user by email.
     *
     * @param email the email of the admin user
     * @return the admin user response
     * @throws com.taarifu_engine_api.modules.common.exception.ApiException if user not found or not an admin
     */
    AdminUserResponseDto getAdminUserByEmail(String email);

    /**
     * Retrieves all admin users with pagination.
     *
     * @param pageable pagination parameters
     * @return a page of admin user responses
     */
    Page<AdminUserResponseDto> getAllAdminUsers(Pageable pageable);

    /**
     * Retrieves admin users by status with pagination.
     *
     * @param status the status to filter by (ACTIVE, INACTIVE, SUSPENDED, PENDING_VERIFICATION)
     * @param pageable pagination parameters
     * @return a page of admin user responses
     */
    Page<AdminUserResponseDto> getAdminUsersByStatus(UserStatus status, Pageable pageable);

    /**
     * Searches admin users by query string (username, email, or phone number).
     * Case-insensitive partial matching with OR logic.
     *
     * @param query the search query string
     * @param pageable pagination parameters
     * @return a page of admin user responses matching the search criteria
     * @throws com.taarifu_engine_api.modules.common.exception.ApiException if query is empty or null
     */
    Page<AdminUserResponseDto> searchAdminUsers(String query, Pageable pageable);

    /**
     * Deactivates an admin user (soft delete).
     *
     * @param userId the ID of the admin user to deactivate
     * @return the deactivated admin user response
     * @throws com.taarifu_engine_api.modules.common.exception.ApiException if user not found or not an admin
     */
    AdminUserResponseDto deactivateAdminUser(Long userId);

    /**
     * Deactivates an admin user by UID (soft delete).
     *
     * @param uid the UID of the admin user to deactivate
     * @return the deactivated admin user response
     * @throws com.taarifu_engine_api.modules.common.exception.ApiException if user not found or not an admin
     */
    AdminUserResponseDto deactivateAdminUserByUid(String uid);

    /**
     * Activates an admin user.
     *
     * @param userId the ID of the admin user to activate
     * @return the activated admin user response
     * @throws com.taarifu_engine_api.modules.common.exception.ApiException if user not found or not an admin
     */
    AdminUserResponseDto activateAdminUser(Long userId);

    /**
     * Activates an admin user by UID.
     *
     * @param uid the UID of the admin user to activate
     * @return the activated admin user response
     * @throws com.taarifu_engine_api.modules.common.exception.ApiException if user not found or not an admin
     */
    AdminUserResponseDto activateAdminUserByUid(String uid);

    /**
     * Suspends an admin user.
     *
     * @param userId the ID of the admin user to suspend
     * @return the suspended admin user response
     * @throws com.taarifu_engine_api.modules.common.exception.ApiException if user not found or not an admin
     */
    AdminUserResponseDto suspendAdminUser(Long userId);

    /**
     * Suspends an admin user by UID.
     *
     * @param uid the UID of the admin user to suspend
     * @return the suspended admin user response
     * @throws com.taarifu_engine_api.modules.common.exception.ApiException if user not found or not an admin
     */
    AdminUserResponseDto suspendAdminUserByUid(String uid);

    /**
     * Gets the count of admin users.
     *
     * @return the total number of admin users
     */
    long getAdminUserCount();

    /**
     * Checks if a username is available for admin users.
     *
     * @param username the username to check
     * @return true if available, false otherwise
     */
    boolean isUsernameAvailable(String username);

    /**
     * Checks if an email is available for admin users.
     *
     * @param email the email to check
     * @return true if available, false otherwise
     */
    boolean isEmailAvailable(String email);

    // Password Management Methods

    /**
     * Changes the password for an admin user by UID.
     * Requires current password verification.
     *
     * @param uid the UID of the admin user
     * @param changePasswordDto the password change data
     * @return the updated admin user response
     * @throws com.taarifu_engine_api.modules.common.exception.ApiException if user not found, current password invalid, or validation fails
     */
    AdminUserResponseDto changePasswordByUid(String uid, ChangePasswordDto changePasswordDto);

    /**
     * Resets the password for an admin user by UID.
     * Admin-only operation that bypasses current password verification.
     *
     * @param uid the UID of the admin user
     * @param resetPasswordDto the password reset data
     * @return the updated admin user response
     * @throws com.taarifu_engine_api.modules.common.exception.ApiException if user not found or validation fails
     */
    AdminUserResponseDto resetPasswordByUid(String uid, ResetPasswordDto resetPasswordDto);

    /**
     * Generates a new temporary password for an admin user by UID.
     * Sends the new password via email.
     *
     * @param uid the UID of the admin user
     * @return the updated admin user response
     * @throws com.taarifu_engine_api.modules.common.exception.ApiException if user not found
     */
    AdminUserResponseDto generateNewPasswordByUid(String uid);

    // Frontend-Friendly Methods

    /**
     * Gets a paginated list of admin users in summary format.
     * Optimized for frontend lists and tables.
     *
     * @param pageable pagination parameters
     * @return paginated list of admin user summaries
     */
    Page<AdminUserSummaryDto> getAdminUserSummaries(Pageable pageable);

    /**
     * Retrieves admin user summaries by status with pagination.
     * Optimized for frontend lists and tables.
     *
     * @param status the status to filter by (ACTIVE, INACTIVE, SUSPENDED, PENDING_VERIFICATION)
     * @param pageable pagination parameters
     * @return paginated list of admin user summaries
     */
    Page<AdminUserSummaryDto> getAdminUserSummariesByStatus(UserStatus status, Pageable pageable);

    /**
     * Gets a single admin user summary by UID.
     * Optimized for frontend quick views and dropdowns.
     *
     * @param uid the UID of the admin user
     * @return admin user summary
     * @throws com.taarifu_engine_api.modules.common.exception.ApiException if user not found
     */
    AdminUserSummaryDto getAdminUserSummaryByUid(String uid);

    /**
     * Validates a password and returns detailed feedback.
     * Used for real-time password strength validation in frontend.
     *
     * @param password the password to validate
     * @param isAdminPassword whether this is for an admin account (stricter requirements)
     * @return password validation feedback
     */
    PasswordValidationDto validatePassword(String password, boolean isAdminPassword);

    /**
     * Soft deletes an admin user by UID.
     *
     * @param uid the UID of the admin user to delete
     * @param deletedByUid the UID of the user performing the deletion
     * @return the deleted admin user response
     * @throws com.taarifu_engine_api.modules.common.exception.ApiException if user not found
     */
    AdminUserResponseDto softDeleteUser(String uid, String deletedByUid);

    /**
     * Restores a soft-deleted admin user by UID.
     *
     * @param uid the UID of the admin user to restore
     * @return the restored admin user response
     * @throws com.taarifu_engine_api.modules.common.exception.ApiException if user not found
     */
    AdminUserResponseDto restoreUser(String uid);

    /**
     * Locks an admin user account by UID.
     *
     * @param uid the UID of the admin user
     * @param lockoutMinutes the number of minutes to lock the account (default 30)
     * @return the updated admin user response
     * @throws com.taarifu_engine_api.modules.common.exception.ApiException if user not found
     */
    AdminUserResponseDto lockUserAccount(String uid, Integer lockoutMinutes);

    /**
     * Unlocks an admin user account by UID.
     *
     * @param uid the UID of the admin user
     * @return the updated admin user response
     * @throws com.taarifu_engine_api.modules.common.exception.ApiException if user not found
     */
    AdminUserResponseDto unlockUserAccount(String uid);

    /**
     * Resends email verification email to an admin user by UID.
     *
     * @param uid the UID of the admin user
     * @return response indicating success
     * @throws com.taarifu_engine_api.modules.common.exception.ApiException if user not found
     */
    com.taarifu_engine_api.modules.auth.domain.dto.ForgotPasswordResponseDto resendVerificationEmail(String uid);
}
