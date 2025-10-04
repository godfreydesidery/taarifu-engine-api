package com.taarifu_engine_api.modules.location.hamlet.repository;

import com.taarifu_engine_api.modules.location.hamlet.domain.entity.Hamlet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Hamlet entity
 */
@Repository
public interface HamletRepository extends JpaRepository<Hamlet, Long> {

    /**
     * Find hamlet by UID
     */
    Optional<Hamlet> findByUid(String uid);

    /**
     * Find hamlet by code
     */
    Optional<Hamlet> findByCode(String code);

    /**
     * Find hamlet by name
     */
    Optional<Hamlet> findByName(String name);

    /**
     * Check if hamlet exists by code
     */
    boolean existsByCode(String code);

    /**
     * Check if hamlet exists by name
     */
    boolean existsByName(String name);

    /**
     * Find all active hamlets
     */
    List<Hamlet> findByIsActiveTrueOrderByNameAsc();

    /**
     * Find all active hamlets with pagination
     */
    Page<Hamlet> findByIsActiveTrueOrderByNameAsc(Pageable pageable);

    /**
     * Find hamlets by village ID
     */
    List<Hamlet> findByVillageIdOrderByNameAsc(Long villageId);

    /**
     * Find hamlets by village ID with pagination
     */
    Page<Hamlet> findByVillageIdOrderByNameAsc(Long villageId, Pageable pageable);

    /**
     * Find active hamlets by village ID
     */
    List<Hamlet> findByVillageIdAndIsActiveTrueOrderByNameAsc(Long villageId);

    /**
     * Find active hamlets by village ID with pagination
     */
    Page<Hamlet> findByVillageIdAndIsActiveTrueOrderByNameAsc(Long villageId, Pageable pageable);

