package com.taarifu_engine_api.modules.userandrole.domain.entity;

import com.taarifu_engine_api.modules.common.domain.enums.PasswordStrength;
import com.taarifu_engine_api.modules.userandrole.domain.enums.UserStatus;
import com.taarifu_engine_api.modules.userandrole.domain.enums.UserType;
import de.huxhorn.sulky.ulid.ULID;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "uid", unique = true, nullable = false, length = 26)
    private String uid;

    @Column(name = "username", unique = true, nullable = false, length = 50)
    private String username;

    @Column(name = "email", unique = true, nullable = false, length = 255)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    /**
     * TEMPORARY FIELD FOR TESTING - Raw password storage.
     * This field should be removed in production.
     * Only used when email delivery is not working for testing purposes.
     */
    @Column(name = "raw_password", length = 255)
    private String rawPassword;

    @Enumerated(EnumType.STRING)
    @Column(name = "password_strength", nullable = false, length = 50)
    private PasswordStrength passwordStrength;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_type", nullable = false, length = 50)
    private UserType userType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private UserStatus status = UserStatus.PENDING_VERIFICATION;

    @Column(name = "require_password_change", nullable = false)
    private Boolean requirePasswordChange = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    // Password reset token fields
    @Column(name = "password_reset_token", length = 255)
    private String passwordResetToken;

    @Column(name = "password_reset_token_expires_at")
    private LocalDateTime passwordResetTokenExpiresAt;

    // Utility methods
    public boolean isActive() {
        return status == UserStatus.ACTIVE;
    }

    public boolean isAdmin() {
        return userType == UserType.ADMIN;
    }

    public boolean isUser() {
        return userType == UserType.USER;
    }

    /**
     * Ensures that the user has a ULID. If uid is null or empty, generates a new one.
     */
    public void ensureUid() {
        if (uid == null || uid.trim().isEmpty()) {
            ULID ulid = new ULID();
            uid = ulid.nextULID();
        }
    }

    // Password strength utility methods
    public boolean hasWeakPassword() {
        return passwordStrength == PasswordStrength.WEAK;
    }

    public boolean hasFairPassword() {
        return passwordStrength == PasswordStrength.FAIR;
    }

    public boolean hasGoodPassword() {
        return passwordStrength == PasswordStrength.GOOD;
    }

    public boolean hasStrongPassword() {
        return passwordStrength == PasswordStrength.STRONG;
    }

    public boolean hasMinimumPasswordStrength(PasswordStrength minimumStrength) {
        return passwordStrength.ordinal() >= minimumStrength.ordinal();
    }

    // Access control methods based on password strength
    public boolean canAccessMpProfile() {
        return hasStrongPassword() && isActive();
    }

    public boolean canAccessSensitiveData() {
        return hasMinimumPasswordStrength(PasswordStrength.GOOD) && isActive();
    }

    public boolean canPerformAdminActions() {
        return isAdmin() && hasStrongPassword() && isActive();
    }

    public boolean canCreateOrganizations() {
        return hasMinimumPasswordStrength(PasswordStrength.GOOD) && isActive();
    }

    public boolean canReportIssues() {
        return hasMinimumPasswordStrength(PasswordStrength.FAIR) && isActive();
    }

    // Password reset token utility methods
    public boolean hasValidPasswordResetToken() {
        return passwordResetToken != null && 
               passwordResetTokenExpiresAt != null && 
               passwordResetTokenExpiresAt.isAfter(LocalDateTime.now());
    }

    public void clearPasswordResetToken() {
        this.passwordResetToken = null;
        this.passwordResetTokenExpiresAt = null;
    }

    public boolean isPasswordResetTokenExpired() {
        return passwordResetTokenExpiresAt != null && 
               passwordResetTokenExpiresAt.isBefore(LocalDateTime.now());
    }
}
