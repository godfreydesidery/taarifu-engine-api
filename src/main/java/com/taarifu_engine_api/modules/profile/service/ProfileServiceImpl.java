package com.taarifu_engine_api.modules.profile.service;

import com.taarifu_engine_api.modules.citizen.service.CitizenService;
import com.taarifu_engine_api.modules.common.exception.ApiException;
import org.springframework.http.HttpStatus;
import com.taarifu_engine_api.modules.profile.domain.dto.CreateProfileRequestDto;
import com.taarifu_engine_api.modules.profile.domain.dto.ProfileResponseDto;
import com.taarifu_engine_api.modules.profile.domain.dto.ProfileSummaryDto;
import com.taarifu_engine_api.modules.profile.domain.dto.UpdateProfileRequestDto;
import com.taarifu_engine_api.modules.profile.domain.entity.Profile;
import com.taarifu_engine_api.modules.profile.domain.enums.IdType;
import com.taarifu_engine_api.modules.profile.domain.enums.ProfileType;
import com.taarifu_engine_api.modules.profile.repository.ProfileRepository;
import com.taarifu_engine_api.modules.userandrole.domain.entity.User;
import com.taarifu_engine_api.modules.userandrole.domain.enums.UserType;
import com.taarifu_engine_api.modules.userandrole.repository.UserRepository;
import com.taarifu_engine_api.modules.common.domain.enums.PasswordStrength;
import com.taarifu_engine_api.modules.notification.service.EmailService;
import com.taarifu_engine_api.modules.notification.domain.enums.EmailType;
import com.taarifu_engine_api.modules.notification.service.SmsService;
import com.taarifu_engine_api.modules.notification.domain.enums.SmsType;
import de.huxhorn.sulky.ulid.ULID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

