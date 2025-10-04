package com.taarifu_engine_api.modules.location.district.service;

import com.taarifu_engine_api.modules.common.domain.enums.AreaType;
import com.taarifu_engine_api.modules.common.exception.ApiException;
import com.taarifu_engine_api.modules.location.area.service.AreaService;
import com.taarifu_engine_api.modules.location.district.domain.dto.CreateDistrictRequest;
import com.taarifu_engine_api.modules.location.district.domain.dto.DistrictResponse;
import com.taarifu_engine_api.modules.location.district.domain.dto.UpdateDistrictRequest;
import com.taarifu_engine_api.modules.location.district.domain.entity.District;
import com.taarifu_engine_api.modules.location.district.repository.DistrictRepository;
import com.taarifu_engine_api.modules.location.region.domain.entity.Region;
import com.taarifu_engine_api.modules.location.region.repository.RegionRepository;
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

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation for District operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DistrictServiceImpl implements DistrictService {

    private final DistrictRepository districtRepository;
    private final RegionRepository regionRepository;
    private final AreaService areaService;

    @Override
    public DistrictResponse createDistrict(CreateDistrictRequest request) {
        log.info("Creating new district: {}", request.getName());

        // Check if district with same name already exists
        if (districtRepository.existsByName(request.getName())) {
            throw new ApiException("District with name '" + request.getName() + "' already exists", HttpStatus.BAD_REQUEST);
        }

        // Validate region exists
        Region region = regionRepository.findById(request.getRegionId())
                .orElseThrow(() -> new ApiException("Region not found with ID: " + request.getRegionId(), HttpStatus.BAD_REQUEST));

        // Generate next district code
        String nextDistrictCode = generateNextDistrictCode();

        // Get current authenticated user
        User currentUser = getCurrentAuthenticatedUser();

        // Create new district
        District district = new District();
        district.setCode(nextDistrictCode);
        district.setName(request.getName());
        district.setHeadquarters(request.getHeadquarters());
        district.setPopulation(request.getPopulation());
        district.setAreaSqKm(request.getAreaSqKm());
        district.setLatitude(request.getLatitude());
        district.setLongitude(request.getLongitude());
        district.setCommissioner(request.getCommissioner());
        district.setDescription(request.getDescription());
        district.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        district.setRegion(region);
        district.setCreatedBy(currentUser);
        district.setUpdatedBy(currentUser);

        // Ensure UID is generated
        district.ensureUid();

        District savedDistrict = districtRepository.save(district);
        log.info("Successfully created district with ID: {}, UID: {}, and code: {}", 
                savedDistrict.getId(), savedDistrict.getUid(), savedDistrict.getCode());

        // Create corresponding Area entity
        areaService.createArea(AreaType.DISTRICT, savedDistrict.getId(), savedDistrict.getName());
        log.info("Successfully created Area for District with ID: {}", savedDistrict.getId());

        return mapToDistrictResponse(savedDistrict);
    }

    @Override
    public DistrictResponse updateDistrict(Long id, UpdateDistrictRequest request) {
        log.info("Updating district with ID: {}", id);

        District district = districtRepository.findById(id)
                .orElseThrow(() -> new ApiException("District not found with ID: " + id, HttpStatus.NOT_FOUND));

        return updateDistrictInternal(district, request);
    }

    @Override
    public DistrictResponse updateDistrictByUid(String uid, UpdateDistrictRequest request) {
        log.info("Updating district with UID: {}", uid);

        District district = districtRepository.findByUid(uid)
                .orElseThrow(() -> new ApiException("District not found with UID: " + uid, HttpStatus.NOT_FOUND));

        return updateDistrictInternal(district, request);
    }

    private DistrictResponse updateDistrictInternal(District district, UpdateDistrictRequest request) {
        // Track if name was changed for area update
        boolean nameChanged = false;
        
        // Check if new name conflicts with existing districts
        if (request.getName() != null && !request.getName().equals(district.getName())) {
            if (districtRepository.existsByName(request.getName())) {
                throw new ApiException("District with name '" + request.getName() + "' already exists", HttpStatus.BAD_REQUEST);
            }
            district.setName(request.getName());
            nameChanged = true;
        }

        // Update other fields
        if (request.getHeadquarters() != null) {
            district.setHeadquarters(request.getHeadquarters());
        }
        if (request.getPopulation() != null) {
            district.setPopulation(request.getPopulation());
        }
        if (request.getAreaSqKm() != null) {
            district.setAreaSqKm(request.getAreaSqKm());
        }
        if (request.getLatitude() != null) {
            district.setLatitude(request.getLatitude());
        }
        if (request.getLongitude() != null) {
            district.setLongitude(request.getLongitude());
        }
        if (request.getCommissioner() != null) {
            district.setCommissioner(request.getCommissioner());
        }
        if (request.getDescription() != null) {
            district.setDescription(request.getDescription());
        }
        if (request.getIsActive() != null) {
            district.setIsActive(request.getIsActive());
        }

        // Set updated by
        district.setUpdatedBy(getCurrentAuthenticatedUser());

        District savedDistrict = districtRepository.save(district);
        log.info("Successfully updated district with ID: {}", savedDistrict.getId());

        // Update corresponding Area entity if name was changed
        if (nameChanged) {
            areaService.updateArea(AreaType.DISTRICT, savedDistrict.getId(), savedDistrict.getName());
            log.info("Successfully updated Area for District with ID: {}", savedDistrict.getId());
        }

        return mapToDistrictResponse(savedDistrict);
    }

    @Override
    @Transactional(readOnly = true)
    public DistrictResponse getDistrictById(Long id) {
        log.debug("Fetching district with ID: {}", id);

        District district = districtRepository.findById(id)
                .orElseThrow(() -> new ApiException("District not found with ID: " + id, HttpStatus.NOT_FOUND));

        return mapToDistrictResponse(district);
    }

    @Override
    @Transactional(readOnly = true)
    public DistrictResponse getDistrictByUid(String uid) {
        log.debug("Fetching district with UID: {}", uid);

        District district = districtRepository.findByUidWithDetails(uid)
                .orElseThrow(() -> new ApiException("District not found with UID: " + uid, HttpStatus.NOT_FOUND));

        return mapToDistrictResponse(district);
    }

    @Override
    @Transactional(readOnly = true)
    public DistrictResponse getDistrictByCode(String code) {
        log.debug("Fetching district with code: {}", code);

        District district = districtRepository.findByCode(code)
                .orElseThrow(() -> new ApiException("District not found with code: " + code, HttpStatus.NOT_FOUND));

        return mapToDistrictResponse(district);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DistrictResponse> getAllDistricts(Pageable pageable) {
        log.debug("Fetching all districts with pagination");

        Page<District> districts = districtRepository.findAll(pageable);
        return districts.map(this::mapToDistrictResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DistrictResponse> getActiveDistricts() {
        log.debug("Fetching all active districts");

        List<District> districts = districtRepository.findByIsActiveTrueOrderByNameAsc();
        return districts.stream()
                .map(this::mapToDistrictResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DistrictResponse> getActiveDistricts(Pageable pageable) {
        log.debug("Fetching active districts with pagination");

        Page<District> districts = districtRepository.findByIsActiveTrueOrderByNameAsc(pageable);
        return districts.map(this::mapToDistrictResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DistrictResponse> getDistrictsByRegionId(Long regionId) {
        log.debug("Fetching districts for region ID: {}", regionId);

        List<District> districts = districtRepository.findByRegionIdOrderByNameAsc(regionId);
        return districts.stream()
                .map(this::mapToDistrictResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DistrictResponse> getDistrictsByRegionId(Long regionId, Pageable pageable) {
        log.debug("Fetching districts for region ID: {} with pagination", regionId);

        Page<District> districts = districtRepository.findByRegionIdOrderByNameAsc(regionId, pageable);
        return districts.map(this::mapToDistrictResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DistrictResponse> getActiveDistrictsByRegionId(Long regionId) {
        log.debug("Fetching active districts for region ID: {}", regionId);

        List<District> districts = districtRepository.findByRegionIdAndIsActiveTrueOrderByNameAsc(regionId);
        return districts.stream()
                .map(this::mapToDistrictResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DistrictResponse> getActiveDistrictsByRegionId(Long regionId, Pageable pageable) {
        log.debug("Fetching active districts for region ID: {} with pagination", regionId);

        Page<District> districts = districtRepository.findByRegionIdAndIsActiveTrueOrderByNameAsc(regionId, pageable);
        return districts.map(this::mapToDistrictResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DistrictResponse> searchDistricts(String searchTerm, Pageable pageable) {
        log.debug("Searching districts with term: {}", searchTerm);

        Page<District> districts = districtRepository.searchDistricts(searchTerm, pageable);
        return districts.map(this::mapToDistrictResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DistrictResponse> searchDistrictsByRegion(Long regionId, String searchTerm, Pageable pageable) {
        log.debug("Searching districts for region ID: {} with term: {}", regionId, searchTerm);

        Page<District> districts = districtRepository.searchDistrictsByRegion(regionId, searchTerm, pageable);
        return districts.map(this::mapToDistrictResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DistrictResponse> getDistrictsByStatus(Boolean isActive, Pageable pageable) {
        log.debug("Fetching districts by status: {}", isActive);

        Page<District> districts = districtRepository.findByIsActiveOrderByNameAsc(isActive, pageable);
        return districts.map(this::mapToDistrictResponse);
    }

    @Override
    public void deleteDistrict(Long id) {
        log.info("Soft deleting district with ID: {}", id);

        District district = districtRepository.findById(id)
                .orElseThrow(() -> new ApiException("District not found with ID: " + id, HttpStatus.NOT_FOUND));

        district.setIsActive(false);
        districtRepository.save(district);

        log.info("Successfully soft deleted district with ID: {}", id);
    }

    @Override
    public void deleteDistrictByUid(String uid) {
        log.info("Soft deleting district with UID: {}", uid);

        District district = districtRepository.findByUid(uid)
                .orElseThrow(() -> new ApiException("District not found with UID: " + uid, HttpStatus.NOT_FOUND));

        district.setIsActive(false);
        districtRepository.save(district);

        log.info("Successfully soft deleted district with UID: {}", uid);
    }

    @Override
    public DistrictResponse toggleDistrictStatus(Long id) {
        log.info("Toggling status for district with ID: {}", id);

        District district = districtRepository.findById(id)
                .orElseThrow(() -> new ApiException("District not found with ID: " + id, HttpStatus.NOT_FOUND));

        district.setIsActive(!district.getIsActive());
        District savedDistrict = districtRepository.save(district);

        log.info("Successfully toggled status for district with ID: {} to {}", id, savedDistrict.getIsActive());

        return mapToDistrictResponse(savedDistrict);
    }

    @Override
    public DistrictResponse toggleDistrictStatusByUid(String uid) {
        log.info("Toggling status for district with UID: {}", uid);

        District district = districtRepository.findByUid(uid)
                .orElseThrow(() -> new ApiException("District not found with UID: " + uid, HttpStatus.NOT_FOUND));

        district.setIsActive(!district.getIsActive());
        District savedDistrict = districtRepository.save(district);

        log.info("Successfully toggled status for district with UID: {} to {}", uid, savedDistrict.getIsActive());

        return mapToDistrictResponse(savedDistrict);
    }

    @Override
    @Transactional(readOnly = true)
    public DistrictStats getDistrictStats() {
        log.debug("Fetching district statistics");

        long totalDistricts = districtRepository.count();
        long activeDistricts = districtRepository.countByIsActiveTrue();
        long inactiveDistricts = totalDistricts - activeDistricts;

        return new DistrictStats(totalDistricts, activeDistricts, inactiveDistricts);
    }

    @Override
    @Transactional(readOnly = true)
    public DistrictStats getDistrictStatsByRegion(Long regionId) {
        log.debug("Fetching district statistics for region ID: {}", regionId);

        long totalDistricts = districtRepository.countByRegionId(regionId);
        long activeDistricts = districtRepository.countByRegionIdAndIsActiveTrue(regionId);
        long inactiveDistricts = totalDistricts - activeDistricts;

        return new DistrictStats(totalDistricts, activeDistricts, inactiveDistricts);
    }

    /**
     * Generates the next available district code with DT prefix
     * @return the next district code (e.g., DT0001, DT0002, etc.)
     */
    private String generateNextDistrictCode() {
        // Find the highest existing district code
        String highestCode = districtRepository.findFirstByOrderByCodeDesc()
                .map(District::getCode)
                .orElse("DT0000");

        // Extract the number part and increment
        if (highestCode.startsWith("DT")) {
            try {
                String numberPart = highestCode.substring(2);
                int nextNumber = Integer.parseInt(numberPart) + 1;
                return String.format("DT%04d", nextNumber);
            } catch (NumberFormatException e) {
                log.warn("Invalid district code format found: {}, starting from DT0001", highestCode);
            }
        }

        // If no valid code found or error, start from DT0001
        return "DT0001";
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
     * Maps District entity to DistrictResponse DTO
     */
    private DistrictResponse mapToDistrictResponse(District district) {
        if (district == null) {
            return null;
        }

        DistrictResponse response = new DistrictResponse();
        response.setId(district.getId());
        response.setUid(district.getUid());
        response.setCode(district.getCode());
        response.setName(district.getName());
        response.setHeadquarters(district.getHeadquarters());
        response.setPopulation(district.getPopulation());
        response.setAreaSqKm(district.getAreaSqKm());
        response.setLatitude(district.getLatitude());
        response.setLongitude(district.getLongitude());
        response.setCommissioner(district.getCommissioner());
        response.setDescription(district.getDescription());
        response.setIsActive(district.getIsActive());
        
        // Region information
        if (district.getRegion() != null) {
            response.setRegionId(district.getRegion().getId());
            response.setRegionName(district.getRegion().getName());
            response.setRegionCode(district.getRegion().getCode());
        }
        
        // Audit fields
        response.setCreatedById(district.getCreatedBy() != null ? district.getCreatedBy().getId() : null);
        response.setCreatedByUsername(district.getCreatedBy() != null ? district.getCreatedBy().getUsername() : null);
        response.setUpdatedById(district.getUpdatedBy() != null ? district.getUpdatedBy().getId() : null);
        response.setUpdatedByUsername(district.getUpdatedBy() != null ? district.getUpdatedBy().getUsername() : null);
        response.setCreatedAt(district.getCreatedAt());
        response.setUpdatedAt(district.getUpdatedAt());
        
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<DistrictResponse> getDistrictsByRegionUid(String regionUid) {
        log.debug("Fetching districts for region UID: {}", regionUid);
        // First find the region by UID, then get districts by region ID
        Region region = regionRepository.findByUid(regionUid)
                .orElseThrow(() -> new ApiException("Region not found with UID: " + regionUid, HttpStatus.NOT_FOUND));
        
        List<District> districts = districtRepository.findByRegionIdOrderByNameAsc(region.getId());
        return districts.stream()
                .map(this::mapToDistrictResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DistrictResponse> getDistrictsByRegionUid(String regionUid, Pageable pageable) {
        log.debug("Fetching districts for region UID: {}, page: {}, size: {}", regionUid, pageable.getPageNumber(), pageable.getPageSize());
        // First find the region by UID, then get districts by region ID
        Region region = regionRepository.findByUid(regionUid)
                .orElseThrow(() -> new ApiException("Region not found with UID: " + regionUid, HttpStatus.NOT_FOUND));
        
        Page<District> districts = districtRepository.findByRegionIdOrderByNameAsc(region.getId(), pageable);
        return districts.map(this::mapToDistrictResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DistrictResponse> getActiveDistrictsByRegionUid(String regionUid) {
        log.debug("Fetching active districts for region UID: {}", regionUid);
        // First find the region by UID, then get active districts by region ID
        Region region = regionRepository.findByUid(regionUid)
                .orElseThrow(() -> new ApiException("Region not found with UID: " + regionUid, HttpStatus.NOT_FOUND));
        
        List<District> districts = districtRepository.findByRegionIdAndIsActiveTrueOrderByNameAsc(region.getId());
        return districts.stream()
                .map(this::mapToDistrictResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DistrictResponse> getActiveDistrictsByRegionUid(String regionUid, Pageable pageable) {
        log.debug("Fetching active districts for region UID: {}, page: {}, size: {}", regionUid, pageable.getPageNumber(), pageable.getPageSize());
        // First find the region by UID, then get active districts by region ID
        Region region = regionRepository.findByUid(regionUid)
                .orElseThrow(() -> new ApiException("Region not found with UID: " + regionUid, HttpStatus.NOT_FOUND));
        
        Page<District> districts = districtRepository.findByRegionIdAndIsActiveTrueOrderByNameAsc(region.getId(), pageable);
        return districts.map(this::mapToDistrictResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DistrictResponse> searchDistrictsByRegionUid(String regionUid, String searchTerm, Pageable pageable) {
        log.debug("Searching districts for region UID: {} with term: {}, page: {}, size: {}", regionUid, searchTerm, pageable.getPageNumber(), pageable.getPageSize());
        // First find the region by UID, then search districts by region ID
        Region region = regionRepository.findByUid(regionUid)
                .orElseThrow(() -> new ApiException("Region not found with UID: " + regionUid, HttpStatus.NOT_FOUND));
        
        Page<District> districts = districtRepository.searchDistrictsByRegion(region.getId(), searchTerm, pageable);
        return districts.map(this::mapToDistrictResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public DistrictStats getDistrictStatsByRegionUid(String regionUid) {
        log.debug("Fetching district statistics for region UID: {}", regionUid);
        // First find the region by UID, then get stats by region ID
        Region region = regionRepository.findByUid(regionUid)
                .orElseThrow(() -> new ApiException("Region not found with UID: " + regionUid, HttpStatus.NOT_FOUND));
        
        long totalDistricts = districtRepository.countByRegionId(region.getId());
        long activeDistricts = districtRepository.countByRegionIdAndIsActiveTrue(region.getId());
        long inactiveDistricts = totalDistricts - activeDistricts;

        return new DistrictStats(totalDistricts, activeDistricts, inactiveDistricts);
    }
}
