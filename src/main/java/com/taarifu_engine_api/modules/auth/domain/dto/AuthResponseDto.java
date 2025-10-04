package com.taarifu_engine_api.modules.auth.domain.dto;

import com.taarifu_engine_api.modules.common.domain.enums.PasswordStrength;
import com.taarifu_engine_api.modules.userandrole.domain.enums.UserStatus;
import com.taarifu_engine_api.modules.userandrole.domain.enums.UserType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for authentication responses.
 * Contains user information and authentication details after successful login.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDto {

    /**
     * User's unique identifier (ULID).
     */
    private String uid;

    /**
     * User's username.
     */
    private String username;

    /**
     * User's email address.
     */
    private String email;

    /**
     * User's type (ADMIN, CITIZEN, ORGANIZATION).
     */
    private UserType userType;

    /**
     * User's account status.
     */
    private UserStatus status;

    /**
     * User's password strength level.
     */
    private PasswordStrength passwordStrength;

    /**
     * Whether the user is required to change their password.
     */
    private boolean requirePasswordChange;

    /**
     * Timestamp of the last login.
     */
    private LocalDateTime lastLoginAt;

    /**
     * Timestamp when the user account was created.
     */
    private LocalDateTime createdAt;

    /**
     * JWT access token for API authentication.
     * This will be added when JWT implementation is complete.
     */
    private String accessToken;

    /**
     * JWT refresh token for token renewal.
     * This will be added when JWT implementation is complete.
     */
    private String refreshToken;

    /**
     * Token expiration time in seconds.
     * This will be added when JWT implementation is complete.
     */
    private Long expiresIn;

    /**
     * Token type (typically "Bearer").
     * This will be added when JWT implementation is complete.
     */
    private String tokenType = "Bearer";
}
