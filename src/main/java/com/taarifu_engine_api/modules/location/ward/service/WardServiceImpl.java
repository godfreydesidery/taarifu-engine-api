package com.taarifu_engine_api.modules.location.ward.service;

import com.taarifu_engine_api.modules.common.domain.enums.AreaType;
import com.taarifu_engine_api.modules.common.exception.ApiException;
import com.taarifu_engine_api.modules.location.area.service.AreaService;
import com.taarifu_engine_api.modules.location.district.domain.entity.District;
import com.taarifu_engine_api.modules.location.district.repository.DistrictRepository;
import com.taarifu_engine_api.modules.location.ward.domain.dto.CreateWardRequest;
import com.taarifu_engine_api.modules.location.ward.domain.dto.WardResponse;
import com.taarifu_engine_api.modules.location.ward.domain.dto.UpdateWardRequest;
import com.taarifu_engine_api.modules.location.ward.domain.entity.Ward;
import com.taarifu_engine_api.modules.location.ward.repository.WardRepository;
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
 * Service implementation for Ward operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class WardServiceImpl implements WardService {

    private final WardRepository wardRepository;
    private final DistrictRepository districtRepository;
    private final AreaService areaService;

    @Override
    public WardResponse createWard(CreateWardRequest request) {
        log.info("Creating new ward: {}", request.getName());

        // Check if ward with same name already exists
        if (wardRepository.existsByName(request.getName())) {
            throw new ApiException("Ward with name '" + request.getName() + "' already exists", HttpStatus.BAD_REQUEST);
        }

        // Validate district exists
        District district = districtRepository.findById(request.getDistrictId())
                .orElseThrow(() -> new ApiException("District not found with ID: " + request.getDistrictId(), HttpStatus.BAD_REQUEST));

        // Generate next ward code
        String nextWardCode = generateNextWardCode();

        // Get current authenticated user
        User currentUser = getCurrentAuthenticatedUser();

        // Create new ward
        Ward ward = new Ward();
        ward.setCode(nextWardCode);
        ward.setName(request.getName());
        ward.setHeadquarters(request.getHeadquarters());
        ward.setPopulation(request.getPopulation());
        ward.setAreaSqKm(request.getAreaSqKm());
        ward.setLatitude(request.getLatitude());
        ward.setLongitude(request.getLongitude());
        ward.setExecutiveOfficer(request.getExecutiveOfficer());
        ward.setDescription(request.getDescription());
        ward.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        ward.setDistrict(district);
        ward.setCreatedBy(currentUser);
        ward.setUpdatedBy(currentUser);

        // Ensure UID is generated
        ward.ensureUid();

        Ward savedWard = wardRepository.save(ward);
        log.info("Successfully created ward with ID: {}, UID: {}, and code: {}", 
                savedWard.getId(), savedWard.getUid(), savedWard.getCode());

        // Create corresponding Area entity
        areaService.createArea(AreaType.WARD, savedWard.getId(), savedWard.getName());
        log.info("Successfully created Area for Ward with ID: {}", savedWard.getId());

        return mapToWardResponse(savedWard);
    }

    @Override
    public WardResponse updateWardByUid(String uid, UpdateWardRequest request) {
        log.info("Updating ward with UID: {}", uid);

        Ward ward = wardRepository.findByUid(uid)
                .orElseThrow(() -> new ApiException("Ward not found with UID: " + uid, HttpStatus.NOT_FOUND));

        return updateWardInternal(ward, request);
    }

    @Override
    public WardResponse getWardByUid(String uid) {
        log.debug("Fetching ward with UID: {}", uid);

        Ward ward = wardRepository.findByUidWithDetails(uid)
                .orElseThrow(() -> new ApiException("Ward not found with UID: " + uid, HttpStatus.NOT_FOUND));

        return mapToWardResponse(ward);
    }

    @Override
    public WardResponse getWardByCode(String code) {
        log.debug("Fetching ward with code: {}", code);

        Ward ward = wardRepository.findByCode(code)
                .orElseThrow(() -> new ApiException("Ward not found with code: " + code, HttpStatus.NOT_FOUND));

        return mapToWardResponse(ward);
    }

    @Override
    public Page<WardResponse> getAllWards(Pageable pageable) {
        log.debug("Fetching all wards with pagination");

        Page<Ward> wards = wardRepository.findAll(pageable);
        return wards.map(this::mapToWardResponse);
    }

    @Override
    public Page<WardResponse> getActiveWards(Pageable pageable) {
        log.debug("Fetching active wards with pagination");

        Page<Ward> wards = wardRepository.findByIsActiveTrueOrderByNameAsc(pageable);
        return wards.map(this::mapToWardResponse);
    }

    @Override
    public Page<WardResponse> getWardsByDistrictUid(String districtUid, Pageable pageable) {
        log.debug("Fetching wards for district UID: {}", districtUid);

        Page<Ward> wards = wardRepository.findByDistrictUidWithUsersAndDistrict(districtUid, pageable);
        return wards.map(this::mapToWardResponse);
    }

    @Override
    public Page<WardResponse> getActiveWardsByDistrictUid(String districtUid, Pageable pageable) {
        log.debug("Fetching active wards for district UID: {}", districtUid);

        Page<Ward> wards = wardRepository.findByDistrictUidWithUsersAndDistrict(districtUid, pageable);
        return wards.getContent().stream()
                .filter(Ward::isActive)
                .map(this::mapToWardResponse)
                .collect(Collectors.collectingAndThen(
                        Collectors.toList(),
                        list -> new org.springframework.data.domain.PageImpl<>(list, pageable, list.size())
                ));
    }

    @Override
    public Page<WardResponse> searchWards(String searchTerm, Pageable pageable) {
        log.debug("Searching wards with term: {}", searchTerm);

        Page<Ward> wards = wardRepository.searchWards(searchTerm, pageable);
        return wards.map(this::mapToWardResponse);
    }

    @Override
    public Page<WardResponse> searchWardsByDistrictUid(String districtUid, String searchTerm, Pageable pageable) {
        log.debug("Searching wards for district UID: {} with term: {}", districtUid, searchTerm);

        District district = districtRepository.findByUid(districtUid)
                .orElseThrow(() -> new ApiException("District not found with UID: " + districtUid, HttpStatus.NOT_FOUND));

        Page<Ward> wards = wardRepository.searchWardsByDistrict(district.getId(), searchTerm, pageable);
        return wards.map(this::mapToWardResponse);
    }

    @Override
    public Page<WardResponse> getWardsByStatus(Boolean isActive, Pageable pageable) {
        log.debug("Fetching wards by status: {}", isActive);

        Page<Ward> wards = wardRepository.findByIsActiveOrderByNameAsc(isActive, pageable);
        return wards.map(this::mapToWardResponse);
    }

    @Override
    public void deleteWardByUid(String uid) {
        log.info("Soft deleting ward with UID: {}", uid);

        Ward ward = wardRepository.findByUid(uid)
                .orElseThrow(() -> new ApiException("Ward not found with UID: " + uid, HttpStatus.NOT_FOUND));

        ward.setIsActive(false);
        ward.setUpdatedBy(getCurrentAuthenticatedUser());
        wardRepository.save(ward);

        log.info("Successfully soft deleted ward with UID: {}", uid);
    }

    @Override
    public WardResponse toggleWardStatusByUid(String uid) {
        log.info("Toggling status for ward with UID: {}", uid);

        Ward ward = wardRepository.findByUid(uid)
                .orElseThrow(() -> new ApiException("Ward not found with UID: " + uid, HttpStatus.NOT_FOUND));

        ward.setIsActive(!ward.isActive());
        ward.setUpdatedBy(getCurrentAuthenticatedUser());
        Ward updatedWard = wardRepository.save(ward);

        log.info("Successfully toggled ward status to: {} for UID: {}", updatedWard.isActive(), uid);
        return mapToWardResponse(updatedWard);
    }

    @Override
    public WardStats getWardStats() {
        log.debug("Fetching ward statistics");

        long totalWards = wardRepository.count();
        long activeWards = wardRepository.countByIsActiveTrue();
        long inactiveWards = totalWards - activeWards;

        return new WardStats(totalWards, activeWards, inactiveWards);
    }

    @Override
    public WardStats getWardStatsByDistrictUid(String districtUid) {
        log.debug("Fetching ward statistics for district UID: {}", districtUid);

        District district = districtRepository.findByUid(districtUid)
                .orElseThrow(() -> new ApiException("District not found with UID: " + districtUid, HttpStatus.NOT_FOUND));

        long totalWards = wardRepository.countByDistrictId(district.getId());
        long activeWards = wardRepository.countByDistrictIdAndIsActiveTrue(district.getId());
        long inactiveWards = totalWards - activeWards;

        return new WardStats(totalWards, activeWards, inactiveWards);
    }

    private WardResponse updateWardInternal(Ward ward, UpdateWardRequest request) {
        // Track if name was changed for area update
        boolean nameChanged = false;
        
        // Check if name is being changed and if new name already exists
        if (request.getName() != null && !request.getName().equals(ward.getName())) {
            if (wardRepository.existsByName(request.getName())) {
                throw new ApiException("Ward with name '" + request.getName() + "' already exists", HttpStatus.BAD_REQUEST);
            }
            ward.setName(request.getName());
            nameChanged = true;
        }

        // Update fields if provided
        if (request.getHeadquarters() != null) {
            ward.setHeadquarters(request.getHeadquarters());
        }
        if (request.getPopulation() != null) {
            ward.setPopulation(request.getPopulation());
        }
        if (request.getAreaSqKm() != null) {
            ward.setAreaSqKm(request.getAreaSqKm());
        }
        if (request.getLatitude() != null) {
            ward.setLatitude(request.getLatitude());
        }
        if (request.getLongitude() != null) {
            ward.setLongitude(request.getLongitude());
        }
        if (request.getExecutiveOfficer() != null) {
            ward.setExecutiveOfficer(request.getExecutiveOfficer());
        }
        if (request.getDescription() != null) {
            ward.setDescription(request.getDescription());
        }
        if (request.getIsActive() != null) {
            ward.setIsActive(request.getIsActive());
        }

        ward.setUpdatedBy(getCurrentAuthenticatedUser());

        Ward updatedWard = wardRepository.save(ward);
        log.info("Successfully updated ward with UID: {}", updatedWard.getUid());

        // Update corresponding Area entity if name was changed
        if (nameChanged) {
            areaService.updateArea(AreaType.WARD, updatedWard.getId(), updatedWard.getName());
            log.info("Successfully updated Area for Ward with ID: {}", updatedWard.getId());
        }

        return mapToWardResponse(updatedWard);
    }

    private String generateNextWardCode() {
        Ward lastWard = wardRepository.findFirstByOrderByCodeDesc().orElse(null);
        
        int nextSequence = 1;
        if (lastWard != null && lastWard.getCode() != null) {
            try {
                String lastCode = lastWard.getCode();
                if (lastCode.startsWith("WD") && lastCode.length() == 8) {
                    String sequencePart = lastCode.substring(2);
                    nextSequence = Integer.parseInt(sequencePart) + 1;
                }
            } catch (NumberFormatException e) {
                log.warn("Failed to parse last ward code: {}, starting from 1", lastWard.getCode());
            }
        }

        return String.format("WD%06d", nextSequence);
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

    private WardResponse mapToWardResponse(Ward ward) {
        WardResponse response = new WardResponse();
        response.setId(ward.getId());
        response.setUid(ward.getUid());
        response.setCode(ward.getCode());
        response.setName(ward.getName());
        response.setHeadquarters(ward.getHeadquarters());
        response.setPopulation(ward.getPopulation());
        response.setAreaSqKm(ward.getAreaSqKm());
        response.setLatitude(ward.getLatitude());
        response.setLongitude(ward.getLongitude());
        response.setExecutiveOfficer(ward.getExecutiveOfficer());
        response.setDescription(ward.getDescription());
        response.setIsActive(ward.getIsActive());

        // District information
        if (ward.getDistrict() != null) {
            response.setDistrictId(ward.getDistrict().getId());
            response.setDistrictName(ward.getDistrict().getName());
            response.setDistrictCode(ward.getDistrict().getCode());

            // Region information through district
            if (ward.getDistrict().getRegion() != null) {
                response.setRegionId(ward.getDistrict().getRegion().getId());
                response.setRegionName(ward.getDistrict().getRegion().getName());
                response.setRegionCode(ward.getDistrict().getRegion().getCode());
            }
        }

        // Audit fields
        if (ward.getCreatedBy() != null) {
            response.setCreatedById(ward.getCreatedBy().getId());
            response.setCreatedByUsername(ward.getCreatedBy().getUsername());
        }
        if (ward.getUpdatedBy() != null) {
            response.setUpdatedById(ward.getUpdatedBy().getId());
            response.setUpdatedByUsername(ward.getUpdatedBy().getUsername());
        }
        response.setCreatedAt(ward.getCreatedAt());
        response.setUpdatedAt(ward.getUpdatedAt());

        return response;
    }
}
