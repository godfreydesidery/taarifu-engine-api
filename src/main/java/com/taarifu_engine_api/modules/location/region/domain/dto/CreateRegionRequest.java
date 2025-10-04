package com.taarifu_engine_api.modules.location.region.domain.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * DTO for creating a new region
 */
@Data
public class CreateRegionRequest {

    @NotBlank(message = "Region name is required")
    @Size(max = 100, message = "Region name must not exceed 100 characters")
    private String name;

    @NotBlank(message = "Regional capital is required")
    @Size(max = 100, message = "Regional capital must not exceed 100 characters")
    private String capital;

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

    @Size(max = 150, message = "Commissioner name must not exceed 150 characters")
    private String commissioner;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    private Boolean isActive = true;
}
