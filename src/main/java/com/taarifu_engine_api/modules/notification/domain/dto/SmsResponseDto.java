package com.taarifu_engine_api.modules.notification.domain.dto;

import com.taarifu_engine_api.modules.notification.domain.enums.SmsStatus;
import com.taarifu_engine_api.modules.notification.domain.enums.SmsType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for SMS responses.
 * Used to return information about sent SMS messages.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SmsResponseDto {

    /**
     * Unique identifier for the SMS.
     */
    private String smsId;

    /**
     * Recipient's phone number.
     */
    private String to;

    /**
     * SMS message content.
     */
    private String message;

    /**
     * Type of SMS that was sent.
     */
    private SmsType smsType;

    /**
     * Current status of the SMS.
     */
    private SmsStatus status;

    /**
     * Timestamp when the SMS was queued.
     */
    private LocalDateTime queuedAt;

    /**
     * Timestamp when the SMS was sent.
     */
    private LocalDateTime sentAt;

    /**
     * Timestamp when the SMS was delivered.
     */
    private LocalDateTime deliveredAt;

    /**
     * Error message if SMS sending failed.
     */
    private String errorMessage;

    /**
     * Number of retry attempts.
     */
    private int retryCount;

    /**
     * Maximum number of retry attempts.
     */
    private int maxRetries = 3;
}

