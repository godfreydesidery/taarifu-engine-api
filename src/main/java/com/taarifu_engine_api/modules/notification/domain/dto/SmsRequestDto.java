package com.taarifu_engine_api.modules.notification.domain.dto;

import com.taarifu_engine_api.modules.notification.domain.enums.SmsType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Data Transfer Object for SMS requests.
 * Used to send SMS messages through the SMS service.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SmsRequestDto {

    /**
     * Recipient's phone number (international format: +255XXXXXXXXX)
     */
    @NotBlank(message = "Recipient phone number is required")
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number format")
    private String to;

    /**
     * SMS message content.
     */
    @NotBlank(message = "SMS message is required")
    private String message;

    /**
     * Type of SMS being sent.
     */
    @NotNull(message = "SMS type is required")
    private SmsType smsType;

    /**
     * Priority of the SMS (1 = highest, 5 = lowest).
     */
    private int priority = 3;

    /**
     * Template variables for dynamic content (if using templates).
     */
    private Map<String, Object> templateVariables;
}

