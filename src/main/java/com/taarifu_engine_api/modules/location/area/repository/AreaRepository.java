package com.taarifu_engine_api.modules.location.area.repository;

import com.taarifu_engine_api.modules.common.domain.enums.AreaType;
import com.taarifu_engine_api.modules.location.area.domain.entity.Area;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for Area entity operations
 */
@Repository
public interface AreaRepository extends JpaRepository<Area, Long> {

    /**
     * Find area by UID
     */
    Optional<Area> findByUid(String uid);

    /**
     * Find area by code
     */
    Optional<Area> findByCode(String code);

    /**
     * Find area by area type and area ID
     */
    Optional<Area> findByAreaTypeAndAreaId(AreaType areaType, Long areaId);

    /**
     * Check if area exists by code
     */
    boolean existsByCode(String code);

    /**
     * Check if area exists by area type and area ID
     */
    boolean existsByAreaTypeAndAreaId(AreaType areaType, Long areaId);

    /**
     * Find areas by area type
     */
    Page<Area> findByAreaTypeOrderByCodeAsc(AreaType areaType, Pageable pageable);

    /**
     * Find areas by area type and area ID
     */
    Page<Area> findByAreaTypeAndAreaIdOrderByCodeAsc(AreaType areaType, Long areaId, Pageable pageable);

    /**
     * Search areas by code (case-insensitive)
     */
    @Query("SELECT a FROM Area a WHERE LOWER(a.code) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Area> searchAreas(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Search areas by area type and search term
     */
    @Query("SELECT a FROM Area a WHERE a.areaType = :areaType AND " +
           "LOWER(a.code) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Area> searchAreasByType(@Param("areaType") AreaType areaType, @Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Count areas by area type
     */
    long countByAreaType(AreaType areaType);

    /**
     * Find the area with the highest code number for generating next code
     */
    @Query("SELECT a FROM Area a ORDER BY a.code DESC")
    Optional<Area> findTopByOrderByCodeDesc();

    /**
     * Find areas by area type with pagination
     */
    Page<Area> findByAreaType(AreaType areaType, Pageable pageable);

    /**
     * Find all areas with pagination
     */
    Page<Area> findAllByOrderByCodeAsc(Pageable pageable);
}
