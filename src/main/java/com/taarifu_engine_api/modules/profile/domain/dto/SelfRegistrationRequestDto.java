package com.taarifu_engine_api.modules.profile.domain.dto;

import com.taarifu_engine_api.modules.profile.domain.enums.IdType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Request DTO for self-registration (public registration)
 */
@Data
public class SelfRegistrationRequestDto {

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 255, message = "Name must be between 2 and 255 characters")
    private String name;

    @Size(max = 255, message = "Display name cannot exceed 255 characters")
    private String displayName;

    @Email(message = "Email must be valid")
    @NotBlank(message = "Email is required")
    @Size(max = 255, message = "Email cannot exceed 255 characters")
    private String email;

    @Size(max = 20, message = "Phone number cannot exceed 20 characters")
    private String phoneNumber;

    private LocalDateTime dateOfBirth;

    @Size(max = 20, message = "Gender cannot exceed 20 characters")
    private String gender;

    @NotNull(message = "ID type is required")
    private IdType idType;
    
    @NotBlank(message = "ID number is required")
    @Size(max = 50, message = "ID number cannot exceed 50 characters")
    private String idNumber;

    @Size(max = 500, message = "Address cannot exceed 500 characters")
    private String address;

    @Size(max = 1000, message = "Bio cannot exceed 1000 characters")
    private String bio;
}
