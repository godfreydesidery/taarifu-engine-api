package com.taarifu_engine_api.modules.profile.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Request DTO for updating profile residence
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateResidenceRequest {

    private Boolean isPrimary;

    private String detailedAddress;

    private LocalDateTime validFrom;

    private LocalDateTime validTo;

    private Boolean isActive;

    private String notes;
}
