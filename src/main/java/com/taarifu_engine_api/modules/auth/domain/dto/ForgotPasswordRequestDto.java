package com.taarifu_engine_api.modules.auth.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Data Transfer Object for forgot password requests.
 * Used when a user requests a password reset via email.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ForgotPasswordRequestDto {

    /**
     * Email address of the user requesting password reset.
     * Must be a valid email format.
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    private String email;

    /**
     * Optional field to specify the user type for password reset.
     * If not provided, the system will search across all user types.
     */
    private String userType;
}
