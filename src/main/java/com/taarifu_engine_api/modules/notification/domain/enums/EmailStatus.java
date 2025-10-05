package com.taarifu_engine_api.modules.notification.domain.enums;

/**
 * Enum representing the status of an email during its lifecycle.
 * Used to track email processing and delivery status.
 */
public enum EmailStatus {
    
    PENDING("pending", "Pending", "Email is queued for sending", "#F59E0B"),
    PROCESSING("processing", "Processing", "Email is being processed", "#3B82F6"),
    SENT("sent", "Sent", "Email has been successfully sent", "#10B981"),
    DELIVERED("delivered", "Delivered", "Email has been delivered to recipient", "#059669"),
    FAILED("failed", "Failed", "Email sending failed", "#DC2626"),
    BOUNCED("bounced", "Bounced", "Email bounced back", "#EF4444"),
    REJECTED("rejected", "Rejected", "Email was rejected by recipient server", "#7C2D12");

    private final String code;
    private final String displayName;
    private final String description;
    private final String colorCode;

    EmailStatus(String code, String displayName, String description, String colorCode) {
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
