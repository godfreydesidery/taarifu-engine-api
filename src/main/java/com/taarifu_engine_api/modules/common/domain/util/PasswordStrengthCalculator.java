package com.taarifu_engine_api.modules.common.domain.util;

import com.taarifu_engine_api.modules.common.domain.enums.PasswordStrength;

import java.util.regex.Pattern;

/**
 * Utility class for calculating password strength based on various criteria.
 * Provides methods to evaluate password complexity and return appropriate strength levels.
 */
public class PasswordStrengthCalculator {

    // Common weak passwords patterns
    private static final String[] COMMON_PASSWORDS = {
        "password", "123456", "123456789", "qwerty", "abc123", "password123",
        "admin", "letmein", "welcome", "monkey", "1234567890", "password1",
        "qwerty123", "dragon", "master", "hello", "freedom", "whatever",
        "qazwsx", "trustno1", "654321", "jordan23", "harley", "password1",
        "1234", "robert", "matthew", "jordan", "asshole", "daniel"
    };

    // Regex patterns for different character types
    private static final Pattern UPPERCASE_PATTERN = Pattern.compile("[A-Z]");
    private static final Pattern LOWERCASE_PATTERN = Pattern.compile("[a-z]");
    private static final Pattern DIGIT_PATTERN = Pattern.compile("[0-9]");
    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile("[^a-zA-Z0-9]");
    private static final Pattern REPEATING_CHAR_PATTERN = Pattern.compile("(.)\\1{2,}");
    private static final Pattern SEQUENTIAL_PATTERN = Pattern.compile("(?:012|123|234|345|456|567|678|789|890|abc|bcd|cde|def|efg|fgh|ghi|hij|ijk|jkl|klm|lmn|mno|nop|opq|pqr|qrs|rst|stu|tuv|uvw|vwx|wxy|xyz)");

    /**
     * Calculates the strength of a given password.
     *
     * @param password the password to evaluate
     * @return the calculated password strength
     */
    public static PasswordStrength calculatePasswordStrength(String password) {
        if (password == null || password.trim().isEmpty()) {
            return PasswordStrength.WEAK;
        }

        // Check for common passwords
        if (isCommonPassword(password)) {
            return PasswordStrength.WEAK;
        }

        int score = 0;
        int length = password.length();

        // Length scoring
        if (length >= 8) score += 1;
        if (length >= 10) score += 1;
        if (length >= 12) score += 1;
        if (length >= 16) score += 1;

        // Character type scoring
        if (UPPERCASE_PATTERN.matcher(password).find()) score += 1;
        if (LOWERCASE_PATTERN.matcher(password).find()) score += 1;
        if (DIGIT_PATTERN.matcher(password).find()) score += 1;
        if (SPECIAL_CHAR_PATTERN.matcher(password).find()) score += 1;

        // Complexity scoring
        if (hasNoRepeatingCharacters(password)) score += 1;
        if (hasNoSequentialPatterns(password)) score += 1;
        if (hasGoodCharacterDistribution(password)) score += 1;

        // Determine strength based on score
        if (score < 4) {
            return PasswordStrength.WEAK;
        } else if (score < 6) {
            return PasswordStrength.FAIR;
        } else if (score < 8) {
            return PasswordStrength.GOOD;
        } else {
            return PasswordStrength.STRONG;
        }
    }

    /**
     * Checks if the password is a common weak password.
     *
     * @param password the password to check
     * @return true if the password is common, false otherwise
     */
    private static boolean isCommonPassword(String password) {
        String lowerPassword = password.toLowerCase();
        for (String common : COMMON_PASSWORDS) {
            if (lowerPassword.contains(common) || common.contains(lowerPassword)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the password has no repeating characters (3 or more consecutive).
     *
     * @param password the password to check
     * @return true if no repeating characters, false otherwise
     */
    private static boolean hasNoRepeatingCharacters(String password) {
        return !REPEATING_CHAR_PATTERN.matcher(password).find();
    }

    /**
     * Checks if the password has no sequential patterns.
     *
     * @param password the password to check
     * @return true if no sequential patterns, false otherwise
     */
    private static boolean hasNoSequentialPatterns(String password) {
        return !SEQUENTIAL_PATTERN.matcher(password.toLowerCase()).find();
    }

    /**
     * Checks if the password has good character distribution.
     *
     * @param password the password to check
     * @return true if good distribution, false otherwise
     */
    private static boolean hasGoodCharacterDistribution(String password) {
        int length = password.length();
        int upperCount = 0, lowerCount = 0, digitCount = 0, specialCount = 0;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) upperCount++;
            else if (Character.isLowerCase(c)) lowerCount++;
            else if (Character.isDigit(c)) digitCount++;
            else specialCount++;
        }

        // Good distribution means no single character type dominates
        double upperRatio = (double) upperCount / length;
        double lowerRatio = (double) lowerCount / length;
        double digitRatio = (double) digitCount / length;
        double specialRatio = (double) specialCount / length;

        return upperRatio <= 0.6 && lowerRatio <= 0.6 && digitRatio <= 0.6 && specialRatio <= 0.6;
    }

    /**
     * Validates if a password meets the minimum strength requirement for admin users.
     * Admin users require STRONG password strength.
     *
     * @param password the password to validate
     * @return true if password meets admin requirements, false otherwise
     */
    public static boolean meetsAdminPasswordRequirements(String password) {
        return calculatePasswordStrength(password) == PasswordStrength.STRONG;
    }

    /**
     * Gets a descriptive message about password requirements for admin users.
     *
     * @return a string describing admin password requirements
     */
    public static String getAdminPasswordRequirements() {
        return "Admin passwords must be STRONG and contain: " +
               "at least 12 characters, uppercase letters, lowercase letters, " +
               "numbers, special characters, no repeating characters, " +
               "no sequential patterns, and no common passwords.";
    }
}
