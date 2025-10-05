package com.taarifu_engine_api.modules.notification.email;

import com.taarifu_engine_api.modules.notification.domain.dto.EmailRequestDto;
import com.taarifu_engine_api.modules.notification.domain.dto.EmailResponseDto;
import com.taarifu_engine_api.modules.notification.domain.enums.EmailStatus;
import com.taarifu_engine_api.modules.notification.service.EmailServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import jakarta.mail.MessagingException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Event listener for processing email events asynchronously.
 * This component listens for EmailEvent and processes them without blocking the main thread.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class EmailEventListener {

    private final EmailServiceImpl emailService;
    
    // In-memory storage for email status tracking (in production, use a database)
    private final Map<String, EmailResponseDto> emailStatusMap = new ConcurrentHashMap<>();

    /**
     * Handles EmailEvent asynchronously.
     * This method is called when an EmailEvent is published and processes the email sending.
     *
     * @param emailEvent the email event to process
     */
    @EventListener
    @Async
    public void handleEmailEvent(EmailEvent emailEvent) {
        EmailRequestDto emailRequest = emailEvent.getEmailRequest();
        String eventId = emailEvent.getEventId();
        
        log.info("Processing email event asynchronously: {} for recipient: {}", 
                eventId, emailRequest.getTo());
        
        try {
            // Update status to processing
            updateEmailStatus(eventId, EmailStatus.PROCESSING, null);
            
            // Send the email
            emailService.sendEmailSync(emailRequest);
            
            // Update status to sent
            updateEmailStatus(eventId, EmailStatus.SENT, null);
            
            log.info("Email event processed successfully: {} for recipient: {}", 
                    eventId, emailRequest.getTo());
            
        } catch (Exception e) {
            log.error("Failed to process email event: {} for recipient: {}", 
                    eventId, emailRequest.getTo(), e);
            
            // Update status to failed
            updateEmailStatus(eventId, EmailStatus.FAILED, e.getMessage());
            
            // Handle retry logic if needed
            handleEmailFailure(eventId, emailRequest, e);
        }
    }

    /**
     * Updates the status of an email in the tracking map.
     *
     * @param eventId the event identifier
     * @param status the new status
     * @param errorMessage error message if any
     */
    private void updateEmailStatus(String eventId, EmailStatus status, String errorMessage) {
        EmailResponseDto emailResponse = emailStatusMap.get(eventId);
        if (emailResponse != null) {
            emailResponse.setStatus(status);
            if (status == EmailStatus.SENT) {
                emailResponse.setSentAt(LocalDateTime.now());
            }
            if (errorMessage != null) {
                emailResponse.setErrorMessage(errorMessage);
            }
            emailStatusMap.put(eventId, emailResponse);
        }
    }

    /**
     * Handles email failure and implements retry logic.
     *
     * @param eventId the event identifier
     * @param emailRequest the original email request
     * @param exception the exception that caused the failure
     */
    private void handleEmailFailure(String eventId, EmailRequestDto emailRequest, Exception exception) {
        EmailResponseDto emailResponse = emailStatusMap.get(eventId);
        if (emailResponse == null) {
            return;
        }
        
        int currentRetryCount = emailResponse.getRetryCount();
        int maxRetries = emailResponse.getMaxRetries();
        
        if (currentRetryCount < maxRetries) {
            log.info("Retrying email sending for event: {} (attempt {}/{})", 
                    eventId, currentRetryCount + 1, maxRetries);
            
            // Increment retry count
            emailResponse.setRetryCount(currentRetryCount + 1);
            emailResponse.setStatus(EmailStatus.PENDING);
            emailResponse.setErrorMessage(null);
            
            // Schedule retry (in production, use a proper retry mechanism with delays)
            scheduleRetry(eventId, emailRequest);
        } else {
            log.error("Max retry attempts reached for email event: {}", eventId);
            emailResponse.setStatus(EmailStatus.FAILED);
            emailResponse.setErrorMessage("Max retry attempts reached: " + exception.getMessage());
        }
    }

    /**
     * Schedules a retry for failed email sending.
     * In production, implement proper retry scheduling with exponential backoff.
     *
     * @param eventId the event identifier
     * @param emailRequest the email request to retry
     */
    private void scheduleRetry(String eventId, EmailRequestDto emailRequest) {
        // Simple retry implementation - in production, use proper scheduling
        try {
            Thread.sleep(5000); // Wait 5 seconds before retry
            
            // Retry sending the email
            emailService.sendEmailSync(emailRequest);
            updateEmailStatus(eventId, EmailStatus.SENT, null);
            
            log.info("Email retry successful for event: {}", eventId);
            
        } catch (Exception e) {
            log.error("Email retry failed for event: {}", eventId, e);
            updateEmailStatus(eventId, EmailStatus.FAILED, e.getMessage());
        }
    }

    /**
     * Gets the current status of an email by event ID.
     *
     * @param eventId the event identifier
     * @return email response with current status
     */
    public EmailResponseDto getEmailStatus(String eventId) {
        return emailStatusMap.get(eventId);
    }

    /**
     * Gets all email statuses.
     *
     * @return map of all email statuses
     */
    public Map<String, EmailResponseDto> getAllEmailStatuses() {
        return Map.copyOf(emailStatusMap);
    }
}
