package com.taarifu_engine_api.modules.profile.domain.entity;

import com.taarifu_engine_api.modules.location.area.domain.entity.Area;
import com.taarifu_engine_api.modules.location.constituency.domain.entity.Constituency;
import com.taarifu_engine_api.modules.profile.domain.enums.ResidenceType;
import com.taarifu_engine_api.modules.profile.domain.enums.VerificationStatus;
import de.huxhorn.sulky.ulid.ULID;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * ProfileResidence entity representing residence assignments for profiles.
 * Links profiles to administrative areas and constituencies with residence types.
 */
@Entity
@Table(name = "profile_residences", 
       uniqueConstraints = {
           @UniqueConstraint(columnNames = {"profile_id", "residence_type", "administrative_area_id"}),
           @UniqueConstraint(columnNames = {"profile_id", "residence_type", "constituency_id"})
       })
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"profile", "administrativeArea", "constituency"})
@ToString(exclude = {"profile", "administrativeArea", "constituency"})
public class ProfileResidence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "uid", unique = true, nullable = false, length = 26)
    private String uid;

    /**
     * Profile this residence belongs to
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    private Profile profile;

    /**
     * Type of residence (BIRTH, WORK, PRIMARY, PROPERTY, etc.)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "residence_type", nullable = false)
    private ResidenceType residenceType;

    /**
     * Whether this is the primary residence of this type
     */
    @Column(name = "is_primary", nullable = false)
    private Boolean isPrimary = false;

    /**
     * Administrative Area (for normal areas like Region, District, Ward, Village, Hamlet)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "administrative_area_id")
    private Area administrativeArea;

    /**
     * Constituency (for political areas)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "constituency_id")
    private Constituency constituency;

    /**
     * Detailed address information
     */
    @Column(name = "detailed_address", columnDefinition = "TEXT")
    private String detailedAddress;

    /**
     * Validity period start
     */
    @Column(name = "valid_from")
    private LocalDateTime validFrom;

    /**
     * Validity period end
     */
    @Column(name = "valid_to")
    private LocalDateTime validTo;

    /**
     * Whether this residence is currently active
     */
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    /**
     * Additional notes about this residence
     */
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    /**
     * Verification status for this residence
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "verification_status", nullable = false)
    private VerificationStatus verificationStatus = VerificationStatus.PENDING;

    /**
     * JSON array of verification document URLs
     */
    @Column(name = "verification_documents", columnDefinition = "TEXT")
    private String verificationDocuments;

    /**
     * Timestamp when this residence was created
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when this residence was last updated
     */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Ensures that the residence has a ULID. If uid is null or empty, generates a new one.
     */
    public void ensureUid() {
        if (uid == null || uid.trim().isEmpty()) {
            ULID ulid = new ULID();
            uid = ulid.nextULID();
        }
    }

    /**
     * Checks if this residence is currently valid
     * @return true if valid, false otherwise
     */
    public boolean isValid() {
        LocalDateTime now = LocalDateTime.now();
        boolean withinValidityPeriod = true;
        
        if (validFrom != null && now.isBefore(validFrom)) {
            withinValidityPeriod = false;
        }
        
        if (validTo != null && now.isAfter(validTo)) {
            withinValidityPeriod = false;
        }
        
        return isActive && withinValidityPeriod;
    }

    /**
     * Checks if this residence has an administrative area assigned
     * @return true if has administrative area, false otherwise
     */
    public boolean hasAdministrativeArea() {
        return administrativeArea != null;
    }

    /**
     * Checks if this residence has a constituency assigned
     * @return true if has constituency, false otherwise
     */
    public boolean hasConstituency() {
        return constituency != null;
    }

    /**
     * Checks if this residence requires verification
     * @return true if verification required, false otherwise
     */
    public boolean requiresVerification() {
        return residenceType != null && residenceType.requiresVerification();
    }

    /**
     * Gets the display name for this residence
     * @return display name
     */
    public String getDisplayName() {
        if (hasAdministrativeArea()) {
            return administrativeArea.getName() + " (" + administrativeArea.getAreaType().getDisplayName() + ")";
        } else if (hasConstituency()) {
            return constituency.getName() + " (Constituency)";
        }
        return "Unknown Residence";
    }
}
