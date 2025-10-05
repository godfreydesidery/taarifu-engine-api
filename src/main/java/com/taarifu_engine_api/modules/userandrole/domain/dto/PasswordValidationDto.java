package com.taarifu_engine_api.modules.userandrole.domain.dto;

import com.taarifu_engine_api.modules.common.domain.enums.PasswordStrength;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Data Transfer Object for password validation feedback.
 * Used to provide real-time password strength feedback to frontend.
 * 
 * Frontend Usage: Use this for password strength indicators, validation messages, and user guidance.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordValidationDto {

    /**
     * Overall password strength rating.
     * Frontend Note: Use this to show strength meter (Weak, Fair, Good, Strong).
     * Values: WEAK, FAIR, GOOD, STRONG
     */
    private PasswordStrength strength;

    /**
     * Whether the password meets minimum requirements.
     * Frontend Note: Use this to enable/disable submit buttons.
     */
    private Boolean isValid;

    /**
     * List of validation messages for the password.
     * Frontend Note: Display these as bullet points or inline messages.
     */
    private List<String> messages;
}
