package com.taarifu_engine_api.modules.auth.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for email verification requests.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailVerificationDto {

    /**
     * Email verification token received via email.
     */
    @NotBlank(message = "Verification token is required")
    private String token;

    /**
     * Email address (optional, can be used for resend verification).
     */
    private String email;
}

