package com.taarifu_engine_api.modules.userandrole.domain.entity;

import com.taarifu_engine_api.modules.common.domain.enums.PasswordStrength;
import com.taarifu_engine_api.modules.userandrole.domain.enums.UserStatus;
import com.taarifu_engine_api.modules.userandrole.domain.enums.UserType;
import de.huxhorn.sulky.ulid.ULID;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_username", columnList = "username"),
    @Index(name = "idx_email", columnList = "email"),
    @Index(name = "idx_uid", columnList = "uid"),
    @Index(name = "idx_user_type_status", columnList = "user_type,status"),
    @Index(name = "idx_password_reset_token", columnList = "password_reset_token"),
    @Index(name = "idx_email_verification_token", columnList = "email_verification_token"),
    @Index(name = "idx_deleted", columnList = "deleted"),
    @Index(name = "idx_phone_number", columnList = "phone_number")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"passwordHash", "passwordResetToken", "emailVerificationToken"})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "uid", unique = true, nullable = false, length = 26)
    private String uid;

    @Column(name = "username", unique = true, nullable = false, length = 50)
    private String username;

    @Column(name = "email", unique = true, nullable = false, length = 255)
    private String email;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

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

    // Email verification fields
    @Column(name = "email_verified", nullable = false)
    private Boolean emailVerified = false;

    @Column(name = "email_verification_token", length = 255)
    private String emailVerificationToken;

    @Column(name = "email_verification_token_expires_at")
    private LocalDateTime emailVerificationTokenExpiresAt;

    @Column(name = "email_verified_at")
    private LocalDateTime emailVerifiedAt;

    // Account lockout fields
    @Column(name = "failed_login_attempts", nullable = false)
    private Integer failedLoginAttempts = 0;

    @Column(name = "account_locked_until")
    private LocalDateTime accountLockedUntil;

    @Column(name = "last_failed_login_at")
    private LocalDateTime lastFailedLoginAt;

    // Password tracking fields
    @Column(name = "password_changed_at")
    private LocalDateTime passwordChangedAt;

    @Column(name = "password_expires_at")
    private LocalDateTime passwordExpiresAt;

    @Column(name = "password_expiry_days", nullable = false)
    private Integer passwordExpiryDays = 90;

    // Soft delete fields
    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "deleted_by", length = 26)
    private String deletedBy; // Stores UID of user who deleted

    // Audit fields
    @Column(name = "created_by", length = 26)
    private String createdBy; // Stores UID of creator

    @Column(name = "updated_by", length = 26)
    private String updatedBy; // Stores UID of last updater

    // Optimistic locking
    @Version
    @Column(name = "version")
    private Long version = 0L;

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

    // Email verification utility methods
    public boolean isEmailVerified() {
        return emailVerified != null && emailVerified;
    }

    public boolean hasValidEmailVerificationToken() {
        return emailVerificationToken != null && 
               emailVerificationTokenExpiresAt != null && 
               emailVerificationTokenExpiresAt.isAfter(LocalDateTime.now());
    }

    public void clearEmailVerificationToken() {
        this.emailVerificationToken = null;
        this.emailVerificationTokenExpiresAt = null;
    }

    public boolean isEmailVerificationTokenExpired() {
        return emailVerificationTokenExpiresAt != null && 
               emailVerificationTokenExpiresAt.isBefore(LocalDateTime.now());
    }

    // Account lockout utility methods
    public boolean isAccountLocked() {
        return accountLockedUntil != null && accountLockedUntil.isAfter(LocalDateTime.now());
    }

    public void incrementFailedLoginAttempts() {
        this.failedLoginAttempts++;
        this.lastFailedLoginAt = LocalDateTime.now();
    }

    public void resetFailedLoginAttempts() {
        this.failedLoginAttempts = 0;
        this.accountLockedUntil = null;
        this.lastFailedLoginAt = null;
    }

    public void lockAccount(int lockoutMinutes) {
        this.accountLockedUntil = LocalDateTime.now().plusMinutes(lockoutMinutes);
    }

    public void unlockAccount() {
        this.accountLockedUntil = null;
        resetFailedLoginAttempts();
    }

    // Password expiry utility methods
    public boolean isPasswordExpired() {
        return passwordExpiresAt != null && passwordExpiresAt.isBefore(LocalDateTime.now());
    }

    public void updatePasswordExpiry() {
        if (passwordExpiryDays != null && passwordExpiryDays > 0) {
            this.passwordExpiresAt = LocalDateTime.now().plusDays(passwordExpiryDays);
        }
    }

    // Soft delete utility methods
    public void softDelete(String deletedByUid) {
        this.deleted = true;
        this.deletedAt = LocalDateTime.now();
        this.deletedBy = deletedByUid;
    }

    public void restore() {
        this.deleted = false;
        this.deletedAt = null;
        this.deletedBy = null;
    }

    public boolean isDeleted() {
        return deleted != null && deleted;
    }
}
