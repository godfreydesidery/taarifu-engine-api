package com.taarifu_engine_api.modules.notification.domain.enums;

/**
 * Enum representing different types of emails that can be sent by the system.
 * Used to categorize and handle different email templates and processing logic.
 */
public enum EmailType {
    
    // Admin User Management
    ADMIN_USER_CREATED("admin_user_created", "Admin User Created", "Notification when a new admin user is created"),
    ADMIN_USER_UPDATED("admin_user_updated", "Admin User Updated", "Notification when an admin user is updated"),
    ADMIN_USER_ACTIVATED("admin_user_activated", "Admin User Activated", "Notification when an admin user is activated"),
    ADMIN_USER_DEACTIVATED("admin_user_deactivated", "Admin User Deactivated", "Notification when an admin user is deactivated"),
    ADMIN_USER_SUSPENDED("admin_user_suspended", "Admin User Suspended", "Notification when an admin user is suspended"),
    
    // Authentication & Security
    PASSWORD_RESET("password_reset", "Password Reset", "Password reset request email"),
    FORGOT_PASSWORD("forgot_password", "Forgot Password", "Forgot password reset instructions"),
    PASSWORD_CHANGED("password_changed", "Password Changed", "Notification when password is changed"),
    NEW_PASSWORD_GENERATED("new_password_generated", "New Password Generated", "Notification when a new password is generated"),
    LOGIN_ALERT("login_alert", "Login Alert", "Security alert for new login"),
    
    // System Notifications
    SYSTEM_MAINTENANCE("system_maintenance", "System Maintenance", "System maintenance notification"),
    SYSTEM_ALERT("system_alert", "System Alert", "Critical system alert"),
    
    // General
    WELCOME("welcome", "Welcome", "Welcome email for new users"),
    NOTIFICATION("notification", "Notification", "General notification email");

    private final String code;
    private final String displayName;
    private final String description;

    EmailType(String code, String displayName, String description) {
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
