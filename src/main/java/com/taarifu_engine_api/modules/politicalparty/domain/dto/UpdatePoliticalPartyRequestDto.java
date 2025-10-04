package com.taarifu_engine_api.modules.politicalparty.domain.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

/**
 * Request DTO for updating an existing Political Party
 */
@Data
public class UpdatePoliticalPartyRequestDto {

    @Size(min = 2, max = 200, message = "Political party name must be between 2 and 200 characters")
    private String name;

    @Size(min = 2, max = 20, message = "Abbreviation must be between 2 and 20 characters")
    private String abbreviation;

    @Size(max = 2000, message = "Description cannot exceed 2000 characters")
    private String description;

    private LocalDate foundingDate;

    @Size(max = 200, message = "Founding location cannot exceed 200 characters")
    private String foundingLocation;

    @Size(max = 500, message = "Ideology cannot exceed 500 characters")
    private String ideology;

    @Size(max = 100, message = "Colors cannot exceed 100 characters")
    private String colors;

    @Size(max = 200, message = "Symbol cannot exceed 200 characters")
    private String symbol;

    @Size(max = 300, message = "Motto cannot exceed 300 characters")
    private String motto;

    @Size(max = 500, message = "Website URL cannot exceed 500 characters")
    private String websiteUrl;

    @Size(max = 100, message = "Email cannot exceed 100 characters")
    private String email;

    @Size(max = 20, message = "Phone cannot exceed 20 characters")
    private String phone;

    @Size(max = 500, message = "Headquarters address cannot exceed 500 characters")
    private String headquartersAddress;

    private Boolean isRegistered;

    private Boolean isActive;

    @Size(max = 50, message = "Registration number cannot exceed 50 characters")
    private String registrationNumber;

    private LocalDate registrationDate;

    private Long memberCount;
}
