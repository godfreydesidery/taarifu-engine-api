package com.taarifu_engine_api.modules.profile.repository;

import com.taarifu_engine_api.modules.location.area.domain.entity.Area;
import com.taarifu_engine_api.modules.location.constituency.domain.entity.Constituency;
import com.taarifu_engine_api.modules.profile.domain.entity.Profile;
import com.taarifu_engine_api.modules.profile.domain.entity.ProfileResidence;
import com.taarifu_engine_api.modules.profile.domain.enums.ResidenceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for ProfileResidence entity operations
 */
@Repository
public interface ProfileResidenceRepository extends JpaRepository<ProfileResidence, Long> {

    /**
     * Find all residences for a specific profile
     */
    List<ProfileResidence> findByProfileAndIsActiveTrueOrderByCreatedAtDesc(Profile profile);

    /**
     * Find residences by profile and residence type
     */
    List<ProfileResidence> findByProfileAndResidenceTypeAndIsActiveTrueOrderByCreatedAtDesc(
            Profile profile, ResidenceType residenceType);

    /**
     * Find primary administrative area residence for a profile
     */
    @Query("SELECT pr FROM ProfileResidence pr WHERE pr.profile = :profile " +
           "AND pr.isPrimary = true AND pr.administrativeArea IS NOT NULL " +
           "AND pr.isActive = true")
    Optional<ProfileResidence> findPrimaryAdministrativeResidence(@Param("profile") Profile profile);

    /**
     * Find primary constituency residence for a profile
     */
    @Query("SELECT pr FROM ProfileResidence pr WHERE pr.profile = :profile " +
           "AND pr.isPrimary = true AND pr.constituency IS NOT NULL " +
           "AND pr.isActive = true")
    Optional<ProfileResidence> findPrimaryConstituencyResidence(@Param("profile") Profile profile);

    /**
     * Find primary residence of specific type for a profile
     */
    @Query("SELECT pr FROM ProfileResidence pr WHERE pr.profile = :profile " +
           "AND pr.residenceType = :residenceType AND pr.isPrimary = true " +
           "AND pr.isActive = true")
    Optional<ProfileResidence> findPrimaryResidenceByType(@Param("profile") Profile profile, 
                                                          @Param("residenceType") ResidenceType residenceType);

    /**
     * Find residence by profile and administrative area
     */
    Optional<ProfileResidence> findByProfileAndAdministrativeAreaAndIsActiveTrue(Profile profile, Area area);

    /**
     * Find residence by profile and constituency
     */
    Optional<ProfileResidence> findByProfileAndConstituencyAndIsActiveTrue(Profile profile, Constituency constituency);

    /**
     * Find residence by UID
     */
    Optional<ProfileResidence> findByUidAndIsActiveTrue(String uid);

    /**
     * Find all primary residences for a profile
     */
    @Query("SELECT pr FROM ProfileResidence pr WHERE pr.profile = :profile " +
           "AND pr.isPrimary = true AND pr.isActive = true")
    List<ProfileResidence> findAllPrimaryResidences(@Param("profile") Profile profile);

    /**
     * Count residences by profile and residence type
     */
    long countByProfileAndResidenceTypeAndIsActiveTrue(Profile profile, ResidenceType residenceType);

    /**
     * Find residences that need verification
     */
    @Query("SELECT pr FROM ProfileResidence pr WHERE pr.profile = :profile " +
           "AND pr.verificationStatus = 'PENDING' AND pr.isActive = true")
    List<ProfileResidence> findPendingVerificationResidences(@Param("profile") Profile profile);

    /**
     * Check if profile has residence with specific administrative area and residence type
     */
    boolean existsByProfileAndAdministrativeAreaAndResidenceTypeAndIsActiveTrue(
            Profile profile, Area area, ResidenceType residenceType);

    /**
     * Check if profile has residence with specific constituency and residence type
     */
    boolean existsByProfileAndConstituencyAndResidenceTypeAndIsActiveTrue(
            Profile profile, Constituency constituency, ResidenceType residenceType);
}
