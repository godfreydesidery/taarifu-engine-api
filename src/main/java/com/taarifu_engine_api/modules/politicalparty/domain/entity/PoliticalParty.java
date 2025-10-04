package com.taarifu_engine_api.modules.politicalparty.domain.entity;

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
 * PoliticalParty entity representing a political party in Tanzania.
 * Each political party has a name, abbreviation, description, founding information,
 * and tracks its status and activities.
 */
@Entity
@Table(name = "political_parties", 
       uniqueConstraints = {
           @UniqueConstraint(columnNames = {"name"}),
           @UniqueConstraint(columnNames = {"abbreviation"}),
           @UniqueConstraint(columnNames = {"code"})
       })
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
public class PoliticalParty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "uid", unique = true, nullable = false, length = 26)
    private String uid;

    /**
     * Auto-generated political party code with PP prefix (e.g., "PP000001", "PP000002")
     */
    @Column(name = "code", unique = true, nullable = false, length = 10)
    private String code;

    /**
     * Full name of the political party (e.g., "Chama Cha Mapinduzi", "Chadema")
     */
    @Column(name = "name", nullable = false, length = 200)
    private String name;

    /**
     * Abbreviation or short form of the party name (e.g., "CCM", "CHADEMA")
     */
    @Column(name = "abbreviation", nullable = false, length = 20)
    private String abbreviation;

    /**
     * Description of the political party
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * Founding date of the political party
     */
    @Column(name = "founding_date")
    private LocalDate foundingDate;

    /**
     * Founding location of the political party
     */
    @Column(name = "founding_location", length = 200)
    private String foundingLocation;

    /**
     * Party ideology or political stance
     */
    @Column(name = "ideology", length = 500)
    private String ideology;

    /**
     * Party colors (e.g., "Red, Green, Gold")
     */
    @Column(name = "colors", length = 100)
    private String colors;

    /**
     * Party symbol or emblem description
     */
    @Column(name = "symbol", length = 200)
    private String symbol;

    /**
     * Party motto or slogan
     */
    @Column(name = "motto", length = 300)
    private String motto;

    /**
     * Website URL of the political party
     */
    @Column(name = "website_url", length = 500)
    private String websiteUrl;

    /**
     * Email address of the political party
     */
    @Column(name = "email", length = 100)
    private String email;

    /**
     * Phone number of the political party
     */
    @Column(name = "phone", length = 20)
    private String phone;

    /**
     * Physical address of the party headquarters
     */
    @Column(name = "headquarters_address", length = 500)
    private String headquartersAddress;

    /**
     * Whether the party is currently registered
     */
    @Column(name = "is_registered", nullable = false)
    private Boolean isRegistered = true;

    /**
     * Whether the party is currently active
     */
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    /**
     * Registration number with electoral commission
     */
    @Column(name = "registration_number", length = 50)
    private String registrationNumber;

    /**
     * Date when the party was registered
     */
    @Column(name = "registration_date")
    private LocalDate registrationDate;

    /**
     * Number of members (approximate)
     */
    @Column(name = "member_count")
    private Long memberCount;

    /**
     * Timestamp when the political party was created
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when the political party was last updated
     */
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * User who created the political party
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false, updatable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User createdBy;

    /**
     * User who last updated the political party
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User updatedBy;

    /**
     * Ensures that the political party has a ULID. If uid is null or empty, generates a new one.
     */
    public void ensureUid() {
        if (uid == null || uid.trim().isEmpty()) {
            ULID ulid = new ULID();
            uid = ulid.nextULID();
        }
    }

    /**
     * Sets the political party code with PP prefix and sequential number
     * @param sequenceNumber the sequential number for the political party
     */
    public void setPoliticalPartyCode(int sequenceNumber) {
        this.code = String.format("PP%06d", sequenceNumber);
    }

    /**
     * Gets the age of the political party in years
     */
    public int getAgeInYears() {
        if (foundingDate == null) {
            return 0;
        }
        return LocalDate.now().getYear() - foundingDate.getYear();
    }

    /**
     * Checks if the political party is currently registered and active
     */
    public boolean isOperational() {
        return isRegistered != null && isRegistered && isActive != null && isActive;
    }

    /**
     * Gets a display name combining name and abbreviation
     */
    public String getDisplayName() {
        if (abbreviation != null && !abbreviation.trim().isEmpty()) {
            return name + " (" + abbreviation + ")";
        }
        return name;
    }
}
