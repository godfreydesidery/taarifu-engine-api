package com.taarifu_engine_api.modules.citizen.service;

import com.taarifu_engine_api.modules.citizen.domain.dto.CitizenResponseDto;
import com.taarifu_engine_api.modules.citizen.domain.dto.CreateCitizenRequestDto;
import com.taarifu_engine_api.modules.citizen.domain.dto.UpdateCitizenRequestDto;
import com.taarifu_engine_api.modules.citizen.domain.entity.Citizen;
import com.taarifu_engine_api.modules.profile.domain.entity.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for Citizen management operations
 * These methods are designed for internal use by other services, not direct controller access
 */
public interface CitizenService {
    
    /**
     * Create a new citizen record for a profile
     * This method should be called when a PERSON profile is created
     */
    CitizenResponseDto createCitizen(CreateCitizenRequestDto request);
    
    /**
     * Create a citizen automatically when a PERSON profile is created
     * This is the main method used internally by ProfileService
     */
    CitizenResponseDto createCitizenForProfile(Profile profile);
    
    /**
     * Update citizen information
     */
    CitizenResponseDto updateCitizen(Long citizenId, UpdateCitizenRequestDto request);
    
    /**
     * Update citizen information by profile
     */
    CitizenResponseDto updateCitizenByProfile(Profile profile, UpdateCitizenRequestDto request);
    
    /**
     * Get citizen by ID
     */
    Optional<CitizenResponseDto> getCitizenById(Long citizenId);
    
    /**
     * Get citizen by profile
     */
    Optional<CitizenResponseDto> getCitizenByProfile(Profile profile);
    
    /**
     * Get citizen by profile ID
     */
    Optional<CitizenResponseDto> getCitizenByProfileId(Long profileId);
    
    /**
     * Get citizen by profile UID
     */
    Optional<CitizenResponseDto> getCitizenByProfileUid(String profileUid);
    
    /**
     * Check if a citizen exists for the given profile
     */
    boolean existsCitizenForProfile(Profile profile);
    
    /**
     * Check if a citizen exists for the given profile ID
     */
    boolean existsCitizenForProfileId(Long profileId);
    
    /**
     * Get all citizens with pagination
     */
    Page<CitizenResponseDto> getAllCitizens(Pageable pageable);
    
    /**
     * Get citizens by citizenship type
     */
    Page<CitizenResponseDto> getCitizensByType(String citizenshipType, Pageable pageable);
    
    /**
     * Get citizens by citizenship status
     */
    Page<CitizenResponseDto> getCitizensByStatus(String citizenshipStatus, Pageable pageable);
    
    /**
     * Get citizens by citizenship type and status
     */
    Page<CitizenResponseDto> getCitizensByTypeAndStatus(String citizenshipType, String citizenshipStatus, Pageable pageable);
    
    /**
     * Search citizens by name, email, or ID number
     */
    Page<CitizenResponseDto> searchCitizens(String searchTerm, Pageable pageable);
    
    /**
     * Get citizens who became citizens within a date range
     */
    Page<CitizenResponseDto> getCitizensByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    
    /**
     * Get citizen statistics
     */
    CitizenStats getCitizenStats();
    
    /**
     * Get most recent citizens
     */
    List<CitizenResponseDto> getMostRecentCitizens(int limit);
    
    /**
     * Convert Citizen entity to CitizenResponseDto
     */
    CitizenResponseDto convertToResponseDto(Citizen citizen);
    
    /**
     * Get Citizen entity by ID (for internal use)
     */
    Optional<Citizen> getCitizenEntityById(Long citizenId);
    
    /**
     * Get Citizen entity by profile (for internal use)
     */
    Optional<Citizen> getCitizenEntityByProfile(Profile profile);
    
    /**
     * Citizen statistics inner class
     */
    class CitizenStats {
        private long totalCitizens;
        private long byBirthCitizens;
        private long byNaturalizationCitizens;
        private long activeCitizens;
        private long inactiveCitizens;
        
        public CitizenStats(long totalCitizens, long byBirthCitizens, long byNaturalizationCitizens, 
                           long activeCitizens, long inactiveCitizens) {
            this.totalCitizens = totalCitizens;
            this.byBirthCitizens = byBirthCitizens;
            this.byNaturalizationCitizens = byNaturalizationCitizens;
            this.activeCitizens = activeCitizens;
            this.inactiveCitizens = inactiveCitizens;
        }
        
        // Getters
        public long getTotalCitizens() { return totalCitizens; }
        public long getByBirthCitizens() { return byBirthCitizens; }
        public long getByNaturalizationCitizens() { return byNaturalizationCitizens; }
        public long getActiveCitizens() { return activeCitizens; }
        public long getInactiveCitizens() { return inactiveCitizens; }
    }
}
