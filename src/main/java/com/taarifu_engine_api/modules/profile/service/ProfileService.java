package com.taarifu_engine_api.modules.profile.service;

import com.taarifu_engine_api.modules.profile.domain.dto.CreateProfileRequestDto;
import com.taarifu_engine_api.modules.profile.domain.dto.ProfileResponseDto;
import com.taarifu_engine_api.modules.profile.domain.dto.ProfileSummaryDto;
import com.taarifu_engine_api.modules.profile.domain.dto.UpdateProfileRequestDto;
import com.taarifu_engine_api.modules.profile.domain.enums.IdType;
import com.taarifu_engine_api.modules.profile.domain.enums.ProfileType;
import com.taarifu_engine_api.modules.userandrole.domain.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

/**
 * Service interface for Profile operations
 */
public interface ProfileService {

    /**
     * Create a new profile with user creation
     */
    ProfileResponseDto createProfile(CreateProfileRequestDto request, String username, String email, String password);

    /**
     * Update profile by ID
     */
    ProfileResponseDto updateProfile(Long id, UpdateProfileRequestDto request);

    /**
     * Update profile by UID
     */
    ProfileResponseDto updateProfileByUid(String uid, UpdateProfileRequestDto request);

    /**
     * Update profile by user
     */
    ProfileResponseDto updateProfileByUser(User user, UpdateProfileRequestDto request);

    /**
     * Get profile by ID
     */
    ProfileResponseDto getProfileById(Long id);

    /**
     * Get profile by UID
     */
    ProfileResponseDto getProfileByUid(String uid);

    /**
     * Get profile by user
     */
    ProfileResponseDto getProfileByUser(User user);

    /**
     * Get profile by user ID
     */
    ProfileResponseDto getProfileByUserId(Long userId);

    /**
     * Get profile by email
     */
    ProfileResponseDto getProfileByEmail(String email);

    /**
     * Get profile by phone number
     */
    ProfileResponseDto getProfileByPhoneNumber(String phoneNumber);

    /**
     * Get profile by ID type and ID number combination
     */
    ProfileResponseDto getProfileByIdTypeAndIdNumber(IdType idType, String idNumber);

    /**
     * Get all profiles with pagination
     */
    Page<ProfileResponseDto> getAllProfiles(Pageable pageable);

    /**
     * Get all active profiles with pagination
     */
    Page<ProfileResponseDto> getActiveProfiles(Pageable pageable);

    /**
     * Get profiles by type with pagination
     */
    Page<ProfileResponseDto> getProfilesByType(ProfileType profileType, Pageable pageable);

    /**
     * Get active profiles by type with pagination
     */
    Page<ProfileResponseDto> getActiveProfilesByType(ProfileType profileType, Pageable pageable);

    /**
     * Search profiles with pagination
     */
    Page<ProfileResponseDto> searchProfiles(String searchTerm, Pageable pageable);

    /**
     * Search profiles by type with pagination
     */
    Page<ProfileResponseDto> searchProfilesByType(ProfileType profileType, String searchTerm, Pageable pageable);

    /**
     * Get profile summaries with pagination (for lists)
     */
    Page<ProfileSummaryDto> getProfileSummaries(Pageable pageable);

    /**
     * Get active profile summaries with pagination
     */
    Page<ProfileSummaryDto> getActiveProfileSummaries(Pageable pageable);

    /**
     * Get profile summaries by type with pagination
     */
    Page<ProfileSummaryDto> getProfileSummariesByType(ProfileType profileType, Pageable pageable);

    /**
     * Get profiles created within a date range
     */
    Page<ProfileResponseDto> getProfilesCreatedBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    /**
     * Delete profile by ID (soft delete)
     */
    void deleteProfile(Long id);

    /**
     * Delete profile by UID (soft delete)
     */
    void deleteProfileByUid(String uid);

    /**
     * Delete profile by user (soft delete)
     */
    void deleteProfileByUser(User user);

    /**
     * Activate profile by ID
     */
    ProfileResponseDto activateProfile(Long id);

    /**
     * Activate profile by UID
     */
    ProfileResponseDto activateProfileByUid(String uid);

    /**
     * Deactivate profile by ID
     */
    ProfileResponseDto deactivateProfile(Long id);

    /**
     * Deactivate profile by UID
     */
    ProfileResponseDto deactivateProfileByUid(String uid);

    /**
     * Check if profile exists by email
     */
    boolean existsByEmail(String email);

    /**
     * Check if profile exists by phone number
     */
    boolean existsByPhoneNumber(String phoneNumber);

    /**
     * Check if profile exists by ID type and ID number combination
     */
    boolean existsByIdTypeAndIdNumber(IdType idType, String idNumber);

    /**
     * Check if profile exists by registration number
     */
    boolean existsByRegistrationNumber(String registrationNumber);

    /**
     * Check if user has a profile
     */
    boolean userHasProfile(User user);

    /**
     * Get profile statistics
     */
    ProfileStats getProfileStats();

    /**
     * Record class for profile statistics
     */
    record ProfileStats(
            long totalProfiles,
            long activeProfiles,
            long inactiveProfiles,
            long personProfiles,
            long organizationProfiles,
            long profilesCreatedThisMonth,
            long profilesCreatedThisYear
    ) {}
}
