package com.taarifu_engine_api.modules.location.region.domain.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO for region responses
 */
@Data
public class RegionResponse {

    private Long id;
    private String uid;
    private String code;
    private String name;
    private String capital;
    private Long population;
    private Double areaSqKm;
    private Double latitude;
    private Double longitude;
    private String commissioner;
    private String description;
    private Boolean isActive;
    private Long createdById;
    private String createdByUsername;
    private Long updatedById;
    private String updatedByUsername;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
