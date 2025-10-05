package com.taarifu_engine_api.modules.citizen.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for creating a new citizen
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCitizenRequestDto {
    
    private Long profileId;
    
    private String citizenshipType;
    
    private String citizenshipStatus;
    
    private LocalDateTime dateOfCitizenship;
}
