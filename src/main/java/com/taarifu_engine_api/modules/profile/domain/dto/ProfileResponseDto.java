package com.taarifu_engine_api.modules.profile.domain.dto;

import com.taarifu_engine_api.modules.profile.domain.enums.IdType;
import com.taarifu_engine_api.modules.profile.domain.enums.ProfileType;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Response DTO for Profile entity
 */
@Data
public class ProfileResponseDto {

    private Long id;
    private String uid;
    private ProfileType profileType;
    private String name;
    private String displayName;
    private String email;
    private String phoneNumber;
    private LocalDateTime dateOfBirth;
    private String gender;
    private IdType idType;
    private String idNumber;
    private String registrationNumber;
    private String website;
    private String contactPerson;
    private String address;
    private String profilePictureUrl;
    private String bio;
    private Boolean isActive;
    private String whatsappNumber;
    private String socialMediaLinks;
    private String civicInterests;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // User information (without sensitive data)
    private Long userId;
    private String username;
    private String userEmail;
}
