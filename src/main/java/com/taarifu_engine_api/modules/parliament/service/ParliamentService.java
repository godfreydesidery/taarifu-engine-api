package com.taarifu_engine_api.modules.parliament.service;

import com.taarifu_engine_api.modules.common.domain.util.PageResponseWrapper;
import com.taarifu_engine_api.modules.parliament.domain.dto.CreateParliamentRequestDto;
import com.taarifu_engine_api.modules.parliament.domain.dto.ParliamentResponseDto;
import com.taarifu_engine_api.modules.parliament.domain.dto.UpdateParliamentRequestDto;
import org.springframework.data.domain.Pageable;

/**
 * Service interface for Parliament operations
 */
public interface ParliamentService {

    /**
     * Create a new parliament
     */
    ParliamentResponseDto createParliament(CreateParliamentRequestDto request);

    /**
     * Update parliament by ID
     */
    ParliamentResponseDto updateParliament(Long id, UpdateParliamentRequestDto request);

    /**
     * Update parliament by UID
     */
    ParliamentResponseDto updateParliamentByUid(String uid, UpdateParliamentRequestDto request);

    /**
     * Get parliament by ID
     */
    ParliamentResponseDto getParliamentById(Long id);

    /**
     * Get parliament by UID
     */
    ParliamentResponseDto getParliamentByUid(String uid);

    /**
     * Get parliament by code
     */
    ParliamentResponseDto getParliamentByCode(String code);

    /**
     * Get the current parliament
     */
    ParliamentResponseDto getCurrentParliament();

    /**
     * Get all parliaments with pagination
     */
    PageResponseWrapper<ParliamentResponseDto> getAllParliaments(Pageable pageable);

    /**
     * Get all active parliaments with pagination
     */
    PageResponseWrapper<ParliamentResponseDto> getActiveParliaments(Pageable pageable);

    /**
     * Search parliaments with pagination
     */
    PageResponseWrapper<ParliamentResponseDto> searchParliaments(String searchTerm, Pageable pageable);

    /**
     * Delete parliament by ID (soft delete)
     */
    void deleteParliament(Long id);

    /**
     * Delete parliament by UID (soft delete)
     */
    void deleteParliamentByUid(String uid);

    /**
     * Set parliament as current
     */
    ParliamentResponseDto setCurrentParliament(Long id);

    /**
     * Set parliament as current by UID
     */
    ParliamentResponseDto setCurrentParliamentByUid(String uid);

    /**
     * Get parliament statistics
     */
    ParliamentStats getParliamentStats();

    /**
     * Record class for parliament statistics
     */
    record ParliamentStats(
            long totalParliaments,
            long activeParliaments,
            long inactiveParliaments,
            long currentParliaments,
            long endedParliaments,
            long inSessionParliaments
    ) {}
}
