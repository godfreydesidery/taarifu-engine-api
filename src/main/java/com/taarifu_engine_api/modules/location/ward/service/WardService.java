package com.taarifu_engine_api.modules.location.ward.service;

import com.taarifu_engine_api.modules.location.ward.domain.dto.CreateWardRequest;
import com.taarifu_engine_api.modules.location.ward.domain.dto.WardResponse;
import com.taarifu_engine_api.modules.location.ward.domain.dto.UpdateWardRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


/**
 * Service interface for Ward operations
 */
public interface WardService {

    /**
     * Create a new ward
     */
    WardResponse createWard(CreateWardRequest request);

    /**
     * Update an existing ward by UID
     */
    WardResponse updateWardByUid(String uid, UpdateWardRequest request);

    /**
     * Get ward by UID
     */
    WardResponse getWardByUid(String uid);

    /**
     * Get ward by code
     */
    WardResponse getWardByCode(String code);

    /**
     * Get all wards with pagination
     */
    Page<WardResponse> getAllWards(Pageable pageable);

    /**
     * Get all active wards with pagination
     */
    Page<WardResponse> getActiveWards(Pageable pageable);

    /**
     * Get wards by district UID with pagination
     */
    Page<WardResponse> getWardsByDistrictUid(String districtUid, Pageable pageable);

    /**
     * Get active wards by district UID with pagination
     */
    Page<WardResponse> getActiveWardsByDistrictUid(String districtUid, Pageable pageable);

    /**
     * Search wards by search term
     */
    Page<WardResponse> searchWards(String searchTerm, Pageable pageable);

    /**
     * Search wards by district UID and search term
     */
    Page<WardResponse> searchWardsByDistrictUid(String districtUid, String searchTerm, Pageable pageable);

    /**
     * Get wards by activity status
     */
    Page<WardResponse> getWardsByStatus(Boolean isActive, Pageable pageable);

    /**
     * Delete ward by UID (soft delete)
     */
    void deleteWardByUid(String uid);

    /**
     * Activate/deactivate ward by UID
     */
    WardResponse toggleWardStatusByUid(String uid);

    /**
     * Get ward statistics
     */
    WardStats getWardStats();

    /**
     * Get ward statistics by district UID
     */
    WardStats getWardStatsByDistrictUid(String districtUid);

    /**
     * Inner class for ward statistics
     */
    class WardStats {
        private final long totalWards;
        private final long activeWards;
        private final long inactiveWards;

        public WardStats(long totalWards, long activeWards, long inactiveWards) {
            this.totalWards = totalWards;
            this.activeWards = activeWards;
            this.inactiveWards = inactiveWards;
        }

        public long getTotalWards() { return totalWards; }
        public long getActiveWards() { return activeWards; }
        public long getInactiveWards() { return inactiveWards; }
    }
}
