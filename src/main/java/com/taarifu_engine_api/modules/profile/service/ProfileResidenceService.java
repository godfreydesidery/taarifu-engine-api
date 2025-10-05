package com.taarifu_engine_api.modules.profile.service;

import com.taarifu_engine_api.modules.location.area.domain.entity.Area;
import com.taarifu_engine_api.modules.location.constituency.domain.entity.Constituency;
import com.taarifu_engine_api.modules.profile.domain.dto.AssignAdministrativeResidenceRequest;
import com.taarifu_engine_api.modules.profile.domain.dto.AssignConstituencyResidenceRequest;
import com.taarifu_engine_api.modules.profile.domain.dto.ProfileResidenceResponse;
import com.taarifu_engine_api.modules.profile.domain.dto.UpdateResidenceRequest;
import com.taarifu_engine_api.modules.profile.domain.entity.Profile;
import com.taarifu_engine_api.modules.profile.domain.dto.ProfileResponseDto;
import com.taarifu_engine_api.modules.profile.domain.enums.ResidenceType;

import java.util.List;

/**
 * Service interface for ProfileResidence operations
 */
public interface ProfileResidenceService {

    /**
     * Assign administrative area residence to profile
     */
    ProfileResidenceResponse assignAdministrativeResidence(Profile profile, AssignAdministrativeResidenceRequest request);

    /**
     * Assign administrative area residence to profile (using ProfileResponseDto)
     */
    ProfileResidenceResponse assignAdministrativeResidence(ProfileResponseDto profile, AssignAdministrativeResidenceRequest request);

    /**
     * Assign constituency residence to profile
     */
    ProfileResidenceResponse assignConstituencyResidence(Profile profile, AssignConstituencyResidenceRequest request);

    /**
     * Assign constituency residence to profile (using ProfileResponseDto)
     */
    ProfileResidenceResponse assignConstituencyResidence(ProfileResponseDto profile, AssignConstituencyResidenceRequest request);

    /**
     * Get all residences for a profile
     */
    List<ProfileResidenceResponse> getProfileResidences(Profile profile);

    /**
     * Get all residences for a profile (using ProfileResponseDto)
     */
    List<ProfileResidenceResponse> getProfileResidences(ProfileResponseDto profile);

    /**
     * Get residences by type for a profile
     */
    List<ProfileResidenceResponse> getProfileResidencesByType(Profile profile, ResidenceType residenceType);

    /**
     * Get residences by type for a profile (using ProfileResponseDto)
     */
    List<ProfileResidenceResponse> getProfileResidencesByType(ProfileResponseDto profile, ResidenceType residenceType);

    /**
     * Get primary administrative area residence for a profile
     */
    ProfileResidenceResponse getPrimaryAdministrativeResidence(Profile profile);

    /**
     * Get primary administrative area residence for a profile (using ProfileResponseDto)
     */
    ProfileResidenceResponse getPrimaryAdministrativeResidence(ProfileResponseDto profile);

    /**
     * Get primary constituency residence for a profile
     */
    ProfileResidenceResponse getPrimaryConstituencyResidence(Profile profile);

    /**
     * Get primary constituency residence for a profile (using ProfileResponseDto)
     */
    ProfileResidenceResponse getPrimaryConstituencyResidence(ProfileResponseDto profile);

    /**
     * Get primary residence of specific type for a profile
     */
    ProfileResidenceResponse getPrimaryResidenceByType(Profile profile, ResidenceType residenceType);

    /**
     * Get primary residence of specific type for a profile (using ProfileResponseDto)
     */
    ProfileResidenceResponse getPrimaryResidenceByType(ProfileResponseDto profile, ResidenceType residenceType);

    /**
     * Update residence
     */
    ProfileResidenceResponse updateResidence(String residenceUid, UpdateResidenceRequest request);

    /**
     * Set residence as primary
     */
    ProfileResidenceResponse setPrimaryResidence(String residenceUid);

    /**
     * Remove residence (soft delete)
     */
    void removeResidence(String residenceUid);

    /**
     * Get residence by UID
     */
    ProfileResidenceResponse getResidenceByUid(String residenceUid);

    /**
     * Validate residence compatibility between administrative area and constituency
     */
    boolean validateResidenceCompatibility(Area area, Constituency constituency);
}
