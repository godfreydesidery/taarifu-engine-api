package com.taarifu_engine_api.modules.location.region.domain.entity;

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
 * Region entity representing Tanzanian administrative regions.
 * Tanzania has 31 regions as of 2024, each with a regional commissioner.
 */
@Entity
@Table(name = "regions", 
       uniqueConstraints = {
           @UniqueConstraint(columnNames = {"code"}),
           @UniqueConstraint(columnNames = {"name"})
       })
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
public class Region {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "uid", unique = true, nullable = false, length = 26)
    private String uid;

    /**
     * Auto-generated region code with RG prefix (e.g., "RG0001" for Arusha, "RG0002" for Dar es Salaam)
     */
    @Column(name = "code", unique = true, nullable = false, length = 10)
    private String code;

    /**
     * Official region name (e.g., "Arusha", "Dar es Salaam")
     */
    @Column(name = "name", unique = true, nullable = false, length = 100)
    private String name;

    /**
     * Regional capital/major city
     */
    @Column(name = "capital", nullable = false, length = 100)
    private String capital;

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
     * Regional commissioner name
     */
    @Column(name = "commissioner", length = 150)
    private String commissioner;

    /**
     * Brief description of the region
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * Whether the region is currently active
     */
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    /**
     * User who created this region
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false, updatable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User createdBy;

    /**
     * User who last updated this region
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
     * Ensures that the region has a ULID. If uid is null or empty, generates a new one.
     */
    public void ensureUid() {
        if (uid == null || uid.trim().isEmpty()) {
            ULID ulid = new ULID();
            uid = ulid.nextULID();
        }
    }

    /**
     * Sets the region code with RG prefix and sequential number
     * @param sequenceNumber the sequential number for the region
     */
    public void setRegionCode(int sequenceNumber) {
        this.code = String.format("RG%04d", sequenceNumber);
    }

    /**
     * Checks if the region is active
     */
    public boolean isActive() {
        return Boolean.TRUE.equals(isActive);
    }

    /**
     * Checks if the region has complete geographic information
     */
    public boolean hasGeographicInfo() {
        return latitude != null && longitude != null;
    }

    /**
     * Checks if the region has demographic information
     */
    public boolean hasDemographicInfo() {
        return population != null && areaSqKm != null;
    }
}
