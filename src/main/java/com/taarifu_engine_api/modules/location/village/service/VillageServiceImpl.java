package com.taarifu_engine_api.modules.location.village.service;

import com.taarifu_engine_api.modules.common.domain.enums.AreaType;
import com.taarifu_engine_api.modules.common.exception.ApiException;
import com.taarifu_engine_api.modules.location.area.service.AreaService;
import com.taarifu_engine_api.modules.location.ward.domain.entity.Ward;
import com.taarifu_engine_api.modules.location.ward.repository.WardRepository;
import com.taarifu_engine_api.modules.location.village.domain.dto.CreateVillageRequest;
import com.taarifu_engine_api.modules.location.village.domain.dto.VillageResponse;
import com.taarifu_engine_api.modules.location.village.domain.dto.UpdateVillageRequest;
import com.taarifu_engine_api.modules.location.village.domain.entity.Village;
import com.taarifu_engine_api.modules.location.village.repository.VillageRepository;
import com.taarifu_engine_api.modules.userandrole.domain.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

/**
 * Service implementation for Village operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class VillageServiceImpl implements VillageService {

    private final VillageRepository villageRepository;
    private final WardRepository wardRepository;
    private final AreaService areaService;

    @Override
    public VillageResponse createVillage(CreateVillageRequest request) {
        log.info("Creating new village: {}", request.getName());

        // Check if village with same name already exists
        if (villageRepository.existsByName(request.getName())) {
            throw new ApiException("Village with name '" + request.getName() + "' already exists", HttpStatus.BAD_REQUEST);
        }

        // Validate ward exists
        Ward ward = wardRepository.findById(request.getWardId())
                .orElseThrow(() -> new ApiException("Ward not found with ID: " + request.getWardId(), HttpStatus.BAD_REQUEST));

        // Generate next village code
        String nextVillageCode = generateNextVillageCode();

        // Get current authenticated user
        User currentUser = getCurrentAuthenticatedUser();

        // Create new village
        Village village = new Village();
        village.setCode(nextVillageCode);
        village.setName(request.getName());
        village.setHeadquarters(request.getHeadquarters());
        village.setPopulation(request.getPopulation());
        village.setAreaSqKm(request.getAreaSqKm());
        village.setLatitude(request.getLatitude());
        village.setLongitude(request.getLongitude());
        village.setExecutiveOfficer(request.getExecutiveOfficer());
        village.setDescription(request.getDescription());
        village.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        village.setWard(ward);
        village.setCreatedBy(currentUser);
        village.setUpdatedBy(currentUser);

        // Ensure UID is generated
        village.ensureUid();

        Village savedVillage = villageRepository.save(village);
        log.info("Successfully created village with ID: {}, UID: {}, and code: {}", 
                savedVillage.getId(), savedVillage.getUid(), savedVillage.getCode());

        // Create corresponding Area entity
        areaService.createArea(AreaType.VILLAGE, savedVillage.getId(), savedVillage.getName());
        log.info("Successfully created Area for Village with ID: {}", savedVillage.getId());

        return mapToVillageResponse(savedVillage);
    }

    @Override
    public VillageResponse updateVillageByUid(String uid, UpdateVillageRequest request) {
        log.info("Updating village with UID: {}", uid);

        Village village = villageRepository.findByUid(uid)
                .orElseThrow(() -> new ApiException("Village not found with UID: " + uid, HttpStatus.NOT_FOUND));

        return updateVillageInternal(village, request);
    }

    @Override
    public VillageResponse getVillageByUid(String uid) {
        log.debug("Fetching village with UID: {}", uid);

        Village village = villageRepository.findByUidWithDetails(uid)
                .orElseThrow(() -> new ApiException("Village not found with UID: " + uid, HttpStatus.NOT_FOUND));

        return mapToVillageResponse(village);
    }

    @Override
    public VillageResponse getVillageByCode(String code) {
        log.debug("Fetching village with code: {}", code);

        Village village = villageRepository.findByCode(code)
                .orElseThrow(() -> new ApiException("Village not found with code: " + code, HttpStatus.NOT_FOUND));

        return mapToVillageResponse(village);
    }

    @Override
    public Page<VillageResponse> getAllVillages(Pageable pageable) {
        log.debug("Fetching all villages with pagination");

        Page<Village> villages = villageRepository.findAll(pageable);
        return villages.map(this::mapToVillageResponse);
    }

    @Override
    public Page<VillageResponse> getActiveVillages(Pageable pageable) {
        log.debug("Fetching active villages with pagination");

        Page<Village> villages = villageRepository.findByIsActiveTrueOrderByNameAsc(pageable);
        return villages.map(this::mapToVillageResponse);
    }

    @Override
    public Page<VillageResponse> getVillagesByWardUid(String wardUid, Pageable pageable) {
        log.debug("Fetching villages for ward UID: {}", wardUid);

        Page<Village> villages = villageRepository.findByWardUidWithUsersAndWard(wardUid, pageable);
        return villages.map(this::mapToVillageResponse);
    }

    @Override
    public Page<VillageResponse> getActiveVillagesByWardUid(String wardUid, Pageable pageable) {
        log.debug("Fetching active villages for ward UID: {}", wardUid);

        Page<Village> villages = villageRepository.findByWardUidWithUsersAndWard(wardUid, pageable);
        return villages.getContent().stream()
                .filter(Village::isActive)
                .map(this::mapToVillageResponse)
                .collect(Collectors.collectingAndThen(
                        Collectors.toList(),
                        list -> new org.springframework.data.domain.PageImpl<>(list, pageable, list.size())
                ));
    }

    @Override
    public Page<VillageResponse> searchVillages(String searchTerm, Pageable pageable) {
        log.debug("Searching villages with term: {}", searchTerm);

        Page<Village> villages = villageRepository.searchVillages(searchTerm, pageable);
        return villages.map(this::mapToVillageResponse);
    }

    @Override
    public Page<VillageResponse> searchVillagesByWardUid(String wardUid, String searchTerm, Pageable pageable) {
        log.debug("Searching villages for ward UID: {} with term: {}", wardUid, searchTerm);

        Ward ward = wardRepository.findByUid(wardUid)
                .orElseThrow(() -> new ApiException("Ward not found with UID: " + wardUid, HttpStatus.NOT_FOUND));

        Page<Village> villages = villageRepository.searchVillagesByWard(ward.getId(), searchTerm, pageable);
        return villages.map(this::mapToVillageResponse);
    }

    @Override
    public Page<VillageResponse> getVillagesByStatus(Boolean isActive, Pageable pageable) {
        log.debug("Fetching villages by status: {}", isActive);

        Page<Village> villages = villageRepository.findByIsActiveOrderByNameAsc(isActive, pageable);
        return villages.map(this::mapToVillageResponse);
    }

    @Override
    public void deleteVillageByUid(String uid) {
        log.info("Soft deleting village with UID: {}", uid);

        Village village = villageRepository.findByUid(uid)
                .orElseThrow(() -> new ApiException("Village not found with UID: " + uid, HttpStatus.NOT_FOUND));

        village.setIsActive(false);
        village.setUpdatedBy(getCurrentAuthenticatedUser());
        villageRepository.save(village);

        log.info("Successfully soft deleted village with UID: {}", uid);
    }

    @Override
    public VillageResponse toggleVillageStatusByUid(String uid) {
        log.info("Toggling status for village with UID: {}", uid);

        Village village = villageRepository.findByUid(uid)
                .orElseThrow(() -> new ApiException("Village not found with UID: " + uid, HttpStatus.NOT_FOUND));

        village.setIsActive(!village.isActive());
        village.setUpdatedBy(getCurrentAuthenticatedUser());
        Village updatedVillage = villageRepository.save(village);

        log.info("Successfully toggled village status to: {} for UID: {}", updatedVillage.isActive(), uid);
        return mapToVillageResponse(updatedVillage);
    }

    @Override
    public VillageStats getVillageStats() {
        log.debug("Fetching village statistics");

        long totalVillages = villageRepository.count();
        long activeVillages = villageRepository.countByIsActiveTrue();
        long inactiveVillages = totalVillages - activeVillages;

        return new VillageStats(totalVillages, activeVillages, inactiveVillages);
    }

    @Override
    public VillageStats getVillageStatsByWardUid(String wardUid) {
        log.debug("Fetching village statistics for ward UID: {}", wardUid);

        Ward ward = wardRepository.findByUid(wardUid)
                .orElseThrow(() -> new ApiException("Ward not found with UID: " + wardUid, HttpStatus.NOT_FOUND));

        long totalVillages = villageRepository.countByWardId(ward.getId());
        long activeVillages = villageRepository.countByWardIdAndIsActiveTrue(ward.getId());
        long inactiveVillages = totalVillages - activeVillages;

        return new VillageStats(totalVillages, activeVillages, inactiveVillages);
    }

    private VillageResponse updateVillageInternal(Village village, UpdateVillageRequest request) {
        // Track if name was changed for area update
        boolean nameChanged = false;
        
        // Check if name is being changed and if new name already exists
        if (request.getName() != null && !request.getName().equals(village.getName())) {
            if (villageRepository.existsByName(request.getName())) {
                throw new ApiException("Village with name '" + request.getName() + "' already exists", HttpStatus.BAD_REQUEST);
            }
            village.setName(request.getName());
            nameChanged = true;
        }

        // Update fields if provided
        if (request.getHeadquarters() != null) {
            village.setHeadquarters(request.getHeadquarters());
        }
        if (request.getPopulation() != null) {
            village.setPopulation(request.getPopulation());
        }
        if (request.getAreaSqKm() != null) {
            village.setAreaSqKm(request.getAreaSqKm());
        }
        if (request.getLatitude() != null) {
            village.setLatitude(request.getLatitude());
        }
        if (request.getLongitude() != null) {
            village.setLongitude(request.getLongitude());
        }
        if (request.getExecutiveOfficer() != null) {
            village.setExecutiveOfficer(request.getExecutiveOfficer());
        }
        if (request.getDescription() != null) {
            village.setDescription(request.getDescription());
        }
        if (request.getIsActive() != null) {
            village.setIsActive(request.getIsActive());
        }

        village.setUpdatedBy(getCurrentAuthenticatedUser());

        Village updatedVillage = villageRepository.save(village);
        log.info("Successfully updated village with UID: {}", updatedVillage.getUid());

        // Update corresponding Area entity if name was changed
        if (nameChanged) {
            areaService.updateArea(AreaType.VILLAGE, updatedVillage.getId(), updatedVillage.getName());
            log.info("Successfully updated Area for Village with ID: {}", updatedVillage.getId());
        }

        return mapToVillageResponse(updatedVillage);
    }

    private String generateNextVillageCode() {
        Village lastVillage = villageRepository.findFirstByOrderByCodeDesc().orElse(null);
        
        int nextSequence = 1;
        if (lastVillage != null && lastVillage.getCode() != null) {
            try {
                String lastCode = lastVillage.getCode();
                if (lastCode.startsWith("VL") && lastCode.length() == 9) {
                    String sequencePart = lastCode.substring(2);
                    nextSequence = Integer.parseInt(sequencePart) + 1;
                }
            } catch (NumberFormatException e) {
                log.warn("Failed to parse last village code: {}, starting from 1", lastVillage.getCode());
            }
        }

        return String.format("VL%07d", nextSequence);
    }

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

    private VillageResponse mapToVillageResponse(Village village) {
        VillageResponse response = new VillageResponse();
        response.setId(village.getId());
        response.setUid(village.getUid());
        response.setCode(village.getCode());
        response.setName(village.getName());
        response.setHeadquarters(village.getHeadquarters());
        response.setPopulation(village.getPopulation());
        response.setAreaSqKm(village.getAreaSqKm());
        response.setLatitude(village.getLatitude());
        response.setLongitude(village.getLongitude());
        response.setExecutiveOfficer(village.getExecutiveOfficer());
        response.setDescription(village.getDescription());
        response.setIsActive(village.getIsActive());

        // Ward information
        if (village.getWard() != null) {
            response.setWardId(village.getWard().getId());
            response.setWardName(village.getWard().getName());
            response.setWardCode(village.getWard().getCode());

            // District information through ward
            if (village.getWard().getDistrict() != null) {
                response.setDistrictId(village.getWard().getDistrict().getId());
                response.setDistrictName(village.getWard().getDistrict().getName());
                response.setDistrictCode(village.getWard().getDistrict().getCode());

                // Region information through district
                if (village.getWard().getDistrict().getRegion() != null) {
                    response.setRegionId(village.getWard().getDistrict().getRegion().getId());
                    response.setRegionName(village.getWard().getDistrict().getRegion().getName());
                    response.setRegionCode(village.getWard().getDistrict().getRegion().getCode());
                }
            }
        }

        // Audit fields
        if (village.getCreatedBy() != null) {
            response.setCreatedById(village.getCreatedBy().getId());
            response.setCreatedByUsername(village.getCreatedBy().getUsername());
        }
        if (village.getUpdatedBy() != null) {
            response.setUpdatedById(village.getUpdatedBy().getId());
            response.setUpdatedByUsername(village.getUpdatedBy().getUsername());
        }
        response.setCreatedAt(village.getCreatedAt());
        response.setUpdatedAt(village.getUpdatedAt());

        return response;
    }
}
