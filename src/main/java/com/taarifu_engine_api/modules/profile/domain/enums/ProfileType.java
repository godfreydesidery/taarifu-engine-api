package com.taarifu_engine_api.modules.profile.domain.enums;

/**
 * Enum representing different types of profiles in the system.
 * Used to distinguish between personal and organizational profiles.
 */
public enum ProfileType {
    
    /**
     * Personal profile for individual users
     */
    PERSON("PERSON", "Person", "Individual personal profile", "#059669"),
    
    /**
     * Organizational profile for institutions, companies, or groups
     */
    ORGANIZATION("ORGANIZATION", "Organization", "Organizational or institutional profile", "#7C3AED");

    private final String name;
    private final String displayName;
    private final String description;
    private final String colorCode;

    ProfileType(String name, String displayName, String description, String colorCode) {
        this.name = name;
        this.displayName = displayName;
        this.description = description;
        this.colorCode = colorCode;
    }

    /**
     * Get the name of the profile type
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Get the display name of the profile type
     * @return the display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Get the description of the profile type
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Get the color code for the profile type
     * @return the color code (hex format)
     */
    public String getColorCode() {
        return colorCode;
    }

    /**
     * Check if this profile type is for a person
     * @return true if person, false if organization
     */
    public boolean isPerson() {
        return this == PERSON;
    }

    /**
     * Check if this profile type is for an organization
     * @return true if organization, false if person
     */
    public boolean isOrganization() {
        return this == ORGANIZATION;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
