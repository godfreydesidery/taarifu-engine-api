package com.taarifu_engine_api.api.mob;

import com.taarifu_engine_api.modules.common.exception.ApiException;
import com.taarifu_engine_api.modules.common.domain.util.ResponseWrapper;
import com.taarifu_engine_api.modules.common.domain.enums.AreaType;
import com.taarifu_engine_api.modules.location.area.domain.dto.AreaResponse;
import com.taarifu_engine_api.modules.location.area.service.AreaService;
import com.taarifu_engine_api.modules.location.constituency.domain.dto.ConstituencyResponse;
import com.taarifu_engine_api.modules.location.constituency.service.ConstituencyService;
import com.taarifu_engine_api.modules.profile.domain.dto.*;
import com.taarifu_engine_api.modules.profile.domain.enums.ProfileType;
import com.taarifu_engine_api.modules.profile.service.ProfileResidenceService;
import com.taarifu_engine_api.modules.profile.service.ProfileService;
import com.taarifu_engine_api.modules.userandrole.domain.entity.User;
import com.taarifu_engine_api.modules.userandrole.repository.UserRepository;
import com.taarifu_engine_api.modules.notification.service.EmailService;
import com.taarifu_engine_api.modules.notification.domain.enums.EmailType;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/mob/v1/profiles")
@RequiredArgsConstructor
@Slf4j
public class MobProfileController {
    
