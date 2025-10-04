package com.taarifu_engine_api.modules.location.district.domain.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO for district responses
 */
@Data
public class DistrictResponse {

    private Long id;
    private String uid;
    private String code;
    private String name;
    private String headquarters;
    private Long population;
    private Double areaSqKm;
    private Double latitude;
    private Double longitude;
    private String commissioner;
    private String description;
    private Boolean isActive;
    
    // Region information
    private Long regionId;
    private String regionName;
    private String regionCode;
    
    // Audit fields
    private Long createdById;
    private String createdByUsername;
    private Long updatedById;
    private String updatedByUsername;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
