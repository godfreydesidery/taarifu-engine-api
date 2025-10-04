package com.taarifu_engine_api.modules.common.domain.enums;

/**
 * Enum representing different types of administrative areas in Tanzania
 */
public enum AreaType {
    
    /**
     * Region - Top level administrative division
     */
    REGION("REGION", "Region", "Top level administrative division in Tanzania", "#1E40AF"),
    
    /**
     * District - Second level administrative division
     */
    DISTRICT("DISTRICT", "District", "Second level administrative division under region", "#059669"),
    
    /**
     * Ward - Third level administrative division
     */
    WARD("WARD", "Ward", "Third level administrative division under district", "#DC2626"),
    
    /**
     * Village - Fourth level administrative division
     */
    VILLAGE("VILLAGE", "Village", "Fourth level administrative division under ward", "#7C3AED"),
    
    /**
     * Hamlet - Fifth level administrative division
     */
    HAMLET("HAMLET", "Hamlet", "Fifth level administrative division under village", "#EA580C"),
    
    /**
     * Constituency - Political/parliamentary division
     */
    CONSTITUENCY("CONSTITUENCY", "Constituency", "Political/parliamentary division for elections", "#0891B2");
    
    private final String name;
    private final String displayName;
    private final String description;
    private final String colorCode;
    
    AreaType(String name, String displayName, String description, String colorCode) {
        this.name = name;
        this.displayName = displayName;
        this.description = description;
        this.colorCode = colorCode;
    }
    
    /**
     * Get the name of the area type
     * @return the name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Get the display name of the area type
     * @return the display name
     */
    public String getDisplayName() {
        return displayName;
    }
    
    
    /**
     * Get the description of the area type
     * @return the description
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Get the color code for the area type
     * @return the color code (hex format)
     */
    public String getColorCode() {
        return colorCode;
    }
    
    /**
     * Get the hierarchical level of the area type
     * @return the level (1 = highest, 5 = lowest)
     */
    public int getLevel() {
        return switch (this) {
            case REGION -> 1;
            case DISTRICT -> 2;
            case WARD -> 3;
            case VILLAGE -> 4;
            case HAMLET -> 5;
            case CONSTITUENCY -> 2; // Same level as district but different purpose
        };
    }
    
    /**
     * Check if this area type is administrative
     * @return true if administrative, false if political
     */
    public boolean isAdministrative() {
        return this != CONSTITUENCY;
    }
    
    /**
     * Check if this area type is political
     * @return true if political, false if administrative
     */
    public boolean isPolitical() {
        return this == CONSTITUENCY;
    }
    
    /**
     * Get the parent area type for this area type
     * @return the parent area type, or null if this is the top level
     */
    public AreaType getParentType() {
        return switch (this) {
            case REGION -> null;
            case DISTRICT, CONSTITUENCY -> REGION;
            case WARD -> DISTRICT;
            case VILLAGE -> WARD;
            case HAMLET -> VILLAGE;
        };
    }
    
    /**
     * Get the child area types for this area type
     * @return array of child area types
     */
    public AreaType[] getChildTypes() {
        return switch (this) {
            case REGION -> new AreaType[]{DISTRICT, CONSTITUENCY};
            case DISTRICT -> new AreaType[]{WARD};
            case WARD -> new AreaType[]{VILLAGE};
            case VILLAGE -> new AreaType[]{HAMLET};
            case HAMLET, CONSTITUENCY -> new AreaType[]{};
        };
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}
