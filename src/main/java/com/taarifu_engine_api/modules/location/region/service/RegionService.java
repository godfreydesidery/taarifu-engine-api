package com.taarifu_engine_api.modules.location.region.service;

import com.taarifu_engine_api.modules.location.region.domain.dto.CreateRegionRequest;
import com.taarifu_engine_api.modules.location.region.domain.dto.RegionResponse;
import com.taarifu_engine_api.modules.location.region.domain.dto.UpdateRegionRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service interface for Region operations
 */
public interface RegionService {

    /**
     * Create a new region
     */
    RegionResponse createRegion(CreateRegionRequest request);

    /**
     * Update an existing region
     */
    RegionResponse updateRegion(Long id, UpdateRegionRequest request);

    /**
     * Update an existing region by UID
     */
    RegionResponse updateRegionByUid(String uid, UpdateRegionRequest request);

    /**
     * Get region by ID
     */
    RegionResponse getRegionById(Long id);

    /**
     * Get region by UID
     */
    RegionResponse getRegionByUid(String uid);

    /**
     * Get region by code
     */
    RegionResponse getRegionByCode(String code);

    /**
     * Get all regions with pagination
     */
    Page<RegionResponse> getAllRegions(Pageable pageable);

    /**
     * Get all active regions
     */
    List<RegionResponse> getActiveRegions();

    /**
     * Get all active regions with pagination
     */
    Page<RegionResponse> getActiveRegions(Pageable pageable);

    /**
     * Search regions by search term
     */
    Page<RegionResponse> searchRegions(String searchTerm, Pageable pageable);

    /**
     * Get regions by activity status
     */
    Page<RegionResponse> getRegionsByStatus(Boolean isActive, Pageable pageable);

    /**
     * Delete region by ID (soft delete)
     */
    void deleteRegion(Long id);

    /**
     * Delete region by UID (soft delete)
     */
    void deleteRegionByUid(String uid);

    /**
     * Activate/deactivate region
     */
    RegionResponse toggleRegionStatus(Long id);

    /**
     * Activate/deactivate region by UID
     */
    RegionResponse toggleRegionStatusByUid(String uid);

    /**
     * Get region statistics
     */
    RegionStats getRegionStats();

    /**
     * Inner class for region statistics
     */
    class RegionStats {
        private final long totalRegions;
        private final long activeRegions;
        private final long inactiveRegions;

        public RegionStats(long totalRegions, long activeRegions, long inactiveRegions) {
            this.totalRegions = totalRegions;
            this.activeRegions = activeRegions;
            this.inactiveRegions = inactiveRegions;
        }

        public long getTotalRegions() { return totalRegions; }
        public long getActiveRegions() { return activeRegions; }
        public long getInactiveRegions() { return inactiveRegions; }
    }
}
