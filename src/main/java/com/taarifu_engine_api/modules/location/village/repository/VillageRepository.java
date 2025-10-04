package com.taarifu_engine_api.modules.location.village.repository;

import com.taarifu_engine_api.modules.location.village.domain.entity.Village;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Village entity
 */
@Repository
public interface VillageRepository extends JpaRepository<Village, Long> {

    /**
     * Find village by UID
     */
    Optional<Village> findByUid(String uid);

    /**
     * Find village by code
     */
    Optional<Village> findByCode(String code);

    /**
     * Find village by name
     */
    Optional<Village> findByName(String name);

    /**
     * Check if village exists by code
     */
    boolean existsByCode(String code);

    /**
     * Check if village exists by name
     */
    boolean existsByName(String name);

    /**
     * Find all active villages
     */
    List<Village> findByIsActiveTrueOrderByNameAsc();

    /**
     * Find all active villages with pagination
     */
    Page<Village> findByIsActiveTrueOrderByNameAsc(Pageable pageable);

    /**
     * Find villages by ward ID
     */
    List<Village> findByWardIdOrderByNameAsc(Long wardId);

    /**
     * Find villages by ward ID with pagination
     */
    Page<Village> findByWardIdOrderByNameAsc(Long wardId, Pageable pageable);

    /**
     * Find active villages by ward ID
     */
    List<Village> findByWardIdAndIsActiveTrueOrderByNameAsc(Long wardId);

    /**
     * Find active villages by ward ID with pagination
     */
    Page<Village> findByWardIdAndIsActiveTrueOrderByNameAsc(Long wardId, Pageable pageable);

