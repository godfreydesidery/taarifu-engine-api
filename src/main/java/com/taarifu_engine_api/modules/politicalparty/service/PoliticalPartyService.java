package com.taarifu_engine_api.modules.politicalparty.service;

import com.taarifu_engine_api.modules.common.domain.util.PageResponseWrapper;
import com.taarifu_engine_api.modules.politicalparty.domain.dto.CreatePoliticalPartyRequestDto;
import com.taarifu_engine_api.modules.politicalparty.domain.dto.PoliticalPartyResponseDto;
import com.taarifu_engine_api.modules.politicalparty.domain.dto.UpdatePoliticalPartyRequestDto;
import org.springframework.data.domain.Pageable;

/**
 * Service interface for Political Party operations
 */
public interface PoliticalPartyService {

    /**
     * Create a new political party
     */
    PoliticalPartyResponseDto createPoliticalParty(CreatePoliticalPartyRequestDto request);

    /**
     * Update political party by ID
     */
    PoliticalPartyResponseDto updatePoliticalParty(Long id, UpdatePoliticalPartyRequestDto request);

    /**
     * Update political party by UID
     */
    PoliticalPartyResponseDto updatePoliticalPartyByUid(String uid, UpdatePoliticalPartyRequestDto request);

    /**
     * Get political party by ID
     */
    PoliticalPartyResponseDto getPoliticalPartyById(Long id);

    /**
     * Get political party by UID
     */
    PoliticalPartyResponseDto getPoliticalPartyByUid(String uid);

    /**
     * Get political party by code
     */
    PoliticalPartyResponseDto getPoliticalPartyByCode(String code);

    /**
     * Get political party by name
     */
    PoliticalPartyResponseDto getPoliticalPartyByName(String name);

    /**
     * Get political party by abbreviation
     */
    PoliticalPartyResponseDto getPoliticalPartyByAbbreviation(String abbreviation);

    /**
     * Get all political parties with pagination
     */
    PageResponseWrapper<PoliticalPartyResponseDto> getAllPoliticalParties(Pageable pageable);

    /**
     * Get all active political parties with pagination
     */
    PageResponseWrapper<PoliticalPartyResponseDto> getActivePoliticalParties(Pageable pageable);

    /**
     * Get all registered political parties with pagination
     */
    PageResponseWrapper<PoliticalPartyResponseDto> getRegisteredPoliticalParties(Pageable pageable);

    /**
     * Get all operational political parties with pagination
     */
    PageResponseWrapper<PoliticalPartyResponseDto> getOperationalPoliticalParties(Pageable pageable);

    /**
     * Search political parties with pagination
     */
    PageResponseWrapper<PoliticalPartyResponseDto> searchPoliticalParties(String searchTerm, Pageable pageable);

    /**
     * Get political parties by founding year
     */
    PageResponseWrapper<PoliticalPartyResponseDto> getPoliticalPartiesByFoundingYear(int year, Pageable pageable);

    /**
     * Get political parties by ideology
     */
    PageResponseWrapper<PoliticalPartyResponseDto> getPoliticalPartiesByIdeology(String ideology, Pageable pageable);

    /**
     * Delete political party by ID (soft delete)
     */
    void deletePoliticalParty(Long id);

    /**
     * Delete political party by UID (soft delete)
     */
    void deletePoliticalPartyByUid(String uid);

    /**
     * Activate political party by ID
     */
    PoliticalPartyResponseDto activatePoliticalParty(Long id);

    /**
     * Deactivate political party by ID
     */
    PoliticalPartyResponseDto deactivatePoliticalParty(Long id);

    /**
     * Register political party by ID
     */
    PoliticalPartyResponseDto registerPoliticalParty(Long id);

    /**
     * Deregister political party by ID
     */
    PoliticalPartyResponseDto deregisterPoliticalParty(Long id);

    /**
     * Get political party statistics
     */
    PoliticalPartyStats getPoliticalPartyStats();

    /**
     * Record class for political party statistics
     */
    record PoliticalPartyStats(
            long totalParties,
            long activeParties,
            long inactiveParties,
            long registeredParties,
            long unregisteredParties,
            long operationalParties,
            long partiesWithWebsite,
            long partiesFoundedThisYear,
            long averageMemberCount
    ) {}
}
