package com.taarifu_engine_api.modules.location.constituency.repository;

import com.taarifu_engine_api.modules.location.constituency.domain.entity.Constituency;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Constituency entity
 */
@Repository
public interface ConstituencyRepository extends JpaRepository<Constituency, Long> {

    /**
     * Find constituency by UID
     */
    Optional<Constituency> findByUid(String uid);

    /**
     * Find constituency by code
     */
    Optional<Constituency> findByCode(String code);

    /**
     * Find constituency by name
     */
    Optional<Constituency> findByName(String name);

    /**
     * Check if constituency exists by code
     */
    boolean existsByCode(String code);

    /**
     * Check if constituency exists by name
     */
    boolean existsByName(String name);

    /**
     * Find all active constituencies
     */
    List<Constituency> findByIsActiveTrueOrderByNameAsc();

    /**
     * Find all active constituencies with pagination
     */
    Page<Constituency> findByIsActiveTrueOrderByNameAsc(Pageable pageable);

    /**
     * Find constituencies by district ID
     */
    List<Constituency> findByDistrictIdOrderByNameAsc(Long districtId);

    /**
     * Find constituencies by district ID with pagination
     */
    Page<Constituency> findByDistrictIdOrderByNameAsc(Long districtId, Pageable pageable);

    /**
     * Find active constituencies by district ID
     */
    List<Constituency> findByDistrictIdAndIsActiveTrueOrderByNameAsc(Long districtId);

    /**
     * Find active constituencies by district ID with pagination
     */
    Page<Constituency> findByDistrictIdAndIsActiveTrueOrderByNameAsc(Long districtId, Pageable pageable);

    /**
     * Search constituencies by name or headquarters (case-insensitive)
     */
    @Query("SELECT c FROM Constituency c WHERE " +
           "LOWER(c.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(c.headquarters) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Constituency> searchConstituencies(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Search constituencies by district and search term
     */
    @Query("SELECT c FROM Constituency c WHERE c.district.id = :districtId AND " +
           "(LOWER(c.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(c.headquarters) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Constituency> searchConstituenciesByDistrict(@Param("districtId") Long districtId, @Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Find constituencies by activity status
     */
    Page<Constituency> findByIsActiveOrderByNameAsc(Boolean isActive, Pageable pageable);

    /**
     * Count total constituencies
     */
    long count();

    /**
     * Count active constituencies
     */
    long countByIsActiveTrue();

    /**
     * Count constituencies by district
     */
    long countByDistrictId(Long districtId);

    /**
     * Count active constituencies by district
     */
    long countByDistrictIdAndIsActiveTrue(Long districtId);

    /**
     * Find the constituency with the highest code (for generating next code)
     */
    Optional<Constituency> findTopByOrderByCodeDesc();

    /**
     * Find constituency by UID with user and district details (to avoid N+1 queries)
     */
    @Query("SELECT c FROM Constituency c LEFT JOIN FETCH c.createdBy LEFT JOIN FETCH c.updatedBy LEFT JOIN FETCH c.district LEFT JOIN FETCH c.district.region WHERE c.uid = :uid")
    Optional<Constituency> findByUidWithDetails(@Param("uid") String uid);

    /**
     * Find constituency by ID with user and district details (to avoid N+1 queries)
     */
    @Query("SELECT c FROM Constituency c LEFT JOIN FETCH c.createdBy LEFT JOIN FETCH c.updatedBy LEFT JOIN FETCH c.district LEFT JOIN FETCH c.district.region WHERE c.id = :id")
    Optional<Constituency> findByIdWithDetails(@Param("id") Long id);

    /**
     * Find all constituencies with user and district details (to avoid N+1 queries)
     */
    @Query("SELECT c FROM Constituency c LEFT JOIN FETCH c.createdBy LEFT JOIN FETCH c.updatedBy LEFT JOIN FETCH c.district LEFT JOIN FETCH c.district.region ORDER BY c.name ASC")
    List<Constituency> findAllWithDetails();

    /**
     * Find all active constituencies with user and district details (to avoid N+1 queries)
     */
    @Query("SELECT c FROM Constituency c LEFT JOIN FETCH c.createdBy LEFT JOIN FETCH c.updatedBy LEFT JOIN FETCH c.district LEFT JOIN FETCH c.district.region WHERE c.isActive = true ORDER BY c.name ASC")
    List<Constituency> findActiveConstituenciesWithDetails();

    /**
     * Find constituencies by district UID with user and district details (to avoid N+1 queries)
     */
    @Query("SELECT c FROM Constituency c LEFT JOIN FETCH c.createdBy LEFT JOIN FETCH c.updatedBy LEFT JOIN FETCH c.district LEFT JOIN FETCH c.district.region WHERE c.district.uid = :districtUid ORDER BY c.name ASC")
    List<Constituency> findByDistrictUidWithUsersAndDistrict(@Param("districtUid") String districtUid);

    /**
     * Find constituencies by district UID with user and district details and pagination (to avoid N+1 queries)
     */
    @Query("SELECT c FROM Constituency c LEFT JOIN FETCH c.createdBy LEFT JOIN FETCH c.updatedBy LEFT JOIN FETCH c.district LEFT JOIN FETCH c.district.region WHERE c.district.uid = :districtUid")
    Page<Constituency> findByDistrictUidWithUsersAndDistrict(@Param("districtUid") String districtUid, Pageable pageable);

    /**
     * Find constituencies by region UID with user and district details (to avoid N+1 queries)
     */
    @Query("SELECT c FROM Constituency c LEFT JOIN FETCH c.createdBy LEFT JOIN FETCH c.updatedBy LEFT JOIN FETCH c.district LEFT JOIN FETCH c.district.region WHERE c.district.region.uid = :regionUid ORDER BY c.name ASC")
    List<Constituency> findByRegionUidWithUsersAndDistrict(@Param("regionUid") String regionUid);

    /**
     * Find constituencies by region UID with user and district details and pagination (to avoid N+1 queries)
     */
    @Query("SELECT c FROM Constituency c LEFT JOIN FETCH c.createdBy LEFT JOIN FETCH c.updatedBy LEFT JOIN FETCH c.district LEFT JOIN FETCH c.district.region WHERE c.district.region.uid = :regionUid")
    Page<Constituency> findByRegionUidWithUsersAndDistrict(@Param("regionUid") String regionUid, Pageable pageable);
}
