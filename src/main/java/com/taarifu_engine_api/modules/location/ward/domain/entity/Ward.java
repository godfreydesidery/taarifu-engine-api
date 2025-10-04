package com.taarifu_engine_api.modules.location.ward.domain.entity;

import com.taarifu_engine_api.modules.location.district.domain.entity.District;
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
import java.util.List;

/**
 * Ward entity representing Tanzanian administrative wards.
 * Each ward belongs to a district.
 */
@Entity
@Table(name = "wards", 
       uniqueConstraints = {
           @UniqueConstraint(columnNames = {"code"}),
           @UniqueConstraint(columnNames = {"name"})
       })
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
public class Ward {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "uid", unique = true, nullable = false, length = 26)
    private String uid;

    /**
     * Auto-generated ward code with WD prefix (e.g., "WD000001", "WD000002")
     */
    @Column(name = "code", unique = true, nullable = false, length = 10)
    private String code;

    /**
     * Official ward name (e.g., "Arusha Central", "Ngaramtoni")
     */
    @Column(name = "name", unique = true, nullable = false, length = 100)
    private String name;

    /**
     * Ward headquarters/main area
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
     * Ward executive officer name
     */
    @Column(name = "executive_officer", length = 150)
    private String executiveOfficer;

    /**
     * Brief description of the ward
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * Whether the ward is currently active
     */
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    /**
     * District this ward belongs to
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "district_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private District district;

    /**
     * User who created this ward
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false, updatable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User createdBy;

    /**
     * User who last updated this ward
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
     * Villages belonging to this ward
     */
    @OneToMany(mappedBy = "ward", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Village> villages;

    /**
     * Ensures that the ward has a ULID. If uid is null or empty, generates a new one.
     */
    public void ensureUid() {
        if (uid == null || uid.trim().isEmpty()) {
            ULID ulid = new ULID();
            uid = ulid.nextULID();
        }
    }

    /**
     * Sets the ward code with WD prefix and sequential number
     * @param sequenceNumber the sequential number for the ward
     */
    public void setWardCode(int sequenceNumber) {
        this.code = String.format("WD%06d", sequenceNumber);
    }

    /**
     * Checks if the ward is active
     */
    public boolean isActive() {
        return Boolean.TRUE.equals(isActive);
    }

    /**
     * Checks if the ward has complete geographic information
     */
    public boolean hasGeographicInfo() {
        return latitude != null && longitude != null;
    }

    /**
     * Checks if the ward has demographic information
     */
    public boolean hasDemographicInfo() {
        return population != null && areaSqKm != null;
    }
}
