package com.taarifu_engine_api.modules.notification.domain.enums;

/**
 * Enum representing different types of SMS messages that can be sent by the system.
 * Used to categorize and handle different SMS templates and processing logic.
 */
public enum SmsType {
    
    /**
     * Welcome SMS for new users
     */
    WELCOME("welcome", "Welcome", "Welcome SMS for new users", "#10B981"),
    
    /**
     * User account created notification
     */
    USER_CREATED("user_created", "User Created", "Notification when a new user account is created", "#059669"),
    
    /**
     * Admin user created notification
     */
    ADMIN_USER_CREATED("admin_user_created", "Admin User Created", "Notification when a new admin user is created", "#DC2626"),
    
    /**
     * Password reset OTP/code
     */
    PASSWORD_RESET("password_reset", "Password Reset", "Password reset OTP/code", "#F59E0B"),
    
    /**
     * Email verification OTP/code
     */
    EMAIL_VERIFICATION("email_verification", "Email Verification", "Email verification OTP/code", "#3B82F6"),
    
    /**
     * General notification
     */
    NOTIFICATION("notification", "Notification", "General notification SMS", "#6B7280");

    private final String code;
    private final String displayName;
    private final String description;
    private final String colorCode;

    SmsType(String code, String displayName, String description, String colorCode) {
        this.code = code;
        this.displayName = displayName;
        this.description = description;
        this.colorCode = colorCode;
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

    public String getColorCode() {
        return colorCode;
    }

    @Override
    public String toString() {
        return displayName;
    }
}

