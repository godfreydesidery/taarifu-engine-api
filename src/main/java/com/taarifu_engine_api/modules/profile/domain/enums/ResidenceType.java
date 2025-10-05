package com.taarifu_engine_api.modules.profile.domain.enums;

/**
 * Enum representing different types of residences for profiles.
 * Used to categorize various forms of residence assignments.
 */
public enum ResidenceType {
    
    /**
     * Birth place - where the profile was born/established
     */
    BIRTH("BIRTH", "Birth Place", "Where the profile was born/established", "#1E40AF"),
    
    /**
     * Work location - where the profile works/operates
     */
    WORK("WORK", "Work Location", "Where the profile works/operates", "#059669"),
    
    /**
     * Primary residence - main place of residence/operation
     */
    PRIMARY("PRIMARY", "Primary Residence", "Main place of residence/operation", "#DC2626"),
    
    /**
     * Property ownership - property owned by profile
     */
    PROPERTY("PROPERTY", "Property Ownership", "Property owned by profile", "#7C3AED"),
    
    /**
     * Investment location - where profile has investments
     */
    INVESTMENT("INVESTMENT", "Investment Location", "Where profile has investments", "#EA580C"),
    
    /**
     * Temporary residence - temporary place of stay
     */
    TEMPORARY("TEMPORARY", "Temporary Residence", "Temporary place of stay", "#0891B2"),
    
    /**
     * Emergency contact - emergency contact location
     */
    EMERGENCY("EMERGENCY", "Emergency Contact", "Emergency contact location", "#BE185D"),
    
    /**
     * Branch office - branch office location (for organizations)
     */
    BRANCH("BRANCH", "Branch Office", "Branch office location (for organizations)", "#6B7280"),
    
    /**
     * Headquarters - main headquarters (for organizations)
     */
    HEADQUARTERS("HEADQUARTERS", "Headquarters", "Main headquarters (for organizations)", "#059669");

    private final String name;
    private final String displayName;
    private final String description;
    private final String colorCode;

    ResidenceType(String name, String displayName, String description, String colorCode) {
        this.name = name;
        this.displayName = displayName;
        this.description = description;
        this.colorCode = colorCode;
    }

    /**
     * Get the name of the residence type
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Get the display name of the residence type
     * @return the display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Get the description of the residence type
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Get the color code for the residence type
     * @return the color code (hex format)
     */
    public String getColorCode() {
        return colorCode;
    }

    /**
     * Check if this residence type is for personal use
     * @return true if personal, false if organizational
     */
    public boolean isPersonal() {
        return this != BRANCH && this != HEADQUARTERS;
    }

    /**
     * Check if this residence type is for organizational use
     * @return true if organizational, false if personal
     */
    public boolean isOrganizational() {
        return this == BRANCH || this == HEADQUARTERS;
    }

    /**
     * Check if this residence type requires verification
     * @return true if verification required, false otherwise
     */
    public boolean requiresVerification() {
        return this == PROPERTY || this == INVESTMENT;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
