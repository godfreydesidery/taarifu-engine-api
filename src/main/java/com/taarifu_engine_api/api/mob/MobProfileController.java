package com.taarifu_engine_api.api.mob;

import com.taarifu_engine_api.modules.common.exception.ApiException;
import com.taarifu_engine_api.modules.profile.domain.dto.SelfRegistrationRequestDto;
import com.taarifu_engine_api.modules.profile.domain.dto.SelfRegistrationResponseDto;
import com.taarifu_engine_api.modules.profile.domain.enums.ProfileType;
import com.taarifu_engine_api.modules.profile.service.ProfileService;
import com.taarifu_engine_api.modules.userandrole.repository.UserRepository;
import com.taarifu_engine_api.modules.notification.service.EmailService;
import com.taarifu_engine_api.modules.notification.domain.enums.EmailType;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Random;

@RestController
@RequestMapping("/mob/v1/profiles")
@RequiredArgsConstructor
@Slf4j
public class MobProfileController {
    
    private final ProfileService profileService;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final Random random = new Random();
    
    /**
     * Self-registration endpoint for persons to register themselves
     * Creates profile, user account, and citizen record automatically
     * 
     * Note: This endpoint is publicly accessible (no authentication required)
     * as configured in SecurityConfig.java
     */
    @PostMapping("/register")
    public ResponseEntity<SelfRegistrationResponseDto> selfRegister(@Valid @RequestBody SelfRegistrationRequestDto request) {
        log.info("Self-registration attempt for email: {}", request.getEmail());
        
        try {
            // Generate username from email (part before @)
            String username = generateUsernameFromEmail(request.getEmail());
            
            // Generate temporary password
            String temporaryPassword = generateTemporaryPassword();
            
            // Create profile using existing service
            var createProfileRequest = new com.taarifu_engine_api.modules.profile.domain.dto.CreateProfileRequestDto();
            createProfileRequest.setProfileType(ProfileType.PERSON);
            createProfileRequest.setName(request.getName());
            createProfileRequest.setDisplayName(request.getDisplayName());
            createProfileRequest.setEmail(request.getEmail());
            createProfileRequest.setPhoneNumber(request.getPhoneNumber());
            createProfileRequest.setDateOfBirth(request.getDateOfBirth());
            createProfileRequest.setGender(request.getGender());
            createProfileRequest.setIdType(request.getIdType());
            createProfileRequest.setIdNumber(request.getIdNumber());
            createProfileRequest.setAddress(request.getAddress());
            createProfileRequest.setBio(request.getBio());
            createProfileRequest.setIsActive(true);
            
            // Create profile with user account
            var profileResponse = profileService.createProfile(createProfileRequest, username, request.getEmail(), temporaryPassword);
            
            // Send welcome email with temporary password
            try {
                sendWelcomeEmail(request.getEmail(), request.getName(), username, temporaryPassword);
                log.info("Welcome email sent successfully to: {}", request.getEmail());
            } catch (Exception e) {
                log.error("Failed to send welcome email to: {}", request.getEmail(), e);
                // Don't fail registration if email fails - user can still login
            }
            
            // Prepare response
            SelfRegistrationResponseDto response = SelfRegistrationResponseDto.builder()
                    .message("Registration successful! Please check your email for login credentials.")
                    .profileUid(profileResponse.getUid())
                    .username(username)
                    .email(request.getEmail())
                    .registrationDate(LocalDateTime.now())
                    .requirePasswordChange(true)
                    .loginInstructions("Please check your email for your temporary password and change it after first login for security.")
                    .build();
            
            log.info("Self-registration successful for: {}", request.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            log.error("Self-registration failed for: {}", request.getEmail(), e);
            throw new ApiException("Registration failed: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    /**
     * Generate username from email (part before @)
     */
    private String generateUsernameFromEmail(String email) {
        String baseUsername = email.substring(0, email.indexOf("@"));
        
        // Clean username (remove special characters, keep only alphanumeric)
        String cleanUsername = baseUsername.replaceAll("[^a-zA-Z0-9]", "");
        
        // Ensure minimum length
        if (cleanUsername.length() < 3) {
            cleanUsername = cleanUsername + "user";
        }
        
        // Truncate if too long
        if (cleanUsername.length() > 20) {
            cleanUsername = cleanUsername.substring(0, 20);
        }
        
        // Check if username exists and add number if needed
        String finalUsername = cleanUsername;
        int counter = 1;
        while (userRepository.existsByUsername(finalUsername)) {
            finalUsername = cleanUsername + counter;
            counter++;
            if (finalUsername.length() > 20) {
                finalUsername = cleanUsername.substring(0, 20 - String.valueOf(counter).length()) + counter;
            }
        }
        
        return finalUsername;
    }
    
    /**
     * Generate a temporary password
     */
    private String generateTemporaryPassword() {
        // Generate a secure temporary password
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%";
        StringBuilder password = new StringBuilder();
        
        // Ensure at least one of each required character type
        password.append((char) (random.nextInt(26) + 'A')); // Uppercase
        password.append((char) (random.nextInt(26) + 'a')); // Lowercase
        password.append((char) (random.nextInt(10) + '0')); // Digit
        password.append("!@#$%".charAt(random.nextInt(5))); // Special char
        
        // Fill the rest randomly
        for (int i = 4; i < 12; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }
        
        // Shuffle the password
        String passwordStr = password.toString();
        char[] passwordArray = passwordStr.toCharArray();
        for (int i = 0; i < passwordArray.length; i++) {
            int j = random.nextInt(passwordArray.length);
            char temp = passwordArray[i];
            passwordArray[i] = passwordArray[j];
            passwordArray[j] = temp;
        }
        
        return new String(passwordArray);
    }
    
    /**
     * Send welcome email with temporary password to new user
     */
    private void sendWelcomeEmail(String email, String name, String username, String temporaryPassword) {
        String subject = "Welcome to Taarifu - Your Account Credentials";
        
        String emailBody = String.format("""
            <html>
            <body style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px;">
                <h2 style="color: #059669;">Welcome to Taarifu, %s!</h2>
                
                <p>Your account has been successfully created. Here are your login credentials:</p>
                
                <div style="background-color: #f8f9fa; padding: 15px; border-radius: 5px; margin: 20px 0;">
                    <p><strong>Username:</strong> %s</p>
                    <p><strong>Temporary Password:</strong> %s</p>
                </div>
                
                <p><strong>Important Security Instructions:</strong></p>
                <ul>
                    <li>Please log in immediately using these credentials</li>
                    <li>You will be prompted to change your password on first login</li>
                    <li>Choose a strong password for security</li>
                    <li>Keep your credentials secure and do not share them</li>
                </ul>
                
                <p>If you have any questions or need assistance, please contact our support team.</p>
                
                <p>Best regards,<br>The Taarifu Team</p>
            </body>
            </html>
            """, name, username, temporaryPassword);
        
        try {
            emailService.sendSimpleEmail(email, subject, emailBody, EmailType.WELCOME);
            log.info("Welcome email queued for sending to: {}", email);
        } catch (Exception e) {
            log.error("Failed to queue welcome email for: {}", email, e);
            throw e;
        }
    }
}
