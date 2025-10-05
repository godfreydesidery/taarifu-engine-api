package com.taarifu_engine_api.modules.auth.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for forgot password responses.
 * Used to provide feedback to the user about their password reset request.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ForgotPasswordResponseDto {

    /**
     * Indicates whether the password reset request was processed successfully.
     * Always true for security reasons (prevents email enumeration).
     */
    private boolean success;

    /**
     * Message to display to the user.
     * Always the same message regardless of whether the email exists.
     */
    private String message;

    /**
     * The email address that the reset instructions were sent to.
     * Only included if the request was successful.
     */
    private String email;

    /**
     * Indicates whether the user should check their email for reset instructions.
     */
    private boolean checkEmail;

    /**
     * Time in minutes after which the reset token will expire.
     */
    private int tokenExpirationMinutes;
}
