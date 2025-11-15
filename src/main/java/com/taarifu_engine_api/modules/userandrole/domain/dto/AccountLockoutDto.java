package com.taarifu_engine_api.modules.userandrole.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for account lockout requests.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountLockoutDto {

    /**
     * User UID.
     */
    private String uid;

    /**
     * Number of minutes to lock the account (default 30).
     */
    private Integer lockoutMinutes;

    /**
     * Reason for lockout (optional).
     */
    private String reason;
}

