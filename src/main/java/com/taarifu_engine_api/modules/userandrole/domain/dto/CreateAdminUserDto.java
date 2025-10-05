package com.taarifu_engine_api.modules.userandrole.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object for creating admin users.
 * Used specifically for admin user creation operations.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateAdminUserDto {

    /**
     * Username for the admin user.
     * Must be unique across the system.
     */
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "Username can only contain letters, numbers, underscores, and hyphens")
    private String username;

    /**
     * Email address for the admin user.
     * Must be unique across the system and valid email format.
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid email address")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    private String email;


    /**
     * Whether the admin user should be required to change password on first login.
     * Defaults to true for security.
     * 
     * Frontend Note: This field is optional. If not provided, defaults to true.
     * Set to false only if you want the user to keep the generated password.
     */
    private Boolean requirePasswordChange = true;
}
