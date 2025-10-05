package com.taarifu_engine_api.modules.profile.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response DTO for self-registration containing login credentials
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SelfRegistrationResponseDto {
    
    private String message;
    
    private String profileUid;
    
    private String username;
    
    private String email;
    
    private LocalDateTime registrationDate;
    
    private boolean requirePasswordChange;
    
    private String loginInstructions;
}