    private final ProfileService profileService;
    private final ProfileResidenceService profileResidenceService;
    private final AreaService areaService;
    private final ConstituencyService constituencyService;
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
    public ResponseEntity<ResponseWrapper<SelfRegistrationResponseDto>> selfRegister(@Valid @RequestBody SelfRegistrationRequestDto request) {
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
            SelfRegistrationResponseDto registrationResponse = SelfRegistrationResponseDto.builder()
                    .message("Registration successful! Please check your email for login credentials.")
                    .profileUid(profileResponse.getUid())
                    .username(username)
                    .email(request.getEmail())
                    .registrationDate(LocalDateTime.now())
                    .requirePasswordChange(true)
                    .loginInstructions("Please check your email for your temporary password and change it after first login for security.")
                    .build();
            
            ResponseWrapper<SelfRegistrationResponseDto> response = new ResponseWrapper<>(
                    true,
                    HttpStatus.CREATED.value(),
                    "Registration successful! Please check your email for login credentials.",
                    registrationResponse
            );
            
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

    // ==================== RESIDENCE MANAGEMENT ENDPOINTS ====================

    /**
     * Get all residences for the authenticated user's profile
     */
    @GetMapping("/residences")
    public ResponseEntity<ResponseWrapper<List<ProfileResidenceResponse>>> getUserResidences(Authentication authentication) {
        log.info("Getting residences for user: {}", authentication.getName());
        
        try {
            User user = (User) authentication.getPrincipal();
            ProfileResponseDto profile = profileService.getProfileByUser(user);
            
            if (profile == null) {
                throw new ApiException("Profile not found for user", HttpStatus.NOT_FOUND);
            }
            
            List<ProfileResidenceResponse> residences = profileResidenceService.getProfileResidences(profile);
            log.info("Retrieved {} residences for profile: {}", residences.size(), profile.getUid());
            
            ResponseWrapper<List<ProfileResidenceResponse>> response = new ResponseWrapper<>(
                    true,
                    HttpStatus.OK.value(),
                    "Residences retrieved successfully",
                    residences
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to get residences for user: {}", authentication.getName(), e);
            throw new ApiException("Failed to retrieve residences: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Assign administrative area residence to the authenticated user's profile
     */
    @PostMapping("/residences/administrative")
    public ResponseEntity<ResponseWrapper<ProfileResidenceResponse>> assignAdministrativeResidence(
            @Valid @RequestBody AssignAdministrativeResidenceRequest request,
            Authentication authentication) {
        log.info("Assigning administrative residence to user: {}", authentication.getName());
        
        try {
            User user = (User) authentication.getPrincipal();
            ProfileResponseDto profile = profileService.getProfileByUser(user);
            
            if (profile == null) {
                throw new ApiException("Profile not found for user", HttpStatus.NOT_FOUND);
            }
            
            ProfileResidenceResponse residenceResponse = profileResidenceService.assignAdministrativeResidence(profile, request);
            log.info("Successfully assigned administrative residence: {} to profile: {}", 
                    residenceResponse.getUid(), profile.getUid());
            
            ResponseWrapper<ProfileResidenceResponse> response = new ResponseWrapper<>(
                    true,
                    HttpStatus.CREATED.value(),
                    "Administrative residence assigned successfully",
                    residenceResponse
            );
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            log.error("Failed to assign administrative residence for user: {}", authentication.getName(), e);
            throw new ApiException("Failed to assign administrative residence: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Assign constituency residence to the authenticated user's profile
     */
    @PostMapping("/residences/constituency")
    public ResponseEntity<ResponseWrapper<ProfileResidenceResponse>> assignConstituencyResidence(
            @Valid @RequestBody AssignConstituencyResidenceRequest request,
            Authentication authentication) {
        log.info("Assigning constituency residence to user: {}", authentication.getName());
        
        try {
            User user = (User) authentication.getPrincipal();
            ProfileResponseDto profile = profileService.getProfileByUser(user);
            
            if (profile == null) {
                throw new ApiException("Profile not found for user", HttpStatus.NOT_FOUND);
            }
            
            ProfileResidenceResponse residenceResponse = profileResidenceService.assignConstituencyResidence(profile, request);
            log.info("Successfully assigned constituency residence: {} to profile: {}", 
                    residenceResponse.getUid(), profile.getUid());
            
            ResponseWrapper<ProfileResidenceResponse> response = new ResponseWrapper<>(
                    true,
                    HttpStatus.CREATED.value(),
                    "Constituency residence assigned successfully",
                    residenceResponse
            );
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            log.error("Failed to assign constituency residence for user: {}", authentication.getName(), e);
            throw new ApiException("Failed to assign constituency residence: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Update residence
     */
    @PutMapping("/residences/uid/{residenceUid}")
    public ResponseEntity<ResponseWrapper<ProfileResidenceResponse>> updateResidence(
            @PathVariable String residenceUid,
            @Valid @RequestBody UpdateResidenceRequest request,
            Authentication authentication) {
        log.info("Updating residence: {} for user: {}", residenceUid, authentication.getName());
        
        try {
            User user = (User) authentication.getPrincipal();
            ProfileResponseDto profile = profileService.getProfileByUser(user);
            
            if (profile == null) {
                throw new ApiException("Profile not found for user", HttpStatus.NOT_FOUND);
            }
            
            // Verify the residence belongs to the user's profile
            ProfileResidenceResponse existingResidence = profileResidenceService.getResidenceByUid(residenceUid);
            if (!existingResidence.getProfileUid().equals(profile.getUid())) {
                throw new ApiException("Residence not found or access denied", HttpStatus.NOT_FOUND);
            }
            
            ProfileResidenceResponse residenceResponse = profileResidenceService.updateResidence(residenceUid, request);
            log.info("Successfully updated residence: {} for profile: {}", residenceUid, profile.getUid());
            
            ResponseWrapper<ProfileResidenceResponse> response = new ResponseWrapper<>(
                    true,
                    HttpStatus.OK.value(),
                    "Residence updated successfully",
                    residenceResponse
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to update residence: {} for user: {}", residenceUid, authentication.getName(), e);
            throw new ApiException("Failed to update residence: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Set residence as primary
     */
    @PutMapping("/residences/uid/{residenceUid}/primary")
    public ResponseEntity<ResponseWrapper<ProfileResidenceResponse>> setPrimaryResidence(
            @PathVariable String residenceUid,
            Authentication authentication) {
        log.info("Setting residence as primary: {} for user: {}", residenceUid, authentication.getName());
        
        try {
            User user = (User) authentication.getPrincipal();
            ProfileResponseDto profile = profileService.getProfileByUser(user);
            
            if (profile == null) {
                throw new ApiException("Profile not found for user", HttpStatus.NOT_FOUND);
            }
            
            // Verify the residence belongs to the user's profile
            ProfileResidenceResponse existingResidence = profileResidenceService.getResidenceByUid(residenceUid);
            if (!existingResidence.getProfileUid().equals(profile.getUid())) {
                throw new ApiException("Residence not found or access denied", HttpStatus.NOT_FOUND);
            }
            
            ProfileResidenceResponse residenceResponse = profileResidenceService.setPrimaryResidence(residenceUid);
            log.info("Successfully set residence as primary: {} for profile: {}", residenceUid, profile.getUid());
            
            ResponseWrapper<ProfileResidenceResponse> response = new ResponseWrapper<>(
                    true,
                    HttpStatus.OK.value(),
                    "Residence set as primary successfully",
                    residenceResponse
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to set residence as primary: {} for user: {}", residenceUid, authentication.getName(), e);
            throw new ApiException("Failed to set residence as primary: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Remove residence (soft delete)
     */
    @DeleteMapping("/residences/uid/{residenceUid}")
    public ResponseEntity<ResponseWrapper<String>> removeResidence(
            @PathVariable String residenceUid,
            Authentication authentication) {
        log.info("Removing residence: {} for user: {}", residenceUid, authentication.getName());
        
        try {
            User user = (User) authentication.getPrincipal();
            ProfileResponseDto profile = profileService.getProfileByUser(user);
            
            if (profile == null) {
                throw new ApiException("Profile not found for user", HttpStatus.NOT_FOUND);
            }
            
            // Verify the residence belongs to the user's profile
            ProfileResidenceResponse existingResidence = profileResidenceService.getResidenceByUid(residenceUid);
            if (!existingResidence.getProfileUid().equals(profile.getUid())) {
                throw new ApiException("Residence not found or access denied", HttpStatus.NOT_FOUND);
            }
            
            profileResidenceService.removeResidence(residenceUid);
            log.info("Successfully removed residence: {} for profile: {}", residenceUid, profile.getUid());
            
            ResponseWrapper<String> response = new ResponseWrapper<>(
                    true,
                    HttpStatus.OK.value(),
                    "Residence removed successfully",
                    "Residence has been removed successfully"
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to remove residence: {} for user: {}", residenceUid, authentication.getName(), e);
            throw new ApiException("Failed to remove residence: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Get primary administrative area residence
     */
    @GetMapping("/residences/primary/administrative")
    public ResponseEntity<ResponseWrapper<ProfileResidenceResponse>> getPrimaryAdministrativeResidence(Authentication authentication) {
        log.info("Getting primary administrative residence for user: {}", authentication.getName());
        
        try {
            User user = (User) authentication.getPrincipal();
            ProfileResponseDto profile = profileService.getProfileByUser(user);
            
            if (profile == null) {
                throw new ApiException("Profile not found for user", HttpStatus.NOT_FOUND);
            }
            
            ProfileResidenceResponse residenceResponse = profileResidenceService.getPrimaryAdministrativeResidence(profile);
            
            ResponseWrapper<ProfileResidenceResponse> response = new ResponseWrapper<>(
                    true,
                    HttpStatus.OK.value(),
                    "Primary administrative residence retrieved successfully",
                    residenceResponse
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to get primary administrative residence for user: {}", authentication.getName(), e);
            throw new ApiException("Failed to retrieve primary administrative residence: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get primary constituency residence
     */
    @GetMapping("/residences/primary/constituency")
    public ResponseEntity<ResponseWrapper<ProfileResidenceResponse>> getPrimaryConstituencyResidence(Authentication authentication) {
        log.info("Getting primary constituency residence for user: {}", authentication.getName());
        
        try {
            User user = (User) authentication.getPrincipal();
            ProfileResponseDto profile = profileService.getProfileByUser(user);
            
            if (profile == null) {
                throw new ApiException("Profile not found for user", HttpStatus.NOT_FOUND);
            }
            
            ProfileResidenceResponse residenceResponse = profileResidenceService.getPrimaryConstituencyResidence(profile);
            
            ResponseWrapper<ProfileResidenceResponse> response = new ResponseWrapper<>(
                    true,
                    HttpStatus.OK.value(),
                    "Primary constituency residence retrieved successfully",
                    residenceResponse
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to get primary constituency residence for user: {}", authentication.getName(), e);
            throw new ApiException("Failed to retrieve primary constituency residence: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get available areas for assignment
     * Mobile-optimized: Returns all matching areas without pagination for better UX
     * Performance: Limited to 1000 items max to prevent performance issues
     */
    @GetMapping("/residences/available/areas")
    public ResponseEntity<ResponseWrapper<List<AreaResponse>>> getAvailableAreas(
            @RequestParam(required = false) String areaType,
            @RequestParam(required = false) String search) {
        log.info("Getting available areas with type: {} and search: {}", areaType, search);
        
        try {
            // Use reasonable page size for mobile (1000 max to prevent performance issues)
            Pageable pageable = PageRequest.of(0, 1000);
            List<AreaResponse> areas;
            
            if (areaType != null && !areaType.trim().isEmpty()) {
                // Filter by area type
                AreaType type = AreaType.valueOf(areaType.toUpperCase());
                if (search != null && !search.trim().isEmpty()) {
                    // Search within specific area type
                    areas = areaService.searchAreasByType(type, search, pageable).getData();
                } else {
                    // Get all areas of specific type
                    areas = areaService.getAreasByType(type, pageable).getData();
                }
            } else if (search != null && !search.trim().isEmpty()) {
                // Search all areas
                areas = areaService.searchAreas(search, pageable).getData();
            } else {
                // Get all areas (limited to 1000 for performance)
                areas = areaService.getAllAreas(pageable).getData();
            }
            
            ResponseWrapper<List<AreaResponse>> response = new ResponseWrapper<>(
                    true,
                    HttpStatus.OK.value(),
                    "Available areas retrieved successfully",
                    areas
            );
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            log.error("Invalid area type: {}", areaType, e);
            throw new ApiException("Invalid area type: " + areaType, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("Failed to get available areas", e);
            throw new ApiException("Failed to retrieve available areas: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get available constituencies
     * Mobile-optimized: Returns all matching constituencies without pagination for better UX
     * Performance: Limited to 1000 items max to prevent performance issues
     */
    @GetMapping("/residences/available/constituencies")
    public ResponseEntity<ResponseWrapper<List<ConstituencyResponse>>> getAvailableConstituencies(
            @RequestParam(required = false) String search) {
        log.info("Getting available constituencies with search: {}", search);
        
        try {
            // Use reasonable page size for mobile (1000 max to prevent performance issues)
            Pageable pageable = PageRequest.of(0, 1000);
            List<ConstituencyResponse> constituencies;
            
            if (search != null && !search.trim().isEmpty()) {
                // Search constituencies
                constituencies = constituencyService.searchConstituencies(search, pageable).getContent();
            } else {
                // Get all active constituencies
                constituencies = constituencyService.getActiveConstituencies(pageable).getContent();
            }
            
            ResponseWrapper<List<ConstituencyResponse>> response = new ResponseWrapper<>(
                    true,
                    HttpStatus.OK.value(),
                    "Available constituencies retrieved successfully",
                    constituencies
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to get available constituencies", e);
            throw new ApiException("Failed to retrieve available constituencies: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
