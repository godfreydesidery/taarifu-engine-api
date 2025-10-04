package com.taarifu_engine_api.modules.location.constituency.service;

import com.taarifu_engine_api.modules.location.constituency.domain.dto.CreateConstituencyRequest;
import com.taarifu_engine_api.modules.location.constituency.domain.dto.ConstituencyResponse;
import com.taarifu_engine_api.modules.location.constituency.domain.dto.UpdateConstituencyRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


/**
 * Service interface for Constituency operations
 */
public interface ConstituencyService {

    /**
     * Create a new constituency
     */
    ConstituencyResponse createConstituency(CreateConstituencyRequest request);

    /**
     * Update an existing constituency by UID
     */
    ConstituencyResponse updateConstituencyByUid(String uid, UpdateConstituencyRequest request);

    /**
     * Get constituency by UID
     */
    ConstituencyResponse getConstituencyByUid(String uid);

    /**
     * Get constituency by code
     */
    ConstituencyResponse getConstituencyByCode(String code);

    /**
     * Get all constituencies with pagination
     */
    Page<ConstituencyResponse> getAllConstituencies(Pageable pageable);

    /**
     * Get all active constituencies with pagination
     */
    Page<ConstituencyResponse> getActiveConstituencies(Pageable pageable);

    /**
     * Get constituencies by district UID with pagination
     */
    Page<ConstituencyResponse> getConstituenciesByDistrictUid(String districtUid, Pageable pageable);

    /**
     * Get active constituencies by district UID with pagination
     */
    Page<ConstituencyResponse> getActiveConstituenciesByDistrictUid(String districtUid, Pageable pageable);

    /**
     * Search constituencies by search term
     */
    Page<ConstituencyResponse> searchConstituencies(String searchTerm, Pageable pageable);

    /**
     * Search constituencies by district UID and search term
     */
    Page<ConstituencyResponse> searchConstituenciesByDistrictUid(String districtUid, String searchTerm, Pageable pageable);

    /**
     * Get constituencies by activity status
     */
    Page<ConstituencyResponse> getConstituenciesByStatus(Boolean isActive, Pageable pageable);

    /**
     * Delete constituency by UID (soft delete)
     */
    void deleteConstituencyByUid(String uid);

    /**
     * Activate/deactivate constituency by UID
     */
    ConstituencyResponse toggleConstituencyStatusByUid(String uid);

    /**
     * Get constituency statistics
     */
    ConstituencyStats getConstituencyStats();

    /**
     * Get constituency statistics by district UID
     */
    ConstituencyStats getConstituencyStatsByDistrictUid(String districtUid);

    /**
     * Inner class for constituency statistics
     */
    class ConstituencyStats {
        private final long totalConstituencies;
        private final long activeConstituencies;
        private final long inactiveConstituencies;

        public ConstituencyStats(long totalConstituencies, long activeConstituencies, long inactiveConstituencies) {
            this.totalConstituencies = totalConstituencies;
            this.activeConstituencies = activeConstituencies;
            this.inactiveConstituencies = inactiveConstituencies;
        }

        public long getTotalConstituencies() { return totalConstituencies; }
        public long getActiveConstituencies() { return activeConstituencies; }
        public long getInactiveConstituencies() { return inactiveConstituencies; }
    }
}
