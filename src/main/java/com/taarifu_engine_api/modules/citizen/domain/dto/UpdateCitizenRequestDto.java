package com.taarifu_engine_api.modules.citizen.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for updating citizen information
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCitizenRequestDto {
    
    private String citizenshipType;
    
    private String citizenshipStatus;
    
    private LocalDateTime dateOfCitizenship;
}
