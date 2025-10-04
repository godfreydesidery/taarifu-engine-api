package com.taarifu_engine_api.modules.parliament.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

/**
 * Request DTO for creating a new Parliament
 */
@Data
public class CreateParliamentRequest {

    @NotBlank(message = "Parliament name is required")
    @Size(min = 2, max = 100, message = "Parliament name must be between 2 and 100 characters")
    private String name;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    private Boolean isActive = true;

    private Boolean isCurrent = false;
}
