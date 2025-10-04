package com.taarifu_engine_api.modules.common.domain.enums;

public enum PasswordStrength {
    WEAK("WEAK", "Weak", "Password is too weak and should be strengthened. Contains less than 8 characters, no uppercase, no numbers, or no special characters", "#DC2626"),
    FAIR("FAIR", "Fair", "Password meets minimum requirements but could be stronger. Contains 8+ characters with basic complexity (uppercase, lowercase, numbers)", "#F59E0B"),
    GOOD("GOOD", "Good", "Password is reasonably strong. Contains 10+ characters with uppercase, lowercase, numbers, and at least one special character", "#10B981"),
    STRONG("STRONG", "Strong", "Password is very strong and secure. Contains 12+ characters with uppercase, lowercase, numbers, special characters, and no common patterns", "#059669");

    private final String name;
    private final String displayName;
    private final String description;
    private final String colorCode;

    PasswordStrength(String name, String displayName, String description, String colorCode) {
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
