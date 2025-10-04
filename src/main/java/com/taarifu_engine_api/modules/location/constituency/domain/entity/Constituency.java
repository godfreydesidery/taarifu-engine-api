package com.taarifu_engine_api.modules.location.constituency.domain.entity;

import com.taarifu_engine_api.modules.location.district.domain.entity.District;
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
 * Constituency entity representing Tanzanian parliamentary constituencies.
 * Each constituency belongs to a district.
 */
@Entity
@Table(name = "constituencies", 
       uniqueConstraints = {
           @UniqueConstraint(columnNames = {"code"}),
           @UniqueConstraint(columnNames = {"name"})
       })
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
public class Constituency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "uid", unique = true, nullable = false, length = 26)
    private String uid;

    /**
     * Auto-generated constituency code with CT prefix (e.g., "CT000001", "CT000002")
     */
    @Column(name = "code", unique = true, nullable = false, length = 10)
    private String code;

    /**
     * Official constituency name (e.g., "Arusha Urban", "Meru")
     */
    @Column(name = "name", unique = true, nullable = false, length = 100)
    private String name;

    /**
     * Constituency headquarters/main area
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
     * Brief description of the constituency
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * Whether the constituency is currently active
     */
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    /**
     * District this constituency belongs to
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "district_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private District district;

    /**
     * User who created this constituency
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false, updatable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User createdBy;

    /**
     * User who last updated this constituency
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
     * Ensures that the constituency has a ULID. If uid is null or empty, generates a new one.
     */
    public void ensureUid() {
        if (uid == null || uid.trim().isEmpty()) {
            ULID ulid = new ULID();
            uid = ulid.nextULID();
        }
    }

    /**
     * Sets the constituency code with CT prefix and sequential number
     * @param sequenceNumber the sequential number for the constituency
     */
    public void setConstituencyCode(int sequenceNumber) {
        this.code = String.format("CT%06d", sequenceNumber);
    }

    /**
     * Checks if the constituency is active
     */
    public boolean isActive() {
        return Boolean.TRUE.equals(isActive);
    }

    /**
     * Checks if the constituency has complete geographic information
     */
    public boolean hasGeographicInfo() {
        return latitude != null && longitude != null;
    }

    /**
     * Checks if the constituency has demographic information
     */
    public boolean hasDemographicInfo() {
        return population != null && areaSqKm != null;
    }
}
