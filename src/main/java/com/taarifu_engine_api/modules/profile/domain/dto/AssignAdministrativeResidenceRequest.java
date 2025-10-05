package com.taarifu_engine_api.modules.profile.domain.dto;

import com.taarifu_engine_api.modules.profile.domain.enums.ResidenceType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Request DTO for assigning administrative area residence to profile
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignAdministrativeResidenceRequest {

    @NotNull(message = "Administrative area UID is required")
    private String administrativeAreaUid;

    @NotNull(message = "Residence type is required")
    private ResidenceType residenceType;

    @Builder.Default
    private Boolean isPrimary = false;

    private String detailedAddress;

    private LocalDateTime validFrom;

    private LocalDateTime validTo;

    private String notes;
}
