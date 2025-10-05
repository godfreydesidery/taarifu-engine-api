package com.taarifu_engine_api.modules.userandrole.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object for password change operations.
 * Used for secure password management operations.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordDto {

    /**
     * Current password for verification.
     * Required for password change operations.
     */
    @NotBlank(message = "Current password is required")
    private String currentPassword;

    /**
     * New password for the admin user.
     * Must meet strong password requirements for admin accounts.
     */
    @NotBlank(message = "New password is required")
    @Size(min = 12, max = 128, message = "Admin password must be between 12 and 128 characters")
    private String newPassword;

    /**
     * Confirmation of the new password.
     * Must match the new password.
     */
    @NotBlank(message = "Password confirmation is required")
    private String confirmPassword;
}
