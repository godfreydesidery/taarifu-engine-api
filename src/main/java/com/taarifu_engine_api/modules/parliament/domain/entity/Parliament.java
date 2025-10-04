package com.taarifu_engine_api.modules.parliament.domain.entity;

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

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Parliament entity representing a five-year parliamentary session in Tanzania.
 * Each parliament has a name (e.g., "Fifth Parliament"), start and end dates,
 * and tracks the session period.
 */
@Entity
@Table(name = "parliaments", 
       uniqueConstraints = {
           @UniqueConstraint(columnNames = {"name"}),
           @UniqueConstraint(columnNames = {"code"})
       })
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
public class Parliament {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "uid", unique = true, nullable = false, length = 26)
    private String uid;

    /**
     * Auto-generated parliament code with PT prefix (e.g., "PT000001", "PT000002")
     */
    @Column(name = "code", unique = true, nullable = false, length = 10)
    private String code;

    /**
     * Name of the parliament (e.g., "Fifth Parliament", "Sixth Parliament")
     */
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    /**
     * Description of the parliament session
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * Start date of the parliament session
     */
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    /**
     * End date of the parliament session
     */
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    /**
     * Whether this parliament is currently active
     */
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    /**
     * Whether this parliament is the current parliament
     */
    @Column(name = "is_current", nullable = false)
    private Boolean isCurrent = false;

    /**
     * Timestamp when the parliament was created
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when the parliament was last updated
     */
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * User who created the parliament
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false, updatable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User createdBy;

    /**
     * User who last updated the parliament
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User updatedBy;

    /**
     * Ensures that the parliament has a ULID. If uid is null or empty, generates a new one.
     */
    public void ensureUid() {
        if (uid == null || uid.trim().isEmpty()) {
            ULID ulid = new ULID();
            uid = ulid.nextULID();
        }
    }

    /**
     * Sets the parliament code with PT prefix and sequential number
     * @param sequenceNumber the sequential number for the parliament
     */
    public void setParliamentCode(int sequenceNumber) {
        this.code = String.format("PT%06d", sequenceNumber);
    }

    /**
     * Gets the duration of the parliament in years
     */
    public int getDurationInYears() {
        if (startDate == null || endDate == null) {
            return 0;
        }
        return endDate.getYear() - startDate.getYear();
    }

    /**
     * Checks if the parliament is currently in session
     */
    public boolean isInSession() {
        if (startDate == null || endDate == null) {
            return false;
        }
        LocalDate now = LocalDate.now();
        return !now.isBefore(startDate) && !now.isAfter(endDate);
    }

    /**
     * Checks if the parliament has ended
     */
    public boolean hasEnded() {
        if (endDate == null) {
            return false;
        }
        return LocalDate.now().isAfter(endDate);
    }

    /**
     * Checks if the parliament has started
     */
    public boolean hasStarted() {
        if (startDate == null) {
            return false;
        }
        return !LocalDate.now().isBefore(startDate);
    }
}
