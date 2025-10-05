package com.taarifu_engine_api.modules.citizen.repository;

import com.taarifu_engine_api.modules.citizen.domain.entity.Citizen;
import com.taarifu_engine_api.modules.profile.domain.entity.Profile;
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
 * Repository interface for Citizen operations
 */
@Repository
public interface CitizenRepository extends JpaRepository<Citizen, Long> {

    /**
     * Find citizen by profile
     */
    Optional<Citizen> findByProfile(Profile profile);

    /**
     * Find citizen by profile ID
     */
    Optional<Citizen> findByProfileId(Long profileId);

    /**
     * Find citizen by profile UID
     */
    @Query("SELECT c FROM Citizen c JOIN c.profile p WHERE p.uid = :profileUid")
    Optional<Citizen> findByProfileUid(@Param("profileUid") String profileUid);

    /**
     * Check if citizen exists for profile
     */
    boolean existsByProfile(Profile profile);

    /**
     * Check if citizen exists for profile ID
     */
    boolean existsByProfileId(Long profileId);

    /**
     * Find all citizens by citizenship type
     */
    List<Citizen> findByCitizenshipType(String citizenshipType);

    /**
     * Find all citizens by citizenship type with pagination
     */
    Page<Citizen> findByCitizenshipType(String citizenshipType, Pageable pageable);

    /**
     * Find all citizens by citizenship status
     */
    List<Citizen> findByCitizenshipStatus(String citizenshipStatus);

    /**
     * Find all citizens by citizenship status with pagination
     */
    Page<Citizen> findByCitizenshipStatus(String citizenshipStatus, Pageable pageable);

    /**
     * Find citizens by citizenship type and status
     */
    List<Citizen> findByCitizenshipTypeAndCitizenshipStatus(String citizenshipType, String citizenshipStatus);

    /**
     * Find citizens by citizenship type and status with pagination
     */
    Page<Citizen> findByCitizenshipTypeAndCitizenshipStatus(String citizenshipType, String citizenshipStatus, Pageable pageable);

    /**
     * Find citizens who became citizens within a date range
     */
    @Query("SELECT c FROM Citizen c WHERE c.dateOfCitizenship BETWEEN :startDate AND :endDate")
    List<Citizen> findCitizensByDateRange(@Param("startDate") LocalDateTime startDate, 
                                         @Param("endDate") LocalDateTime endDate);

    /**
     * Find citizens who became citizens within a date range with pagination
     */
    @Query("SELECT c FROM Citizen c WHERE c.dateOfCitizenship BETWEEN :startDate AND :endDate")
    Page<Citizen> findCitizensByDateRange(@Param("startDate") LocalDateTime startDate, 
                                         @Param("endDate") LocalDateTime endDate, 
                                         Pageable pageable);

    /**
     * Search citizens by profile name, email, or ID number
     */
    @Query("SELECT c FROM Citizen c JOIN c.profile p WHERE " +
           "LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.idNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Citizen> searchCitizens(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Find all citizens ordered by citizenship date descending
     */
    @Query("SELECT c FROM Citizen c ORDER BY c.dateOfCitizenship DESC")
    Page<Citizen> findAllByOrderByDateOfCitizenshipDesc(Pageable pageable);

    /**
     * Find citizens by profile type (PERSON only)
     */
    @Query("SELECT c FROM Citizen c JOIN c.profile p WHERE p.profileType = 'PERSON'")
    List<Citizen> findByProfileTypePerson();

    /**
     * Find citizens by profile type (PERSON only) with pagination
     */
    @Query("SELECT c FROM Citizen c JOIN c.profile p WHERE p.profileType = 'PERSON'")
    Page<Citizen> findByProfileTypePerson(Pageable pageable);

    /**
     * Count citizens by citizenship type
     */
    long countByCitizenshipType(String citizenshipType);

    /**
     * Count citizens by citizenship status
     */
    long countByCitizenshipStatus(String citizenshipStatus);

    /**
     * Count citizens by citizenship type and status
     */
    long countByCitizenshipTypeAndCitizenshipStatus(String citizenshipType, String citizenshipStatus);

    /**
     * Count total citizens
     */
    @Query("SELECT COUNT(c) FROM Citizen c")
    long countTotalCitizens();

    /**
     * Find citizens created within a date range
     */
    @Query("SELECT c FROM Citizen c WHERE c.dateOfCitizenship >= :startDate AND c.dateOfCitizenship <= :endDate")
    List<Citizen> findCitizensCreatedBetween(@Param("startDate") LocalDateTime startDate, 
                                           @Param("endDate") LocalDateTime endDate);

    /**
     * Find the most recent citizens
     */
    @Query("SELECT c FROM Citizen c ORDER BY c.dateOfCitizenship DESC")
    List<Citizen> findMostRecentCitizens(@Param("limit") int limit);
}
