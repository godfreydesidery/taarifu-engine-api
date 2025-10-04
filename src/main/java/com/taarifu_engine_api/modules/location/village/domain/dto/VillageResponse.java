package com.taarifu_engine_api.modules.location.village.domain.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO for village responses
 */
@Data
public class VillageResponse {

    private Long id;
    private String uid;
    private String code;
    private String name;
    private String headquarters;
    private Long population;
    private Double areaSqKm;
    private Double latitude;
    private Double longitude;
    private String executiveOfficer;
    private String description;
    private Boolean isActive;
    
    // Ward information
    private Long wardId;
    private String wardName;
    private String wardCode;
    
    // District information (through ward)
    private Long districtId;
    private String districtName;
    private String districtCode;
    
    // Region information (through district)
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
