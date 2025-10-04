package com.taarifu_engine_api.modules.userandrole.domain.enums;

public enum UserStatus {
    ACTIVE("active", "Active", "User account is active and verified", "#059669"),
    INACTIVE("inactive", "Inactive", "User account is deactivated", "#6B7280"),
    SUSPENDED("suspended", "Suspended", "User account is temporarily suspended", "#DC2626"),
    PENDING_VERIFICATION("pending_verification", "Pending Verification", "User account is awaiting email verification", "#F59E0B");

    private final String name;
    private final String displayName;
    private final String description;
    private final String colorCode;

    UserStatus(String name, String displayName, String description, String colorCode) {
        this.name = name;
        this.displayName = displayName;
        this.description = description;
        this.colorCode = colorCode;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public String getColorCode() {
        return colorCode;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
