package com.taarifu_engine_api.modules.citizen.domain.dto;

import com.taarifu_engine_api.modules.profile.domain.dto.ProfileResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for Citizen response data
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CitizenResponseDto {
    
    private Long id;
    
    private String profileUid;
    
    private ProfileResponseDto profile;
    
    private LocalDateTime dateOfCitizenship;
    
    private String citizenshipType;
    
    private String citizenshipStatus;
}
