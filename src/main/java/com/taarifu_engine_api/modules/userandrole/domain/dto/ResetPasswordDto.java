package com.taarifu_engine_api.modules.userandrole.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object for password reset operations.
 * Used by administrators to reset user passwords.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordDto {

    /**
     * New password for the admin user.
     * Must meet strong password requirements for admin accounts.
     */
    @NotBlank(message = "New password is required")
    @Size(min = 12, max = 128, message = "Admin password must be between 12 and 128 characters")
    private String newPassword;

    /**
     * Whether the admin user should be required to change password on next login.
     * Defaults to true for security.
     */
    private Boolean requirePasswordChange = true;

    /**
     * Whether to send the new password via email to the user.
     * Defaults to true for security.
     */
    private Boolean sendEmailNotification = true;
}
