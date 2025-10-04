package com.taarifu_engine_api.modules.auth.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object for authentication requests.
 * Used for login operations across different user types (admin, citizen, organization).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthRequestDto {

    /**
     * Username or email address for authentication.
     * Can be either username or email depending on user preference.
     */
    @NotBlank(message = "Username or email is required")
    @Size(min = 3, max = 255, message = "Username or email must be between 3 and 255 characters")
    private String usernameOrEmail;

    /**
     * User's password for authentication.
     * Should be hashed before storage but sent as plain text in requests.
     */
    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 128, message = "Password must be between 8 and 128 characters")
    private String password;

    /**
     * Optional field to remember the user's session.
     * If true, the session should be extended or made persistent.
     */
    private boolean rememberMe = false;
}
