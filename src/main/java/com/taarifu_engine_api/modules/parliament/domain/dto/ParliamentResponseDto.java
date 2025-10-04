package com.taarifu_engine_api.modules.parliament.domain.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Response DTO for Parliament entity
 */
@Data
public class ParliamentResponseDto {

    private Long id;
    private String uid;
    private String code;
    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isActive;
    private Boolean isCurrent;
    private Integer durationInYears;
    private Boolean inSession;
    private Boolean hasEnded;
    private Boolean hasStarted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
