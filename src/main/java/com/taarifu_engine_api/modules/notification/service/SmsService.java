package com.taarifu_engine_api.modules.notification.service;

import com.taarifu_engine_api.modules.notification.domain.dto.SmsRequestDto;
import com.taarifu_engine_api.modules.notification.domain.dto.SmsResponseDto;
import com.taarifu_engine_api.modules.notification.domain.enums.SmsType;

import java.util.List;
import java.util.Map;

/**
 * Service interface for SMS operations.
 * Provides methods for sending SMS messages both synchronously and asynchronously.
 */
public interface SmsService {

    /**
     * Sends an SMS asynchronously using event-driven architecture.
     * This method publishes an SmsEvent and returns immediately without blocking.
     *
     * @param smsRequest the SMS request data
     * @return SMS response with queued status
     */
    SmsResponseDto sendSmsAsync(SmsRequestDto smsRequest);

    /**
     * Sends an SMS synchronously.
     * This method blocks until the SMS is sent or fails.
     *
     * @param smsRequest the SMS request data
     * @return SMS response with final status
     */
    SmsResponseDto sendSmsSync(SmsRequestDto smsRequest);

    /**
     * Sends a simple SMS with basic parameters.
     *
     * @param phoneNumber recipient phone number
     * @param message SMS message content
     * @param smsType type of SMS
     * @return SMS response
     */
    SmsResponseDto sendSimpleSms(String phoneNumber, String message, SmsType smsType);

    /**
     * Sends a simple SMS asynchronously.
     *
     * @param phoneNumber recipient phone number
     * @param message SMS message content
     * @param smsType type of SMS
     * @return SMS response
     */
    SmsResponseDto sendSimpleSmsAsync(String phoneNumber, String message, SmsType smsType);

    /**
     * Sends an SMS using a template with variables.
     *
     * @param phoneNumber recipient phone number
     * @param templateName name of the SMS template
     * @param templateVariables variables to substitute in the template
     * @param smsType type of SMS
     * @return SMS response
     */
    SmsResponseDto sendTemplateSms(String phoneNumber, String templateName, 
                                  Map<String, Object> templateVariables, SmsType smsType);

    /**
     * Sends an SMS using a template asynchronously.
     *
     * @param phoneNumber recipient phone number
     * @param templateName name of the SMS template
     * @param templateVariables variables to substitute in the template
     * @param smsType type of SMS
     * @return SMS response
     */
    SmsResponseDto sendTemplateSmsAsync(String phoneNumber, String templateName, 
                                       Map<String, Object> templateVariables, SmsType smsType);

    /**
     * Gets the status of a sent SMS by SMS ID.
     *
     * @param smsId the SMS identifier
     * @return SMS response with current status
     */
    SmsResponseDto getSmsStatus(String smsId);

    /**
     * Gets all SMS messages sent to a specific recipient.
     *
     * @param phoneNumber recipient phone number
     * @return list of SMS responses
     */
    List<SmsResponseDto> getSmsByRecipient(String phoneNumber);

    /**
     * Gets all SMS messages of a specific type.
     *
     * @param smsType the SMS type
     * @return list of SMS responses
     */
    List<SmsResponseDto> getSmsByType(SmsType smsType);

    /**
     * Retries sending a failed SMS.
     *
     * @param smsId the SMS identifier
     * @return SMS response with retry status
     */
    SmsResponseDto retrySms(String smsId);

    /**
     * Validates SMS configuration and connectivity.
     *
     * @return true if SMS service is properly configured and accessible
     */
    boolean validateSmsConfiguration();
}

