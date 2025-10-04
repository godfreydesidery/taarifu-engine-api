package com.taarifu_engine_api.modules.location.village.service;

import com.taarifu_engine_api.modules.location.village.domain.dto.CreateVillageRequest;
import com.taarifu_engine_api.modules.location.village.domain.dto.VillageResponse;
import com.taarifu_engine_api.modules.location.village.domain.dto.UpdateVillageRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


/**
 * Service interface for Village operations
 */
public interface VillageService {

    /**
     * Create a new village
     */
    VillageResponse createVillage(CreateVillageRequest request);

    /**
     * Update an existing village by UID
     */
    VillageResponse updateVillageByUid(String uid, UpdateVillageRequest request);

    /**
     * Get village by UID
     */
    VillageResponse getVillageByUid(String uid);

    /**
     * Get village by code
     */
    VillageResponse getVillageByCode(String code);

    /**
     * Get all villages with pagination
     */
    Page<VillageResponse> getAllVillages(Pageable pageable);

    /**
     * Get all active villages with pagination
     */
    Page<VillageResponse> getActiveVillages(Pageable pageable);

    /**
     * Get villages by ward UID with pagination
     */
    Page<VillageResponse> getVillagesByWardUid(String wardUid, Pageable pageable);

    /**
     * Get active villages by ward UID with pagination
     */
    Page<VillageResponse> getActiveVillagesByWardUid(String wardUid, Pageable pageable);

    /**
     * Search villages by search term
     */
    Page<VillageResponse> searchVillages(String searchTerm, Pageable pageable);

    /**
     * Search villages by ward UID and search term
     */
    Page<VillageResponse> searchVillagesByWardUid(String wardUid, String searchTerm, Pageable pageable);

    /**
     * Get villages by activity status
     */
    Page<VillageResponse> getVillagesByStatus(Boolean isActive, Pageable pageable);

    /**
     * Delete village by UID (soft delete)
     */
    void deleteVillageByUid(String uid);

    /**
     * Activate/deactivate village by UID
     */
    VillageResponse toggleVillageStatusByUid(String uid);

    /**
     * Get village statistics
     */
    VillageStats getVillageStats();

    /**
     * Get village statistics by ward UID
     */
    VillageStats getVillageStatsByWardUid(String wardUid);

    /**
     * Inner class for village statistics
     */
    class VillageStats {
        private final long totalVillages;
        private final long activeVillages;
        private final long inactiveVillages;

        public VillageStats(long totalVillages, long activeVillages, long inactiveVillages) {
            this.totalVillages = totalVillages;
            this.activeVillages = activeVillages;
            this.inactiveVillages = inactiveVillages;
        }

        public long getTotalVillages() { return totalVillages; }
        public long getActiveVillages() { return activeVillages; }
        public long getInactiveVillages() { return inactiveVillages; }
    }
}
