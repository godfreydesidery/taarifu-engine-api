package com.taarifu_engine_api.modules.location.district.repository;

import com.taarifu_engine_api.modules.location.district.domain.entity.District;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for District entity
 */
@Repository
public interface DistrictRepository extends JpaRepository<District, Long> {

    /**
     * Find district by UID
     */
    Optional<District> findByUid(String uid);

    /**
     * Find district by code
     */
    Optional<District> findByCode(String code);

    /**
     * Find district by name
     */
    Optional<District> findByName(String name);

    /**
     * Check if district exists by code
     */
    boolean existsByCode(String code);

    /**
     * Check if district exists by name
     */
    boolean existsByName(String name);

    /**
     * Find all active districts
     */
    List<District> findByIsActiveTrueOrderByNameAsc();

    /**
     * Find all active districts with pagination
     */
    Page<District> findByIsActiveTrueOrderByNameAsc(Pageable pageable);

    /**
     * Find districts by region ID
     */
    List<District> findByRegionIdOrderByNameAsc(Long regionId);

    /**
     * Find districts by region ID with pagination
     */
    Page<District> findByRegionIdOrderByNameAsc(Long regionId, Pageable pageable);

    /**
     * Find active districts by region ID
     */
    List<District> findByRegionIdAndIsActiveTrueOrderByNameAsc(Long regionId);

    /**
     * Find active districts by region ID with pagination
     */
    Page<District> findByRegionIdAndIsActiveTrueOrderByNameAsc(Long regionId, Pageable pageable);

    /**
     * Search districts by name, headquarters, or commissioner (case-insensitive)
     */
    @Query("SELECT d FROM District d WHERE " +
           "LOWER(d.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(d.headquarters) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(d.commissioner) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<District> searchDistricts(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Search districts by region and search term
     */
    @Query("SELECT d FROM District d WHERE d.region.id = :regionId AND " +
           "(LOWER(d.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(d.headquarters) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(d.commissioner) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<District> searchDistrictsByRegion(@Param("regionId") Long regionId, @Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Find districts by activity status
     */
    Page<District> findByIsActiveOrderByNameAsc(Boolean isActive, Pageable pageable);

    /**
     * Count total districts
     */
    long count();

    /**
     * Count active districts
     */
    long countByIsActiveTrue();

    /**
     * Count districts by region
     */
    long countByRegionId(Long regionId);

    /**
     * Count active districts by region
     */
    long countByRegionIdAndIsActiveTrue(Long regionId);

    /**
     * Find the district with the highest code (for generating next code)
     */
    Optional<District> findTopByOrderByCodeDesc();

    /**
     * Find district by UID with user and region details (to avoid N+1 queries)
     */
    @Query("SELECT d FROM District d LEFT JOIN FETCH d.createdBy LEFT JOIN FETCH d.updatedBy LEFT JOIN FETCH d.region WHERE d.uid = :uid")
    Optional<District> findByUidWithDetails(@Param("uid") String uid);

    /**
     * Find district by ID with user and region details (to avoid N+1 queries)
     */
    @Query("SELECT d FROM District d LEFT JOIN FETCH d.createdBy LEFT JOIN FETCH d.updatedBy LEFT JOIN FETCH d.region WHERE d.id = :id")
    Optional<District> findByIdWithDetails(@Param("id") Long id);

    /**
     * Find all districts with user and region details (to avoid N+1 queries)
     */
    @Query("SELECT d FROM District d LEFT JOIN FETCH d.createdBy LEFT JOIN FETCH d.updatedBy LEFT JOIN FETCH d.region ORDER BY d.name ASC")
    List<District> findAllWithDetails();

    /**
     * Find all active districts with user and region details (to avoid N+1 queries)
     */
    @Query("SELECT d FROM District d LEFT JOIN FETCH d.createdBy LEFT JOIN FETCH d.updatedBy LEFT JOIN FETCH d.region WHERE d.isActive = true ORDER BY d.name ASC")
    List<District> findActiveDistrictsWithDetails();

    /**
     * Find districts by region UID with user and region details (to avoid N+1 queries)
     */
    @Query("SELECT d FROM District d LEFT JOIN FETCH d.createdBy LEFT JOIN FETCH d.updatedBy LEFT JOIN FETCH d.region WHERE d.region.uid = :regionUid ORDER BY d.name ASC")
    List<District> findByRegionUidWithUsersAndRegion(@Param("regionUid") String regionUid);

    /**
     * Find districts by region UID with user and region details and pagination (to avoid N+1 queries)
     */
    @Query("SELECT d FROM District d LEFT JOIN FETCH d.createdBy LEFT JOIN FETCH d.updatedBy LEFT JOIN FETCH d.region WHERE d.region.uid = :regionUid")
    Page<District> findByRegionUidWithUsersAndRegion(@Param("regionUid") String regionUid, Pageable pageable);
}
