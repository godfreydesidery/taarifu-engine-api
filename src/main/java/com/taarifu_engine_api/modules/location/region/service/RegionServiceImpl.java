package com.taarifu_engine_api.modules.location.region.service;

import com.taarifu_engine_api.modules.common.domain.enums.AreaType;
import com.taarifu_engine_api.modules.common.exception.ApiException;
import com.taarifu_engine_api.modules.location.area.service.AreaService;
import com.taarifu_engine_api.modules.userandrole.domain.entity.User;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.taarifu_engine_api.modules.location.region.domain.dto.CreateRegionRequest;
import com.taarifu_engine_api.modules.location.region.domain.dto.RegionResponse;
import com.taarifu_engine_api.modules.location.region.domain.dto.UpdateRegionRequest;
import com.taarifu_engine_api.modules.location.region.domain.entity.Region;
import com.taarifu_engine_api.modules.location.region.repository.RegionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation for Region operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RegionServiceImpl implements RegionService {

    private final RegionRepository regionRepository;
    private final AreaService areaService;

    @Override
    public RegionResponse createRegion(CreateRegionRequest request) {
        log.info("Creating new region: {}", request.getName());

        // Check if region with same name already exists
        if (regionRepository.existsByName(request.getName())) {
            throw new ApiException("Region with name '" + request.getName() + "' already exists", HttpStatus.BAD_REQUEST);
        }

        // Generate next region code
        String nextRegionCode = generateNextRegionCode();

        // Get current authenticated user
        User currentUser = getCurrentAuthenticatedUser();

        // Create new region
        Region region = new Region();
        region.setCode(nextRegionCode);
        region.setName(request.getName());
        region.setCapital(request.getCapital());
        region.setPopulation(request.getPopulation());
        region.setAreaSqKm(request.getAreaSqKm());
        region.setLatitude(request.getLatitude());
        region.setLongitude(request.getLongitude());
        region.setCommissioner(request.getCommissioner());
        region.setDescription(request.getDescription());
        region.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        region.setCreatedBy(currentUser);
        region.setUpdatedBy(currentUser);

        // Ensure UID is generated
        region.ensureUid();

        Region savedRegion = regionRepository.save(region);
        log.info("Successfully created region with ID: {}, UID: {}, and code: {}", 
                savedRegion.getId(), savedRegion.getUid(), savedRegion.getCode());

        // Create corresponding Area entity
        areaService.createArea(AreaType.REGION, savedRegion.getId(), savedRegion.getName());
        log.info("Successfully created Area for Region with ID: {}", savedRegion.getId());

        return mapToRegionResponse(savedRegion);
    }

    @Override
    public RegionResponse updateRegion(Long id, UpdateRegionRequest request) {
        log.info("Updating region with ID: {}", id);

        Region region = regionRepository.findById(id)
                .orElseThrow(() -> new ApiException("Region not found with ID: " + id, HttpStatus.NOT_FOUND));

        return updateRegionInternal(region, request);
    }

    @Override
    public RegionResponse updateRegionByUid(String uid, UpdateRegionRequest request) {
        log.info("Updating region with UID: {}", uid);

        Region region = regionRepository.findByUid(uid)
                .orElseThrow(() -> new ApiException("Region not found with UID: " + uid, HttpStatus.NOT_FOUND));

        return updateRegionInternal(region, request);
    }

    private RegionResponse updateRegionInternal(Region region, UpdateRegionRequest request) {
        // Track if name was changed for area update
        boolean nameChanged = false;
        
        // Check if new name conflicts with existing regions
        if (request.getName() != null && !request.getName().equals(region.getName())) {
            if (regionRepository.existsByName(request.getName())) {
                throw new ApiException("Region with name '" + request.getName() + "' already exists", HttpStatus.BAD_REQUEST);
            }
            region.setName(request.getName());
            nameChanged = true;
        }

        // Update other fields
        if (request.getCapital() != null) {
            region.setCapital(request.getCapital());
        }
        if (request.getPopulation() != null) {
            region.setPopulation(request.getPopulation());
        }
        if (request.getAreaSqKm() != null) {
            region.setAreaSqKm(request.getAreaSqKm());
        }
        if (request.getLatitude() != null) {
            region.setLatitude(request.getLatitude());
        }
        if (request.getLongitude() != null) {
            region.setLongitude(request.getLongitude());
        }
        if (request.getCommissioner() != null) {
            region.setCommissioner(request.getCommissioner());
        }
        if (request.getDescription() != null) {
            region.setDescription(request.getDescription());
        }
        if (request.getIsActive() != null) {
            region.setIsActive(request.getIsActive());
        }

        // Set updated by
        region.setUpdatedBy(getCurrentAuthenticatedUser());

        Region savedRegion = regionRepository.save(region);
        log.info("Successfully updated region with ID: {}", savedRegion.getId());

        // Update corresponding Area entity if name was changed
        if (nameChanged) {
            areaService.updateArea(AreaType.REGION, savedRegion.getId(), savedRegion.getName());
            log.info("Successfully updated Area for Region with ID: {}", savedRegion.getId());
        }

        return mapToRegionResponse(savedRegion);
    }

    @Override
    @Transactional(readOnly = true)
    public RegionResponse getRegionById(Long id) {
        log.debug("Fetching region with ID: {}", id);

        Region region = regionRepository.findById(id)
                .orElseThrow(() -> new ApiException("Region not found with ID: " + id, HttpStatus.NOT_FOUND));

        return mapToRegionResponse(region);
    }

    @Override
    @Transactional(readOnly = true)
    public RegionResponse getRegionByUid(String uid) {
        log.debug("Fetching region with UID: {}", uid);

        Region region = regionRepository.findByUidWithUsers(uid)
                .orElseThrow(() -> new ApiException("Region not found with UID: " + uid, HttpStatus.NOT_FOUND));

        return mapToRegionResponse(region);
    }

    @Override
    @Transactional(readOnly = true)
    public RegionResponse getRegionByCode(String code) {
        log.debug("Fetching region with code: {}", code);

        Region region = regionRepository.findByCode(code)
                .orElseThrow(() -> new ApiException("Region not found with code: " + code, HttpStatus.NOT_FOUND));

        return mapToRegionResponse(region);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RegionResponse> getAllRegions(Pageable pageable) {
        log.debug("Fetching all regions with pagination");

        Page<Region> regions = regionRepository.findAll(pageable);
        return regions.map(this::mapToRegionResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RegionResponse> getActiveRegions() {
        log.debug("Fetching all active regions");

        List<Region> regions = regionRepository.findByIsActiveTrueOrderByNameAsc();
        return regions.stream()
                .map(this::mapToRegionResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RegionResponse> getActiveRegions(Pageable pageable) {
        log.debug("Fetching active regions with pagination");

        Page<Region> regions = regionRepository.findByIsActiveTrueOrderByNameAsc(pageable);
        return regions.map(this::mapToRegionResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RegionResponse> searchRegions(String searchTerm, Pageable pageable) {
        log.debug("Searching regions with term: {}", searchTerm);

        Page<Region> regions = regionRepository.searchRegions(searchTerm, pageable);
        return regions.map(this::mapToRegionResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RegionResponse> getRegionsByStatus(Boolean isActive, Pageable pageable) {
        log.debug("Fetching regions by status: {}", isActive);

        Page<Region> regions = regionRepository.findByIsActiveOrderByNameAsc(isActive, pageable);
        return regions.map(this::mapToRegionResponse);
    }

    @Override
    public void deleteRegion(Long id) {
        log.info("Soft deleting region with ID: {}", id);

        Region region = regionRepository.findById(id)
                .orElseThrow(() -> new ApiException("Region not found with ID: " + id, HttpStatus.NOT_FOUND));

        region.setIsActive(false);
        regionRepository.save(region);

        log.info("Successfully soft deleted region with ID: {}", id);
    }

    @Override
    public void deleteRegionByUid(String uid) {
        log.info("Soft deleting region with UID: {}", uid);

        Region region = regionRepository.findByUid(uid)
                .orElseThrow(() -> new ApiException("Region not found with UID: " + uid, HttpStatus.NOT_FOUND));

        region.setIsActive(false);
        regionRepository.save(region);

        log.info("Successfully soft deleted region with UID: {}", uid);
    }

    @Override
    public RegionResponse toggleRegionStatus(Long id) {
        log.info("Toggling status for region with ID: {}", id);

        Region region = regionRepository.findById(id)
                .orElseThrow(() -> new ApiException("Region not found with ID: " + id, HttpStatus.NOT_FOUND));

        region.setIsActive(!region.getIsActive());
        Region savedRegion = regionRepository.save(region);

        log.info("Successfully toggled status for region with ID: {} to {}", id, savedRegion.getIsActive());

        return mapToRegionResponse(savedRegion);
    }

    @Override
    public RegionResponse toggleRegionStatusByUid(String uid) {
        log.info("Toggling status for region with UID: {}", uid);

        Region region = regionRepository.findByUid(uid)
                .orElseThrow(() -> new ApiException("Region not found with UID: " + uid, HttpStatus.NOT_FOUND));

        region.setIsActive(!region.getIsActive());
        Region savedRegion = regionRepository.save(region);

        log.info("Successfully toggled status for region with UID: {} to {}", uid, savedRegion.getIsActive());

        return mapToRegionResponse(savedRegion);
    }

    @Override
    @Transactional(readOnly = true)
    public RegionStats getRegionStats() {
        log.debug("Fetching region statistics");

        long totalRegions = regionRepository.count();
        long activeRegions = regionRepository.countByIsActiveTrue();
        long inactiveRegions = totalRegions - activeRegions;

        return new RegionStats(totalRegions, activeRegions, inactiveRegions);
    }

    /**
     * Generates the next available region code with RG prefix
     * @return the next region code (e.g., RG0001, RG0002, etc.)
     */
    private String generateNextRegionCode() {
        // Use the maximum ID + 1 as the sequence number
        Long maxId = regionRepository.findMaxId();
        int nextSequence = maxId != null ? maxId.intValue() + 1 : 1;
        
        return String.format("RG%04d", nextSequence);
    }

    /**
     * Gets the current authenticated user from SecurityContext
     * @return the current authenticated user
     */
    private User getCurrentAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new ApiException("No authenticated user found", HttpStatus.UNAUTHORIZED);
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof User) {
            return (User) principal;
        } else {
            throw new ApiException("Invalid authentication principal", HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * Maps Region entity to RegionResponse DTO
     */
    private RegionResponse mapToRegionResponse(Region region) {
        if (region == null) {
            return null;
        }

        RegionResponse response = new RegionResponse();
        response.setId(region.getId());
        response.setUid(region.getUid());
        response.setCode(region.getCode());
        response.setName(region.getName());
        response.setCapital(region.getCapital());
        response.setPopulation(region.getPopulation());
        response.setAreaSqKm(region.getAreaSqKm());
        response.setLatitude(region.getLatitude());
        response.setLongitude(region.getLongitude());
        response.setCommissioner(region.getCommissioner());
        response.setDescription(region.getDescription());
        response.setIsActive(region.getIsActive());
        response.setCreatedById(region.getCreatedBy() != null ? region.getCreatedBy().getId() : null);
        response.setCreatedByUsername(region.getCreatedBy() != null ? region.getCreatedBy().getUsername() : null);
        response.setUpdatedById(region.getUpdatedBy() != null ? region.getUpdatedBy().getId() : null);
        response.setUpdatedByUsername(region.getUpdatedBy() != null ? region.getUpdatedBy().getUsername() : null);
        response.setCreatedAt(region.getCreatedAt());
        response.setUpdatedAt(region.getUpdatedAt());
        
        return response;
    }
}
