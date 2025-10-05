package com.taarifu_engine_api.modules.profile.repository;

import com.taarifu_engine_api.modules.profile.domain.entity.Profile;
import com.taarifu_engine_api.modules.profile.domain.enums.IdType;
import com.taarifu_engine_api.modules.profile.domain.enums.ProfileType;
import com.taarifu_engine_api.modules.userandrole.domain.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Profile operations
 */
@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {

    /**
     * Find profile by UID
     */
    Optional<Profile> findByUid(String uid);

    /**
     * Find profile by user
     */
    Optional<Profile> findByUser(User user);

    /**
     * Find profile by user ID
     */
    Optional<Profile> findByUserId(Long userId);

    /**
     * Find profile by email
     */
    Optional<Profile> findByEmail(String email);

    /**
     * Find profile by phone number
     */
    Optional<Profile> findByPhoneNumber(String phoneNumber);

    /**
     * Find profile by ID type and ID number combination
     */
    Optional<Profile> findByIdTypeAndIdNumber(IdType idType, String idNumber);

    /**
     * Find profile by registration number
     */
    Optional<Profile> findByRegistrationNumber(String registrationNumber);

    /**
     * Check if profile exists by email
     */
    boolean existsByEmail(String email);

    /**
     * Check if profile exists by phone number
     */
    boolean existsByPhoneNumber(String phoneNumber);

    /**
     * Check if profile exists by ID type and ID number combination
     */
    boolean existsByIdTypeAndIdNumber(IdType idType, String idNumber);

    /**
     * Check if profile exists by registration number
     */
    boolean existsByRegistrationNumber(String registrationNumber);

    /**
     * Check if profile exists by user
     */
    boolean existsByUser(User user);

    /**
     * Find all profiles by type
     */
    List<Profile> findByProfileType(ProfileType profileType);

    /**
     * Find all profiles by type with pagination
     */
    Page<Profile> findByProfileType(ProfileType profileType, Pageable pageable);

    /**
     * Find all active profiles
     */
    List<Profile> findByIsActiveTrue();

    /**
     * Find all active profiles with pagination
     */
    Page<Profile> findByIsActiveTrue(Pageable pageable);

    /**
     * Find all active profiles by type
     */
    List<Profile> findByProfileTypeAndIsActiveTrue(ProfileType profileType);

    /**
     * Find all active profiles by type with pagination
     */
    Page<Profile> findByProfileTypeAndIsActiveTrue(ProfileType profileType, Pageable pageable);

    /**
     * Find all profiles ordered by creation date descending
     */
    Page<Profile> findAllByOrderByCreatedAtDesc(Pageable pageable);

    /**
     * Find all active profiles ordered by creation date descending
     */
    Page<Profile> findByIsActiveTrueOrderByCreatedAtDesc(Pageable pageable);

    /**
     * Search profiles by name, email, or phone number
     */
    @Query("SELECT p FROM Profile p WHERE " +
           "LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.phoneNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.idNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Profile> searchProfiles(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Search profiles by type and search term
     */
    @Query("SELECT p FROM Profile p WHERE p.profileType = :profileType AND " +
           "(LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.phoneNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.idNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Profile> searchProfilesByType(@Param("profileType") ProfileType profileType, 
                                      @Param("searchTerm") String searchTerm, 
                                      Pageable pageable);

    /**
     * Find profiles created within a date range
     */
    @Query("SELECT p FROM Profile p WHERE p.createdAt BETWEEN :startDate AND :endDate")
    List<Profile> findProfilesCreatedBetween(@Param("startDate") LocalDateTime startDate, 
                                           @Param("endDate") LocalDateTime endDate);

    /**
     * Find profiles created within a date range with pagination
     */
    @Query("SELECT p FROM Profile p WHERE p.createdAt BETWEEN :startDate AND :endDate")
    Page<Profile> findProfilesCreatedBetween(@Param("startDate") LocalDateTime startDate, 
                                           @Param("endDate") LocalDateTime endDate, 
                                           Pageable pageable);

    /**
     * Count profiles by type
     */
    long countByProfileType(ProfileType profileType);

    /**
     * Count active profiles by type
     */
    long countByProfileTypeAndIsActiveTrue(ProfileType profileType);

    /**
     * Count all active profiles
     */
    long countByIsActiveTrue();

    /**
     * Count all inactive profiles
     */
    long countByIsActiveFalse();

    /**
     * Find profiles that need to be updated (older than specified date)
     */
    @Query("SELECT p FROM Profile p WHERE p.updatedAt < :cutoffDate")
    List<Profile> findProfilesNeedingUpdate(@Param("cutoffDate") LocalDateTime cutoffDate);

    /**
     * Find profiles by user type (through user relationship)
     */
    @Query("SELECT p FROM Profile p JOIN p.user u WHERE u.userType = :userType")
    List<Profile> findByUserType(@Param("userType") String userType);

    /**
     * Find profiles by user type with pagination
     */
    @Query("SELECT p FROM Profile p JOIN p.user u WHERE u.userType = :userType")
    Page<Profile> findByUserType(@Param("userType") String userType, Pageable pageable);
}
