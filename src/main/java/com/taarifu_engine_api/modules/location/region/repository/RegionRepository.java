package com.taarifu_engine_api.modules.location.region.repository;

import com.taarifu_engine_api.modules.location.region.domain.entity.Region;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Region entity
 */
@Repository
public interface RegionRepository extends JpaRepository<Region, Long> {

    /**
     * Find region by UID
     */
    Optional<Region> findByUid(String uid);

    /**
     * Find region by code
     */
    Optional<Region> findByCode(String code);

    /**
     * Find region by name
     */
    Optional<Region> findByName(String name);

    /**
     * Check if region exists by code
     */
    boolean existsByCode(String code);

    /**
     * Check if region exists by name
     */
    boolean existsByName(String name);

    /**
     * Find all active regions
     */
    List<Region> findByIsActiveTrueOrderByNameAsc();

    /**
     * Find all active regions with pagination
     */
    Page<Region> findByIsActiveTrueOrderByNameAsc(Pageable pageable);

    /**
     * Search regions by name containing the given text (case-insensitive)
     */
    @Query("SELECT r FROM Region r WHERE " +
           "LOWER(r.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(r.capital) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(r.commissioner) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Region> searchRegions(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Find regions by activity status
     */
    Page<Region> findByIsActiveOrderByNameAsc(Boolean isActive, Pageable pageable);

    /**
     * Count total regions
     */
    long count();

    /**
     * Count active regions
     */
    long countByIsActiveTrue();

    /**
     * Find regions by capital city
     */
    List<Region> findByCapitalContainingIgnoreCase(String capital);

    /**
     * Find the region with the highest code (for generating next code)
     */
    Optional<Region> findTopByOrderByCodeDesc();

    /**
     * Find region by UID with user details (to avoid N+1 queries)
     */
    @Query("SELECT r FROM Region r LEFT JOIN FETCH r.createdBy LEFT JOIN FETCH r.updatedBy WHERE r.uid = :uid")
    Optional<Region> findByUidWithUsers(@Param("uid") String uid);

    /**
     * Find region by ID with user details (to avoid N+1 queries)
     */
    @Query("SELECT r FROM Region r LEFT JOIN FETCH r.createdBy LEFT JOIN FETCH r.updatedBy WHERE r.id = :id")
    Optional<Region> findByIdWithUsers(@Param("id") Long id);

    /**
     * Find all regions with user details (to avoid N+1 queries)
     */
    @Query("SELECT r FROM Region r LEFT JOIN FETCH r.createdBy LEFT JOIN FETCH r.updatedBy ORDER BY r.name ASC")
    List<Region> findAllWithUsers();

    /**
     * Find all active regions with user details (to avoid N+1 queries)
     */
    @Query("SELECT r FROM Region r LEFT JOIN FETCH r.createdBy LEFT JOIN FETCH r.updatedBy WHERE r.isActive = true ORDER BY r.name ASC")
    List<Region> findActiveRegionsWithUsers();
}
