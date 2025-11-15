package com.taarifu_engine_api.modules.notification.domain.enums;

/**
 * Enum representing the status of an SMS during its lifecycle.
 * Used to track SMS processing and delivery status.
 */
public enum SmsStatus {
    
    PENDING("pending", "Pending", "SMS is queued for sending", "#F59E0B"),
    PROCESSING("processing", "Processing", "SMS is being processed", "#3B82F6"),
    SENT("sent", "Sent", "SMS has been successfully sent", "#10B981"),
    DELIVERED("delivered", "Delivered", "SMS has been delivered to recipient", "#059669"),
    FAILED("failed", "Failed", "SMS sending failed", "#DC2626"),
    REJECTED("rejected", "Rejected", "SMS was rejected by provider", "#7C2D12");

    private final String code;
    private final String displayName;
    private final String description;
    private final String colorCode;

    SmsStatus(String code, String displayName, String description, String colorCode) {
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

