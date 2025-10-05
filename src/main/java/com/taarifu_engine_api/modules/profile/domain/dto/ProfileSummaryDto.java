package com.taarifu_engine_api.modules.profile.domain.dto;

import com.taarifu_engine_api.modules.profile.domain.enums.ProfileType;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Summary DTO for Profile entity - used in lists and brief displays
 */
@Data
public class ProfileSummaryDto {

    private Long id;
    private String uid;
    private ProfileType profileType;
    private String name;
    private String displayName;
    private String email;
    private String phoneNumber;
    private String profilePictureUrl;
    private Boolean isActive;
    private LocalDateTime createdAt;
    
    // User information (minimal)
    private String username;
}
