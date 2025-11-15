package com.taarifu_engine_api.modules.userandrole.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object for updating admin users.
 * Used specifically for admin user update operations.
 * All fields are optional for partial updates.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAdminUserDto {

    /**
     * New username for the admin user.
     * Must be unique across the system if provided.
     */
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "Username can only contain letters, numbers, underscores, and hyphens")
    private String username;

    /**
     * New email address for the admin user.
     * Must be unique across the system and valid email format if provided.
     */
    @Email(message = "Email must be a valid email address")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    private String email;

    /**
     * New phone number for the admin user (optional).
     * Must be a valid Tanzanian phone number format if provided.
     */
    @Pattern(regexp = "^(?:\\+255|0)?(6[1-24-9]\\d{7}|7[1-9]\\d{7})$", 
             message = "Phone number must be a valid Tanzanian phone number")
    @Size(max = 20, message = "Phone number must not exceed 20 characters")
    private String phoneNumber;

    /**
     * Whether the admin user should be required to change password on next login.
     */
    private Boolean requirePasswordChange;
}
