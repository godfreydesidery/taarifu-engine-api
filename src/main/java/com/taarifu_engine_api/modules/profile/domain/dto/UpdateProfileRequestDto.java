package com.taarifu_engine_api.modules.profile.domain.dto;

import com.taarifu_engine_api.modules.profile.domain.enums.IdType;
import com.taarifu_engine_api.modules.profile.domain.enums.ProfileType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Request DTO for updating an existing Profile
 */
@Data
public class UpdateProfileRequestDto {

    private ProfileType profileType;

    @Size(min = 2, max = 255, message = "Name must be between 2 and 255 characters")
    private String name;

    @Size(max = 255, message = "Display name cannot exceed 255 characters")
    private String displayName;

    @Email(message = "Email must be valid")
    @Size(max = 255, message = "Email cannot exceed 255 characters")
    private String email;

    @Size(max = 20, message = "Phone number cannot exceed 20 characters")
    private String phoneNumber;

    private LocalDateTime dateOfBirth;

    @Size(max = 20, message = "Gender cannot exceed 20 characters")
    private String gender;

    private IdType idType;
    
    @Size(max = 50, message = "ID number cannot exceed 50 characters")
    private String idNumber;

    @Size(max = 50, message = "Registration number cannot exceed 50 characters")
    private String registrationNumber;

    @Size(max = 255, message = "Website cannot exceed 255 characters")
    private String website;

    @Size(max = 255, message = "Contact person cannot exceed 255 characters")
    private String contactPerson;

    @Size(max = 500, message = "Address cannot exceed 500 characters")
    private String address;

    @Size(max = 500, message = "Profile picture URL cannot exceed 500 characters")
    private String profilePictureUrl;

    @Size(max = 1000, message = "Bio cannot exceed 1000 characters")
    private String bio;

    private Boolean isActive;
}
