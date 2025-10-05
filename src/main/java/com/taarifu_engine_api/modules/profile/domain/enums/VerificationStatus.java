package com.taarifu_engine_api.modules.profile.domain.enums;

/**
 * Enum representing verification status for profile residences.
 * Used to track the verification state of residence assignments.
 */
public enum VerificationStatus {
    
    /**
     * Pending verification - awaiting verification
     */
    PENDING("PENDING", "Pending", "Awaiting verification", "#F59E0B"),
    
    /**
     * Verified - successfully verified
     */
    VERIFIED("VERIFIED", "Verified", "Successfully verified", "#10B981"),
    
    /**
     * Rejected - verification rejected
     */
    REJECTED("REJECTED", "Rejected", "Verification rejected", "#EF4444"),
    
    /**
     * Expired - verification expired
     */
    EXPIRED("EXPIRED", "Expired", "Verification expired", "#6B7280");

    private final String name;
    private final String displayName;
    private final String description;
    private final String colorCode;

    VerificationStatus(String name, String displayName, String description, String colorCode) {
        this.name = name;
        this.displayName = displayName;
        this.description = description;
        this.colorCode = colorCode;
    }

    /**
     * Get the name of the verification status
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Get the display name of the verification status
     * @return the display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Get the description of the verification status
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Get the color code for the verification status
     * @return the color code (hex format)
     */
    public String getColorCode() {
        return colorCode;
    }

    /**
     * Check if this status indicates verification is complete
     * @return true if verified, false otherwise
     */
    public boolean isVerified() {
        return this == VERIFIED;
    }

    /**
     * Check if this status indicates verification is pending
     * @return true if pending, false otherwise
     */
    public boolean isPending() {
        return this == PENDING;
    }

    /**
     * Check if this status indicates verification failed
     * @return true if rejected or expired, false otherwise
     */
    public boolean isFailed() {
        return this == REJECTED || this == EXPIRED;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
