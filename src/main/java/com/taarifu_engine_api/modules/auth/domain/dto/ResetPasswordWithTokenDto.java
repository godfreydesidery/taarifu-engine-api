package com.taarifu_engine_api.modules.auth.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object for resetting password with a token.
 * Used when a user resets their password using a token received via email.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordWithTokenDto {

    /**
     * The password reset token received via email.
     * This token is used to verify the user's identity.
     */
    @NotBlank(message = "Reset token is required")
    private String token;

    /**
     * The new password to set.
     * Must meet the system's password strength requirements.
     */
    @NotBlank(message = "New password is required")
    @Size(min = 8, max = 128, message = "Password must be between 8 and 128 characters")
    private String newPassword;

    /**
     * Confirmation of the new password.
     * Must match the newPassword field.
     */
    @NotBlank(message = "Password confirmation is required")
    @Size(min = 8, max = 128, message = "Password confirmation must be between 8 and 128 characters")
    private String confirmPassword;
}
