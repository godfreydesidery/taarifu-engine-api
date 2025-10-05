package com.taarifu_engine_api.modules.notification.domain.dto;

import com.taarifu_engine_api.modules.notification.domain.enums.EmailType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Map;

/**
 * Data Transfer Object for email requests.
 * Used to send emails through the email service.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailRequestDto {

    /**
     * Recipient's email address.
     */
    @NotBlank(message = "Recipient email is required")
    @Email(message = "Invalid email format")
    private String to;

    /**
     * Email subject line.
     */
    @NotBlank(message = "Email subject is required")
    private String subject;

    /**
     * Email body content (HTML or plain text).
     */
    @NotBlank(message = "Email body is required")
    private String body;

    /**
     * Type of email being sent.
     */
    @NotNull(message = "Email type is required")
    private EmailType emailType;

    /**
     * Optional CC recipients.
     */
    private String[] cc;

    /**
     * Optional BCC recipients.
     */
    private String[] bcc;

    /**
     * Optional reply-to address.
     */
    @Email(message = "Invalid reply-to email format")
    private String replyTo;

    /**
     * Template variables for dynamic content (if using templates).
     */
    private Map<String, Object> templateVariables;

    /**
     * Whether the email body is HTML format.
     */
    private boolean html = true;

    /**
     * Priority of the email (1 = highest, 5 = lowest).
     */
    private int priority = 3;

    /**
     * Optional attachment file paths.
     */
    private String[] attachments;

    /**
     * Custom headers for the email.
     */
    private Map<String, String> headers;
}
