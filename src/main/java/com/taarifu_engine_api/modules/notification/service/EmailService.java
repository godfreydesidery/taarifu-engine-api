package com.taarifu_engine_api.modules.notification.service;

import com.taarifu_engine_api.modules.notification.domain.dto.EmailRequestDto;
import com.taarifu_engine_api.modules.notification.domain.dto.EmailResponseDto;
import com.taarifu_engine_api.modules.notification.domain.enums.EmailType;

import java.util.List;

/**
 * Service interface for email operations.
 * Provides methods for sending emails both synchronously and asynchronously.
 */
public interface EmailService {

    /**
     * Sends an email asynchronously using event-driven architecture.
     * This method publishes an EmailEvent and returns immediately without blocking.
     *
     * @param emailRequest the email request data
     * @return email response with queued status
     */
    EmailResponseDto sendEmailAsync(EmailRequestDto emailRequest);

    /**
     * Sends an email synchronously.
     * This method blocks until the email is sent or fails.
     *
     * @param emailRequest the email request data
     * @return email response with final status
     */
    EmailResponseDto sendEmailSync(EmailRequestDto emailRequest);

    /**
     * Sends a simple email with basic parameters.
     *
     * @param to recipient email address
     * @param subject email subject
     * @param body email body
     * @param emailType type of email
     * @return email response
     */
    EmailResponseDto sendSimpleEmail(String to, String subject, String body, EmailType emailType);

    /**
     * Sends a simple email asynchronously.
     *
     * @param to recipient email address
     * @param subject email subject
     * @param body email body
     * @param emailType type of email
     * @return email response
     */
    EmailResponseDto sendSimpleEmailAsync(String to, String subject, String body, EmailType emailType);

    /**
     * Sends an email using a template with variables.
     *
     * @param to recipient email address
     * @param subject email subject
     * @param templateName name of the email template
     * @param templateVariables variables to substitute in the template
     * @param emailType type of email
     * @return email response
     */
    EmailResponseDto sendTemplateEmail(String to, String subject, String templateName, 
                                     java.util.Map<String, Object> templateVariables, EmailType emailType);

    /**
     * Sends an email using a template asynchronously.
     *
     * @param to recipient email address
     * @param subject email subject
     * @param templateName name of the email template
     * @param templateVariables variables to substitute in the template
     * @param emailType type of email
     * @return email response
     */
    EmailResponseDto sendTemplateEmailAsync(String to, String subject, String templateName, 
                                          java.util.Map<String, Object> templateVariables, EmailType emailType);

    /**
     * Gets the status of a sent email by email ID.
     *
     * @param emailId the email identifier
     * @return email response with current status
     */
    EmailResponseDto getEmailStatus(String emailId);

    /**
     * Gets all emails sent to a specific recipient.
     *
     * @param recipientEmail recipient email address
     * @return list of email responses
     */
    List<EmailResponseDto> getEmailsByRecipient(String recipientEmail);

    /**
     * Gets all emails of a specific type.
     *
     * @param emailType the email type
     * @return list of email responses
     */
    List<EmailResponseDto> getEmailsByType(EmailType emailType);

    /**
     * Retries sending a failed email.
     *
     * @param emailId the email identifier
     * @return email response with retry status
     */
    EmailResponseDto retryEmail(String emailId);

    /**
     * Validates email configuration and connectivity.
     *
     * @return true if email service is properly configured and accessible
     */
    boolean validateEmailConfiguration();
}
