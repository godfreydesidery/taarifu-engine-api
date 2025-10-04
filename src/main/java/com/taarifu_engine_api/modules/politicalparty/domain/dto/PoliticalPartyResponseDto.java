package com.taarifu_engine_api.modules.politicalparty.domain.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Response DTO for Political Party entity
 */
@Data
public class PoliticalPartyResponseDto {

    private Long id;
    private String uid;
    private String code;
    private String name;
    private String abbreviation;
    private String description;
    private LocalDate foundingDate;
    private String foundingLocation;
    private String ideology;
    private String colors;
    private String symbol;
    private String motto;
    private String websiteUrl;
    private String email;
    private String phone;
    private String headquartersAddress;
    private Boolean isRegistered;
    private Boolean isActive;
    private String registrationNumber;
    private LocalDate registrationDate;
    private Long memberCount;
    private Integer ageInYears;
    private Boolean operational;
    private String displayName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
