package com.taarifu_engine_api.modules.location.district.service;

import com.taarifu_engine_api.modules.location.district.domain.dto.CreateDistrictRequest;
import com.taarifu_engine_api.modules.location.district.domain.dto.DistrictResponse;
import com.taarifu_engine_api.modules.location.district.domain.dto.UpdateDistrictRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service interface for District operations
 */
public interface DistrictService {

    /**
     * Create a new district
     */
    DistrictResponse createDistrict(CreateDistrictRequest request);

    /**
     * Update an existing district by ID
     */
    DistrictResponse updateDistrict(Long id, UpdateDistrictRequest request);

    /**
     * Update an existing district by UID
     */
    DistrictResponse updateDistrictByUid(String uid, UpdateDistrictRequest request);

    /**
     * Get district by ID
     */
    DistrictResponse getDistrictById(Long id);

    /**
     * Get district by UID
     */
    DistrictResponse getDistrictByUid(String uid);

    /**
     * Get district by code
     */
    DistrictResponse getDistrictByCode(String code);

    /**
     * Get all districts with pagination
     */
    Page<DistrictResponse> getAllDistricts(Pageable pageable);

    /**
     * Get all active districts
     */
    List<DistrictResponse> getActiveDistricts();

    /**
     * Get all active districts with pagination
     */
    Page<DistrictResponse> getActiveDistricts(Pageable pageable);

    /**
     * Get districts by region ID
     */
    List<DistrictResponse> getDistrictsByRegionId(Long regionId);

    /**
     * Get districts by region ID with pagination
     */
    Page<DistrictResponse> getDistrictsByRegionId(Long regionId, Pageable pageable);

    /**
     * Get districts by region UID
     */
    List<DistrictResponse> getDistrictsByRegionUid(String regionUid);

    /**
     * Get districts by region UID with pagination
     */
    Page<DistrictResponse> getDistrictsByRegionUid(String regionUid, Pageable pageable);

    /**
     * Get active districts by region ID
     */
    List<DistrictResponse> getActiveDistrictsByRegionId(Long regionId);

    /**
     * Get active districts by region ID with pagination
     */
    Page<DistrictResponse> getActiveDistrictsByRegionId(Long regionId, Pageable pageable);

    /**
     * Get active districts by region UID
     */
    List<DistrictResponse> getActiveDistrictsByRegionUid(String regionUid);

    /**
     * Get active districts by region UID with pagination
     */
    Page<DistrictResponse> getActiveDistrictsByRegionUid(String regionUid, Pageable pageable);

    /**
     * Search districts by search term
     */
    Page<DistrictResponse> searchDistricts(String searchTerm, Pageable pageable);

    /**
     * Search districts by region and search term
     */
    Page<DistrictResponse> searchDistrictsByRegion(Long regionId, String searchTerm, Pageable pageable);

    /**
     * Search districts by region UID and search term
     */
    Page<DistrictResponse> searchDistrictsByRegionUid(String regionUid, String searchTerm, Pageable pageable);

    /**
     * Get districts by activity status
     */
    Page<DistrictResponse> getDistrictsByStatus(Boolean isActive, Pageable pageable);

    /**
     * Delete district by ID (soft delete)
     */
    void deleteDistrict(Long id);

    /**
     * Delete district by UID (soft delete)
     */
    void deleteDistrictByUid(String uid);

    /**
     * Activate/deactivate district
     */
    DistrictResponse toggleDistrictStatus(Long id);

    /**
     * Activate/deactivate district by UID
     */
    DistrictResponse toggleDistrictStatusByUid(String uid);

    /**
     * Get district statistics
     */
    DistrictStats getDistrictStats();

    /**
     * Get district statistics by region
     */
    DistrictStats getDistrictStatsByRegion(Long regionId);

    /**
     * Get district statistics by region UID
     */
    DistrictStats getDistrictStatsByRegionUid(String regionUid);

    /**
     * Inner class for district statistics
     */
    class DistrictStats {
        private final long totalDistricts;
        private final long activeDistricts;
        private final long inactiveDistricts;

        public DistrictStats(long totalDistricts, long activeDistricts, long inactiveDistricts) {
            this.totalDistricts = totalDistricts;
            this.activeDistricts = activeDistricts;
            this.inactiveDistricts = inactiveDistricts;
        }

        public long getTotalDistricts() { return totalDistricts; }
        public long getActiveDistricts() { return activeDistricts; }
        public long getInactiveDistricts() { return inactiveDistricts; }
    }
}