    /**
     * Search hamlets by name, headquarters, or executive officer (case-insensitive)
     */
    @Query("SELECT h FROM Hamlet h WHERE " +
           "LOWER(h.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(h.headquarters) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(h.executiveOfficer) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Hamlet> searchHamlets(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Search hamlets by village and search term
     */
    @Query("SELECT h FROM Hamlet h WHERE h.village.id = :villageId AND " +
           "(LOWER(h.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(h.headquarters) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(h.executiveOfficer) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Hamlet> searchHamletsByVillage(@Param("villageId") Long villageId, @Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Find hamlets by activity status
     */
    Page<Hamlet> findByIsActiveOrderByNameAsc(Boolean isActive, Pageable pageable);

    /**
     * Count total hamlets
     */
    long count();

    /**
     * Count active hamlets
     */
    long countByIsActiveTrue();

    /**
     * Count hamlets by village
     */
    long countByVillageId(Long villageId);

    /**
     * Count active hamlets by village
     */
    long countByVillageIdAndIsActiveTrue(Long villageId);

    /**
     * Find the hamlet with the highest code (for generating next code)
     */
    Optional<Hamlet> findFirstByOrderByCodeDesc();

    /**
     * Find hamlet by UID with user and village details (to avoid N+1 queries)
     */
    @Query("SELECT h FROM Hamlet h LEFT JOIN FETCH h.createdBy LEFT JOIN FETCH h.updatedBy LEFT JOIN FETCH h.village LEFT JOIN FETCH h.village.ward LEFT JOIN FETCH h.village.ward.district LEFT JOIN FETCH h.village.ward.district.region WHERE h.uid = :uid")
    Optional<Hamlet> findByUidWithDetails(@Param("uid") String uid);

    /**
     * Find hamlet by ID with user and village details (to avoid N+1 queries)
     */
    @Query("SELECT h FROM Hamlet h LEFT JOIN FETCH h.createdBy LEFT JOIN FETCH h.updatedBy LEFT JOIN FETCH h.village LEFT JOIN FETCH h.village.ward LEFT JOIN FETCH h.village.ward.district LEFT JOIN FETCH h.village.ward.district.region WHERE h.id = :id")
    Optional<Hamlet> findByIdWithDetails(@Param("id") Long id);

    /**
     * Find all hamlets with user and village details (to avoid N+1 queries)
     */
    @Query("SELECT h FROM Hamlet h LEFT JOIN FETCH h.createdBy LEFT JOIN FETCH h.updatedBy LEFT JOIN FETCH h.village LEFT JOIN FETCH h.village.ward LEFT JOIN FETCH h.village.ward.district LEFT JOIN FETCH h.village.ward.district.region ORDER BY h.name ASC")
    List<Hamlet> findAllWithDetails();

    /**
     * Find all active hamlets with user and village details (to avoid N+1 queries)
     */
    @Query("SELECT h FROM Hamlet h LEFT JOIN FETCH h.createdBy LEFT JOIN FETCH h.updatedBy LEFT JOIN FETCH h.village LEFT JOIN FETCH h.village.ward LEFT JOIN FETCH h.village.ward.district LEFT JOIN FETCH h.village.ward.district.region WHERE h.isActive = true ORDER BY h.name ASC")
    List<Hamlet> findActiveHamletsWithDetails();

    /**
     * Find hamlets by village UID with user and village details (to avoid N+1 queries)
     */
    @Query("SELECT h FROM Hamlet h LEFT JOIN FETCH h.createdBy LEFT JOIN FETCH h.updatedBy LEFT JOIN FETCH h.village LEFT JOIN FETCH h.village.ward LEFT JOIN FETCH h.village.ward.district LEFT JOIN FETCH h.village.ward.district.region WHERE h.village.uid = :villageUid ORDER BY h.name ASC")
    List<Hamlet> findByVillageUidWithUsersAndVillage(@Param("villageUid") String villageUid);

    /**
     * Find hamlets by village UID with user and village details and pagination (to avoid N+1 queries)
     */
    @Query("SELECT h FROM Hamlet h LEFT JOIN FETCH h.createdBy LEFT JOIN FETCH h.updatedBy LEFT JOIN FETCH h.village LEFT JOIN FETCH h.village.ward LEFT JOIN FETCH h.village.ward.district LEFT JOIN FETCH h.village.ward.district.region WHERE h.village.uid = :villageUid")
    Page<Hamlet> findByVillageUidWithUsersAndVillage(@Param("villageUid") String villageUid, Pageable pageable);

    /**
     * Find hamlets by ward UID with user and village details (to avoid N+1 queries)
     */
    @Query("SELECT h FROM Hamlet h LEFT JOIN FETCH h.createdBy LEFT JOIN FETCH h.updatedBy LEFT JOIN FETCH h.village LEFT JOIN FETCH h.village.ward LEFT JOIN FETCH h.village.ward.district LEFT JOIN FETCH h.village.ward.district.region WHERE h.village.ward.uid = :wardUid ORDER BY h.name ASC")
    List<Hamlet> findByWardUidWithUsersAndVillage(@Param("wardUid") String wardUid);

    /**
     * Find hamlets by ward UID with user and village details and pagination (to avoid N+1 queries)
     */
    @Query("SELECT h FROM Hamlet h LEFT JOIN FETCH h.createdBy LEFT JOIN FETCH h.updatedBy LEFT JOIN FETCH h.village LEFT JOIN FETCH h.village.ward LEFT JOIN FETCH h.village.ward.district LEFT JOIN FETCH h.village.ward.district.region WHERE h.village.ward.uid = :wardUid")
    Page<Hamlet> findByWardUidWithUsersAndVillage(@Param("wardUid") String wardUid, Pageable pageable);

    /**
     * Find hamlets by district UID with user and village details (to avoid N+1 queries)
     */
    @Query("SELECT h FROM Hamlet h LEFT JOIN FETCH h.createdBy LEFT JOIN FETCH h.updatedBy LEFT JOIN FETCH h.village LEFT JOIN FETCH h.village.ward LEFT JOIN FETCH h.village.ward.district LEFT JOIN FETCH h.village.ward.district.region WHERE h.village.ward.district.uid = :districtUid ORDER BY h.name ASC")
    List<Hamlet> findByDistrictUidWithUsersAndVillage(@Param("districtUid") String districtUid);

    /**
     * Find hamlets by district UID with user and village details and pagination (to avoid N+1 queries)
     */
    @Query("SELECT h FROM Hamlet h LEFT JOIN FETCH h.createdBy LEFT JOIN FETCH h.updatedBy LEFT JOIN FETCH h.village LEFT JOIN FETCH h.village.ward LEFT JOIN FETCH h.village.ward.district LEFT JOIN FETCH h.village.ward.district.region WHERE h.village.ward.district.uid = :districtUid")
    Page<Hamlet> findByDistrictUidWithUsersAndVillage(@Param("districtUid") String districtUid, Pageable pageable);

    /**
     * Find hamlets by region UID with user and village details (to avoid N+1 queries)
     */
    @Query("SELECT h FROM Hamlet h LEFT JOIN FETCH h.createdBy LEFT JOIN FETCH h.updatedBy LEFT JOIN FETCH h.village LEFT JOIN FETCH h.village.ward LEFT JOIN FETCH h.village.ward.district LEFT JOIN FETCH h.village.ward.district.region WHERE h.village.ward.district.region.uid = :regionUid ORDER BY h.name ASC")
    List<Hamlet> findByRegionUidWithUsersAndVillage(@Param("regionUid") String regionUid);

    /**
     * Find hamlets by region UID with user and village details and pagination (to avoid N+1 queries)
     */
    @Query("SELECT h FROM Hamlet h LEFT JOIN FETCH h.createdBy LEFT JOIN FETCH h.updatedBy LEFT JOIN FETCH h.village LEFT JOIN FETCH h.village.ward LEFT JOIN FETCH h.village.ward.district LEFT JOIN FETCH h.village.ward.district.region WHERE h.village.ward.district.region.uid = :regionUid")
    Page<Hamlet> findByRegionUidWithUsersAndVillage(@Param("regionUid") String regionUid, Pageable pageable);
}
