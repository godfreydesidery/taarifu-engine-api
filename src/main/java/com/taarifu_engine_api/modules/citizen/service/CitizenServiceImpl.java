package com.taarifu_engine_api.modules.citizen.service;

import com.taarifu_engine_api.modules.citizen.domain.dto.CitizenResponseDto;
import com.taarifu_engine_api.modules.citizen.domain.dto.CreateCitizenRequestDto;
import com.taarifu_engine_api.modules.citizen.domain.dto.UpdateCitizenRequestDto;
import com.taarifu_engine_api.modules.citizen.domain.entity.Citizen;
import com.taarifu_engine_api.modules.citizen.repository.CitizenRepository;
import com.taarifu_engine_api.modules.common.exception.ApiException;
import com.taarifu_engine_api.modules.profile.domain.entity.Profile;
import com.taarifu_engine_api.modules.profile.domain.enums.ProfileType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of CitizenService
 * Handles citizen management operations including automatic creation for PERSON profiles
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CitizenServiceImpl implements CitizenService {
    
    private final CitizenRepository citizenRepository;
    
    @Override
    public CitizenResponseDto createCitizen(CreateCitizenRequestDto request) {
        log.info("Creating citizen for profile ID: {}", request.getProfileId());
        
        // Check if citizen already exists for this profile
        if (citizenRepository.existsByProfileId(request.getProfileId())) {
            throw new ApiException("Citizen already exists for this profile", HttpStatus.CONFLICT);
        }
        
        Citizen citizen = Citizen.builder()
                .profile(new Profile()) // Will be set properly
                .citizenshipType(request.getCitizenshipType() != null ? request.getCitizenshipType() : "by_birth")
                .citizenshipStatus(request.getCitizenshipStatus() != null ? request.getCitizenshipStatus() : "active")
                .dateOfCitizenship(request.getDateOfCitizenship() != null ? request.getDateOfCitizenship() : LocalDateTime.now())
                .build();
        
        // Set the profile
        citizen.getProfile().setId(request.getProfileId());
        
        Citizen savedCitizen = citizenRepository.save(citizen);
        log.info("Citizen created successfully with ID: {}", savedCitizen.getId());
        
        return convertToResponseDto(savedCitizen);
    }
    
    @Override
    public CitizenResponseDto createCitizenForProfile(Profile profile) {
        log.info("Creating citizen automatically for profile: {} (type: {})", profile.getUid(), profile.getProfileType());
        
        // Only create citizen for PERSON profiles
        if (!ProfileType.PERSON.equals(profile.getProfileType())) {
            throw new ApiException("Citizens can only be created for PERSON profile types", HttpStatus.BAD_REQUEST);
        }
        
        // Check if citizen already exists for this profile
        if (citizenRepository.existsByProfile(profile)) {
            log.warn("Citizen already exists for profile: {}", profile.getUid());
            return getCitizenByProfile(profile).orElseThrow(() -> 
                new ApiException("Citizen exists but could not be retrieved", HttpStatus.INTERNAL_SERVER_ERROR));
        }
        
        Citizen citizen = Citizen.builder()
                .profile(profile)
                .citizenshipType("by_birth") // Default citizenship type
                .citizenshipStatus("active") // Default status
                .dateOfCitizenship(LocalDateTime.now())
                .build();
        
        Citizen savedCitizen = citizenRepository.save(citizen);
        log.info("Citizen created automatically for profile: {} with ID: {}", profile.getUid(), savedCitizen.getId());
        
        return convertToResponseDto(savedCitizen);
    }
    
    @Override
    public CitizenResponseDto updateCitizen(Long citizenId, UpdateCitizenRequestDto request) {
        log.info("Updating citizen with ID: {}", citizenId);
        
        Citizen citizen = citizenRepository.findById(citizenId)
                .orElseThrow(() -> new ApiException("Citizen not found with ID: " + citizenId, HttpStatus.NOT_FOUND));
        
        // Update fields if provided
        if (request.getCitizenshipType() != null) {
            citizen.setCitizenshipType(request.getCitizenshipType());
        }
        if (request.getCitizenshipStatus() != null) {
            citizen.setCitizenshipStatus(request.getCitizenshipStatus());
        }
        if (request.getDateOfCitizenship() != null) {
            citizen.setDateOfCitizenship(request.getDateOfCitizenship());
        }
        
        Citizen updatedCitizen = citizenRepository.save(citizen);
        log.info("Citizen updated successfully with ID: {}", updatedCitizen.getId());
        
        return convertToResponseDto(updatedCitizen);
    }
    
    @Override
    public CitizenResponseDto updateCitizenByProfile(Profile profile, UpdateCitizenRequestDto request) {
        log.info("Updating citizen for profile: {}", profile.getUid());
        
        Citizen citizen = citizenRepository.findByProfile(profile)
                .orElseThrow(() -> new ApiException("Citizen not found for profile: " + profile.getUid(), HttpStatus.NOT_FOUND));
        
        return updateCitizen(citizen.getId(), request);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<CitizenResponseDto> getCitizenById(Long citizenId) {
        log.debug("Getting citizen by ID: {}", citizenId);
        
        return citizenRepository.findById(citizenId)
                .map(this::convertToResponseDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<CitizenResponseDto> getCitizenByProfile(Profile profile) {
        log.debug("Getting citizen by profile: {}", profile.getUid());
        
        return citizenRepository.findByProfile(profile)
                .map(this::convertToResponseDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<CitizenResponseDto> getCitizenByProfileId(Long profileId) {
        log.debug("Getting citizen by profile ID: {}", profileId);
        
        return citizenRepository.findByProfileId(profileId)
                .map(this::convertToResponseDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<CitizenResponseDto> getCitizenByProfileUid(String profileUid) {
        log.debug("Getting citizen by profile UID: {}", profileUid);
        
        return citizenRepository.findByProfileUid(profileUid)
                .map(this::convertToResponseDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsCitizenForProfile(Profile profile) {
        return citizenRepository.existsByProfile(profile);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsCitizenForProfileId(Long profileId) {
        return citizenRepository.existsByProfileId(profileId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<CitizenResponseDto> getAllCitizens(Pageable pageable) {
        log.debug("Getting all citizens with pagination");
        
        return citizenRepository.findAll(pageable)
                .map(this::convertToResponseDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<CitizenResponseDto> getCitizensByType(String citizenshipType, Pageable pageable) {
        log.debug("Getting citizens by type: {}", citizenshipType);
        
        return citizenRepository.findByCitizenshipType(citizenshipType, pageable)
                .map(this::convertToResponseDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<CitizenResponseDto> getCitizensByStatus(String citizenshipStatus, Pageable pageable) {
        log.debug("Getting citizens by status: {}", citizenshipStatus);
        
        return citizenRepository.findByCitizenshipStatus(citizenshipStatus, pageable)
                .map(this::convertToResponseDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<CitizenResponseDto> getCitizensByTypeAndStatus(String citizenshipType, String citizenshipStatus, Pageable pageable) {
        log.debug("Getting citizens by type: {} and status: {}", citizenshipType, citizenshipStatus);
        
        return citizenRepository.findByCitizenshipTypeAndCitizenshipStatus(citizenshipType, citizenshipStatus, pageable)
                .map(this::convertToResponseDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<CitizenResponseDto> searchCitizens(String searchTerm, Pageable pageable) {
        log.debug("Searching citizens with term: {}", searchTerm);
        
        return citizenRepository.searchCitizens(searchTerm, pageable)
                .map(this::convertToResponseDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<CitizenResponseDto> getCitizensByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        log.debug("Getting citizens by date range: {} to {}", startDate, endDate);
        
        return citizenRepository.findCitizensByDateRange(startDate, endDate, pageable)
                .map(this::convertToResponseDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public CitizenStats getCitizenStats() {
        log.debug("Getting citizen statistics");
        
        long totalCitizens = citizenRepository.countTotalCitizens();
        long byBirthCitizens = citizenRepository.countByCitizenshipType("by_birth");
        long byNaturalizationCitizens = citizenRepository.countByCitizenshipType("by_naturalization");
        long activeCitizens = citizenRepository.countByCitizenshipStatus("active");
        long inactiveCitizens = citizenRepository.countByCitizenshipStatus("inactive");
        
        return new CitizenStats(totalCitizens, byBirthCitizens, byNaturalizationCitizens, activeCitizens, inactiveCitizens);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CitizenResponseDto> getMostRecentCitizens(int limit) {
        log.debug("Getting most recent citizens, limit: {}", limit);
        
        return citizenRepository.findMostRecentCitizens(limit).stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public CitizenResponseDto convertToResponseDto(Citizen citizen) {
        if (citizen == null) {
            return null;
        }
        
        return CitizenResponseDto.builder()
                .id(citizen.getId())
                .profileUid(citizen.getProfile().getUid())
                .profile(null) // Set this if needed, but avoid circular references
                .dateOfCitizenship(citizen.getDateOfCitizenship())
                .citizenshipType(citizen.getCitizenshipType())
                .citizenshipStatus(citizen.getCitizenshipStatus())
                .build();
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Citizen> getCitizenEntityById(Long citizenId) {
        return citizenRepository.findById(citizenId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Citizen> getCitizenEntityByProfile(Profile profile) {
        return citizenRepository.findByProfile(profile);
    }
}
