package com.taarifu_engine_api.modules.notification.util;

import java.util.regex.Pattern;

/**
 * Utility class for phone number formatting and validation.
 * Specifically handles Tanzania phone numbers.
 */
public class PhoneNumberUtil {

    private static final Pattern TANZANIA_PHONE_PATTERN = Pattern.compile("^(\\+?255|0)?([67]\\d{8})$");
    private static final String TANZANIA_COUNTRY_CODE = "+255";

    /**
     * Formats a phone number to international format (+255XXXXXXXXX).
     * Handles various input formats:
     * - 0712345678 -> +255712345678
     * - 255712345678 -> +255712345678
     * - +255712345678 -> +255712345678
     * - 712345678 -> +255712345678
     *
     * @param phoneNumber the phone number to format
     * @return formatted phone number in international format
     * @throws IllegalArgumentException if phone number is invalid
     */
    public static String formatToInternational(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Phone number cannot be null or empty");
        }

        // Remove all non-digit characters except +
        String cleaned = phoneNumber.replaceAll("[^+\\d]", "");

        // If already in international format
        if (cleaned.startsWith("+255")) {
            return cleaned;
        }

        // If starts with 255 (without +)
        if (cleaned.startsWith("255")) {
            return "+" + cleaned;
        }

        // If starts with 0 (local format)
        if (cleaned.startsWith("0")) {
            cleaned = cleaned.substring(1);
        }

        // Validate Tanzania mobile number (should start with 6 or 7 and be 9 digits)
        if (!cleaned.matches("^[67]\\d{8}$")) {
            throw new IllegalArgumentException("Invalid Tanzania phone number format: " + phoneNumber);
        }

        return TANZANIA_COUNTRY_CODE + cleaned;
    }

    /**
     * Validates if a phone number is a valid Tanzania mobile number.
     *
     * @param phoneNumber the phone number to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidTanzaniaNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return false;
        }

        try {
            String formatted = formatToInternational(phoneNumber);
            return formatted.startsWith(TANZANIA_COUNTRY_CODE) && 
                   formatted.length() == 13 && // +255 + 9 digits
                   (formatted.charAt(4) == '6' || formatted.charAt(4) == '7');
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Removes all formatting from a phone number (spaces, dashes, etc.).
     *
     * @param phoneNumber the phone number to clean
     * @return cleaned phone number with only digits and +
     */
    public static String removeFormatting(String phoneNumber) {
        if (phoneNumber == null) {
            return null;
        }
        return phoneNumber.replaceAll("[^+\\d]", "");
    }
}

