package com.taarifu_engine_api.modules.parliament.service;

import com.taarifu_engine_api.modules.common.domain.util.PageResponseWrapper;
import com.taarifu_engine_api.modules.common.exception.ApiException;
import com.taarifu_engine_api.modules.parliament.domain.dto.CreateParliamentRequestDto;
import com.taarifu_engine_api.modules.parliament.domain.dto.ParliamentResponseDto;
import com.taarifu_engine_api.modules.parliament.domain.dto.UpdateParliamentRequestDto;
import com.taarifu_engine_api.modules.parliament.domain.entity.Parliament;
import com.taarifu_engine_api.modules.parliament.repository.ParliamentRepository;
import com.taarifu_engine_api.modules.userandrole.domain.entity.User;
import com.taarifu_engine_api.modules.userandrole.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * Service implementation for Parliament operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ParliamentServiceImpl implements ParliamentService {

    private final ParliamentRepository parliamentRepository;
    private final UserRepository userRepository;

    @Override
    public ParliamentResponseDto createParliament(CreateParliamentRequestDto request) {
        log.info("Creating new parliament: {}", request.getName());

        // Validate dates
        validateParliamentDates(request.getStartDate(), request.getEndDate());

        // Check if parliament with same name already exists
        if (parliamentRepository.existsByName(request.getName())) {
            throw new ApiException("Parliament with name '" + request.getName() + "' already exists", HttpStatus.BAD_REQUEST);
        }

        // Generate next parliament code
        String nextParliamentCode = generateNextParliamentCode();

        // Get current authenticated user
        User currentUser = getCurrentAuthenticatedUser();

        // Create new parliament
        Parliament parliament = new Parliament();
        parliament.setCode(nextParliamentCode);
        parliament.setName(request.getName());
        parliament.setDescription(request.getDescription());
        parliament.setStartDate(request.getStartDate());
        parliament.setEndDate(request.getEndDate());
        parliament.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        parliament.setIsCurrent(request.getIsCurrent() != null ? request.getIsCurrent() : false);
        parliament.setCreatedBy(currentUser);
        parliament.setUpdatedBy(currentUser);

        // Ensure UID is generated
        parliament.ensureUid();

        Parliament savedParliament = parliamentRepository.save(parliament);
        log.info("Successfully created parliament with ID: {}, UID: {}, and code: {}", 
                savedParliament.getId(), savedParliament.getUid(), savedParliament.getCode());

        return mapToParliamentResponse(savedParliament);
    }

    @Override
    public ParliamentResponseDto updateParliament(Long id, UpdateParliamentRequestDto request) {
        log.info("Updating parliament with ID: {}", id);

        Parliament parliament = parliamentRepository.findById(id)
                .orElseThrow(() -> new ApiException("Parliament not found with ID: " + id, HttpStatus.NOT_FOUND));

        return updateParliamentInternal(parliament, request);
    }

    @Override
    public ParliamentResponseDto updateParliamentByUid(String uid, UpdateParliamentRequestDto request) {
        log.info("Updating parliament with UID: {}", uid);

        Parliament parliament = parliamentRepository.findByUid(uid)
                .orElseThrow(() -> new ApiException("Parliament not found with UID: " + uid, HttpStatus.NOT_FOUND));

        return updateParliamentInternal(parliament, request);
    }

    private ParliamentResponseDto updateParliamentInternal(Parliament parliament, UpdateParliamentRequestDto request) {
        // Check if new name conflicts with existing parliaments
        if (request.getName() != null && !request.getName().equals(parliament.getName())) {
            if (parliamentRepository.existsByName(request.getName())) {
                throw new ApiException("Parliament with name '" + request.getName() + "' already exists", HttpStatus.BAD_REQUEST);
            }
            parliament.setName(request.getName());
        }

        // Update other fields
        if (request.getDescription() != null) {
            parliament.setDescription(request.getDescription());
        }
        if (request.getStartDate() != null) {
            parliament.setStartDate(request.getStartDate());
        }
        if (request.getEndDate() != null) {
            parliament.setEndDate(request.getEndDate());
        }
        if (request.getIsActive() != null) {
            parliament.setIsActive(request.getIsActive());
        }
        if (request.getIsCurrent() != null) {
            parliament.setIsCurrent(request.getIsCurrent());
        }

        // Validate dates if they were updated
        if (request.getStartDate() != null || request.getEndDate() != null) {
            validateParliamentDates(parliament.getStartDate(), parliament.getEndDate());
        }

        // Set updated by
        parliament.setUpdatedBy(getCurrentAuthenticatedUser());

        Parliament savedParliament = parliamentRepository.save(parliament);
        log.info("Successfully updated parliament with ID: {}", savedParliament.getId());

        return mapToParliamentResponse(savedParliament);
    }

    @Override
    @Transactional(readOnly = true)
    public ParliamentResponseDto getParliamentById(Long id) {
        log.debug("Fetching parliament with ID: {}", id);

        Parliament parliament = parliamentRepository.findById(id)
                .orElseThrow(() -> new ApiException("Parliament not found with ID: " + id, HttpStatus.NOT_FOUND));

        return mapToParliamentResponse(parliament);
    }

    @Override
    @Transactional(readOnly = true)
    public ParliamentResponseDto getParliamentByUid(String uid) {
        log.debug("Fetching parliament with UID: {}", uid);

        Parliament parliament = parliamentRepository.findByUid(uid)
                .orElseThrow(() -> new ApiException("Parliament not found with UID: " + uid, HttpStatus.NOT_FOUND));

        return mapToParliamentResponse(parliament);
    }

    @Override
    @Transactional(readOnly = true)
    public ParliamentResponseDto getParliamentByCode(String code) {
        log.debug("Fetching parliament with code: {}", code);

        Parliament parliament = parliamentRepository.findByCode(code)
                .orElseThrow(() -> new ApiException("Parliament not found with code: " + code, HttpStatus.NOT_FOUND));

        return mapToParliamentResponse(parliament);
    }

    @Override
    @Transactional(readOnly = true)
    public ParliamentResponseDto getCurrentParliament() {
        log.debug("Fetching current parliament");

        Parliament parliament = parliamentRepository.findByIsCurrentTrue()
                .orElseThrow(() -> new ApiException("No current parliament found", HttpStatus.NOT_FOUND));

        return mapToParliamentResponse(parliament);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseWrapper<ParliamentResponseDto> getAllParliaments(Pageable pageable) {
        Page<Parliament> parliaments = parliamentRepository.findAllByOrderByStartDateDesc(pageable);
        Page<ParliamentResponseDto> parliamentResponses = parliaments.map(this::mapToParliamentResponse);
        return PageResponseWrapper.fromPage(parliamentResponses, "Parliaments fetched successfully");
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseWrapper<ParliamentResponseDto> getActiveParliaments(Pageable pageable) {
        Page<Parliament> parliaments = parliamentRepository.findByIsActiveTrueOrderByStartDateDesc(pageable);
        Page<ParliamentResponseDto> parliamentResponses = parliaments.map(this::mapToParliamentResponse);
        return PageResponseWrapper.fromPage(parliamentResponses, "Active parliaments fetched successfully");
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseWrapper<ParliamentResponseDto> searchParliaments(String searchTerm, Pageable pageable) {
        Page<Parliament> parliaments = parliamentRepository.searchParliaments(searchTerm, pageable);
        Page<ParliamentResponseDto> parliamentResponses = parliaments.map(this::mapToParliamentResponse);
        return PageResponseWrapper.fromPage(parliamentResponses, "Parliaments fetched successfully");
    }

    @Override
    public void deleteParliament(Long id) {
        log.info("Deleting parliament with ID: {}", id);

        Parliament parliament = parliamentRepository.findById(id)
                .orElseThrow(() -> new ApiException("Parliament not found with ID: " + id, HttpStatus.NOT_FOUND));

        // Soft delete by setting isActive to false
        parliament.setIsActive(false);
        parliament.setIsCurrent(false);
        parliament.setUpdatedBy(getCurrentAuthenticatedUser());

        parliamentRepository.save(parliament);
        log.info("Successfully deleted parliament with ID: {}", id);
    }

    @Override
    public void deleteParliamentByUid(String uid) {
        log.info("Deleting parliament with UID: {}", uid);

        Parliament parliament = parliamentRepository.findByUid(uid)
                .orElseThrow(() -> new ApiException("Parliament not found with UID: " + uid, HttpStatus.NOT_FOUND));

        // Soft delete by setting isActive to false
        parliament.setIsActive(false);
        parliament.setIsCurrent(false);
        parliament.setUpdatedBy(getCurrentAuthenticatedUser());

        parliamentRepository.save(parliament);
        log.info("Successfully deleted parliament with UID: {}", uid);
    }

    @Override
    public ParliamentResponseDto setCurrentParliament(Long id) {
        log.info("Setting parliament with ID: {} as current", id);

        Parliament parliament = parliamentRepository.findById(id)
                .orElseThrow(() -> new ApiException("Parliament not found with ID: " + id, HttpStatus.NOT_FOUND));

        // Set all other parliaments as not current
        parliamentRepository.findAll().forEach(p -> {
            if (!p.getId().equals(id)) {
                p.setIsCurrent(false);
                p.setUpdatedBy(getCurrentAuthenticatedUser());
                parliamentRepository.save(p);
            }
        });

        // Set this parliament as current
        parliament.setIsCurrent(true);
        parliament.setIsActive(true);
        parliament.setUpdatedBy(getCurrentAuthenticatedUser());

        Parliament savedParliament = parliamentRepository.save(parliament);
        log.info("Successfully set parliament with ID: {} as current", id);

        return mapToParliamentResponse(savedParliament);
    }

    @Override
    public ParliamentResponseDto setCurrentParliamentByUid(String uid) {
        log.info("Setting parliament with UID: {} as current", uid);

        Parliament parliament = parliamentRepository.findByUid(uid)
                .orElseThrow(() -> new ApiException("Parliament not found with UID: " + uid, HttpStatus.NOT_FOUND));

        // Set all other parliaments as not current
        parliamentRepository.findAll().forEach(p -> {
            if (!p.getUid().equals(uid)) {
                p.setIsCurrent(false);
                p.setUpdatedBy(getCurrentAuthenticatedUser());
                parliamentRepository.save(p);
            }
        });

        // Set this parliament as current
        parliament.setIsCurrent(true);
        parliament.setIsActive(true);
        parliament.setUpdatedBy(getCurrentAuthenticatedUser());

        Parliament savedParliament = parliamentRepository.save(parliament);
        log.info("Successfully set parliament with UID: {} as current", uid);

        return mapToParliamentResponse(savedParliament);
    }

    @Override
    @Transactional(readOnly = true)
    public ParliamentStats getParliamentStats() {
        long totalParliaments = parliamentRepository.count();
        long activeParliaments = parliamentRepository.countByIsActiveTrue();
        long inactiveParliaments = parliamentRepository.countByIsActiveFalse();
        long currentParliaments = parliamentRepository.countByIsCurrentTrue();
        
        LocalDate currentDate = LocalDate.now();
        long endedParliaments = parliamentRepository.findEndedParliaments(currentDate).size();
        long inSessionParliaments = parliamentRepository.findCurrentSessionParliaments(currentDate).size();

        return new ParliamentStats(
                totalParliaments,
                activeParliaments,
                inactiveParliaments,
                currentParliaments,
                endedParliaments,
                inSessionParliaments
        );
    }

    /**
     * Maps Parliament entity to ParliamentResponseDto
     */
    private ParliamentResponseDto mapToParliamentResponse(Parliament parliament) {
        if (parliament == null) {
            return null;
        }

        ParliamentResponseDto response = new ParliamentResponseDto();
        response.setId(parliament.getId());
        response.setUid(parliament.getUid());
        response.setCode(parliament.getCode());
        response.setName(parliament.getName());
        response.setDescription(parliament.getDescription());
        response.setStartDate(parliament.getStartDate());
        response.setEndDate(parliament.getEndDate());
        response.setIsActive(parliament.getIsActive());
        response.setIsCurrent(parliament.getIsCurrent());
        response.setDurationInYears(parliament.getDurationInYears());
        response.setInSession(parliament.isInSession());
        response.setHasEnded(parliament.hasEnded());
        response.setHasStarted(parliament.hasStarted());
        response.setCreatedAt(parliament.getCreatedAt());
        response.setUpdatedAt(parliament.getUpdatedAt());

        return response;
    }

    /**
     * Validates parliament dates
     */
    private void validateParliamentDates(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new ApiException("Start date and end date are required", HttpStatus.BAD_REQUEST);
        }

        if (startDate.isAfter(endDate)) {
            throw new ApiException("Start date cannot be after end date", HttpStatus.BAD_REQUEST);
        }

        if (startDate.isBefore(LocalDate.now().minusYears(50))) {
            throw new ApiException("Start date cannot be more than 50 years in the past", HttpStatus.BAD_REQUEST);
        }

        if (endDate.isAfter(LocalDate.now().plusYears(10))) {
            throw new ApiException("End date cannot be more than 10 years in the future", HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Generate the next parliament code
     */
    private String generateNextParliamentCode() {
        Parliament lastParliament = parliamentRepository.findFirstByOrderByCodeDesc().orElse(null);
        
        int nextSequenceNumber = 1;
        if (lastParliament != null) {
            String code = lastParliament.getCode();
            if (code != null && code.startsWith("PT") && code.length() == 8) {
                try {
                    nextSequenceNumber = Integer.parseInt(code.substring(2)) + 1;
                } catch (NumberFormatException e) {
                    log.warn("Invalid parliament code format: {}", code);
                }
            }
        }
        
        return String.format("PT%06d", nextSequenceNumber);
    }

    /**
     * Get the current authenticated user from SecurityContext
     */
    private User getCurrentAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ApiException("No authenticated user found", HttpStatus.UNAUTHORIZED);
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof User) {
            return (User) principal;
        } else {
            throw new ApiException("Invalid user principal type", HttpStatus.UNAUTHORIZED);
        }
    }
}
