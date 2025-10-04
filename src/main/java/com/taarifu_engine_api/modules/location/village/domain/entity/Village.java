package com.taarifu_engine_api.modules.location.village.domain.entity;

import com.taarifu_engine_api.modules.location.ward.domain.entity.Ward;
import com.taarifu_engine_api.modules.location.hamlet.domain.entity.Hamlet;
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
 * Village entity representing Tanzanian administrative villages.
 * Each village belongs to a ward.
 */
@Entity
@Table(name = "villages", 
       uniqueConstraints = {
           @UniqueConstraint(columnNames = {"code"}),
           @UniqueConstraint(columnNames = {"name"})
       })
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
public class Village {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "uid", unique = true, nullable = false, length = 26)
    private String uid;

    /**
     * Auto-generated village code with VL prefix (e.g., "VL0000001", "VL0000002")
     */
    @Column(name = "code", unique = true, nullable = false, length = 10)
    private String code;

    /**
     * Official village name (e.g., "Mto wa Mbu", "Ngorongoro")
     */
    @Column(name = "name", unique = true, nullable = false, length = 100)
    private String name;

    /**
     * Village headquarters/main area
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
     * Village executive officer name
     */
    @Column(name = "executive_officer", length = 150)
    private String executiveOfficer;

    /**
     * Brief description of the village
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * Whether the village is currently active
     */
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    /**
     * Ward this village belongs to
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ward_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Ward ward;

    /**
     * User who created this village
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false, updatable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User createdBy;

    /**
     * User who last updated this village
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
     * Hamlets belonging to this village
     */
    @OneToMany(mappedBy = "village", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Hamlet> hamlets;

    /**
     * Ensures that the village has a ULID. If uid is null or empty, generates a new one.
     */
    public void ensureUid() {
        if (uid == null || uid.trim().isEmpty()) {
            ULID ulid = new ULID();
            uid = ulid.nextULID();
        }
    }

    /**
     * Sets the village code with VL prefix and sequential number
     * @param sequenceNumber the sequential number for the village
     */
    public void setVillageCode(int sequenceNumber) {
        this.code = String.format("VL%07d", sequenceNumber);
    }

    /**
     * Checks if the village is active
     */
    public boolean isActive() {
        return Boolean.TRUE.equals(isActive);
    }

    /**
     * Checks if the village has complete geographic information
     */
    public boolean hasGeographicInfo() {
        return latitude != null && longitude != null;
    }

    /**
     * Checks if the village has demographic information
     */
    public boolean hasDemographicInfo() {
        return population != null && areaSqKm != null;
    }
}
