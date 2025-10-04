package com.taarifu_engine_api.modules.politicalparty.repository;

import com.taarifu_engine_api.modules.politicalparty.domain.entity.PoliticalParty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Political Party operations
 */
@Repository
public interface PoliticalPartyRepository extends JpaRepository<PoliticalParty, Long> {

    /**
     * Find political party by UID
     */
    Optional<PoliticalParty> findByUid(String uid);

    /**
     * Find political party by code
     */
    Optional<PoliticalParty> findByCode(String code);

    /**
     * Find political party by name
     */
    Optional<PoliticalParty> findByName(String name);

    /**
     * Find political party by abbreviation
     */
    Optional<PoliticalParty> findByAbbreviation(String abbreviation);

    /**
     * Check if political party exists by name
     */
    boolean existsByName(String name);

    /**
     * Check if political party exists by abbreviation
     */
    boolean existsByAbbreviation(String abbreviation);

    /**
     * Check if political party exists by code
     */
    boolean existsByCode(String code);

    /**
     * Find all active political parties
     */
    List<PoliticalParty> findByIsActiveTrueOrderByNameAsc();

    /**
     * Find all registered political parties
     */
    List<PoliticalParty> findByIsRegisteredTrueOrderByNameAsc();

    /**
     * Find all operational political parties (registered and active)
     */
    @Query("SELECT p FROM PoliticalParty p WHERE p.isRegistered = true AND p.isActive = true ORDER BY p.name ASC")
    List<PoliticalParty> findOperationalParties();

    /**
     * Find all political parties ordered by name
     */
    Page<PoliticalParty> findAllByOrderByNameAsc(Pageable pageable);

    /**
     * Find all active political parties with pagination
     */
    Page<PoliticalParty> findByIsActiveTrueOrderByNameAsc(Pageable pageable);

    /**
     * Find all registered political parties with pagination
     */
    Page<PoliticalParty> findByIsRegisteredTrueOrderByNameAsc(Pageable pageable);

    /**
     * Find political parties by founding date range
     */
    @Query("SELECT p FROM PoliticalParty p WHERE p.foundingDate BETWEEN :startDate AND :endDate ORDER BY p.foundingDate ASC")
    List<PoliticalParty> findByFoundingDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    /**
     * Find political parties founded in a specific year
     */
    @Query("SELECT p FROM PoliticalParty p WHERE YEAR(p.foundingDate) = :year ORDER BY p.foundingDate ASC")
    List<PoliticalParty> findByFoundingYear(@Param("year") int year);

    /**
     * Find political parties by ideology
     */
    @Query("SELECT p FROM PoliticalParty p WHERE LOWER(p.ideology) LIKE LOWER(CONCAT('%', :ideology, '%')) ORDER BY p.name ASC")
    List<PoliticalParty> findByIdeologyContaining(@Param("ideology") String ideology);

    /**
     * Search political parties by name, abbreviation, or description
     */
    @Query("SELECT p FROM PoliticalParty p WHERE " +
           "LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.abbreviation) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.ideology) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<PoliticalParty> searchPoliticalParties(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Find the political party with the highest code (for sequence generation)
     */
    Optional<PoliticalParty> findFirstByOrderByCodeDesc();

    /**
     * Count political parties by status
     */
    long countByIsActiveTrue();
    long countByIsActiveFalse();
    long countByIsRegisteredTrue();
    long countByIsRegisteredFalse();

    /**
     * Count political parties by founding year
     */
    @Query("SELECT COUNT(p) FROM PoliticalParty p WHERE YEAR(p.foundingDate) = :year")
    long countByFoundingYear(@Param("year") int year);

    /**
     * Find political parties with member count above threshold
     */
    @Query("SELECT p FROM PoliticalParty p WHERE p.memberCount >= :threshold ORDER BY p.memberCount DESC")
    List<PoliticalParty> findByMemberCountAbove(@Param("threshold") Long threshold);

    /**
     * Find political parties by registration date range
     */
    @Query("SELECT p FROM PoliticalParty p WHERE p.registrationDate BETWEEN :startDate AND :endDate ORDER BY p.registrationDate ASC")
    List<PoliticalParty> findByRegistrationDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    /**
     * Find political parties by founding location
     */
    @Query("SELECT p FROM PoliticalParty p WHERE LOWER(p.foundingLocation) LIKE LOWER(CONCAT('%', :location, '%')) ORDER BY p.name ASC")
    List<PoliticalParty> findByFoundingLocationContaining(@Param("location") String location);

    /**
     * Find political parties with website
     */
    @Query("SELECT p FROM PoliticalParty p WHERE p.websiteUrl IS NOT NULL AND p.websiteUrl != '' ORDER BY p.name ASC")
    List<PoliticalParty> findPartiesWithWebsite();

    /**
     * Find political parties by colors
     */
    @Query("SELECT p FROM PoliticalParty p WHERE LOWER(p.colors) LIKE LOWER(CONCAT('%', :color, '%')) ORDER BY p.name ASC")
    List<PoliticalParty> findByColorsContaining(@Param("color") String color);
}
