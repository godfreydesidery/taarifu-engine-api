package com.taarifu_engine_api.modules.notification.sms.provider;

import com.taarifu_engine_api.modules.notification.domain.dto.SmsResponseDto;
import com.taarifu_engine_api.modules.notification.domain.enums.SmsStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * BLS MS Gateway SMS provider implementation.
 * Integrates with BLS MS Gateway API for sending SMS messages.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class BeemSmsProvider implements SmsProvider {

    private final RestTemplate restTemplate;

    @Value("${sms.username:}")
    private String username;

    @Value("${sms.password:}")
    private String password;

    @Value("${sms.sender-id:OTAPP}")
    private String senderId;

    private static final String BLS_API_URL = "https://api.blsmsgw.com:8443/bin/send";

    @Override
    public SmsResponseDto sendSms(String phoneNumber, String message, String senderIdParam) {
        String smsId = UUID.randomUUID().toString();
        SmsResponseDto response = new SmsResponseDto();
        response.setSmsId(smsId);
        response.setTo(phoneNumber);
        response.setMessage(message);
        response.setQueuedAt(LocalDateTime.now());
        response.setStatus(SmsStatus.PROCESSING);

        // Validate credentials before attempting to send
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            log.error("BLS MS Gateway SMS API credentials are not configured. Please set SMS_USERNAME and SMS_PASSWORD environment variables.");
            response.setStatus(SmsStatus.FAILED);
            response.setErrorMessage("SMS API credentials not configured");
            return response;
        }

        try {
            // Use provided senderId or fall back to configured default
            String finalSenderId = senderIdParam != null && !senderIdParam.isEmpty() 
                ? senderIdParam 
                : (this.senderId != null ? this.senderId : "OTAPP");

            // Format phone number to 255XXXXXXXXX format (remove + if present)
            String formattedPhone = formatPhoneNumber(phoneNumber);

            log.info("Sending SMS to: {} with sender ID: {}", formattedPhone, finalSenderId);

            // Build the URL with query parameters using UriComponentsBuilder
            // This handles encoding properly without double encoding
            String url = UriComponentsBuilder.fromHttpUrl(BLS_API_URL)
                .queryParam("USERNAME", username)
                .queryParam("PASSWORD", password)
                .queryParam("DESTADDR", formattedPhone)
                .queryParam("SOURCEADDR", finalSenderId)
                .queryParam("MESSAGE", message)
                .build()
                .toUriString();

            log.debug("Sending SMS request to: {}", BLS_API_URL);
            log.debug("Destination: {}, Sender ID: {}", formattedPhone, finalSenderId);

            // Make GET request to BLS MS Gateway API
            ResponseEntity<String> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                String.class
            );

            // Log the full response for debugging
            log.debug("SMS Gateway Response - Status: {}, Body: '{}'", responseEntity.getStatusCode(), responseEntity.getBody());

            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                String responseBody = responseEntity.getBody();

                // Check if response indicates success
                // BLS MS Gateway returns format:
                // <MESSAGE_ID>
                // <ERROR_CODE>
                // <STATUS>
                // Error code "0" means success
                if (responseBody != null && !responseBody.trim().isEmpty()) {
                    String[] lines = responseBody.trim().split("\\r?\\n");
                    if (lines.length >= 2) {
                        String messageId = lines[0].trim();
                        String errorCode = lines[1].trim();

                        if ("0".equals(errorCode)) {
                            response.setStatus(SmsStatus.SENT);
                            response.setSentAt(LocalDateTime.now());
                            log.info("SMS sent successfully to: {}. Message ID: {}", formattedPhone, messageId);
                        } else {
                            String errorMessage = lines.length >= 3 ? lines[2].trim() : "Unknown error";
                            response.setStatus(SmsStatus.FAILED);
                            response.setErrorMessage("SMS Gateway error: " + errorMessage + " (Code: " + errorCode + ")");
                            log.error("SMS Gateway returned error code '{}' with message '{}' for phone: {}", 
                                    errorCode, errorMessage, formattedPhone);
                        }
                    } else {
                        // If response format is unexpected, but status is OK, assume success
                        response.setStatus(SmsStatus.SENT);
                        response.setSentAt(LocalDateTime.now());
                        log.warn("SMS Gateway returned unexpected response format: '{}' for phone: {}. Assuming success.", 
                                responseBody, formattedPhone);
                    }
                } else {
                    // Empty response but 200 OK - assume success
                    response.setStatus(SmsStatus.SENT);
                    response.setSentAt(LocalDateTime.now());
                    log.warn("SMS Gateway returned empty response for phone: {}. Assuming success.", formattedPhone);
                }
            } else {
                response.setStatus(SmsStatus.FAILED);
                String errorMsg = responseEntity.getBody() != null 
                    ? responseEntity.getBody() 
                    : "HTTP " + responseEntity.getStatusCode().value();
                response.setErrorMessage(errorMsg);
                log.error("Failed to send SMS to: {}. Status: {}", formattedPhone, responseEntity.getStatusCode());
            }

        } catch (Exception e) {
            log.error("Exception while sending SMS via BLS MS Gateway API to: {}", phoneNumber, e);
            response.setStatus(SmsStatus.FAILED);
            response.setErrorMessage(e.getMessage());
        }

        return response;
    }

    /**
     * Format phone number to standard format 255XXXXXXXXX (without + prefix)
     * 
     * @param phoneNumber The input phone number
     * @return Formatted phone number without + prefix
     */
    private String formatPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Phone number cannot be null or empty");
        }

        // Remove all non-numeric characters
        String cleanedNumber = phoneNumber.replaceAll("\\D", "");

        // If number is 12 digits and starts with valid prefix, return as is
        if (cleanedNumber.length() == 12) {
            String[] validPrefixes = {"255", "254", "256", "243"}; // Tanzania, Kenya, Uganda, Congo
            for (String prefix : validPrefixes) {
                if (cleanedNumber.startsWith(prefix)) {
                    return cleanedNumber;
                }
            }
        }

        // If number starts with 0 and is 10 digits, replace 0 with 255
        if (cleanedNumber.length() == 10 && cleanedNumber.startsWith("0")) {
            return "255" + cleanedNumber.substring(1);
        }

        // If number is 9 digits, prepend 255
        if (cleanedNumber.length() == 9) {
            return "255" + cleanedNumber;
        }

        // If already in +255 format, remove the +
        if (cleanedNumber.startsWith("+255")) {
            return cleanedNumber.substring(1);
        }

        // Return as is if no transformation needed
        return cleanedNumber;
    }
}

