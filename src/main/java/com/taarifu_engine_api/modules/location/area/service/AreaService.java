package com.taarifu_engine_api.modules.location.area.service;

import com.taarifu_engine_api.modules.common.domain.enums.AreaType;
import com.taarifu_engine_api.modules.common.domain.util.PageResponseWrapper;
import com.taarifu_engine_api.modules.location.area.domain.dto.AreaResponse;
import com.taarifu_engine_api.modules.location.area.domain.entity.Area;
import org.springframework.data.domain.Pageable;

/**
 * Service interface for Area operations
 */
public interface AreaService {

    /**
     * Create a new area
     */
    Area createArea(AreaType areaType, Long areaId, String name);

    /**
     * Update an existing area
     */
    Area updateArea(AreaType areaType, Long areaId, String name);

    /**
     * Find area by UID
     */
    Area findByUid(String uid);

    /**
     * Find area by code
     */
    Area findByCode(String code);

    /**
     * Find area by area type and area ID
     */
    Area findByAreaTypeAndAreaId(AreaType areaType, Long areaId);

    /**
     * Get all areas with pagination
     */
    PageResponseWrapper<AreaResponse> getAllAreas(Pageable pageable);

    /**
     * Get areas by area type with pagination
     */
    PageResponseWrapper<AreaResponse> getAreasByType(AreaType areaType, Pageable pageable);

    /**
     * Search areas with pagination
     */
    PageResponseWrapper<AreaResponse> searchAreas(String searchTerm, Pageable pageable);

    /**
     * Search areas by type with pagination
     */
    PageResponseWrapper<AreaResponse> searchAreasByType(AreaType areaType, String searchTerm, Pageable pageable);

    /**
     * Get area response by UID
     */
    AreaResponse getAreaResponseByUid(String uid);

    /**
     * Get area response by code
     */
    AreaResponse getAreaResponseByCode(String code);

    /**
     * Get area response by area type and area ID
     */
    AreaResponse getAreaResponseByTypeAndId(AreaType areaType, Long areaId);

    /**
     * Get area statistics
     */
    AreaStats getAreaStats();

    /**
     * Generate next area code
     */
    String generateNextAreaCode();

    /**
     * Check if area exists by area type and area ID
     */
    boolean existsByAreaTypeAndAreaId(AreaType areaType, Long areaId);

    /**
     * Delete area by area type and area ID
     */
    void deleteByAreaTypeAndAreaId(AreaType areaType, Long areaId);

    /**
     * Area statistics record
     */
    record AreaStats(
        long totalAreas,
        long regionCount,
        long districtCount,
        long wardCount,
        long villageCount,
        long hamletCount,
        long constituencyCount
    ) {}
}
