package com.taarifu_engine_api.modules.common.domain.util;

import com.taarifu_engine_api.modules.common.domain.enums.PasswordStrength;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Utility class for generating secure passwords.
 * Provides methods to generate passwords that meet different strength requirements.
 */
public class PasswordGenerator {

    // Character sets for password generation
    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL_CHARS = "!@#$%^&*()_+-=[]{}|;:,.<>?";
    
    // Avoid ambiguous characters
    private static final String UPPERCASE_SAFE = "ABCDEFGHJKLMNPQRSTUVWXYZ";
    private static final String LOWERCASE_SAFE = "abcdefghijkmnpqrstuvwxyz";
    private static final String DIGITS_SAFE = "23456789";
    private static final String SPECIAL_CHARS_SAFE = "!@#$%^&*()_+-=[]{}|;:,.<>?";
    
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * Generates a secure password that meets STRONG requirements for admin users.
     * 
     * @return a strong password (12+ characters with all character types)
     */
    public static String generateStrongPassword() {
        return generatePassword(16, true, true, true, true, true);
    }

    /**
     * Generates a secure password that meets GOOD requirements.
     * 
     * @return a good password (12+ characters with most character types)
     */
    public static String generateGoodPassword() {
        return generatePassword(12, true, true, true, true, false);
    }

    /**
     * Generates a secure password that meets FAIR requirements.
     * 
     * @return a fair password (10+ characters with basic character types)
     */
    public static String generateFairPassword() {
        return generatePassword(10, true, true, true, false, false);
    }

    /**
     * Generates a password with specified requirements.
     * 
     * @param length the desired password length
     * @param includeUppercase whether to include uppercase letters
     * @param includeLowercase whether to include lowercase letters
     * @param includeDigits whether to include digits
     * @param includeSpecialChars whether to include special characters
     * @param avoidAmbiguous whether to avoid ambiguous characters (0, O, l, 1, etc.)
     * @return generated password
     */
    public static String generatePassword(int length, boolean includeUppercase, boolean includeLowercase, 
                                        boolean includeDigits, boolean includeSpecialChars, boolean avoidAmbiguous) {
        
        if (length < 4) {
            throw new IllegalArgumentException("Password length must be at least 4 characters");
        }

        StringBuilder password = new StringBuilder();
        List<Character> allChars = new ArrayList<>();

        // Add required character types
        if (includeUppercase) {
            String upperSet = avoidAmbiguous ? UPPERCASE_SAFE : UPPERCASE;
            char upperChar = upperSet.charAt(RANDOM.nextInt(upperSet.length()));
            password.append(upperChar);
            for (char c : upperSet.toCharArray()) {
                allChars.add(c);
            }
        }

        if (includeLowercase) {
            String lowerSet = avoidAmbiguous ? LOWERCASE_SAFE : LOWERCASE;
            char lowerChar = lowerSet.charAt(RANDOM.nextInt(lowerSet.length()));
            password.append(lowerChar);
            for (char c : lowerSet.toCharArray()) {
                allChars.add(c);
            }
        }

        if (includeDigits) {
            String digitSet = avoidAmbiguous ? DIGITS_SAFE : DIGITS;
            char digitChar = digitSet.charAt(RANDOM.nextInt(digitSet.length()));
            password.append(digitChar);
            for (char c : digitSet.toCharArray()) {
                allChars.add(c);
            }
        }

        if (includeSpecialChars) {
            String specialSet = avoidAmbiguous ? SPECIAL_CHARS_SAFE : SPECIAL_CHARS;
            char specialChar = specialSet.charAt(RANDOM.nextInt(specialSet.length()));
            password.append(specialChar);
            for (char c : specialSet.toCharArray()) {
                allChars.add(c);
            }
        }

        // Fill remaining length with random characters from all sets
        int remainingLength = length - password.length();
        for (int i = 0; i < remainingLength; i++) {
            char randomChar = allChars.get(RANDOM.nextInt(allChars.size()));
            password.append(randomChar);
        }

        // Shuffle the password to avoid predictable patterns
        List<Character> passwordChars = new ArrayList<>();
        for (char c : password.toString().toCharArray()) {
            passwordChars.add(c);
        }
        Collections.shuffle(passwordChars, RANDOM);

        StringBuilder shuffledPassword = new StringBuilder();
        for (char c : passwordChars) {
            shuffledPassword.append(c);
        }

        return shuffledPassword.toString();
    }

    /**
     * Generates a temporary password for admin users.
     * This password is strong and will be sent via email.
     * 
     * @return a temporary strong password
     */
    public static String generateTemporaryAdminPassword() {
        return generateStrongPassword();
    }

    /**
     * Generates a password that meets the specified strength requirement.
     * 
     * @param requiredStrength the required password strength
     * @return generated password meeting the strength requirement
     */
    public static String generatePasswordForStrength(PasswordStrength requiredStrength) {
        return switch (requiredStrength) {
            case STRONG -> generateStrongPassword();
            case GOOD -> generateGoodPassword();
            case FAIR -> generateFairPassword();
            case WEAK -> generatePassword(8, true, true, true, false, false);
        };
    }

    /**
     * Validates that a generated password meets the specified strength.
     * 
     * @param password the password to validate
     * @param requiredStrength the required strength
     * @return true if password meets the strength requirement
     */
    public static boolean validatePasswordStrength(String password, PasswordStrength requiredStrength) {
        PasswordStrength actualStrength = PasswordStrengthCalculator.calculatePasswordStrength(password);
        return actualStrength.ordinal() >= requiredStrength.ordinal();
    }

    /**
     * Generates multiple password options for user selection.
     * 
     * @param count number of passwords to generate
     * @param strength the required strength for all passwords
     * @return list of generated passwords
     */
    public static List<String> generatePasswordOptions(int count, PasswordStrength strength) {
        List<String> passwords = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            passwords.add(generatePasswordForStrength(strength));
        }
        return passwords;
    }

    /**
     * Generates a memorable password using a pattern.
     * Useful for temporary passwords that users need to type.
     * 
     * @return a memorable but secure password
     */
    public static String generateMemorablePassword() {
        // Generate a pattern like: Word123!Word456!
        String[] words = {"Admin", "Secure", "Strong", "Power", "Safe", "Guard", "Key", "Lock"};
        String word1 = words[RANDOM.nextInt(words.length)];
        String word2 = words[RANDOM.nextInt(words.length)];
        
        int num1 = 100 + RANDOM.nextInt(900); // 3-digit number
        int num2 = 100 + RANDOM.nextInt(900); // 3-digit number
        
        String special1 = SPECIAL_CHARS_SAFE.charAt(RANDOM.nextInt(SPECIAL_CHARS_SAFE.length())) + "";
        String special2 = SPECIAL_CHARS_SAFE.charAt(RANDOM.nextInt(SPECIAL_CHARS_SAFE.length())) + "";
        
        return word1 + num1 + special1 + word2 + num2 + special2;
    }
}
