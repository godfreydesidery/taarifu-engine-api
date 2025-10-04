package com.taarifu_engine_api.modules.location.hamlet.service;

import com.taarifu_engine_api.modules.location.hamlet.domain.dto.CreateHamletRequest;
import com.taarifu_engine_api.modules.location.hamlet.domain.dto.HamletResponse;
import com.taarifu_engine_api.modules.location.hamlet.domain.dto.UpdateHamletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


/**
 * Service interface for Hamlet operations
 */
public interface HamletService {

    /**
     * Create a new hamlet
     */
    HamletResponse createHamlet(CreateHamletRequest request);

    /**
     * Update an existing hamlet by UID
     */
    HamletResponse updateHamletByUid(String uid, UpdateHamletRequest request);

    /**
     * Get hamlet by UID
     */
    HamletResponse getHamletByUid(String uid);

    /**
     * Get hamlet by code
     */
    HamletResponse getHamletByCode(String code);

    /**
     * Get all hamlets with pagination
     */
    Page<HamletResponse> getAllHamlets(Pageable pageable);

    /**
     * Get all active hamlets with pagination
     */
    Page<HamletResponse> getActiveHamlets(Pageable pageable);

    /**
     * Get hamlets by village UID with pagination
     */
    Page<HamletResponse> getHamletsByVillageUid(String villageUid, Pageable pageable);

    /**
     * Get active hamlets by village UID with pagination
     */
    Page<HamletResponse> getActiveHamletsByVillageUid(String villageUid, Pageable pageable);

    /**
     * Search hamlets by search term
     */
    Page<HamletResponse> searchHamlets(String searchTerm, Pageable pageable);

    /**
     * Search hamlets by village UID and search term
     */
    Page<HamletResponse> searchHamletsByVillageUid(String villageUid, String searchTerm, Pageable pageable);

    /**
     * Get hamlets by activity status
     */
    Page<HamletResponse> getHamletsByStatus(Boolean isActive, Pageable pageable);

    /**
     * Delete hamlet by UID (soft delete)
     */
    void deleteHamletByUid(String uid);

    /**
     * Activate/deactivate hamlet by UID
     */
    HamletResponse toggleHamletStatusByUid(String uid);

    /**
     * Get hamlet statistics
     */
    HamletStats getHamletStats();

    /**
     * Get hamlet statistics by village UID
     */
    HamletStats getHamletStatsByVillageUid(String villageUid);

    /**
     * Inner class for hamlet statistics
     */
    class HamletStats {
        private final long totalHamlets;
        private final long activeHamlets;
        private final long inactiveHamlets;

        public HamletStats(long totalHamlets, long activeHamlets, long inactiveHamlets) {
            this.totalHamlets = totalHamlets;
            this.activeHamlets = activeHamlets;
            this.inactiveHamlets = inactiveHamlets;
        }

        public long getTotalHamlets() { return totalHamlets; }
        public long getActiveHamlets() { return activeHamlets; }
        public long getInactiveHamlets() { return inactiveHamlets; }
    }
}
