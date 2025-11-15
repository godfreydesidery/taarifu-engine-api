package com.taarifu_engine_api.modules.notification.service;

import com.taarifu_engine_api.modules.notification.config.SmsProperties;
import com.taarifu_engine_api.modules.notification.domain.dto.SmsRequestDto;
import com.taarifu_engine_api.modules.notification.domain.dto.SmsResponseDto;
import com.taarifu_engine_api.modules.notification.domain.enums.SmsStatus;
import com.taarifu_engine_api.modules.notification.domain.enums.SmsType;
import com.taarifu_engine_api.modules.notification.sms.SmsEvent;
import com.taarifu_engine_api.modules.notification.sms.provider.SmsProvider;
import com.taarifu_engine_api.modules.notification.util.PhoneNumberUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation of SmsService interface.
 * Provides both synchronous and asynchronous SMS sending capabilities using event-driven architecture.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SmsServiceImpl implements SmsService {

    private final ApplicationEventPublisher eventPublisher;
    private final SmsProvider smsProvider;
    private final SmsProperties smsProperties;
    
    // In-memory storage for SMS status tracking (in production, use a database)
    private final Map<String, SmsResponseDto> smsStatusMap = new ConcurrentHashMap<>();

    @Override
    public SmsResponseDto sendSmsAsync(SmsRequestDto smsRequest) {
        if (!isSmsEnabled()) {
            log.warn("SMS service is disabled. Skipping SMS to: {}", smsRequest.getTo());
            return createDisabledResponse(smsRequest);
        }

        log.info("Publishing SMS event for async processing: {} to {}", 
                smsRequest.getSmsType(), smsRequest.getTo());
        
        // Format phone number
        try {
            smsRequest.setTo(PhoneNumberUtil.formatToInternational(smsRequest.getTo()));
        } catch (IllegalArgumentException e) {
            log.error("Invalid phone number format: {}", smsRequest.getTo(), e);
            SmsResponseDto errorResponse = createSmsResponse(smsRequest, SmsStatus.FAILED);
            errorResponse.setErrorMessage("Invalid phone number format: " + e.getMessage());
            return errorResponse;
        }
        
        // Create SMS response with pending status
        SmsResponseDto smsResponse = createSmsResponse(smsRequest, SmsStatus.PENDING);
        smsStatusMap.put(smsResponse.getSmsId(), smsResponse);
        
        // Publish event for asynchronous processing with SMS ID
        eventPublisher.publishEvent(new SmsEvent(this, smsRequest, smsResponse.getSmsId()));
        
        log.info("SMS event published successfully with ID: {}", smsResponse.getSmsId());
        return smsResponse;
    }

    @Override
    public SmsResponseDto sendSmsSync(SmsRequestDto smsRequest) {
        if (!isSmsEnabled()) {
            log.warn("SMS service is disabled. Skipping SMS to: {}", smsRequest.getTo());
            return createDisabledResponse(smsRequest);
        }

        log.info("Sending SMS synchronously: {} to {}", 
                smsRequest.getSmsType(), smsRequest.getTo());
        
        // Format phone number
        try {
            smsRequest.setTo(PhoneNumberUtil.formatToInternational(smsRequest.getTo()));
        } catch (IllegalArgumentException e) {
            log.error("Invalid phone number format: {}", smsRequest.getTo(), e);
            SmsResponseDto errorResponse = createSmsResponse(smsRequest, SmsStatus.FAILED);
            errorResponse.setErrorMessage("Invalid phone number format: " + e.getMessage());
            return errorResponse;
        }
        
        SmsResponseDto smsResponse = createSmsResponse(smsRequest, SmsStatus.PROCESSING);
        smsStatusMap.put(smsResponse.getSmsId(), smsResponse);
        
        try {
            // Send SMS immediately
            SmsResponseDto providerResponse = sendSmsInternal(smsRequest);
            
            // Update status from provider response
            smsResponse.setStatus(providerResponse.getStatus());
            smsResponse.setSentAt(providerResponse.getSentAt());
            smsResponse.setDeliveredAt(providerResponse.getDeliveredAt());
            smsResponse.setErrorMessage(providerResponse.getErrorMessage());
            smsStatusMap.put(smsResponse.getSmsId(), smsResponse);
            
            log.info("SMS sent successfully with ID: {}", smsResponse.getSmsId());
            return smsResponse;
            
        } catch (Exception e) {
            log.error("Failed to send SMS with ID: {}", smsResponse.getSmsId(), e);
            
            // Update status to failed
            smsResponse.setStatus(SmsStatus.FAILED);
            smsResponse.setErrorMessage(e.getMessage());
            smsStatusMap.put(smsResponse.getSmsId(), smsResponse);
            
            return smsResponse;
        }
    }

    @Override
    public SmsResponseDto sendSimpleSms(String phoneNumber, String message, SmsType smsType) {
        SmsRequestDto smsRequest = new SmsRequestDto();
        smsRequest.setTo(phoneNumber);
        smsRequest.setMessage(message);
        smsRequest.setSmsType(smsType);
        
        return sendSmsSync(smsRequest);
    }

    @Override
    public SmsResponseDto sendSimpleSmsAsync(String phoneNumber, String message, SmsType smsType) {
        SmsRequestDto smsRequest = new SmsRequestDto();
        smsRequest.setTo(phoneNumber);
        smsRequest.setMessage(message);
        smsRequest.setSmsType(smsType);
        
        return sendSmsAsync(smsRequest);
    }

    @Override
    public SmsResponseDto sendTemplateSms(String phoneNumber, String templateName, 
                                         Map<String, Object> templateVariables, SmsType smsType) {
        // For now, simple template substitution
        // In production, integrate with a template engine
        String message = processTemplate(templateName, templateVariables);
        
        SmsRequestDto smsRequest = new SmsRequestDto();
        smsRequest.setTo(phoneNumber);
        smsRequest.setMessage(message);
        smsRequest.setSmsType(smsType);
        smsRequest.setTemplateVariables(templateVariables);
        
        return sendSmsSync(smsRequest);
    }

    @Override
    public SmsResponseDto sendTemplateSmsAsync(String phoneNumber, String templateName, 
                                              Map<String, Object> templateVariables, SmsType smsType) {
        String message = processTemplate(templateName, templateVariables);
        
        SmsRequestDto smsRequest = new SmsRequestDto();
        smsRequest.setTo(phoneNumber);
        smsRequest.setMessage(message);
        smsRequest.setSmsType(smsType);
        smsRequest.setTemplateVariables(templateVariables);
        
        return sendSmsAsync(smsRequest);
    }

    @Override
    public SmsResponseDto getSmsStatus(String smsId) {
        return smsStatusMap.get(smsId);
    }

    @Override
    public List<SmsResponseDto> getSmsByRecipient(String phoneNumber) {
        try {
            String formattedNumber = PhoneNumberUtil.formatToInternational(phoneNumber);
            return smsStatusMap.values().stream()
                    .filter(sms -> sms.getTo().equals(formattedNumber))
                    .toList();
        } catch (IllegalArgumentException e) {
            log.warn("Invalid phone number format: {}", phoneNumber);
            return List.of();
        }
    }

    @Override
    public List<SmsResponseDto> getSmsByType(SmsType smsType) {
        return smsStatusMap.values().stream()
                .filter(sms -> sms.getSmsType() == smsType)
                .toList();
    }

    @Override
    public SmsResponseDto retrySms(String smsId) {
        SmsResponseDto smsResponse = smsStatusMap.get(smsId);
        if (smsResponse == null) {
            throw new IllegalArgumentException("SMS not found with ID: " + smsId);
        }
        
        if (smsResponse.getStatus() != SmsStatus.FAILED) {
            throw new IllegalStateException("Can only retry failed SMS messages");
        }
        
        // Check retry limit
        if (smsResponse.getRetryCount() >= smsResponse.getMaxRetries()) {
            throw new IllegalStateException("Maximum retry attempts reached");
        }
        
        // Increment retry count
        smsResponse.setRetryCount(smsResponse.getRetryCount() + 1);
        smsResponse.setStatus(SmsStatus.PENDING);
        smsResponse.setErrorMessage(null);
        
        // Create new SMS request and send
        SmsRequestDto smsRequest = new SmsRequestDto();
        smsRequest.setTo(smsResponse.getTo());
        smsRequest.setMessage(smsResponse.getMessage());
        smsRequest.setSmsType(smsResponse.getSmsType());
        
        return sendSmsAsync(smsRequest);
    }

    @Override
    public boolean validateSmsConfiguration() {
        if (!isSmsEnabled()) {
            log.warn("SMS service is disabled");
            return false;
        }
        
        if (smsProperties.getUsername() == null || smsProperties.getUsername().isEmpty()) {
            log.error("SMS username/API key is not configured");
            return false;
        }
        
        if (smsProperties.getPassword() == null || smsProperties.getPassword().isEmpty()) {
            log.error("SMS password/API secret is not configured");
            return false;
        }
        
        log.info("SMS configuration validation successful");
        return true;
    }

    /**
     * Updates SMS status in the status map.
     * Used by event listener to update status after async processing.
     */
    public void updateSmsStatus(String smsId, SmsResponseDto updatedResponse) {
        SmsResponseDto existingResponse = smsStatusMap.get(smsId);
        if (existingResponse != null) {
            existingResponse.setStatus(updatedResponse.getStatus());
            if (updatedResponse.getSentAt() != null) {
                existingResponse.setSentAt(updatedResponse.getSentAt());
            }
            if (updatedResponse.getDeliveredAt() != null) {
                existingResponse.setDeliveredAt(updatedResponse.getDeliveredAt());
            }
            if (updatedResponse.getErrorMessage() != null) {
                existingResponse.setErrorMessage(updatedResponse.getErrorMessage());
            }
            smsStatusMap.put(smsId, existingResponse);
        }
    }

    /**
     * Internal method to send SMS using SMS provider.
     * This method is called by both sync and async processing.
     */
    public SmsResponseDto sendSmsInternal(SmsRequestDto smsRequest) {
        try {
            return smsProvider.sendSms(
                smsRequest.getTo(),
                smsRequest.getMessage(),
                smsProperties.getSenderId()
            );
        } catch (Exception e) {
            log.error("Exception in SMS provider for: {}", smsRequest.getTo(), e);
            SmsResponseDto errorResponse = new SmsResponseDto();
            errorResponse.setSmsId(UUID.randomUUID().toString());
            errorResponse.setTo(smsRequest.getTo());
            errorResponse.setMessage(smsRequest.getMessage());
            errorResponse.setSmsType(smsRequest.getSmsType());
            errorResponse.setStatus(SmsStatus.FAILED);
            errorResponse.setErrorMessage(e.getMessage());
            return errorResponse;
        }
    }

    /**
     * Creates an SMS response DTO.
     */
    private SmsResponseDto createSmsResponse(SmsRequestDto smsRequest, SmsStatus status) {
        SmsResponseDto response = new SmsResponseDto();
        response.setSmsId(UUID.randomUUID().toString());
        response.setTo(smsRequest.getTo());
        response.setMessage(smsRequest.getMessage());
        response.setSmsType(smsRequest.getSmsType());
        response.setStatus(status);
        response.setQueuedAt(LocalDateTime.now());
        response.setRetryCount(0);
        response.setMaxRetries(3);
        return response;
    }

    /**
     * Creates a response when SMS is disabled.
     */
    private SmsResponseDto createDisabledResponse(SmsRequestDto smsRequest) {
        SmsResponseDto response = createSmsResponse(smsRequest, SmsStatus.FAILED);
        response.setErrorMessage("SMS service is disabled");
        return response;
    }

    /**
     * Checks if SMS service is enabled.
     */
    private boolean isSmsEnabled() {
        return smsProperties.getEnabled() != null && smsProperties.getEnabled();
    }

    /**
     * Processes a template with variables.
     * Simple placeholder replacement for now.
     */
    private String processTemplate(String templateName, Map<String, Object> variables) {
        // Simple template processing - replace ${variable} with values
        String template = getTemplateContent(templateName);
        if (variables != null) {
            for (Map.Entry<String, Object> entry : variables.entrySet()) {
                template = template.replace("${" + entry.getKey() + "}", 
                    entry.getValue() != null ? entry.getValue().toString() : "");
            }
        }
        return template;
    }

    /**
     * Gets template content by name.
     * In production, load from files or database.
     */
    private String getTemplateContent(String templateName) {
        // Placeholder - return template name as message for now
        // In production, load from template files
        return "Template: " + templateName;
    }
}

