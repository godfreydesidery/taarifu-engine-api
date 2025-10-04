package com.taarifu_engine_api.modules.parliament.repository;

import com.taarifu_engine_api.modules.parliament.domain.entity.Parliament;
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
 * Repository interface for Parliament operations
 */
@Repository
public interface ParliamentRepository extends JpaRepository<Parliament, Long> {

    /**
     * Find parliament by UID
     */
    Optional<Parliament> findByUid(String uid);

    /**
     * Find parliament by code
     */
    Optional<Parliament> findByCode(String code);

    /**
     * Find parliament by name
     */
    Optional<Parliament> findByName(String name);

    /**
     * Check if parliament exists by name
     */
    boolean existsByName(String name);

    /**
     * Check if parliament exists by code
     */
    boolean existsByCode(String code);

    /**
     * Find the current parliament
     */
    Optional<Parliament> findByIsCurrentTrue();

    /**
     * Find all active parliaments
     */
    List<Parliament> findByIsActiveTrueOrderByStartDateDesc();

    /**
     * Find all parliaments ordered by start date descending
     */
    Page<Parliament> findAllByOrderByStartDateDesc(Pageable pageable);

    /**
     * Find all active parliaments with pagination
     */
    Page<Parliament> findByIsActiveTrueOrderByStartDateDesc(Pageable pageable);

    /**
     * Find parliaments by date range
     */
    @Query("SELECT p FROM Parliament p WHERE p.startDate <= :date AND p.endDate >= :date")
    List<Parliament> findParliamentsInSession(@Param("date") LocalDate date);

    /**
     * Find parliament that was active on a specific date
     */
    @Query("SELECT p FROM Parliament p WHERE p.startDate <= :date AND p.endDate >= :date AND p.isActive = true")
    Optional<Parliament> findActiveParliamentOnDate(@Param("date") LocalDate date);

    /**
     * Search parliaments by name or description
     */
    @Query("SELECT p FROM Parliament p WHERE " +
           "LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Parliament> searchParliaments(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Find the parliament with the highest code (for sequence generation)
     */
    Optional<Parliament> findFirstByOrderByCodeDesc();

    /**
     * Count parliaments by status
     */
    long countByIsActiveTrue();
    long countByIsActiveFalse();
    long countByIsCurrentTrue();

    /**
     * Find parliaments that have ended
     */
    @Query("SELECT p FROM Parliament p WHERE p.endDate < :currentDate")
    List<Parliament> findEndedParliaments(@Param("currentDate") LocalDate currentDate);

    /**
     * Find parliaments that are currently in session
     */
    @Query("SELECT p FROM Parliament p WHERE p.startDate <= :currentDate AND p.endDate >= :currentDate")
    List<Parliament> findCurrentSessionParliaments(@Param("currentDate") LocalDate currentDate);
}
