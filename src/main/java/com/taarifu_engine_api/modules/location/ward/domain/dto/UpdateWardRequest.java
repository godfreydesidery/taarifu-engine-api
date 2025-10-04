package com.taarifu_engine_api.modules.location.ward.domain.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * DTO for updating an existing ward
 */
@Data
public class UpdateWardRequest {

    @Size(max = 100, message = "Ward name must not exceed 100 characters")
    private String name;

    @Size(max = 100, message = "Ward headquarters must not exceed 100 characters")
    private String headquarters;

    @Min(value = 0, message = "Population must be a positive number")
    private Long population;

    @DecimalMin(value = "0.0", message = "Area must be a positive number")
    private Double areaSqKm;

    @DecimalMin(value = "-90.0", message = "Latitude must be between -90 and 90")
    @DecimalMax(value = "90.0", message = "Latitude must be between -90 and 90")
    private Double latitude;

    @DecimalMin(value = "-180.0", message = "Longitude must be between -180 and 180")
    @DecimalMax(value = "180.0", message = "Longitude must be between -180 and 180")
    private Double longitude;

    @Size(max = 150, message = "Executive officer name must not exceed 150 characters")
    private String executiveOfficer;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    private Boolean isActive;
}
