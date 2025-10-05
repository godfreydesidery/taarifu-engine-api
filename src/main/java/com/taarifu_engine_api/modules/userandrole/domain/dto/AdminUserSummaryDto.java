package com.taarifu_engine_api.modules.userandrole.domain.dto;

import com.taarifu_engine_api.modules.userandrole.domain.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Simplified Data Transfer Object for admin user summary information.
 * Used for frontend lists, dropdowns, and quick views where full details are not needed.
 * 
 * Frontend Usage: Use this for admin user lists, selection components, and summary views.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserSummaryDto {

    /**
     * Unique ULID for the admin user.
     * Frontend Note: Use this for API calls instead of the numeric ID.
     */
    private String uid;

    /**
     * Username of the admin user.
     * Frontend Note: Display this as the primary identifier.
     */
    private String username;

    /**
     * Email address of the admin user.
     * Frontend Note: Display this as secondary information.
     */
    private String email;

    /**
     * Current status of the admin user account.
     * Frontend Note: Use this to show status badges (Active, Inactive, Suspended).
     * Values: ACTIVE, INACTIVE, SUSPENDED
     */
    private UserStatus status;

    /**
     * Whether the admin user is required to change password on next login.
     * Frontend Note: Show a warning indicator if true.
     */
    private Boolean requirePasswordChange;

    /**
     * Timestamp when the admin user account was created.
     * Frontend Note: Format as relative time (e.g., "2 days ago") or full date.
     */
    private LocalDateTime createdAt;
}
