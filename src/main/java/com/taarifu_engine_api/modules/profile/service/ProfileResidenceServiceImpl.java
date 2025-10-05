package com.taarifu_engine_api.modules.profile.service;

import com.taarifu_engine_api.modules.common.exception.ApiException;
import com.taarifu_engine_api.modules.location.area.domain.entity.Area;
import com.taarifu_engine_api.modules.location.area.repository.AreaRepository;
import com.taarifu_engine_api.modules.location.constituency.domain.entity.Constituency;
import com.taarifu_engine_api.modules.location.constituency.repository.ConstituencyRepository;
import com.taarifu_engine_api.modules.profile.domain.dto.AssignAdministrativeResidenceRequest;
import com.taarifu_engine_api.modules.profile.domain.dto.AssignConstituencyResidenceRequest;
import com.taarifu_engine_api.modules.profile.domain.dto.ProfileResidenceResponse;
import com.taarifu_engine_api.modules.profile.domain.dto.ProfileResponseDto;
import com.taarifu_engine_api.modules.profile.domain.dto.UpdateResidenceRequest;
import com.taarifu_engine_api.modules.profile.domain.entity.Profile;
import com.taarifu_engine_api.modules.profile.domain.entity.ProfileResidence;
import com.taarifu_engine_api.modules.profile.domain.enums.ResidenceType;
import com.taarifu_engine_api.modules.profile.domain.enums.VerificationStatus;
import com.taarifu_engine_api.modules.profile.repository.ProfileResidenceRepository;
import com.taarifu_engine_api.modules.profile.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service implementation for ProfileResidence operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProfileResidenceServiceImpl implements ProfileResidenceService {

    private final ProfileResidenceRepository profileResidenceRepository;
    private final ProfileRepository profileRepository;
    private final AreaRepository areaRepository;
    private final ConstituencyRepository constituencyRepository;

    @Override
    public ProfileResidenceResponse assignAdministrativeResidence(Profile profile, AssignAdministrativeResidenceRequest request) {
        log.info("Assigning administrative residence to profile: {} with area: {}", profile.getUid(), request.getAdministrativeAreaUid());

        // Find the administrative area
        Area area = areaRepository.findByUid(request.getAdministrativeAreaUid())
                .orElseThrow(() -> new ApiException("Administrative area not found", HttpStatus.NOT_FOUND));

        // Check if residence already exists
        if (profileResidenceRepository.existsByProfileAndAdministrativeAreaAndResidenceTypeAndIsActiveTrue(
                profile, area, request.getResidenceType())) {
            throw new ApiException("Residence of this type already exists for this area", HttpStatus.CONFLICT);
        }

        // Handle primary residence logic
        if (Boolean.TRUE.equals(request.getIsPrimary())) {
            setPrimaryAdministrativeResidence(profile, request.getResidenceType());
        }

        // Create new residence
        ProfileResidence residence = new ProfileResidence();
        residence.ensureUid();
        residence.setProfile(profile);
        residence.setResidenceType(request.getResidenceType());
        residence.setIsPrimary(request.getIsPrimary());
        residence.setAdministrativeArea(area);
        residence.setDetailedAddress(request.getDetailedAddress());
        residence.setValidFrom(request.getValidFrom());
        residence.setValidTo(request.getValidTo());
        residence.setNotes(request.getNotes());
        residence.setIsActive(true);
        residence.setVerificationStatus(
                request.getResidenceType().requiresVerification() ? VerificationStatus.PENDING : VerificationStatus.VERIFIED
        );

        ProfileResidence savedResidence = profileResidenceRepository.save(residence);
        log.info("Successfully assigned administrative residence: {} to profile: {}", savedResidence.getUid(), profile.getUid());

        return mapToResponse(savedResidence);
    }

    @Override
    public ProfileResidenceResponse assignConstituencyResidence(Profile profile, AssignConstituencyResidenceRequest request) {
        log.info("Assigning constituency residence to profile: {} with constituency: {}", profile.getUid(), request.getConstituencyUid());

        // Find the constituency
        Constituency constituency = constituencyRepository.findByUid(request.getConstituencyUid())
                .orElseThrow(() -> new ApiException("Constituency not found", HttpStatus.NOT_FOUND));

        // Check if residence already exists
        if (profileResidenceRepository.existsByProfileAndConstituencyAndResidenceTypeAndIsActiveTrue(
                profile, constituency, request.getResidenceType())) {
            throw new ApiException("Residence of this type already exists for this constituency", HttpStatus.CONFLICT);
        }

        // Handle primary residence logic
        if (Boolean.TRUE.equals(request.getIsPrimary())) {
            setPrimaryConstituencyResidence(profile, request.getResidenceType());
        }

        // Create new residence
        ProfileResidence residence = new ProfileResidence();
        residence.ensureUid();
        residence.setProfile(profile);
        residence.setResidenceType(request.getResidenceType());
        residence.setIsPrimary(request.getIsPrimary());
        residence.setConstituency(constituency);
        residence.setDetailedAddress(request.getDetailedAddress());
        residence.setValidFrom(request.getValidFrom());
        residence.setValidTo(request.getValidTo());
        residence.setNotes(request.getNotes());
        residence.setIsActive(true);
        residence.setVerificationStatus(
                request.getResidenceType().requiresVerification() ? VerificationStatus.PENDING : VerificationStatus.VERIFIED
        );

        ProfileResidence savedResidence = profileResidenceRepository.save(residence);
        log.info("Successfully assigned constituency residence: {} to profile: {}", savedResidence.getUid(), profile.getUid());

        return mapToResponse(savedResidence);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProfileResidenceResponse> getProfileResidences(Profile profile) {
        log.debug("Getting all residences for profile: {}", profile.getUid());
        List<ProfileResidence> residences = profileResidenceRepository.findByProfileAndIsActiveTrueOrderByCreatedAtDesc(profile);
        return residences.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProfileResidenceResponse> getProfileResidencesByType(Profile profile, ResidenceType residenceType) {
        log.debug("Getting residences of type: {} for profile: {}", residenceType, profile.getUid());
        List<ProfileResidence> residences = profileResidenceRepository.findByProfileAndResidenceTypeAndIsActiveTrueOrderByCreatedAtDesc(profile, residenceType);
        return residences.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ProfileResidenceResponse getPrimaryAdministrativeResidence(Profile profile) {
        log.debug("Getting primary administrative residence for profile: {}", profile.getUid());
        Optional<ProfileResidence> residence = profileResidenceRepository.findPrimaryAdministrativeResidence(profile);
        return residence.map(this::mapToResponse).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public ProfileResidenceResponse getPrimaryConstituencyResidence(Profile profile) {
        log.debug("Getting primary constituency residence for profile: {}", profile.getUid());
        Optional<ProfileResidence> residence = profileResidenceRepository.findPrimaryConstituencyResidence(profile);
        return residence.map(this::mapToResponse).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public ProfileResidenceResponse getPrimaryResidenceByType(Profile profile, ResidenceType residenceType) {
        log.debug("Getting primary residence of type: {} for profile: {}", residenceType, profile.getUid());
        Optional<ProfileResidence> residence = profileResidenceRepository.findPrimaryResidenceByType(profile, residenceType);
        return residence.map(this::mapToResponse).orElse(null);
    }

    @Override
    public ProfileResidenceResponse updateResidence(String residenceUid, UpdateResidenceRequest request) {
        log.info("Updating residence: {}", residenceUid);

        ProfileResidence residence = profileResidenceRepository.findByUidAndIsActiveTrue(residenceUid)
                .orElseThrow(() -> new ApiException("Residence not found", HttpStatus.NOT_FOUND));

        // Handle primary residence logic
        if (Boolean.TRUE.equals(request.getIsPrimary()) && !Boolean.TRUE.equals(residence.getIsPrimary())) {
            if (residence.hasAdministrativeArea()) {
                setPrimaryAdministrativeResidence(residence.getProfile(), residence.getResidenceType());
            } else if (residence.hasConstituency()) {
                setPrimaryConstituencyResidence(residence.getProfile(), residence.getResidenceType());
            }
        }

        // Update fields
        if (request.getIsPrimary() != null) {
            residence.setIsPrimary(request.getIsPrimary());
        }
        if (request.getDetailedAddress() != null) {
            residence.setDetailedAddress(request.getDetailedAddress());
        }
        if (request.getValidFrom() != null) {
            residence.setValidFrom(request.getValidFrom());
        }
        if (request.getValidTo() != null) {
            residence.setValidTo(request.getValidTo());
        }
        if (request.getIsActive() != null) {
            residence.setIsActive(request.getIsActive());
        }
        if (request.getNotes() != null) {
            residence.setNotes(request.getNotes());
        }

        ProfileResidence updatedResidence = profileResidenceRepository.save(residence);
        log.info("Successfully updated residence: {}", residenceUid);

        return mapToResponse(updatedResidence);
    }

    @Override
    public ProfileResidenceResponse setPrimaryResidence(String residenceUid) {
        log.info("Setting residence as primary: {}", residenceUid);

        ProfileResidence residence = profileResidenceRepository.findByUidAndIsActiveTrue(residenceUid)
                .orElseThrow(() -> new ApiException("Residence not found", HttpStatus.NOT_FOUND));

        // Set as primary based on type
        if (residence.hasAdministrativeArea()) {
            setPrimaryAdministrativeResidence(residence.getProfile(), residence.getResidenceType());
        } else if (residence.hasConstituency()) {
            setPrimaryConstituencyResidence(residence.getProfile(), residence.getResidenceType());
        }

        residence.setIsPrimary(true);
        ProfileResidence updatedResidence = profileResidenceRepository.save(residence);
        log.info("Successfully set residence as primary: {}", residenceUid);

        return mapToResponse(updatedResidence);
    }

    @Override
    public void removeResidence(String residenceUid) {
        log.info("Removing residence: {}", residenceUid);

        ProfileResidence residence = profileResidenceRepository.findByUidAndIsActiveTrue(residenceUid)
                .orElseThrow(() -> new ApiException("Residence not found", HttpStatus.NOT_FOUND));

        residence.setIsActive(false);
        profileResidenceRepository.save(residence);
        log.info("Successfully removed residence: {}", residenceUid);
    }

    @Override
    @Transactional(readOnly = true)
    public ProfileResidenceResponse getResidenceByUid(String residenceUid) {
        log.debug("Getting residence by UID: {}", residenceUid);
        ProfileResidence residence = profileResidenceRepository.findByUidAndIsActiveTrue(residenceUid)
                .orElseThrow(() -> new ApiException("Residence not found", HttpStatus.NOT_FOUND));
        return mapToResponse(residence);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean validateResidenceCompatibility(Area area, Constituency constituency) {
        // Basic validation - can be enhanced with more complex business rules
        if (area == null || constituency == null) {
            return true; // Allow if either is null
        }

        // Check if constituency belongs to the same district as the area
        // This is a simplified validation - can be enhanced based on business requirements
        return true; // For now, allow all combinations
    }

    /**
     * Set primary administrative residence and demote others
     */
    private void setPrimaryAdministrativeResidence(Profile profile, ResidenceType residenceType) {
        // Find existing primary administrative residence of the same type
        Optional<ProfileResidence> existingPrimary = profileResidenceRepository.findPrimaryResidenceByType(profile, residenceType);
        
        if (existingPrimary.isPresent() && existingPrimary.get().hasAdministrativeArea()) {
            // Demote existing primary
            existingPrimary.get().setIsPrimary(false);
            profileResidenceRepository.save(existingPrimary.get());
            log.debug("Demoted existing primary administrative residence for profile: {}", profile.getUid());
        }
    }

    /**
     * Set primary constituency residence and demote others
     */
    private void setPrimaryConstituencyResidence(Profile profile, ResidenceType residenceType) {
        // Find existing primary constituency residence of the same type
        Optional<ProfileResidence> existingPrimary = profileResidenceRepository.findPrimaryResidenceByType(profile, residenceType);
        
        if (existingPrimary.isPresent() && existingPrimary.get().hasConstituency()) {
            // Demote existing primary
            existingPrimary.get().setIsPrimary(false);
            profileResidenceRepository.save(existingPrimary.get());
            log.debug("Demoted existing primary constituency residence for profile: {}", profile.getUid());
        }
    }

    /**
     * Map ProfileResidence entity to response DTO
     */
    private ProfileResidenceResponse mapToResponse(ProfileResidence residence) {
        return ProfileResidenceResponse.builder()
                .uid(residence.getUid())
                .profileUid(residence.getProfile().getUid())
                .residenceType(residence.getResidenceType())
                .isPrimary(residence.getIsPrimary())
                .administrativeArea(residence.getAdministrativeArea() != null ? 
                    mapAreaToResponse(residence.getAdministrativeArea()) : null)
                .constituency(residence.getConstituency() != null ? 
                    mapConstituencyToResponse(residence.getConstituency()) : null)
                .detailedAddress(residence.getDetailedAddress())
                .validFrom(residence.getValidFrom())
                .validTo(residence.getValidTo())
                .isActive(residence.getIsActive())
                .notes(residence.getNotes())
                .verificationStatus(residence.getVerificationStatus())
                .verificationDocuments(residence.getVerificationDocuments())
                .createdAt(residence.getCreatedAt())
                .updatedAt(residence.getUpdatedAt())
                .displayName(residence.getDisplayName())
                .build();
    }

    /**
     * Map Area entity to response DTO (simplified)
     */
    private com.taarifu_engine_api.modules.location.area.domain.dto.AreaResponse mapAreaToResponse(Area area) {
        com.taarifu_engine_api.modules.location.area.domain.dto.AreaResponse response = new com.taarifu_engine_api.modules.location.area.domain.dto.AreaResponse();
        response.setId(area.getId());
        response.setUid(area.getUid());
        response.setCode(area.getCode());
        response.setAreaType(area.getAreaType());
        response.setAreaId(area.getAreaId());
        response.setName(area.getName());
        response.setCreatedAt(area.getCreatedAt());
        response.setUpdatedAt(area.getUpdatedAt());
        return response;
    }

    /**
     * Map Constituency entity to response DTO (simplified)
     */
    private com.taarifu_engine_api.modules.location.constituency.domain.dto.ConstituencyResponse mapConstituencyToResponse(Constituency constituency) {
        com.taarifu_engine_api.modules.location.constituency.domain.dto.ConstituencyResponse response = new com.taarifu_engine_api.modules.location.constituency.domain.dto.ConstituencyResponse();
        response.setId(constituency.getId());
        response.setUid(constituency.getUid());
        response.setCode(constituency.getCode());
        response.setName(constituency.getName());
        response.setHeadquarters(constituency.getHeadquarters());
        response.setPopulation(constituency.getPopulation());
        response.setAreaSqKm(constituency.getAreaSqKm());
        response.setLatitude(constituency.getLatitude());
        response.setLongitude(constituency.getLongitude());
        response.setDescription(constituency.getDescription());
        response.setIsActive(constituency.getIsActive());
        response.setCreatedAt(constituency.getCreatedAt());
        response.setUpdatedAt(constituency.getUpdatedAt());
        return response;
    }

    // ==================== OVERLOADED METHODS FOR ProfileResponseDto ====================

    @Override
    public ProfileResidenceResponse assignAdministrativeResidence(ProfileResponseDto profileDto, AssignAdministrativeResidenceRequest request) {
        Profile profile = profileRepository.findByUid(profileDto.getUid())
                .orElseThrow(() -> new ApiException("Profile not found", HttpStatus.NOT_FOUND));
        return assignAdministrativeResidence(profile, request);
    }

    @Override
    public ProfileResidenceResponse assignConstituencyResidence(ProfileResponseDto profileDto, AssignConstituencyResidenceRequest request) {
        Profile profile = profileRepository.findByUid(profileDto.getUid())
                .orElseThrow(() -> new ApiException("Profile not found", HttpStatus.NOT_FOUND));
        return assignConstituencyResidence(profile, request);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProfileResidenceResponse> getProfileResidences(ProfileResponseDto profileDto) {
        Profile profile = profileRepository.findByUid(profileDto.getUid())
                .orElseThrow(() -> new ApiException("Profile not found", HttpStatus.NOT_FOUND));
        return getProfileResidences(profile);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProfileResidenceResponse> getProfileResidencesByType(ProfileResponseDto profileDto, ResidenceType residenceType) {
        Profile profile = profileRepository.findByUid(profileDto.getUid())
                .orElseThrow(() -> new ApiException("Profile not found", HttpStatus.NOT_FOUND));
        return getProfileResidencesByType(profile, residenceType);
    }

    @Override
    @Transactional(readOnly = true)
    public ProfileResidenceResponse getPrimaryAdministrativeResidence(ProfileResponseDto profileDto) {
        Profile profile = profileRepository.findByUid(profileDto.getUid())
                .orElseThrow(() -> new ApiException("Profile not found", HttpStatus.NOT_FOUND));
        return getPrimaryAdministrativeResidence(profile);
    }

    @Override
    @Transactional(readOnly = true)
    public ProfileResidenceResponse getPrimaryConstituencyResidence(ProfileResponseDto profileDto) {
        Profile profile = profileRepository.findByUid(profileDto.getUid())
                .orElseThrow(() -> new ApiException("Profile not found", HttpStatus.NOT_FOUND));
        return getPrimaryConstituencyResidence(profile);
    }

    @Override
    @Transactional(readOnly = true)
    public ProfileResidenceResponse getPrimaryResidenceByType(ProfileResponseDto profileDto, ResidenceType residenceType) {
        Profile profile = profileRepository.findByUid(profileDto.getUid())
                .orElseThrow(() -> new ApiException("Profile not found", HttpStatus.NOT_FOUND));
        return getPrimaryResidenceByType(profile, residenceType);
    }
}
