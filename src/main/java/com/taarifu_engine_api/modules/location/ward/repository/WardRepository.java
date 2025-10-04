package com.taarifu_engine_api.modules.location.ward.repository;

import com.taarifu_engine_api.modules.location.ward.domain.entity.Ward;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Ward entity
 */
@Repository
public interface WardRepository extends JpaRepository<Ward, Long> {

    /**
     * Find ward by UID
     */
    Optional<Ward> findByUid(String uid);

    /**
     * Find ward by code
     */
    Optional<Ward> findByCode(String code);

    /**
     * Find ward by name
     */
    Optional<Ward> findByName(String name);

    /**
     * Check if ward exists by code
     */
    boolean existsByCode(String code);

    /**
     * Check if ward exists by name
     */
    boolean existsByName(String name);

    /**
     * Find all active wards
     */
    List<Ward> findByIsActiveTrueOrderByNameAsc();

    /**
     * Find all active wards with pagination
     */
    Page<Ward> findByIsActiveTrueOrderByNameAsc(Pageable pageable);

    /**
     * Find wards by district ID
     */
    List<Ward> findByDistrictIdOrderByNameAsc(Long districtId);

    /**
     * Find wards by district ID with pagination
     */
    Page<Ward> findByDistrictIdOrderByNameAsc(Long districtId, Pageable pageable);

    /**
     * Find active wards by district ID
     */
    List<Ward> findByDistrictIdAndIsActiveTrueOrderByNameAsc(Long districtId);

    /**
     * Find active wards by district ID with pagination
     */
    Page<Ward> findByDistrictIdAndIsActiveTrueOrderByNameAsc(Long districtId, Pageable pageable);

    /**
     * Search wards by name, headquarters, or executive officer (case-insensitive)
     */
    @Query("SELECT w FROM Ward w WHERE " +
           "LOWER(w.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(w.headquarters) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(w.executiveOfficer) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Ward> searchWards(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Search wards by district and search term
     */
    @Query("SELECT w FROM Ward w WHERE w.district.id = :districtId AND " +
           "(LOWER(w.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(w.headquarters) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(w.executiveOfficer) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Ward> searchWardsByDistrict(@Param("districtId") Long districtId, @Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Find wards by activity status
     */
    Page<Ward> findByIsActiveOrderByNameAsc(Boolean isActive, Pageable pageable);

    /**
     * Count total wards
     */
    long count();

    /**
     * Count active wards
     */
    long countByIsActiveTrue();

    /**
     * Count wards by district
     */
    long countByDistrictId(Long districtId);

    /**
     * Count active wards by district
     */
    long countByDistrictIdAndIsActiveTrue(Long districtId);

    /**
     * Find the ward with the highest code (for generating next code)
     */
    Optional<Ward> findFirstByOrderByCodeDesc();

    /**
     * Find ward by UID with user and district details (to avoid N+1 queries)
     */
    @Query("SELECT w FROM Ward w LEFT JOIN FETCH w.createdBy LEFT JOIN FETCH w.updatedBy LEFT JOIN FETCH w.district LEFT JOIN FETCH w.district.region WHERE w.uid = :uid")
    Optional<Ward> findByUidWithDetails(@Param("uid") String uid);

    /**
     * Find ward by ID with user and district details (to avoid N+1 queries)
     */
    @Query("SELECT w FROM Ward w LEFT JOIN FETCH w.createdBy LEFT JOIN FETCH w.updatedBy LEFT JOIN FETCH w.district LEFT JOIN FETCH w.district.region WHERE w.id = :id")
    Optional<Ward> findByIdWithDetails(@Param("id") Long id);

    /**
     * Find all wards with user and district details (to avoid N+1 queries)
     */
    @Query("SELECT w FROM Ward w LEFT JOIN FETCH w.createdBy LEFT JOIN FETCH w.updatedBy LEFT JOIN FETCH w.district LEFT JOIN FETCH w.district.region ORDER BY w.name ASC")
    List<Ward> findAllWithDetails();

    /**
     * Find all active wards with user and district details (to avoid N+1 queries)
     */
    @Query("SELECT w FROM Ward w LEFT JOIN FETCH w.createdBy LEFT JOIN FETCH w.updatedBy LEFT JOIN FETCH w.district LEFT JOIN FETCH w.district.region WHERE w.isActive = true ORDER BY w.name ASC")
    List<Ward> findActiveWardsWithDetails();

    /**
     * Find wards by district UID with user and district details (to avoid N+1 queries)
     */
    @Query("SELECT w FROM Ward w LEFT JOIN FETCH w.createdBy LEFT JOIN FETCH w.updatedBy LEFT JOIN FETCH w.district LEFT JOIN FETCH w.district.region WHERE w.district.uid = :districtUid ORDER BY w.name ASC")
    List<Ward> findByDistrictUidWithUsersAndDistrict(@Param("districtUid") String districtUid);

    /**
     * Find wards by district UID with user and district details and pagination (to avoid N+1 queries)
     */
    @Query("SELECT w FROM Ward w LEFT JOIN FETCH w.createdBy LEFT JOIN FETCH w.updatedBy LEFT JOIN FETCH w.district LEFT JOIN FETCH w.district.region WHERE w.district.uid = :districtUid")
    Page<Ward> findByDistrictUidWithUsersAndDistrict(@Param("districtUid") String districtUid, Pageable pageable);

    /**
     * Find wards by region UID with user and district details (to avoid N+1 queries)
     */
    @Query("SELECT w FROM Ward w LEFT JOIN FETCH w.createdBy LEFT JOIN FETCH w.updatedBy LEFT JOIN FETCH w.district LEFT JOIN FETCH w.district.region WHERE w.district.region.uid = :regionUid ORDER BY w.name ASC")
    List<Ward> findByRegionUidWithUsersAndDistrict(@Param("regionUid") String regionUid);

    /**
     * Find wards by region UID with user and district details and pagination (to avoid N+1 queries)
     */
    @Query("SELECT w FROM Ward w LEFT JOIN FETCH w.createdBy LEFT JOIN FETCH w.updatedBy LEFT JOIN FETCH w.district LEFT JOIN FETCH w.district.region WHERE w.district.region.uid = :regionUid")
    Page<Ward> findByRegionUidWithUsersAndDistrict(@Param("regionUid") String regionUid, Pageable pageable);
}
