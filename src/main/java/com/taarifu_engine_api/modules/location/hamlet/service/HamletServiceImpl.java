package com.taarifu_engine_api.modules.location.hamlet.service;

import com.taarifu_engine_api.modules.common.domain.enums.AreaType;
import com.taarifu_engine_api.modules.common.exception.ApiException;
import com.taarifu_engine_api.modules.location.area.service.AreaService;
import com.taarifu_engine_api.modules.location.hamlet.domain.dto.CreateHamletRequest;
import com.taarifu_engine_api.modules.location.hamlet.domain.dto.HamletResponse;
import com.taarifu_engine_api.modules.location.hamlet.domain.dto.UpdateHamletRequest;
import com.taarifu_engine_api.modules.location.hamlet.domain.entity.Hamlet;
import com.taarifu_engine_api.modules.location.hamlet.repository.HamletRepository;
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
 * Service implementation for Hamlet operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class HamletServiceImpl implements HamletService {

    private final HamletRepository hamletRepository;
    private final VillageRepository villageRepository;
    private final AreaService areaService;

    @Override
    public HamletResponse createHamlet(CreateHamletRequest request) {
        log.info("Creating new hamlet: {}", request.getName());

        // Check if hamlet with same name already exists
        if (hamletRepository.existsByName(request.getName())) {
            throw new ApiException("Hamlet with name '" + request.getName() + "' already exists", HttpStatus.BAD_REQUEST);
        }

        // Validate village exists
        Village village = villageRepository.findById(request.getVillageId())
                .orElseThrow(() -> new ApiException("Village not found with ID: " + request.getVillageId(), HttpStatus.BAD_REQUEST));

        // Generate next hamlet code
        String nextHamletCode = generateNextHamletCode();

        // Get current authenticated user
        User currentUser = getCurrentAuthenticatedUser();

        // Create new hamlet
        Hamlet hamlet = new Hamlet();
        hamlet.setCode(nextHamletCode);
        hamlet.setName(request.getName());
        hamlet.setHeadquarters(request.getHeadquarters());
        hamlet.setPopulation(request.getPopulation());
        hamlet.setAreaSqKm(request.getAreaSqKm());
        hamlet.setLatitude(request.getLatitude());
        hamlet.setLongitude(request.getLongitude());
        hamlet.setExecutiveOfficer(request.getExecutiveOfficer());
        hamlet.setDescription(request.getDescription());
        hamlet.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        hamlet.setVillage(village);
        hamlet.setCreatedBy(currentUser);
        hamlet.setUpdatedBy(currentUser);

        // Ensure UID is generated
        hamlet.ensureUid();

        Hamlet savedHamlet = hamletRepository.save(hamlet);
        log.info("Successfully created hamlet with ID: {}, UID: {}, and code: {}", 
                savedHamlet.getId(), savedHamlet.getUid(), savedHamlet.getCode());

        // Create corresponding Area entity
        areaService.createArea(AreaType.HAMLET, savedHamlet.getId(), savedHamlet.getName());
        log.info("Successfully created Area for Hamlet with ID: {}", savedHamlet.getId());

        return mapToHamletResponse(savedHamlet);
    }

    @Override
    public HamletResponse updateHamletByUid(String uid, UpdateHamletRequest request) {
        log.info("Updating hamlet with UID: {}", uid);

        Hamlet hamlet = hamletRepository.findByUid(uid)
                .orElseThrow(() -> new ApiException("Hamlet not found with UID: " + uid, HttpStatus.NOT_FOUND));

        return updateHamletInternal(hamlet, request);
    }

    @Override
    public HamletResponse getHamletByUid(String uid) {
        log.debug("Fetching hamlet with UID: {}", uid);

        Hamlet hamlet = hamletRepository.findByUidWithDetails(uid)
                .orElseThrow(() -> new ApiException("Hamlet not found with UID: " + uid, HttpStatus.NOT_FOUND));

        return mapToHamletResponse(hamlet);
    }

    @Override
    public HamletResponse getHamletByCode(String code) {
        log.debug("Fetching hamlet with code: {}", code);

        Hamlet hamlet = hamletRepository.findByCode(code)
                .orElseThrow(() -> new ApiException("Hamlet not found with code: " + code, HttpStatus.NOT_FOUND));

        return mapToHamletResponse(hamlet);
    }

    @Override
    public Page<HamletResponse> getAllHamlets(Pageable pageable) {
        log.debug("Fetching all hamlets with pagination");

        Page<Hamlet> hamlets = hamletRepository.findAll(pageable);
        return hamlets.map(this::mapToHamletResponse);
    }

    @Override
    public Page<HamletResponse> getActiveHamlets(Pageable pageable) {
        log.debug("Fetching active hamlets with pagination");

        Page<Hamlet> hamlets = hamletRepository.findByIsActiveTrueOrderByNameAsc(pageable);
        return hamlets.map(this::mapToHamletResponse);
    }

    @Override
    public Page<HamletResponse> getHamletsByVillageUid(String villageUid, Pageable pageable) {
        log.debug("Fetching hamlets for village UID: {}", villageUid);

        Page<Hamlet> hamlets = hamletRepository.findByVillageUidWithUsersAndVillage(villageUid, pageable);
        return hamlets.map(this::mapToHamletResponse);
    }

    @Override
    public Page<HamletResponse> getActiveHamletsByVillageUid(String villageUid, Pageable pageable) {
        log.debug("Fetching active hamlets for village UID: {}", villageUid);

        Page<Hamlet> hamlets = hamletRepository.findByVillageUidWithUsersAndVillage(villageUid, pageable);
        return hamlets.getContent().stream()
                .filter(Hamlet::isActive)
                .map(this::mapToHamletResponse)
                .collect(Collectors.collectingAndThen(
                        Collectors.toList(),
                        list -> new org.springframework.data.domain.PageImpl<>(list, pageable, list.size())
                ));
    }

    @Override
    public Page<HamletResponse> searchHamlets(String searchTerm, Pageable pageable) {
        log.debug("Searching hamlets with term: {}", searchTerm);

        Page<Hamlet> hamlets = hamletRepository.searchHamlets(searchTerm, pageable);
        return hamlets.map(this::mapToHamletResponse);
    }

    @Override
    public Page<HamletResponse> searchHamletsByVillageUid(String villageUid, String searchTerm, Pageable pageable) {
        log.debug("Searching hamlets for village UID: {} with term: {}", villageUid, searchTerm);

        Village village = villageRepository.findByUid(villageUid)
                .orElseThrow(() -> new ApiException("Village not found with UID: " + villageUid, HttpStatus.NOT_FOUND));

        Page<Hamlet> hamlets = hamletRepository.searchHamletsByVillage(village.getId(), searchTerm, pageable);
        return hamlets.map(this::mapToHamletResponse);
    }

    @Override
    public Page<HamletResponse> getHamletsByStatus(Boolean isActive, Pageable pageable) {
        log.debug("Fetching hamlets by status: {}", isActive);

        Page<Hamlet> hamlets = hamletRepository.findByIsActiveOrderByNameAsc(isActive, pageable);
        return hamlets.map(this::mapToHamletResponse);
    }

    @Override
    public void deleteHamletByUid(String uid) {
        log.info("Soft deleting hamlet with UID: {}", uid);

        Hamlet hamlet = hamletRepository.findByUid(uid)
                .orElseThrow(() -> new ApiException("Hamlet not found with UID: " + uid, HttpStatus.NOT_FOUND));

        hamlet.setIsActive(false);
        hamlet.setUpdatedBy(getCurrentAuthenticatedUser());
        hamletRepository.save(hamlet);

        log.info("Successfully soft deleted hamlet with UID: {}", uid);
    }

    @Override
    public HamletResponse toggleHamletStatusByUid(String uid) {
        log.info("Toggling status for hamlet with UID: {}", uid);

        Hamlet hamlet = hamletRepository.findByUid(uid)
                .orElseThrow(() -> new ApiException("Hamlet not found with UID: " + uid, HttpStatus.NOT_FOUND));

        hamlet.setIsActive(!hamlet.isActive());
        hamlet.setUpdatedBy(getCurrentAuthenticatedUser());
        Hamlet updatedHamlet = hamletRepository.save(hamlet);

        log.info("Successfully toggled hamlet status to: {} for UID: {}", updatedHamlet.isActive(), uid);
        return mapToHamletResponse(updatedHamlet);
    }

    @Override
    public HamletStats getHamletStats() {
        log.debug("Fetching hamlet statistics");

        long totalHamlets = hamletRepository.count();
        long activeHamlets = hamletRepository.countByIsActiveTrue();
        long inactiveHamlets = totalHamlets - activeHamlets;

        return new HamletStats(totalHamlets, activeHamlets, inactiveHamlets);
    }

    @Override
    public HamletStats getHamletStatsByVillageUid(String villageUid) {
        log.debug("Fetching hamlet statistics for village UID: {}", villageUid);

        Village village = villageRepository.findByUid(villageUid)
                .orElseThrow(() -> new ApiException("Village not found with UID: " + villageUid, HttpStatus.NOT_FOUND));

        long totalHamlets = hamletRepository.countByVillageId(village.getId());
        long activeHamlets = hamletRepository.countByVillageIdAndIsActiveTrue(village.getId());
        long inactiveHamlets = totalHamlets - activeHamlets;

        return new HamletStats(totalHamlets, activeHamlets, inactiveHamlets);
    }

    private HamletResponse updateHamletInternal(Hamlet hamlet, UpdateHamletRequest request) {
        // Track if name was changed for area update
        boolean nameChanged = false;
        
        // Check if name is being changed and if new name already exists
        if (request.getName() != null && !request.getName().equals(hamlet.getName())) {
            if (hamletRepository.existsByName(request.getName())) {
                throw new ApiException("Hamlet with name '" + request.getName() + "' already exists", HttpStatus.BAD_REQUEST);
            }
            hamlet.setName(request.getName());
            nameChanged = true;
        }

        // Update fields if provided
        if (request.getHeadquarters() != null) {
            hamlet.setHeadquarters(request.getHeadquarters());
        }
        if (request.getPopulation() != null) {
            hamlet.setPopulation(request.getPopulation());
        }
        if (request.getAreaSqKm() != null) {
            hamlet.setAreaSqKm(request.getAreaSqKm());
        }
        if (request.getLatitude() != null) {
            hamlet.setLatitude(request.getLatitude());
        }
        if (request.getLongitude() != null) {
            hamlet.setLongitude(request.getLongitude());
        }
        if (request.getExecutiveOfficer() != null) {
            hamlet.setExecutiveOfficer(request.getExecutiveOfficer());
        }
        if (request.getDescription() != null) {
            hamlet.setDescription(request.getDescription());
        }
        if (request.getIsActive() != null) {
            hamlet.setIsActive(request.getIsActive());
        }

        hamlet.setUpdatedBy(getCurrentAuthenticatedUser());

        Hamlet updatedHamlet = hamletRepository.save(hamlet);
        log.info("Successfully updated hamlet with UID: {}", updatedHamlet.getUid());

        // Update corresponding Area entity if name was changed
        if (nameChanged) {
            areaService.updateArea(AreaType.HAMLET, updatedHamlet.getId(), updatedHamlet.getName());
            log.info("Successfully updated Area for Hamlet with ID: {}", updatedHamlet.getId());
        }

        return mapToHamletResponse(updatedHamlet);
    }

    private String generateNextHamletCode() {
        Hamlet lastHamlet = hamletRepository.findFirstByOrderByCodeDesc().orElse(null);
        
        int nextSequence = 1;
        if (lastHamlet != null && lastHamlet.getCode() != null) {
            try {
                String lastCode = lastHamlet.getCode();
                if (lastCode.startsWith("HM") && lastCode.length() == 9) {
                    String sequencePart = lastCode.substring(2);
                    nextSequence = Integer.parseInt(sequencePart) + 1;
                }
            } catch (NumberFormatException e) {
                log.warn("Failed to parse last hamlet code: {}, starting from 1", lastHamlet.getCode());
            }
        }

        return String.format("HM%07d", nextSequence);
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

    private HamletResponse mapToHamletResponse(Hamlet hamlet) {
        HamletResponse response = new HamletResponse();
        response.setId(hamlet.getId());
        response.setUid(hamlet.getUid());
        response.setCode(hamlet.getCode());
        response.setName(hamlet.getName());
        response.setHeadquarters(hamlet.getHeadquarters());
        response.setPopulation(hamlet.getPopulation());
        response.setAreaSqKm(hamlet.getAreaSqKm());
        response.setLatitude(hamlet.getLatitude());
        response.setLongitude(hamlet.getLongitude());
        response.setExecutiveOfficer(hamlet.getExecutiveOfficer());
        response.setDescription(hamlet.getDescription());
        response.setIsActive(hamlet.getIsActive());

        // Village information
        if (hamlet.getVillage() != null) {
            response.setVillageId(hamlet.getVillage().getId());
            response.setVillageName(hamlet.getVillage().getName());
            response.setVillageCode(hamlet.getVillage().getCode());

            // Ward information through village
            if (hamlet.getVillage().getWard() != null) {
                response.setWardId(hamlet.getVillage().getWard().getId());
                response.setWardName(hamlet.getVillage().getWard().getName());
                response.setWardCode(hamlet.getVillage().getWard().getCode());

                // District information through ward
                if (hamlet.getVillage().getWard().getDistrict() != null) {
                    response.setDistrictId(hamlet.getVillage().getWard().getDistrict().getId());
                    response.setDistrictName(hamlet.getVillage().getWard().getDistrict().getName());
                    response.setDistrictCode(hamlet.getVillage().getWard().getDistrict().getCode());

                    // Region information through district
                    if (hamlet.getVillage().getWard().getDistrict().getRegion() != null) {
                        response.setRegionId(hamlet.getVillage().getWard().getDistrict().getRegion().getId());
                        response.setRegionName(hamlet.getVillage().getWard().getDistrict().getRegion().getName());
                        response.setRegionCode(hamlet.getVillage().getWard().getDistrict().getRegion().getCode());
                    }
                }
            }
        }

        // Audit fields
        if (hamlet.getCreatedBy() != null) {
            response.setCreatedById(hamlet.getCreatedBy().getId());
            response.setCreatedByUsername(hamlet.getCreatedBy().getUsername());
        }
        if (hamlet.getUpdatedBy() != null) {
            response.setUpdatedById(hamlet.getUpdatedBy().getId());
            response.setUpdatedByUsername(hamlet.getUpdatedBy().getUsername());
        }
        response.setCreatedAt(hamlet.getCreatedAt());
        response.setUpdatedAt(hamlet.getUpdatedAt());

        return response;
    }
}
