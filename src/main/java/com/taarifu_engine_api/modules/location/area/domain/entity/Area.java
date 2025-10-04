package com.taarifu_engine_api.modules.location.area.domain.entity;

import com.taarifu_engine_api.modules.common.domain.enums.AreaType;
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

/**
 * Area entity representing a location of a specific type in Tanzania.
 * This is a generic entity that can represent any type of administrative or political area.
 */
@Entity
@Table(name = "areas", 
       uniqueConstraints = {
           @UniqueConstraint(columnNames = {"code"}),
           @UniqueConstraint(columnNames = {"area_type", "area_id"})
       })
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
public class Area {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "uid", unique = true, nullable = false, length = 26)
    private String uid;

    /**
     * Auto-generated area code with AR prefix (e.g., "AR00000000001", "AR00000000002")
     */
    @Column(name = "code", unique = true, nullable = false, length = 15)
    private String code;

    /**
     * Type of area (REGION, DISTRICT, WARD, VILLAGE, HAMLET, CONSTITUENCY)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "area_type", nullable = false)
    private AreaType areaType;

    /**
     * Reference ID to the actual area entity (e.g., region.id, district.id, etc.)
     */
    @Column(name = "area_id", nullable = false)
    private Long areaId;

    /**
     * Name of the area (inherited from the respective location entity)
     */
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    /**
     * Timestamp when the area was created
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private java.time.LocalDateTime createdAt;

    /**
     * Timestamp when the area was last updated
     */
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private java.time.LocalDateTime updatedAt;

    /**
     * User who created the area
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false, updatable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User createdBy;

    /**
     * User who last updated the area
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User updatedBy;


    /**
     * Ensures that the area has a ULID. If uid is null or empty, generates a new one.
     */
    public void ensureUid() {
        if (uid == null || uid.trim().isEmpty()) {
            ULID ulid = new ULID();
            uid = ulid.nextULID();
        }
    }

    /**
     * Sets the area code with AR prefix and sequential number
     * @param sequenceNumber the sequential number for the area
     */
    public void setAreaCode(int sequenceNumber) {
        this.code = String.format("AR%011d", sequenceNumber);
    }

    /**
     * Gets the display name based on area type
     */
    public String getDisplayName() {
        return areaType != null ? areaType.getDisplayName() : "Unknown";
    }

    /**
     * Gets the color code based on area type
     */
    public String getColorCode() {
        return areaType != null ? areaType.getColorCode() : "#6B7280";
    }
}
