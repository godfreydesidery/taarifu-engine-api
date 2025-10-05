package com.taarifu_engine_api.modules.profile.domain.dto;

import com.taarifu_engine_api.modules.location.area.domain.dto.AreaResponse;
import com.taarifu_engine_api.modules.location.constituency.domain.dto.ConstituencyResponse;
import com.taarifu_engine_api.modules.profile.domain.enums.ResidenceType;
import com.taarifu_engine_api.modules.profile.domain.enums.VerificationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response DTO for ProfileResidence operations
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileResidenceResponse {

    private String uid;
    private String profileUid;
    private ResidenceType residenceType;
    private Boolean isPrimary;
    private AreaResponse administrativeArea;
    private ConstituencyResponse constituency;
    private String detailedAddress;
    private LocalDateTime validFrom;
    private LocalDateTime validTo;
    private Boolean isActive;
    private String notes;
    private VerificationStatus verificationStatus;
    private String verificationDocuments;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String displayName;
}
