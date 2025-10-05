package com.taarifu_engine_api.modules.profile.domain.enums;

/**
 * Enum representing different types of identification documents.
 * Used to categorize various forms of personal identification.
 */
public enum IdType {
    
    /**
     * National ID card
     */
    NATIONAL_ID("NATIONAL_ID", "National ID", "National identification card", "#1E40AF"),
    
    /**
     * Driving license
     */
    DRIVING_LICENSE("DRIVING_LICENSE", "Driving License", "Driver's license", "#059669"),
    
    /**
     * Passport
     */
    PASSPORT("PASSPORT", "Passport", "International passport", "#DC2626"),
    
    /**
     * Voter ID
     */
    VOTER_ID("VOTER_ID", "Voter ID", "Voter identification card", "#7C3AED"),
    
    /**
     * Birth certificate
     */
    BIRTH_CERTIFICATE("BIRTH_CERTIFICATE", "Birth Certificate", "Birth certificate document", "#EA580C"),
    
    /**
     * Student ID
     */
    STUDENT_ID("STUDENT_ID", "Student ID", "Student identification card", "#0891B2"),
    
    /**
     * Employee ID
     */
    EMPLOYEE_ID("EMPLOYEE_ID", "Employee ID", "Employee identification card", "#BE185D"),
    
    /**
     * Other identification document
     */
    OTHER("OTHER", "Other", "Other form of identification", "#6B7280");

    private final String name;
    private final String displayName;
    private final String description;
    private final String colorCode;

    IdType(String name, String displayName, String description, String colorCode) {
        this.name = name;
        this.displayName = displayName;
        this.description = description;
        this.colorCode = colorCode;
    }

    /**
     * Get the name of the ID type
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Get the display name of the ID type
     * @return the display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Get the description of the ID type
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Get the color code for the ID type
     * @return the color code (hex format)
     */
    public String getColorCode() {
        return colorCode;
    }

    /**
     * Check if this ID type is a government-issued document
     * @return true if government-issued, false otherwise
     */
    public boolean isGovernmentIssued() {
        return this == NATIONAL_ID || this == DRIVING_LICENSE || this == PASSPORT || this == VOTER_ID || this == BIRTH_CERTIFICATE;
    }

    /**
     * Check if this ID type is an institutional document
     * @return true if institutional, false otherwise
     */
    public boolean isInstitutional() {
        return this == STUDENT_ID || this == EMPLOYEE_ID;
    }

    /**
     * Check if this ID type is a primary identification document
     * @return true if primary, false otherwise
     */
    public boolean isPrimary() {
        return this == NATIONAL_ID || this == PASSPORT;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
