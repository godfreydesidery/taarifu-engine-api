package com.taarifu_engine_api.modules.location.hamlet.domain.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO for hamlet responses
 */
@Data
public class HamletResponse {

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
    
    // Village information
    private Long villageId;
    private String villageName;
    private String villageCode;
    
    // Ward information (through village)
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
