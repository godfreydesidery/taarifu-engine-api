package com.taarifu_engine_api.modules.common.domain.enums;

/**
 * Enum representing different user designations in the system.
 * Designations are manually assigned by administrators and represent user roles or titles.
 */
public enum Designation {
    
    /**
     * Regular citizen designation
     */
    CITIZEN("CITIZEN", "Citizen", "Regular citizen of the system"),
    
    /**
     * Member of Parliament designation
     */
    MP("MP", "Member of Parliament", "Elected member of parliament"),
    
    /**
     * MP Assistant designation
     */
    MP_ASSISTANT("MP_ASSISTANT", "MP Assistant", "Assistant to a Member of Parliament"),
    
    /**
     * Organization designation
     */
    ORGANIZATION("ORGANIZATION", "Organization", "Organizational user account");

    private final String code;
    private final String displayName;
    private final String description;

    Designation(String code, String displayName, String description) {
        this.code = code;
        this.displayName = displayName;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
