package com.taarifu_engine_api.modules.userandrole.domain.dto;

import com.taarifu_engine_api.modules.userandrole.domain.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for admin user responses.
 * Used to return admin user information without sensitive data like password hashes.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserResponseDto {

    /**
     * Unique identifier for the admin user.
     */
    private Long id;

    /**
     * Unique ULID for the admin user.
     */
    private String uid;

    /**
     * Username of the admin user.
     */
    private String username;

    /**
     * Email address of the admin user.
     */
    private String email;


    /**
     * Current status of the admin user account.
     */
    private UserStatus status;

    /**
     * Whether the admin user is required to change password on next login.
     */
    private Boolean requirePasswordChange;

    /**
     * Timestamp when the admin user account was created.
     */
    private LocalDateTime createdAt;

    /**
     * Timestamp when the admin user account was last updated.
     */
    private LocalDateTime updatedAt;

    /**
     * Timestamp of the admin user's last login.
     */
    private LocalDateTime lastLoginAt;

    /**
     * Whether the admin user account is currently active.
     */
    private Boolean isActive;

}