/**
 * Service implementation for Profile operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final CitizenService citizenService;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final SmsService smsService;
    private final ULID ulid = new ULID();
    
    private static final int EMAIL_VERIFICATION_TOKEN_EXPIRY_HOURS = 24;

    @Override
    public ProfileResponseDto createProfile(CreateProfileRequestDto request, String username, String email, String password) {
        log.info("Creating profile with user creation for username: {}", username);
        
        // Validate profile type specific requirements
        validateProfileTypeSpecificFields(request);
        
        // Check if email is already taken
        if (request.getEmail() != null && profileRepository.existsByEmail(request.getEmail())) {
            throw new ApiException("Email already exists", HttpStatus.CONFLICT);
        }
        
        // Check if phone number is already taken
        if (request.getPhoneNumber() != null && profileRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new ApiException("Phone number already exists", HttpStatus.CONFLICT);
        }
        
        // Check if ID type and number combination already exists
        if (request.getIdType() != null && request.getIdNumber() != null && 
            profileRepository.existsByIdTypeAndIdNumber(request.getIdType(), request.getIdNumber())) {
            throw new ApiException("ID type and number combination already exists", HttpStatus.CONFLICT);
        }
        
        // Check if registration number already exists (for organizations)
        if (request.getRegistrationNumber() != null && 
            profileRepository.existsByRegistrationNumber(request.getRegistrationNumber())) {
            throw new ApiException("Registration number already exists", HttpStatus.CONFLICT);
        }
        
        // Create user first
        User user = createUserWithPasswordStrength(username, email, password, request.getProfileType(), request.getPhoneNumber());
        
        // Validate that the created user is not an admin (defensive check)
        validateUserIsNotAdmin(user);
        
        // Create profile
        Profile profile = new Profile();
        profile.setUid(ulid.nextULID());
        profile.setUser(user);
        profile.setProfileType(request.getProfileType());
        profile.setName(request.getName());
        profile.setDisplayName(request.getDisplayName());
        profile.setEmail(request.getEmail());
        profile.setPhoneNumber(request.getPhoneNumber());
        profile.setDateOfBirth(request.getDateOfBirth());
        profile.setGender(request.getGender());
        profile.setIdType(request.getIdType());
        profile.setIdNumber(request.getIdNumber());
        profile.setRegistrationNumber(request.getRegistrationNumber());
        profile.setWebsite(request.getWebsite());
        profile.setContactPerson(request.getContactPerson());
        profile.setAddress(request.getAddress());
        profile.setProfilePictureUrl(request.getProfilePictureUrl());
        profile.setBio(request.getBio());
        profile.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        
        Profile savedProfile = profileRepository.save(profile);
        log.info("Profile with user created successfully with ID: {}", savedProfile.getId());
        
        // Automatically create citizen if profile type is PERSON
        if (ProfileType.PERSON.equals(savedProfile.getProfileType())) {
            try {
                citizenService.createCitizenForProfile(savedProfile);
                log.info("Citizen automatically created for PERSON profile: {}", savedProfile.getUid());
            } catch (Exception e) {
                log.error("Failed to create citizen for profile: {}", savedProfile.getUid(), e);
                // Note: We don't throw here to avoid rolling back the profile creation
                // The citizen can be created later through other means
            }
        }
        
        return mapToResponseDto(savedProfile);
    }

    @Override
    public ProfileResponseDto updateProfile(Long id, UpdateProfileRequestDto request) {
        log.info("Updating profile with ID: {}", id);
        
        Profile profile = profileRepository.findById(id)
                .orElseThrow(() -> new ApiException("Profile not found with ID: " + id, HttpStatus.NOT_FOUND));
        
        return updateProfileFields(profile, request);
    }

    @Override
    public ProfileResponseDto updateProfileByUid(String uid, UpdateProfileRequestDto request) {
        log.info("Updating profile with UID: {}", uid);
        
        Profile profile = profileRepository.findByUid(uid)
                .orElseThrow(() -> new ApiException("Profile not found with UID: " + uid, HttpStatus.NOT_FOUND));
        
        return updateProfileFields(profile, request);
    }

    @Override
    public ProfileResponseDto updateProfileByUser(User user, UpdateProfileRequestDto request) {
        log.info("Updating profile for user: {}", user.getUsername());
        
        Profile profile = profileRepository.findByUser(user)
                .orElseThrow(() -> new ApiException("Profile not found for user: " + user.getUsername(), HttpStatus.NOT_FOUND));
        
        return updateProfileFields(profile, request);
    }

    private ProfileResponseDto updateProfileFields(Profile profile, UpdateProfileRequestDto request) {
        // Validate that the user is not an admin
        validateUserIsNotAdmin(profile.getUser());
        
        // Check if email is already taken by another profile
        if (request.getEmail() != null && !request.getEmail().equals(profile.getEmail()) && 
            profileRepository.existsByEmail(request.getEmail())) {
            throw new ApiException("Email already exists", HttpStatus.CONFLICT);
        }
        
        // Check if phone number is already taken by another profile
        if (request.getPhoneNumber() != null && !request.getPhoneNumber().equals(profile.getPhoneNumber()) && 
            profileRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new ApiException("Phone number already exists", HttpStatus.CONFLICT);
        }
        
        // Check if ID type and number combination already exists for another profile
        if (request.getIdType() != null && request.getIdNumber() != null) {
            boolean isSameId = profile.getIdType() != null && profile.getIdNumber() != null &&
                              profile.getIdType().equals(request.getIdType()) && 
                              profile.getIdNumber().equals(request.getIdNumber());
            
            if (!isSameId && profileRepository.existsByIdTypeAndIdNumber(request.getIdType(), request.getIdNumber())) {
                throw new ApiException("ID type and number combination already exists", HttpStatus.CONFLICT);
            }
        }
        
        // Check if registration number already exists for another profile
        if (request.getRegistrationNumber() != null && !request.getRegistrationNumber().equals(profile.getRegistrationNumber()) && 
            profileRepository.existsByRegistrationNumber(request.getRegistrationNumber())) {
            throw new ApiException("Registration number already exists", HttpStatus.CONFLICT);
        }
        
        // Validate profile type specific requirements for updates
        validateProfileTypeSpecificFieldsForUpdate(profile, request);
        
        // Update fields if provided
        if (request.getProfileType() != null) profile.setProfileType(request.getProfileType());
        if (request.getName() != null) profile.setName(request.getName());
        if (request.getDisplayName() != null) profile.setDisplayName(request.getDisplayName());
        if (request.getEmail() != null) profile.setEmail(request.getEmail());
        if (request.getPhoneNumber() != null) profile.setPhoneNumber(request.getPhoneNumber());
        if (request.getDateOfBirth() != null) profile.setDateOfBirth(request.getDateOfBirth());
        if (request.getGender() != null) profile.setGender(request.getGender());
        if (request.getIdType() != null) profile.setIdType(request.getIdType());
        if (request.getIdNumber() != null) profile.setIdNumber(request.getIdNumber());
        if (request.getRegistrationNumber() != null) profile.setRegistrationNumber(request.getRegistrationNumber());
        if (request.getWebsite() != null) profile.setWebsite(request.getWebsite());
        if (request.getContactPerson() != null) profile.setContactPerson(request.getContactPerson());
        if (request.getAddress() != null) profile.setAddress(request.getAddress());
        if (request.getProfilePictureUrl() != null) profile.setProfilePictureUrl(request.getProfilePictureUrl());
        if (request.getBio() != null) profile.setBio(request.getBio());
        if (request.getIsActive() != null) profile.setIsActive(request.getIsActive());
        
        Profile updatedProfile = profileRepository.save(profile);
        log.info("Profile updated successfully with ID: {}", updatedProfile.getId());
        
        return mapToResponseDto(updatedProfile);
    }

    @Override
    @Transactional(readOnly = true)
    public ProfileResponseDto getProfileById(Long id) {
        Profile profile = profileRepository.findById(id)
                .orElseThrow(() -> new ApiException("Profile not found with ID: " + id, HttpStatus.NOT_FOUND));
        return mapToResponseDto(profile);
    }

    @Override
    @Transactional(readOnly = true)
    public ProfileResponseDto getProfileByUid(String uid) {
        Profile profile = profileRepository.findByUid(uid)
                .orElseThrow(() -> new ApiException("Profile not found with UID: " + uid, HttpStatus.NOT_FOUND));
        return mapToResponseDto(profile);
    }

    @Override
    @Transactional(readOnly = true)
    public ProfileResponseDto getProfileByUser(User user) {
        Profile profile = profileRepository.findByUser(user)
                .orElseThrow(() -> new ApiException("Profile not found for user: " + user.getUsername(), HttpStatus.NOT_FOUND));
        return mapToResponseDto(profile);
    }

    @Override
    @Transactional(readOnly = true)
    public ProfileResponseDto getProfileByUserId(Long userId) {
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ApiException("Profile not found for user ID: " + userId, HttpStatus.NOT_FOUND));
        return mapToResponseDto(profile);
    }

    @Override
    @Transactional(readOnly = true)
    public ProfileResponseDto getProfileByEmail(String email) {
        Profile profile = profileRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException("Profile not found with email: " + email, HttpStatus.NOT_FOUND));
        return mapToResponseDto(profile);
    }

    @Override
    @Transactional(readOnly = true)
    public ProfileResponseDto getProfileByPhoneNumber(String phoneNumber) {
        Profile profile = profileRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new ApiException("Profile not found with phone number: " + phoneNumber, HttpStatus.NOT_FOUND));
        return mapToResponseDto(profile);
    }

    @Override
    @Transactional(readOnly = true)
    public ProfileResponseDto getProfileByIdTypeAndIdNumber(IdType idType, String idNumber) {
        Profile profile = profileRepository.findByIdTypeAndIdNumber(idType, idNumber)
                .orElseThrow(() -> new ApiException("Profile not found with ID type: " + idType + " and number: " + idNumber, HttpStatus.NOT_FOUND));
        return mapToResponseDto(profile);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProfileResponseDto> getAllProfiles(Pageable pageable) {
        return profileRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(this::mapToResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProfileResponseDto> getActiveProfiles(Pageable pageable) {
        return profileRepository.findByIsActiveTrueOrderByCreatedAtDesc(pageable)
                .map(this::mapToResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProfileResponseDto> getProfilesByType(ProfileType profileType, Pageable pageable) {
        return profileRepository.findByProfileType(profileType, pageable)
                .map(this::mapToResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProfileResponseDto> getActiveProfilesByType(ProfileType profileType, Pageable pageable) {
        return profileRepository.findByProfileTypeAndIsActiveTrue(profileType, pageable)
                .map(this::mapToResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProfileResponseDto> searchProfiles(String searchTerm, Pageable pageable) {
        return profileRepository.searchProfiles(searchTerm, pageable)
                .map(this::mapToResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProfileResponseDto> searchProfilesByType(ProfileType profileType, String searchTerm, Pageable pageable) {
        return profileRepository.searchProfilesByType(profileType, searchTerm, pageable)
                .map(this::mapToResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProfileSummaryDto> getProfileSummaries(Pageable pageable) {
        return profileRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(this::mapToSummaryDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProfileSummaryDto> getActiveProfileSummaries(Pageable pageable) {
        return profileRepository.findByIsActiveTrueOrderByCreatedAtDesc(pageable)
                .map(this::mapToSummaryDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProfileSummaryDto> getProfileSummariesByType(ProfileType profileType, Pageable pageable) {
        return profileRepository.findByProfileType(profileType, pageable)
                .map(this::mapToSummaryDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProfileResponseDto> getProfilesCreatedBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        return profileRepository.findProfilesCreatedBetween(startDate, endDate, pageable)
                .map(this::mapToResponseDto);
    }

    @Override
    public void deleteProfile(Long id) {
        log.info("Deleting profile with ID: {}", id);
        
        Profile profile = profileRepository.findById(id)
                .orElseThrow(() -> new ApiException("Profile not found with ID: " + id, HttpStatus.NOT_FOUND));
        
        profile.setIsActive(false);
        profileRepository.save(profile);
        log.info("Profile deactivated successfully with ID: {}", id);
    }

    @Override
    public void deleteProfileByUid(String uid) {
        log.info("Deleting profile with UID: {}", uid);
        
        Profile profile = profileRepository.findByUid(uid)
                .orElseThrow(() -> new ApiException("Profile not found with UID: " + uid, HttpStatus.NOT_FOUND));
        
        profile.setIsActive(false);
        profileRepository.save(profile);
        log.info("Profile deactivated successfully with UID: {}", uid);
    }

    @Override
    public void deleteProfileByUser(User user) {
        log.info("Deleting profile for user: {}", user.getUsername());
        
        Profile profile = profileRepository.findByUser(user)
                .orElseThrow(() -> new ApiException("Profile not found for user: " + user.getUsername(), HttpStatus.NOT_FOUND));
        
        profile.setIsActive(false);
        profileRepository.save(profile);
        log.info("Profile deactivated successfully for user: {}", user.getUsername());
    }

    @Override
    public ProfileResponseDto activateProfile(Long id) {
        log.info("Activating profile with ID: {}", id);
        
        Profile profile = profileRepository.findById(id)
                .orElseThrow(() -> new ApiException("Profile not found with ID: " + id, HttpStatus.NOT_FOUND));
        
        profile.setIsActive(true);
        Profile activatedProfile = profileRepository.save(profile);
        log.info("Profile activated successfully with ID: {}", id);
        
        return mapToResponseDto(activatedProfile);
    }

    @Override
    public ProfileResponseDto activateProfileByUid(String uid) {
        log.info("Activating profile with UID: {}", uid);
        
        Profile profile = profileRepository.findByUid(uid)
                .orElseThrow(() -> new ApiException("Profile not found with UID: " + uid, HttpStatus.NOT_FOUND));
        
        profile.setIsActive(true);
        Profile activatedProfile = profileRepository.save(profile);
        log.info("Profile activated successfully with UID: {}", uid);
        
        return mapToResponseDto(activatedProfile);
    }

    @Override
    public ProfileResponseDto deactivateProfile(Long id) {
        log.info("Deactivating profile with ID: {}", id);
        
        Profile profile = profileRepository.findById(id)
                .orElseThrow(() -> new ApiException("Profile not found with ID: " + id, HttpStatus.NOT_FOUND));
        
        profile.setIsActive(false);
        Profile deactivatedProfile = profileRepository.save(profile);
        log.info("Profile deactivated successfully with ID: {}", id);
        
        return mapToResponseDto(deactivatedProfile);
    }

    @Override
    public ProfileResponseDto deactivateProfileByUid(String uid) {
        log.info("Deactivating profile with UID: {}", uid);
        
        Profile profile = profileRepository.findByUid(uid)
                .orElseThrow(() -> new ApiException("Profile not found with UID: " + uid, HttpStatus.NOT_FOUND));
        
        profile.setIsActive(false);
        Profile deactivatedProfile = profileRepository.save(profile);
        log.info("Profile deactivated successfully with UID: {}", uid);
        
        return mapToResponseDto(deactivatedProfile);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return profileRepository.existsByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByPhoneNumber(String phoneNumber) {
        return profileRepository.existsByPhoneNumber(phoneNumber);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByIdTypeAndIdNumber(IdType idType, String idNumber) {
        return profileRepository.existsByIdTypeAndIdNumber(idType, idNumber);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByRegistrationNumber(String registrationNumber) {
        return profileRepository.existsByRegistrationNumber(registrationNumber);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean userHasProfile(User user) {
        return profileRepository.existsByUser(user);
    }

    @Override
    @Transactional(readOnly = true)
    public ProfileStats getProfileStats() {
        long totalProfiles = profileRepository.count();
        long activeProfiles = profileRepository.countByIsActiveTrue();
        long inactiveProfiles = profileRepository.countByIsActiveFalse();
        long personProfiles = profileRepository.countByProfileType(ProfileType.PERSON);
        long organizationProfiles = profileRepository.countByProfileType(ProfileType.ORGANIZATION);
        
        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime startOfYear = LocalDateTime.now().withDayOfYear(1).withHour(0).withMinute(0).withSecond(0);
        
        long profilesCreatedThisMonth = profileRepository.findProfilesCreatedBetween(startOfMonth, LocalDateTime.now(), Pageable.unpaged()).getTotalElements();
        long profilesCreatedThisYear = profileRepository.findProfilesCreatedBetween(startOfYear, LocalDateTime.now(), Pageable.unpaged()).getTotalElements();
        
        return new ProfileStats(
                totalProfiles,
                activeProfiles,
                inactiveProfiles,
                personProfiles,
                organizationProfiles,
                profilesCreatedThisMonth,
                profilesCreatedThisYear
        );
    }

    private ProfileResponseDto mapToResponseDto(Profile profile) {
        ProfileResponseDto dto = new ProfileResponseDto();
        dto.setId(profile.getId());
        dto.setUid(profile.getUid());
        dto.setProfileType(profile.getProfileType());
        dto.setName(profile.getName());
        dto.setDisplayName(profile.getDisplayName());
        dto.setEmail(profile.getEmail());
        dto.setPhoneNumber(profile.getPhoneNumber());
        dto.setDateOfBirth(profile.getDateOfBirth());
        dto.setGender(profile.getGender());
        dto.setIdType(profile.getIdType());
        dto.setIdNumber(profile.getIdNumber());
        dto.setRegistrationNumber(profile.getRegistrationNumber());
        dto.setWebsite(profile.getWebsite());
        dto.setContactPerson(profile.getContactPerson());
        dto.setAddress(profile.getAddress());
        dto.setProfilePictureUrl(profile.getProfilePictureUrl());
        dto.setBio(profile.getBio());
        dto.setIsActive(profile.getIsActive());
        dto.setCreatedAt(profile.getCreatedAt());
        dto.setUpdatedAt(profile.getUpdatedAt());
        
        // User information
        if (profile.getUser() != null) {
            dto.setUserId(profile.getUser().getId());
            dto.setUsername(profile.getUser().getUsername());
            dto.setUserEmail(profile.getUser().getEmail());
        }
        
        return dto;
    }

    private ProfileSummaryDto mapToSummaryDto(Profile profile) {
        ProfileSummaryDto dto = new ProfileSummaryDto();
        dto.setId(profile.getId());
        dto.setUid(profile.getUid());
        dto.setProfileType(profile.getProfileType());
        dto.setName(profile.getName());
        dto.setDisplayName(profile.getDisplayName());
        dto.setEmail(profile.getEmail());
        dto.setPhoneNumber(profile.getPhoneNumber());
        dto.setProfilePictureUrl(profile.getProfilePictureUrl());
        dto.setIsActive(profile.getIsActive());
        dto.setCreatedAt(profile.getCreatedAt());
        
        // User information
        if (profile.getUser() != null) {
            dto.setUsername(profile.getUser().getUsername());
        }
        
        return dto;
    }

    /**
     * Validates profile type specific field requirements
     */
    private void validateProfileTypeSpecificFields(CreateProfileRequestDto request) {
        if (request.getProfileType() == ProfileType.PERSON) {
            // PERSON profile validations
            if (request.getDateOfBirth() == null) {
                throw new ApiException("Date of birth is required for person profiles", HttpStatus.BAD_REQUEST);
            }
            if (request.getGender() == null || request.getGender().trim().isEmpty()) {
                throw new ApiException("Gender is required for person profiles", HttpStatus.BAD_REQUEST);
            }
            if (request.getIdType() == null) {
                throw new ApiException("ID type is required for person profiles", HttpStatus.BAD_REQUEST);
            }
            if (request.getIdNumber() == null || request.getIdNumber().trim().isEmpty()) {
                throw new ApiException("ID number is required for person profiles", HttpStatus.BAD_REQUEST);
            }
            
            // PERSON profiles should not have organization-specific fields
            if (request.getRegistrationNumber() != null) {
                throw new ApiException("Registration number is not applicable for person profiles", HttpStatus.BAD_REQUEST);
            }
            if (request.getContactPerson() != null) {
                throw new ApiException("Contact person is not applicable for person profiles", HttpStatus.BAD_REQUEST);
            }
            
        } else if (request.getProfileType() == ProfileType.ORGANIZATION) {
            // ORGANIZATION profile validations
            if (request.getRegistrationNumber() == null || request.getRegistrationNumber().trim().isEmpty()) {
                throw new ApiException("Registration number is required for organization profiles", HttpStatus.BAD_REQUEST);
            }
            if (request.getContactPerson() == null || request.getContactPerson().trim().isEmpty()) {
                throw new ApiException("Contact person is required for organization profiles", HttpStatus.BAD_REQUEST);
            }
            
            // ORGANIZATION profiles should not have person-specific fields
            if (request.getDateOfBirth() != null) {
                throw new ApiException("Date of birth is not applicable for organization profiles", HttpStatus.BAD_REQUEST);
            }
            if (request.getGender() != null) {
                throw new ApiException("Gender is not applicable for organization profiles", HttpStatus.BAD_REQUEST);
            }
            if (request.getIdType() != null || request.getIdNumber() != null) {
                throw new ApiException("ID type and number are not applicable for organization profiles", HttpStatus.BAD_REQUEST);
            }
        }
    }

    /**
     * Validates profile type specific field requirements for updates
     */
    private void validateProfileTypeSpecificFieldsForUpdate(Profile existingProfile, UpdateProfileRequestDto request) {
        ProfileType profileType = request.getProfileType() != null ? request.getProfileType() : existingProfile.getProfileType();
        
        if (profileType == ProfileType.PERSON) {
            // PERSON profile validations
            if (request.getDateOfBirth() == null && existingProfile.getDateOfBirth() == null) {
                throw new ApiException("Date of birth is required for person profiles", HttpStatus.BAD_REQUEST);
            }
            if ((request.getGender() == null || request.getGender().trim().isEmpty()) && 
                (existingProfile.getGender() == null || existingProfile.getGender().trim().isEmpty())) {
                throw new ApiException("Gender is required for person profiles", HttpStatus.BAD_REQUEST);
            }
            if (request.getIdType() == null && existingProfile.getIdType() == null) {
                throw new ApiException("ID type is required for person profiles", HttpStatus.BAD_REQUEST);
            }
            if ((request.getIdNumber() == null || request.getIdNumber().trim().isEmpty()) && 
                (existingProfile.getIdNumber() == null || existingProfile.getIdNumber().trim().isEmpty())) {
                throw new ApiException("ID number is required for person profiles", HttpStatus.BAD_REQUEST);
            }
            
            // PERSON profiles should not have organization-specific fields
            if (request.getRegistrationNumber() != null) {
                throw new ApiException("Registration number is not applicable for person profiles", HttpStatus.BAD_REQUEST);
            }
            if (request.getContactPerson() != null) {
                throw new ApiException("Contact person is not applicable for person profiles", HttpStatus.BAD_REQUEST);
            }
            
        } else if (profileType == ProfileType.ORGANIZATION) {
            // ORGANIZATION profile validations
            if ((request.getRegistrationNumber() == null || request.getRegistrationNumber().trim().isEmpty()) && 
                (existingProfile.getRegistrationNumber() == null || existingProfile.getRegistrationNumber().trim().isEmpty())) {
                throw new ApiException("Registration number is required for organization profiles", HttpStatus.BAD_REQUEST);
            }
            if ((request.getContactPerson() == null || request.getContactPerson().trim().isEmpty()) && 
                (existingProfile.getContactPerson() == null || existingProfile.getContactPerson().trim().isEmpty())) {
                throw new ApiException("Contact person is required for organization profiles", HttpStatus.BAD_REQUEST);
            }
            
            // ORGANIZATION profiles should not have person-specific fields
            if (request.getDateOfBirth() != null) {
                throw new ApiException("Date of birth is not applicable for organization profiles", HttpStatus.BAD_REQUEST);
            }
            if (request.getGender() != null) {
                throw new ApiException("Gender is not applicable for organization profiles", HttpStatus.BAD_REQUEST);
            }
            if (request.getIdType() != null || request.getIdNumber() != null) {
                throw new ApiException("ID type and number are not applicable for organization profiles", HttpStatus.BAD_REQUEST);
            }
        }
    }

    /**
     * Validates that a user is not an admin user.
     * Admin users cannot have profiles as they are system operators, not civic participants.
     * 
     * @param user The user to validate
     * @throws ApiException if user is an admin
     */
    private void validateUserIsNotAdmin(User user) {
        if (user.getUserType() == UserType.ADMIN) {
            throw new ApiException(
                "Admin users cannot have profiles. Profiles are only for regular users who participate in civic engagement.", 
                HttpStatus.BAD_REQUEST
            );
        }
    }

    /**
     * Creates a user with appropriate password strength based on profile type
     */
    private User createUserWithPasswordStrength(String username, String email, String password, ProfileType profileType, String phoneNumber) {
        log.info("Creating user with profile type: {}", profileType);
        
        // Check if username already exists
        if (userRepository.existsByUsername(username)) {
            throw new ApiException("Username already exists", HttpStatus.CONFLICT);
        }
        
        // Check if email already exists
        if (userRepository.existsByEmail(email)) {
            throw new ApiException("Email already exists", HttpStatus.CONFLICT);
        }
        
        // Determine required password strength based on profile type
        PasswordStrength requiredPasswordStrength;
        if (profileType == ProfileType.PERSON) {
            requiredPasswordStrength = PasswordStrength.WEAK; // Allow weak passwords for persons
        } else if (profileType == ProfileType.ORGANIZATION) {
            requiredPasswordStrength = PasswordStrength.STRONG; // Require strong passwords for organizations
        } else {
            requiredPasswordStrength = PasswordStrength.FAIR; // Default fallback
        }
        
        // Validate password strength
        PasswordStrength actualPasswordStrength = calculatePasswordStrength(password);
        if (actualPasswordStrength.ordinal() < requiredPasswordStrength.ordinal()) {
            String errorMessage = String.format("Password strength is %s, but %s is required for %s profiles", 
                actualPasswordStrength.name(), requiredPasswordStrength.name(), profileType.name());
            throw new ApiException(errorMessage, HttpStatus.BAD_REQUEST);
        }
        
        // Create user
        User user = new User();
        user.setUid(ulid.nextULID());
        user.setUsername(username);
        user.setEmail(email);
        
        // Hash the password properly
        String hashedPassword = passwordEncoder.encode(password);
        user.setPasswordHash(hashedPassword);
        user.setPasswordStrength(actualPasswordStrength);
        user.setUserType(UserType.USER);
        user.setRequirePasswordChange(false);
        user.ensureUid();
        
        // Generate email verification token
        String verificationToken = generateSecureToken();
        LocalDateTime expirationTime = LocalDateTime.now().plusHours(EMAIL_VERIFICATION_TOKEN_EXPIRY_HOURS);
        user.setEmailVerificationToken(verificationToken);
        user.setEmailVerificationTokenExpiresAt(expirationTime);
        user.setEmailVerified(false);
        
        // Set password tracking
        user.setPasswordChangedAt(LocalDateTime.now());
        user.updatePasswordExpiry();
        
        User savedUser = userRepository.save(user);
        log.info("User created successfully with ID: {} and password strength: {}", savedUser.getId(), actualPasswordStrength);
        
        // Send email verification email
        sendEmailVerificationEmail(savedUser, verificationToken);
        
        // Send welcome SMS if phone number is provided
        if (phoneNumber != null && !phoneNumber.trim().isEmpty()) {
            try {
                sendWelcomeSms(phoneNumber, savedUser.getUsername(), verificationToken);
            } catch (Exception e) {
                log.error("Failed to send welcome SMS to: {}", phoneNumber, e);
                // Don't throw - SMS failure shouldn't break user creation
            }
        }
        
        return savedUser;
    }

    /**
     * Calculates password strength based on password characteristics
     */
    private PasswordStrength calculatePasswordStrength(String password) {
        if (password == null || password.length() < 6) {
            return PasswordStrength.WEAK;
        }
        
        boolean hasUpperCase = password.chars().anyMatch(Character::isUpperCase);
        boolean hasLowerCase = password.chars().anyMatch(Character::isLowerCase);
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);
        boolean hasSpecialChar = password.chars().anyMatch(ch -> "!@#$%^&*()_+-=[]{}|;:,.<>?".indexOf(ch) >= 0);
        
        int strengthScore = 0;
        if (hasUpperCase) strengthScore++;
        if (hasLowerCase) strengthScore++;
        if (hasDigit) strengthScore++;
        if (hasSpecialChar) strengthScore++;
        if (password.length() >= 8) strengthScore++;
        if (password.length() >= 12) strengthScore++;
        
        if (strengthScore <= 2) {
            return PasswordStrength.WEAK;
        } else if (strengthScore <= 4) {
            return PasswordStrength.FAIR;
        } else if (strengthScore <= 5) {
            return PasswordStrength.GOOD;
        } else {
            return PasswordStrength.STRONG;
        }
    }

    /**
     * Generate a secure random token for email verification
     */
    private String generateSecureToken() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    /**
     * Send email verification email to user
     */
    private void sendEmailVerificationEmail(User user, String verificationToken) {
        try {
            // Create verification link
            String verificationLink = "http://localhost:3000/verify-email?token=" + verificationToken;

            String htmlContent = String.format("""
                <html>
                <body>
                    <h2>Email Verification</h2>
                    <p>Hello %s,</p>
                    <p>Thank you for registering. Please verify your email address by clicking the link below:</p>
                    <p><a href="%s" style="background-color: #007bff; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px;">Verify Email</a></p>
                    <p>This link will expire in %d hours.</p>
                    <p>If you did not create an account, please ignore this email.</p>
                    <br>
                    <p>Best regards,<br>Taarifu Engine Team</p>
                </body>
                </html>
                """, user.getUsername(), verificationLink, EMAIL_VERIFICATION_TOKEN_EXPIRY_HOURS);

            emailService.sendSimpleEmailAsync(
                user.getEmail(),
                "Verify Your Email Address",
                htmlContent,
                EmailType.WELCOME
            );

            log.info("Email verification email sent to: {}", user.getEmail());
        } catch (Exception e) {
            log.error("Failed to send email verification email to: {}", user.getEmail(), e);
            // Don't throw exception - email failure shouldn't break the flow
        }
    }

    /**
     * Send welcome SMS to user
     */
    private void sendWelcomeSms(String phoneNumber, String username, String verificationToken) {
        try {
            String message = String.format(
                "Welcome to Taarifu! Your username is: %s. Please verify your email to activate your account. Verification code: %s",
                username, verificationToken.substring(0, Math.min(6, verificationToken.length()))
            );

            smsService.sendSimpleSmsAsync(phoneNumber, message, SmsType.WELCOME);
            log.info("Welcome SMS sent to: {}", phoneNumber);
        } catch (Exception e) {
            log.error("Failed to send welcome SMS to: {}", phoneNumber, e);
            // Don't throw exception - SMS failure shouldn't break the flow
        }
    }
}
