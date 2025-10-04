package com.taarifu_engine_api.modules.location.hamlet.domain.entity;

import com.taarifu_engine_api.modules.location.village.domain.entity.Village;
import com.taarifu_engine_api.modules.userandrole.domain.entity.User;
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
 * Hamlet entity representing Tanzanian administrative hamlets.
 * Each hamlet belongs to a village.
 */
@Entity
@Table(name = "hamlets", 
       uniqueConstraints = {
           @UniqueConstraint(columnNames = {"code"}),
           @UniqueConstraint(columnNames = {"name"})
       })
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
public class Hamlet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "uid", unique = true, nullable = false, length = 26)
    private String uid;

    /**
     * Auto-generated hamlet code with HM prefix (e.g., "HM0000001", "HM0000002")
     */
    @Column(name = "code", unique = true, nullable = false, length = 10)
    private String code;

    /**
     * Official hamlet name (e.g., "Mto wa Mbu North", "Ngorongoro East")
     */
    @Column(name = "name", unique = true, nullable = false, length = 100)
    private String name;

    /**
     * Hamlet headquarters/main area
     */
    @Column(name = "headquarters", nullable = false, length = 100)
    private String headquarters;

    /**
     * Population estimate
     */
    @Column(name = "population")
    private Long population;

    /**
     * Area in square kilometers
     */
    @Column(name = "area_sq_km")
    private Double areaSqKm;

    /**
     * Geographic coordinates - latitude
     */
    @Column(name = "latitude")
    private Double latitude;

    /**
     * Geographic coordinates - longitude
     */
    @Column(name = "longitude")
    private Double longitude;

    /**
     * Hamlet executive officer name
     */
    @Column(name = "executive_officer", length = 150)
    private String executiveOfficer;

    /**
     * Brief description of the hamlet
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * Whether the hamlet is currently active
     */
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    /**
     * Village this hamlet belongs to
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "village_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Village village;

    /**
     * User who created this hamlet
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false, updatable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User createdBy;

    /**
     * User who last updated this hamlet
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User updatedBy;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Ensures that the hamlet has a ULID. If uid is null or empty, generates a new one.
     */
    public void ensureUid() {
        if (uid == null || uid.trim().isEmpty()) {
            ULID ulid = new ULID();
            uid = ulid.nextULID();
        }
    }

    /**
     * Sets the hamlet code with HM prefix and sequential number
     * @param sequenceNumber the sequential number for the hamlet
     */
    public void setHamletCode(int sequenceNumber) {
        this.code = String.format("HM%07d", sequenceNumber);
    }

    /**
     * Checks if the hamlet is active
     */
    public boolean isActive() {
        return Boolean.TRUE.equals(isActive);
    }

    /**
     * Checks if the hamlet has complete geographic information
     */
    public boolean hasGeographicInfo() {
        return latitude != null && longitude != null;
    }

    /**
     * Checks if the hamlet has demographic information
     */
    public boolean hasDemographicInfo() {
        return population != null && areaSqKm != null;
    }
}
