package com.taarifu_engine_api.modules.userandrole.domain.enums;

public enum UserType {
    ADMIN("admin", "Administrator", "Platform administrators with full system access", "#DC2626"),
    CITIZEN("citizen", "Citizen", "Individual citizens who can engage with leaders and report issues", "#059669"),
    ORGANIZATION("organization", "Civic Organization", "Non-profit organizations and community groups", "#7C3AED");

    private final String name;
    private final String displayName;
    private final String description;
    private final String colorCode;

    UserType(String name, String displayName, String description, String colorCode) {
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
