package com.taarifu_engine_api.modules.notification.sms.provider;

import com.taarifu_engine_api.modules.notification.domain.dto.SmsResponseDto;

/**
 * Interface for SMS provider implementations.
 * Allows for different SMS provider integrations (Beem, Twilio, etc.)
 */
public interface SmsProvider {

    /**
     * Sends an SMS message through the provider.
     *
     * @param phoneNumber recipient phone number in international format
     * @param message SMS message content
     * @param senderId sender ID or short code
     * @return SMS response with status and details
     */
    SmsResponseDto sendSms(String phoneNumber, String message, String senderId);
}

