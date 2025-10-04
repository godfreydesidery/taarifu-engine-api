package com.taarifu_engine_api.modules.parliament.domain.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

/**
 * Request DTO for updating an existing Parliament
 */
@Data
public class UpdateParliamentRequestDto {

    @Size(min = 2, max = 100, message = "Parliament name must be between 2 and 100 characters")
    private String name;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    private LocalDate startDate;

    private LocalDate endDate;

    private Boolean isActive;

    private Boolean isCurrent;
}
