package com.taarifu_engine_api.modules.notification.domain.dto;

import com.taarifu_engine_api.modules.notification.domain.enums.EmailStatus;
import com.taarifu_engine_api.modules.notification.domain.enums.EmailType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for email responses.
 * Used to return information about sent emails.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailResponseDto {

    /**
     * Unique identifier for the email.
     */
    private String emailId;

    /**
     * Recipient's email address.
     */
    private String to;

    /**
     * Email subject line.
     */
    private String subject;

    /**
     * Type of email that was sent.
     */
    private EmailType emailType;

    /**
     * Current status of the email.
     */
    private EmailStatus status;

    /**
     * Timestamp when the email was queued.
     */
    private LocalDateTime queuedAt;

    /**
     * Timestamp when the email was sent.
     */
    private LocalDateTime sentAt;

    /**
     * Timestamp when the email was delivered.
     */
    private LocalDateTime deliveredAt;

    /**
     * Error message if email sending failed.
     */
    private String errorMessage;

    /**
     * Number of retry attempts.
     */
    private int retryCount;

    /**
     * Maximum number of retry attempts.
     */
    private int maxRetries;
}
