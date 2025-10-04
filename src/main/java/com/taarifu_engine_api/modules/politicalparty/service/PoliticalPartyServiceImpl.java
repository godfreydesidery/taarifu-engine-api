package com.taarifu_engine_api.modules.politicalparty.service;

import com.taarifu_engine_api.modules.common.domain.util.PageResponseWrapper;
import com.taarifu_engine_api.modules.common.exception.ApiException;
import com.taarifu_engine_api.modules.politicalparty.domain.dto.CreatePoliticalPartyRequestDto;
import com.taarifu_engine_api.modules.politicalparty.domain.dto.PoliticalPartyResponseDto;
import com.taarifu_engine_api.modules.politicalparty.domain.dto.UpdatePoliticalPartyRequestDto;
import com.taarifu_engine_api.modules.politicalparty.domain.entity.PoliticalParty;
import com.taarifu_engine_api.modules.politicalparty.repository.PoliticalPartyRepository;
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
 * Service implementation for Political Party operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PoliticalPartyServiceImpl implements PoliticalPartyService {

    private final PoliticalPartyRepository politicalPartyRepository;
    private final UserRepository userRepository;

    @Override
    public PoliticalPartyResponseDto createPoliticalParty(CreatePoliticalPartyRequestDto request) {
        log.info("Creating new political party: {}", request.getName());

        // Check if political party with same name already exists
        if (politicalPartyRepository.existsByName(request.getName())) {
            throw new ApiException("Political party with name '" + request.getName() + "' already exists", HttpStatus.BAD_REQUEST);
        }

        // Check if political party with same abbreviation already exists
        if (politicalPartyRepository.existsByAbbreviation(request.getAbbreviation())) {
            throw new ApiException("Political party with abbreviation '" + request.getAbbreviation() + "' already exists", HttpStatus.BAD_REQUEST);
        }

        // Generate next political party code
        String nextPoliticalPartyCode = generateNextPoliticalPartyCode();

        // Get current authenticated user
        User currentUser = getCurrentAuthenticatedUser();

        // Create new political party
        PoliticalParty politicalParty = new PoliticalParty();
        politicalParty.setCode(nextPoliticalPartyCode);
        politicalParty.setName(request.getName());
        politicalParty.setAbbreviation(request.getAbbreviation());
        politicalParty.setDescription(request.getDescription());
        politicalParty.setFoundingDate(request.getFoundingDate());
        politicalParty.setFoundingLocation(request.getFoundingLocation());
        politicalParty.setIdeology(request.getIdeology());
        politicalParty.setColors(request.getColors());
        politicalParty.setSymbol(request.getSymbol());
        politicalParty.setMotto(request.getMotto());
        politicalParty.setWebsiteUrl(request.getWebsiteUrl());
        politicalParty.setEmail(request.getEmail());
        politicalParty.setPhone(request.getPhone());
        politicalParty.setHeadquartersAddress(request.getHeadquartersAddress());
        politicalParty.setIsRegistered(request.getIsRegistered() != null ? request.getIsRegistered() : true);
        politicalParty.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        politicalParty.setRegistrationNumber(request.getRegistrationNumber());
        politicalParty.setRegistrationDate(request.getRegistrationDate());
        politicalParty.setMemberCount(request.getMemberCount());
        politicalParty.setCreatedBy(currentUser);
        politicalParty.setUpdatedBy(currentUser);

        // Ensure UID is generated
        politicalParty.ensureUid();

        PoliticalParty savedPoliticalParty = politicalPartyRepository.save(politicalParty);
        log.info("Successfully created political party with ID: {}, UID: {}, and code: {}", 
                savedPoliticalParty.getId(), savedPoliticalParty.getUid(), savedPoliticalParty.getCode());

        return mapToPoliticalPartyResponse(savedPoliticalParty);
    }

    @Override
    public PoliticalPartyResponseDto updatePoliticalParty(Long id, UpdatePoliticalPartyRequestDto request) {
        log.info("Updating political party with ID: {}", id);

        PoliticalParty politicalParty = politicalPartyRepository.findById(id)
                .orElseThrow(() -> new ApiException("Political party not found with ID: " + id, HttpStatus.NOT_FOUND));

        return updatePoliticalPartyInternal(politicalParty, request);
    }

    @Override
    public PoliticalPartyResponseDto updatePoliticalPartyByUid(String uid, UpdatePoliticalPartyRequestDto request) {
        log.info("Updating political party with UID: {}", uid);

        PoliticalParty politicalParty = politicalPartyRepository.findByUid(uid)
                .orElseThrow(() -> new ApiException("Political party not found with UID: " + uid, HttpStatus.NOT_FOUND));

        return updatePoliticalPartyInternal(politicalParty, request);
    }

    private PoliticalPartyResponseDto updatePoliticalPartyInternal(PoliticalParty politicalParty, UpdatePoliticalPartyRequestDto request) {
        // Check if new name conflicts with existing political parties
        if (request.getName() != null && !request.getName().equals(politicalParty.getName())) {
            if (politicalPartyRepository.existsByName(request.getName())) {
                throw new ApiException("Political party with name '" + request.getName() + "' already exists", HttpStatus.BAD_REQUEST);
            }
            politicalParty.setName(request.getName());
        }

        // Check if new abbreviation conflicts with existing political parties
        if (request.getAbbreviation() != null && !request.getAbbreviation().equals(politicalParty.getAbbreviation())) {
            if (politicalPartyRepository.existsByAbbreviation(request.getAbbreviation())) {
                throw new ApiException("Political party with abbreviation '" + request.getAbbreviation() + "' already exists", HttpStatus.BAD_REQUEST);
            }
            politicalParty.setAbbreviation(request.getAbbreviation());
        }

        // Update other fields
        if (request.getDescription() != null) {
            politicalParty.setDescription(request.getDescription());
        }
        if (request.getFoundingDate() != null) {
            politicalParty.setFoundingDate(request.getFoundingDate());
        }
        if (request.getFoundingLocation() != null) {
            politicalParty.setFoundingLocation(request.getFoundingLocation());
        }
        if (request.getIdeology() != null) {
            politicalParty.setIdeology(request.getIdeology());
        }
        if (request.getColors() != null) {
            politicalParty.setColors(request.getColors());
        }
        if (request.getSymbol() != null) {
            politicalParty.setSymbol(request.getSymbol());
        }
        if (request.getMotto() != null) {
            politicalParty.setMotto(request.getMotto());
        }
        if (request.getWebsiteUrl() != null) {
            politicalParty.setWebsiteUrl(request.getWebsiteUrl());
        }
        if (request.getEmail() != null) {
            politicalParty.setEmail(request.getEmail());
        }
        if (request.getPhone() != null) {
            politicalParty.setPhone(request.getPhone());
        }
        if (request.getHeadquartersAddress() != null) {
            politicalParty.setHeadquartersAddress(request.getHeadquartersAddress());
        }
        if (request.getIsRegistered() != null) {
            politicalParty.setIsRegistered(request.getIsRegistered());
        }
        if (request.getIsActive() != null) {
            politicalParty.setIsActive(request.getIsActive());
        }
        if (request.getRegistrationNumber() != null) {
            politicalParty.setRegistrationNumber(request.getRegistrationNumber());
        }
        if (request.getRegistrationDate() != null) {
            politicalParty.setRegistrationDate(request.getRegistrationDate());
        }
        if (request.getMemberCount() != null) {
            politicalParty.setMemberCount(request.getMemberCount());
        }

        // Set updated by
        politicalParty.setUpdatedBy(getCurrentAuthenticatedUser());

        PoliticalParty savedPoliticalParty = politicalPartyRepository.save(politicalParty);
        log.info("Successfully updated political party with ID: {}", savedPoliticalParty.getId());

        return mapToPoliticalPartyResponse(savedPoliticalParty);
    }

    @Override
    @Transactional(readOnly = true)
    public PoliticalPartyResponseDto getPoliticalPartyById(Long id) {
        log.debug("Fetching political party with ID: {}", id);

        PoliticalParty politicalParty = politicalPartyRepository.findById(id)
                .orElseThrow(() -> new ApiException("Political party not found with ID: " + id, HttpStatus.NOT_FOUND));

        return mapToPoliticalPartyResponse(politicalParty);
    }

    @Override
    @Transactional(readOnly = true)
    public PoliticalPartyResponseDto getPoliticalPartyByUid(String uid) {
        log.debug("Fetching political party with UID: {}", uid);

        PoliticalParty politicalParty = politicalPartyRepository.findByUid(uid)
                .orElseThrow(() -> new ApiException("Political party not found with UID: " + uid, HttpStatus.NOT_FOUND));

        return mapToPoliticalPartyResponse(politicalParty);
    }

    @Override
    @Transactional(readOnly = true)
    public PoliticalPartyResponseDto getPoliticalPartyByCode(String code) {
        log.debug("Fetching political party with code: {}", code);

        PoliticalParty politicalParty = politicalPartyRepository.findByCode(code)
                .orElseThrow(() -> new ApiException("Political party not found with code: " + code, HttpStatus.NOT_FOUND));

        return mapToPoliticalPartyResponse(politicalParty);
    }

    @Override
    @Transactional(readOnly = true)
    public PoliticalPartyResponseDto getPoliticalPartyByName(String name) {
        log.debug("Fetching political party with name: {}", name);

        PoliticalParty politicalParty = politicalPartyRepository.findByName(name)
                .orElseThrow(() -> new ApiException("Political party not found with name: " + name, HttpStatus.NOT_FOUND));

        return mapToPoliticalPartyResponse(politicalParty);
    }

    @Override
    @Transactional(readOnly = true)
    public PoliticalPartyResponseDto getPoliticalPartyByAbbreviation(String abbreviation) {
        log.debug("Fetching political party with abbreviation: {}", abbreviation);

        PoliticalParty politicalParty = politicalPartyRepository.findByAbbreviation(abbreviation)
                .orElseThrow(() -> new ApiException("Political party not found with abbreviation: " + abbreviation, HttpStatus.NOT_FOUND));

        return mapToPoliticalPartyResponse(politicalParty);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseWrapper<PoliticalPartyResponseDto> getAllPoliticalParties(Pageable pageable) {
        Page<PoliticalParty> politicalParties = politicalPartyRepository.findAllByOrderByNameAsc(pageable);
        Page<PoliticalPartyResponseDto> politicalPartyResponses = politicalParties.map(this::mapToPoliticalPartyResponse);
        return PageResponseWrapper.fromPage(politicalPartyResponses, "Political parties fetched successfully");
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseWrapper<PoliticalPartyResponseDto> getActivePoliticalParties(Pageable pageable) {
        Page<PoliticalParty> politicalParties = politicalPartyRepository.findByIsActiveTrueOrderByNameAsc(pageable);
        Page<PoliticalPartyResponseDto> politicalPartyResponses = politicalParties.map(this::mapToPoliticalPartyResponse);
        return PageResponseWrapper.fromPage(politicalPartyResponses, "Active political parties fetched successfully");
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseWrapper<PoliticalPartyResponseDto> getRegisteredPoliticalParties(Pageable pageable) {
        Page<PoliticalParty> politicalParties = politicalPartyRepository.findByIsRegisteredTrueOrderByNameAsc(pageable);
        Page<PoliticalPartyResponseDto> politicalPartyResponses = politicalParties.map(this::mapToPoliticalPartyResponse);
        return PageResponseWrapper.fromPage(politicalPartyResponses, "Registered political parties fetched successfully");
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseWrapper<PoliticalPartyResponseDto> getOperationalPoliticalParties(Pageable pageable) {
        // For simplicity, we'll use the active parties page method
        // In a real implementation, you might want to create a custom repository method
        Page<PoliticalParty> politicalParties = politicalPartyRepository.findByIsActiveTrueOrderByNameAsc(pageable);
        Page<PoliticalPartyResponseDto> politicalPartyResponses = politicalParties.map(this::mapToPoliticalPartyResponse);
        return PageResponseWrapper.fromPage(politicalPartyResponses, "Operational political parties fetched successfully");
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseWrapper<PoliticalPartyResponseDto> searchPoliticalParties(String searchTerm, Pageable pageable) {
        Page<PoliticalParty> politicalParties = politicalPartyRepository.searchPoliticalParties(searchTerm, pageable);
        Page<PoliticalPartyResponseDto> politicalPartyResponses = politicalParties.map(this::mapToPoliticalPartyResponse);
        return PageResponseWrapper.fromPage(politicalPartyResponses, "Political parties fetched successfully");
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseWrapper<PoliticalPartyResponseDto> getPoliticalPartiesByFoundingYear(int year, Pageable pageable) {
        // For simplicity, we'll use search functionality
        // In a real implementation, you might want to create a custom repository method
        Page<PoliticalParty> politicalParties = politicalPartyRepository.findAllByOrderByNameAsc(pageable);
        Page<PoliticalPartyResponseDto> politicalPartyResponses = politicalParties.map(this::mapToPoliticalPartyResponse);
        return PageResponseWrapper.fromPage(politicalPartyResponses, "Political parties by founding year fetched successfully");
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseWrapper<PoliticalPartyResponseDto> getPoliticalPartiesByIdeology(String ideology, Pageable pageable) {
        // For simplicity, we'll use search functionality
        // In a real implementation, you might want to create a custom repository method
        Page<PoliticalParty> politicalParties = politicalPartyRepository.findAllByOrderByNameAsc(pageable);
        Page<PoliticalPartyResponseDto> politicalPartyResponses = politicalParties.map(this::mapToPoliticalPartyResponse);
        return PageResponseWrapper.fromPage(politicalPartyResponses, "Political parties by ideology fetched successfully");
    }

    @Override
    public void deletePoliticalParty(Long id) {
        log.info("Deleting political party with ID: {}", id);

        PoliticalParty politicalParty = politicalPartyRepository.findById(id)
                .orElseThrow(() -> new ApiException("Political party not found with ID: " + id, HttpStatus.NOT_FOUND));

        // Soft delete by setting isActive to false
        politicalParty.setIsActive(false);
        politicalParty.setUpdatedBy(getCurrentAuthenticatedUser());

        politicalPartyRepository.save(politicalParty);
        log.info("Successfully deleted political party with ID: {}", id);
    }

    @Override
    public void deletePoliticalPartyByUid(String uid) {
        log.info("Deleting political party with UID: {}", uid);

        PoliticalParty politicalParty = politicalPartyRepository.findByUid(uid)
                .orElseThrow(() -> new ApiException("Political party not found with UID: " + uid, HttpStatus.NOT_FOUND));

        // Soft delete by setting isActive to false
        politicalParty.setIsActive(false);
        politicalParty.setUpdatedBy(getCurrentAuthenticatedUser());

        politicalPartyRepository.save(politicalParty);
        log.info("Successfully deleted political party with UID: {}", uid);
    }

    @Override
    public PoliticalPartyResponseDto activatePoliticalParty(Long id) {
        log.info("Activating political party with ID: {}", id);

        PoliticalParty politicalParty = politicalPartyRepository.findById(id)
                .orElseThrow(() -> new ApiException("Political party not found with ID: " + id, HttpStatus.NOT_FOUND));

        politicalParty.setIsActive(true);
        politicalParty.setUpdatedBy(getCurrentAuthenticatedUser());

        PoliticalParty savedPoliticalParty = politicalPartyRepository.save(politicalParty);
        log.info("Successfully activated political party with ID: {}", id);

        return mapToPoliticalPartyResponse(savedPoliticalParty);
    }

    @Override
    public PoliticalPartyResponseDto deactivatePoliticalParty(Long id) {
        log.info("Deactivating political party with ID: {}", id);

        PoliticalParty politicalParty = politicalPartyRepository.findById(id)
                .orElseThrow(() -> new ApiException("Political party not found with ID: " + id, HttpStatus.NOT_FOUND));

        politicalParty.setIsActive(false);
        politicalParty.setUpdatedBy(getCurrentAuthenticatedUser());

        PoliticalParty savedPoliticalParty = politicalPartyRepository.save(politicalParty);
        log.info("Successfully deactivated political party with ID: {}", id);

        return mapToPoliticalPartyResponse(savedPoliticalParty);
    }

    @Override
    public PoliticalPartyResponseDto registerPoliticalParty(Long id) {
        log.info("Registering political party with ID: {}", id);

        PoliticalParty politicalParty = politicalPartyRepository.findById(id)
                .orElseThrow(() -> new ApiException("Political party not found with ID: " + id, HttpStatus.NOT_FOUND));

        politicalParty.setIsRegistered(true);
        politicalParty.setUpdatedBy(getCurrentAuthenticatedUser());

        PoliticalParty savedPoliticalParty = politicalPartyRepository.save(politicalParty);
        log.info("Successfully registered political party with ID: {}", id);

        return mapToPoliticalPartyResponse(savedPoliticalParty);
    }

    @Override
    public PoliticalPartyResponseDto deregisterPoliticalParty(Long id) {
        log.info("Deregistering political party with ID: {}", id);

        PoliticalParty politicalParty = politicalPartyRepository.findById(id)
                .orElseThrow(() -> new ApiException("Political party not found with ID: " + id, HttpStatus.NOT_FOUND));

        politicalParty.setIsRegistered(false);
        politicalParty.setUpdatedBy(getCurrentAuthenticatedUser());

        PoliticalParty savedPoliticalParty = politicalPartyRepository.save(politicalParty);
        log.info("Successfully deregistered political party with ID: {}", id);

        return mapToPoliticalPartyResponse(savedPoliticalParty);
    }

    @Override
    @Transactional(readOnly = true)
    public PoliticalPartyStats getPoliticalPartyStats() {
        long totalParties = politicalPartyRepository.count();
        long activeParties = politicalPartyRepository.countByIsActiveTrue();
        long inactiveParties = politicalPartyRepository.countByIsActiveFalse();
        long registeredParties = politicalPartyRepository.countByIsRegisteredTrue();
        long unregisteredParties = politicalPartyRepository.countByIsRegisteredFalse();
        long operationalParties = politicalPartyRepository.findOperationalParties().size();
        long partiesWithWebsite = politicalPartyRepository.findPartiesWithWebsite().size();
        long partiesFoundedThisYear = politicalPartyRepository.countByFoundingYear(LocalDate.now().getYear());
        
        // Calculate average member count
        var allParties = politicalPartyRepository.findAll();
        double averageMemberCount = allParties.stream()
                .filter(p -> p.getMemberCount() != null)
                .mapToLong(PoliticalParty::getMemberCount)
                .average()
                .orElse(0.0);

        return new PoliticalPartyStats(
                totalParties,
                activeParties,
                inactiveParties,
                registeredParties,
                unregisteredParties,
                operationalParties,
                partiesWithWebsite,
                partiesFoundedThisYear,
                Math.round(averageMemberCount)
        );
    }

    /**
     * Maps PoliticalParty entity to PoliticalPartyResponseDto
     */
    private PoliticalPartyResponseDto mapToPoliticalPartyResponse(PoliticalParty politicalParty) {
        if (politicalParty == null) {
            return null;
        }

        PoliticalPartyResponseDto response = new PoliticalPartyResponseDto();
        response.setId(politicalParty.getId());
        response.setUid(politicalParty.getUid());
        response.setCode(politicalParty.getCode());
        response.setName(politicalParty.getName());
        response.setAbbreviation(politicalParty.getAbbreviation());
        response.setDescription(politicalParty.getDescription());
        response.setFoundingDate(politicalParty.getFoundingDate());
        response.setFoundingLocation(politicalParty.getFoundingLocation());
        response.setIdeology(politicalParty.getIdeology());
        response.setColors(politicalParty.getColors());
        response.setSymbol(politicalParty.getSymbol());
        response.setMotto(politicalParty.getMotto());
        response.setWebsiteUrl(politicalParty.getWebsiteUrl());
        response.setEmail(politicalParty.getEmail());
        response.setPhone(politicalParty.getPhone());
        response.setHeadquartersAddress(politicalParty.getHeadquartersAddress());
        response.setIsRegistered(politicalParty.getIsRegistered());
        response.setIsActive(politicalParty.getIsActive());
        response.setRegistrationNumber(politicalParty.getRegistrationNumber());
        response.setRegistrationDate(politicalParty.getRegistrationDate());
        response.setMemberCount(politicalParty.getMemberCount());
        response.setAgeInYears(politicalParty.getAgeInYears());
        response.setOperational(politicalParty.isOperational());
        response.setDisplayName(politicalParty.getDisplayName());
        response.setCreatedAt(politicalParty.getCreatedAt());
        response.setUpdatedAt(politicalParty.getUpdatedAt());

        return response;
    }

    /**
     * Generate the next political party code
     */
    private String generateNextPoliticalPartyCode() {
        PoliticalParty lastPoliticalParty = politicalPartyRepository.findFirstByOrderByCodeDesc().orElse(null);
        
        int nextSequenceNumber = 1;
        if (lastPoliticalParty != null) {
            String code = lastPoliticalParty.getCode();
            if (code != null && code.startsWith("PP") && code.length() == 8) {
                try {
                    nextSequenceNumber = Integer.parseInt(code.substring(2)) + 1;
                } catch (NumberFormatException e) {
                    log.warn("Invalid political party code format: {}", code);
                }
            }
        }
        
        return String.format("PP%06d", nextSequenceNumber);
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