    /**
     * Search villages by name, headquarters, or executive officer (case-insensitive)
     */
    @Query("SELECT v FROM Village v WHERE " +
           "LOWER(v.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(v.headquarters) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(v.executiveOfficer) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Village> searchVillages(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Search villages by ward and search term
     */
    @Query("SELECT v FROM Village v WHERE v.ward.id = :wardId AND " +
           "(LOWER(v.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(v.headquarters) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(v.executiveOfficer) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Village> searchVillagesByWard(@Param("wardId") Long wardId, @Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Find villages by activity status
     */
    Page<Village> findByIsActiveOrderByNameAsc(Boolean isActive, Pageable pageable);

    /**
     * Count total villages
     */
    long count();

    /**
     * Count active villages
     */
    long countByIsActiveTrue();

    /**
     * Count villages by ward
     */
    long countByWardId(Long wardId);

    /**
     * Count active villages by ward
     */
    long countByWardIdAndIsActiveTrue(Long wardId);

    /**
     * Find the village with the highest code (for generating next code)
     */
    @Query("SELECT MAX(v.id) FROM Village v")
    Long findMaxId();

    /**
     * Find village by UID with user and ward details (to avoid N+1 queries)
     */
    @Query("SELECT v FROM Village v LEFT JOIN FETCH v.createdBy LEFT JOIN FETCH v.updatedBy LEFT JOIN FETCH v.ward LEFT JOIN FETCH v.ward.district LEFT JOIN FETCH v.ward.district.region WHERE v.uid = :uid")
    Optional<Village> findByUidWithDetails(@Param("uid") String uid);

    /**
     * Find village by ID with user and ward details (to avoid N+1 queries)
     */
    @Query("SELECT v FROM Village v LEFT JOIN FETCH v.createdBy LEFT JOIN FETCH v.updatedBy LEFT JOIN FETCH v.ward LEFT JOIN FETCH v.ward.district LEFT JOIN FETCH v.ward.district.region WHERE v.id = :id")
    Optional<Village> findByIdWithDetails(@Param("id") Long id);

    /**
     * Find all villages with user and ward details (to avoid N+1 queries)
     */
    @Query("SELECT v FROM Village v LEFT JOIN FETCH v.createdBy LEFT JOIN FETCH v.updatedBy LEFT JOIN FETCH v.ward LEFT JOIN FETCH v.ward.district LEFT JOIN FETCH v.ward.district.region ORDER BY v.name ASC")
    List<Village> findAllWithDetails();

    /**
     * Find all active villages with user and ward details (to avoid N+1 queries)
     */
    @Query("SELECT v FROM Village v LEFT JOIN FETCH v.createdBy LEFT JOIN FETCH v.updatedBy LEFT JOIN FETCH v.ward LEFT JOIN FETCH v.ward.district LEFT JOIN FETCH v.ward.district.region WHERE v.isActive = true ORDER BY v.name ASC")
    List<Village> findActiveVillagesWithDetails();

    /**
     * Find villages by ward UID with user and ward details (to avoid N+1 queries)
     */
    @Query("SELECT v FROM Village v LEFT JOIN FETCH v.createdBy LEFT JOIN FETCH v.updatedBy LEFT JOIN FETCH v.ward LEFT JOIN FETCH v.ward.district LEFT JOIN FETCH v.ward.district.region WHERE v.ward.uid = :wardUid ORDER BY v.name ASC")
    List<Village> findByWardUidWithUsersAndWard(@Param("wardUid") String wardUid);

    /**
     * Find villages by ward UID with user and ward details and pagination (to avoid N+1 queries)
     */
    @Query("SELECT v FROM Village v LEFT JOIN FETCH v.createdBy LEFT JOIN FETCH v.updatedBy LEFT JOIN FETCH v.ward LEFT JOIN FETCH v.ward.district LEFT JOIN FETCH v.ward.district.region WHERE v.ward.uid = :wardUid")
    Page<Village> findByWardUidWithUsersAndWard(@Param("wardUid") String wardUid, Pageable pageable);

    /**
     * Find villages by district UID with user and ward details (to avoid N+1 queries)
     */
    @Query("SELECT v FROM Village v LEFT JOIN FETCH v.createdBy LEFT JOIN FETCH v.updatedBy LEFT JOIN FETCH v.ward LEFT JOIN FETCH v.ward.district LEFT JOIN FETCH v.ward.district.region WHERE v.ward.district.uid = :districtUid ORDER BY v.name ASC")
    List<Village> findByDistrictUidWithUsersAndWard(@Param("districtUid") String districtUid);

    /**
     * Find villages by district UID with user and ward details and pagination (to avoid N+1 queries)
     */
    @Query("SELECT v FROM Village v LEFT JOIN FETCH v.createdBy LEFT JOIN FETCH v.updatedBy LEFT JOIN FETCH v.ward LEFT JOIN FETCH v.ward.district LEFT JOIN FETCH v.ward.district.region WHERE v.ward.district.uid = :districtUid")
    Page<Village> findByDistrictUidWithUsersAndWard(@Param("districtUid") String districtUid, Pageable pageable);

    /**
     * Find villages by region UID with user and ward details (to avoid N+1 queries)
     */
    @Query("SELECT v FROM Village v LEFT JOIN FETCH v.createdBy LEFT JOIN FETCH v.updatedBy LEFT JOIN FETCH v.ward LEFT JOIN FETCH v.ward.district LEFT JOIN FETCH v.ward.district.region WHERE v.ward.district.region.uid = :regionUid ORDER BY v.name ASC")
    List<Village> findByRegionUidWithUsersAndWard(@Param("regionUid") String regionUid);

    /**
     * Find villages by region UID with user and ward details and pagination (to avoid N+1 queries)
     */
    @Query("SELECT v FROM Village v LEFT JOIN FETCH v.createdBy LEFT JOIN FETCH v.updatedBy LEFT JOIN FETCH v.ward LEFT JOIN FETCH v.ward.district LEFT JOIN FETCH v.ward.district.region WHERE v.ward.district.region.uid = :regionUid")
    Page<Village> findByRegionUidWithUsersAndWard(@Param("regionUid") String regionUid, Pageable pageable);
}
