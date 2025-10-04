package com.taarifu_engine_api.modules.location.district.domain.entity;

import com.taarifu_engine_api.modules.location.region.domain.entity.Region;
import com.taarifu_engine_api.modules.location.ward.domain.entity.Ward;
import com.taarifu_engine_api.modules.location.constituency.domain.entity.Constituency;
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
 * District entity representing Tanzanian administrative districts.
 * Each district belongs to a region.
 */
@Entity
@Table(name = "districts", 
       uniqueConstraints = {
           @UniqueConstraint(columnNames = {"code"}),
           @UniqueConstraint(columnNames = {"name"})
       })
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
public class District {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "uid", unique = true, nullable = false, length = 26)
    private String uid;

    /**
     * Auto-generated district code with DT prefix (e.g., "DT0001", "DT0002")
     */
    @Column(name = "code", unique = true, nullable = false, length = 10)
    private String code;

    /**
     * Official district name (e.g., "Arusha Urban", "Meru")
     */
    @Column(name = "name", unique = true, nullable = false, length = 100)
    private String name;

    /**
     * District headquarters/main town
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
     * District commissioner name
     */
    @Column(name = "commissioner", length = 150)
    private String commissioner;

    /**
     * Brief description of the district
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * Whether the district is currently active
     */
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    /**
     * Region this district belongs to
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Region region;

    /**
     * User who created this district
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false, updatable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User createdBy;

    /**
     * User who last updated this district
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
     * Wards belonging to this district
     */
    @OneToMany(mappedBy = "district", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Ward> wards;

    /**
     * Constituencies belonging to this district
     */
    @OneToMany(mappedBy = "district", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Constituency> constituencies;

    /**
     * Ensures that the district has a ULID. If uid is null or empty, generates a new one.
     */
    public void ensureUid() {
        if (uid == null || uid.trim().isEmpty()) {
            ULID ulid = new ULID();
            uid = ulid.nextULID();
        }
    }

    /**
     * Sets the district code with DT prefix and sequential number
     * @param sequenceNumber the sequential number for the district
     */
    public void setDistrictCode(int sequenceNumber) {
        this.code = String.format("DT%04d", sequenceNumber);
    }

    /**
     * Checks if the district is active
     */
    public boolean isActive() {
        return Boolean.TRUE.equals(isActive);
    }

    /**
     * Checks if the district has complete geographic information
     */
    public boolean hasGeographicInfo() {
        return latitude != null && longitude != null;
    }

    /**
     * Checks if the district has demographic information
     */
    public boolean hasDemographicInfo() {
        return population != null && areaSqKm != null;
    }
}
