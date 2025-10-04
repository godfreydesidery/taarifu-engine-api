package com.taarifu_engine_api.modules.location.constituency.service;

import com.taarifu_engine_api.modules.common.domain.enums.AreaType;
import com.taarifu_engine_api.modules.common.exception.ApiException;
import com.taarifu_engine_api.modules.location.area.service.AreaService;
import com.taarifu_engine_api.modules.location.constituency.domain.dto.CreateConstituencyRequest;
import com.taarifu_engine_api.modules.location.constituency.domain.dto.ConstituencyResponse;
import com.taarifu_engine_api.modules.location.constituency.domain.dto.UpdateConstituencyRequest;
import com.taarifu_engine_api.modules.location.constituency.domain.entity.Constituency;
import com.taarifu_engine_api.modules.location.constituency.repository.ConstituencyRepository;
import com.taarifu_engine_api.modules.location.district.domain.entity.District;
import com.taarifu_engine_api.modules.location.district.repository.DistrictRepository;
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
 * Service implementation for Constituency operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ConstituencyServiceImpl implements ConstituencyService {

    private final ConstituencyRepository constituencyRepository;
    private final DistrictRepository districtRepository;
    private final AreaService areaService;

    @Override
    public ConstituencyResponse createConstituency(CreateConstituencyRequest request) {
        log.info("Creating new constituency: {}", request.getName());

        // Check if constituency with same name already exists
        if (constituencyRepository.existsByName(request.getName())) {
            throw new ApiException("Constituency with name '" + request.getName() + "' already exists", HttpStatus.BAD_REQUEST);
        }

        // Validate district exists
        District district = districtRepository.findById(request.getDistrictId())
                .orElseThrow(() -> new ApiException("District not found with ID: " + request.getDistrictId(), HttpStatus.BAD_REQUEST));

        // Generate next constituency code
        String nextConstituencyCode = generateNextConstituencyCode();

        // Get current authenticated user
        User currentUser = getCurrentAuthenticatedUser();

        // Create new constituency
        Constituency constituency = new Constituency();
        constituency.setCode(nextConstituencyCode);
        constituency.setName(request.getName());
        constituency.setHeadquarters(request.getHeadquarters());
        constituency.setPopulation(request.getPopulation());
        constituency.setAreaSqKm(request.getAreaSqKm());
        constituency.setLatitude(request.getLatitude());
        constituency.setLongitude(request.getLongitude());
        constituency.setDescription(request.getDescription());
        constituency.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        constituency.setDistrict(district);
        constituency.setCreatedBy(currentUser);
        constituency.setUpdatedBy(currentUser);

        // Ensure UID is generated
        constituency.ensureUid();

        Constituency savedConstituency = constituencyRepository.save(constituency);
        log.info("Successfully created constituency with ID: {}, UID: {}, and code: {}", 
                savedConstituency.getId(), savedConstituency.getUid(), savedConstituency.getCode());

        // Create corresponding Area entity
        areaService.createArea(AreaType.CONSTITUENCY, savedConstituency.getId(), savedConstituency.getName());
        log.info("Successfully created Area for Constituency with ID: {}", savedConstituency.getId());

        return mapToConstituencyResponse(savedConstituency);
    }

    @Override
    public ConstituencyResponse updateConstituencyByUid(String uid, UpdateConstituencyRequest request) {
        log.info("Updating constituency with UID: {}", uid);

        Constituency constituency = constituencyRepository.findByUid(uid)
                .orElseThrow(() -> new ApiException("Constituency not found with UID: " + uid, HttpStatus.NOT_FOUND));

        return updateConstituencyInternal(constituency, request);
    }

    @Override
    public ConstituencyResponse getConstituencyByUid(String uid) {
        log.debug("Fetching constituency with UID: {}", uid);

        Constituency constituency = constituencyRepository.findByUidWithDetails(uid)
                .orElseThrow(() -> new ApiException("Constituency not found with UID: " + uid, HttpStatus.NOT_FOUND));

        return mapToConstituencyResponse(constituency);
    }

    @Override
    public ConstituencyResponse getConstituencyByCode(String code) {
        log.debug("Fetching constituency with code: {}", code);

        Constituency constituency = constituencyRepository.findByCode(code)
                .orElseThrow(() -> new ApiException("Constituency not found with code: " + code, HttpStatus.NOT_FOUND));

        return mapToConstituencyResponse(constituency);
    }

    @Override
    public Page<ConstituencyResponse> getAllConstituencies(Pageable pageable) {
        log.debug("Fetching all constituencies with pagination");

        Page<Constituency> constituencies = constituencyRepository.findAll(pageable);
        return constituencies.map(this::mapToConstituencyResponse);
    }

    @Override
    public Page<ConstituencyResponse> getActiveConstituencies(Pageable pageable) {
        log.debug("Fetching active constituencies with pagination");

        Page<Constituency> constituencies = constituencyRepository.findByIsActiveTrueOrderByNameAsc(pageable);
        return constituencies.map(this::mapToConstituencyResponse);
    }

    @Override
    public Page<ConstituencyResponse> getConstituenciesByDistrictUid(String districtUid, Pageable pageable) {
        log.debug("Fetching constituencies for district UID: {}", districtUid);

        Page<Constituency> constituencies = constituencyRepository.findByDistrictUidWithUsersAndDistrict(districtUid, pageable);
        return constituencies.map(this::mapToConstituencyResponse);
    }

    @Override
    public Page<ConstituencyResponse> getActiveConstituenciesByDistrictUid(String districtUid, Pageable pageable) {
        log.debug("Fetching active constituencies for district UID: {}", districtUid);

        Page<Constituency> constituencies = constituencyRepository.findByDistrictUidWithUsersAndDistrict(districtUid, pageable);
        return constituencies.getContent().stream()
                .filter(Constituency::isActive)
                .map(this::mapToConstituencyResponse)
                .collect(Collectors.collectingAndThen(
                        Collectors.toList(),
                        list -> new org.springframework.data.domain.PageImpl<>(list, pageable, list.size())
                ));
    }

    @Override
    public Page<ConstituencyResponse> searchConstituencies(String searchTerm, Pageable pageable) {
        log.debug("Searching constituencies with term: {}", searchTerm);

        Page<Constituency> constituencies = constituencyRepository.searchConstituencies(searchTerm, pageable);
        return constituencies.map(this::mapToConstituencyResponse);
    }

    @Override
    public Page<ConstituencyResponse> searchConstituenciesByDistrictUid(String districtUid, String searchTerm, Pageable pageable) {
        log.debug("Searching constituencies for district UID: {} with term: {}", districtUid, searchTerm);

        District district = districtRepository.findByUid(districtUid)
                .orElseThrow(() -> new ApiException("District not found with UID: " + districtUid, HttpStatus.NOT_FOUND));

        Page<Constituency> constituencies = constituencyRepository.searchConstituenciesByDistrict(district.getId(), searchTerm, pageable);
        return constituencies.map(this::mapToConstituencyResponse);
    }

    @Override
    public Page<ConstituencyResponse> getConstituenciesByStatus(Boolean isActive, Pageable pageable) {
        log.debug("Fetching constituencies by status: {}", isActive);

        Page<Constituency> constituencies = constituencyRepository.findByIsActiveOrderByNameAsc(isActive, pageable);
        return constituencies.map(this::mapToConstituencyResponse);
    }

    @Override
    public void deleteConstituencyByUid(String uid) {
        log.info("Soft deleting constituency with UID: {}", uid);

        Constituency constituency = constituencyRepository.findByUid(uid)
                .orElseThrow(() -> new ApiException("Constituency not found with UID: " + uid, HttpStatus.NOT_FOUND));

        constituency.setIsActive(false);
        constituency.setUpdatedBy(getCurrentAuthenticatedUser());
        constituencyRepository.save(constituency);

        log.info("Successfully soft deleted constituency with UID: {}", uid);
    }

    @Override
    public ConstituencyResponse toggleConstituencyStatusByUid(String uid) {
        log.info("Toggling status for constituency with UID: {}", uid);

        Constituency constituency = constituencyRepository.findByUid(uid)
                .orElseThrow(() -> new ApiException("Constituency not found with UID: " + uid, HttpStatus.NOT_FOUND));

        constituency.setIsActive(!constituency.isActive());
        constituency.setUpdatedBy(getCurrentAuthenticatedUser());
        Constituency updatedConstituency = constituencyRepository.save(constituency);

        log.info("Successfully toggled constituency status to: {} for UID: {}", updatedConstituency.isActive(), uid);
        return mapToConstituencyResponse(updatedConstituency);
    }

    @Override
    public ConstituencyStats getConstituencyStats() {
        log.debug("Fetching constituency statistics");

        long totalConstituencies = constituencyRepository.count();
        long activeConstituencies = constituencyRepository.countByIsActiveTrue();
        long inactiveConstituencies = totalConstituencies - activeConstituencies;

        return new ConstituencyStats(totalConstituencies, activeConstituencies, inactiveConstituencies);
    }

    @Override
    public ConstituencyStats getConstituencyStatsByDistrictUid(String districtUid) {
        log.debug("Fetching constituency statistics for district UID: {}", districtUid);

        District district = districtRepository.findByUid(districtUid)
                .orElseThrow(() -> new ApiException("District not found with UID: " + districtUid, HttpStatus.NOT_FOUND));

        long totalConstituencies = constituencyRepository.countByDistrictId(district.getId());
        long activeConstituencies = constituencyRepository.countByDistrictIdAndIsActiveTrue(district.getId());
        long inactiveConstituencies = totalConstituencies - activeConstituencies;

        return new ConstituencyStats(totalConstituencies, activeConstituencies, inactiveConstituencies);
    }

    private ConstituencyResponse updateConstituencyInternal(Constituency constituency, UpdateConstituencyRequest request) {
        // Track if name was changed for area update
        boolean nameChanged = false;
        
        // Check if name is being changed and if new name already exists
        if (request.getName() != null && !request.getName().equals(constituency.getName())) {
            if (constituencyRepository.existsByName(request.getName())) {
                throw new ApiException("Constituency with name '" + request.getName() + "' already exists", HttpStatus.BAD_REQUEST);
            }
            constituency.setName(request.getName());
            nameChanged = true;
        }

        // Update fields if provided
        if (request.getHeadquarters() != null) {
            constituency.setHeadquarters(request.getHeadquarters());
        }
        if (request.getPopulation() != null) {
            constituency.setPopulation(request.getPopulation());
        }
        if (request.getAreaSqKm() != null) {
            constituency.setAreaSqKm(request.getAreaSqKm());
        }
        if (request.getLatitude() != null) {
            constituency.setLatitude(request.getLatitude());
        }
        if (request.getLongitude() != null) {
            constituency.setLongitude(request.getLongitude());
        }
        if (request.getDescription() != null) {
            constituency.setDescription(request.getDescription());
        }
        if (request.getIsActive() != null) {
            constituency.setIsActive(request.getIsActive());
        }

        constituency.setUpdatedBy(getCurrentAuthenticatedUser());

        Constituency updatedConstituency = constituencyRepository.save(constituency);
        log.info("Successfully updated constituency with UID: {}", updatedConstituency.getUid());

        // Update corresponding Area entity if name was changed
        if (nameChanged) {
            areaService.updateArea(AreaType.CONSTITUENCY, updatedConstituency.getId(), updatedConstituency.getName());
            log.info("Successfully updated Area for Constituency with ID: {}", updatedConstituency.getId());
        }

        return mapToConstituencyResponse(updatedConstituency);
    }

    private String generateNextConstituencyCode() {
        // Use the maximum ID + 1 as the sequence number
        Long maxId = constituencyRepository.findMaxId();
        int nextSequence = maxId != null ? maxId.intValue() + 1 : 1;
        
        return String.format("CT%06d", nextSequence);
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

    private ConstituencyResponse mapToConstituencyResponse(Constituency constituency) {
        ConstituencyResponse response = new ConstituencyResponse();
        response.setId(constituency.getId());
        response.setUid(constituency.getUid());
        response.setCode(constituency.getCode());
        response.setName(constituency.getName());
        response.setHeadquarters(constituency.getHeadquarters());
        response.setPopulation(constituency.getPopulation());
        response.setAreaSqKm(constituency.getAreaSqKm());
        response.setLatitude(constituency.getLatitude());
        response.setLongitude(constituency.getLongitude());
        response.setDescription(constituency.getDescription());
        response.setIsActive(constituency.getIsActive());

        // District information
        if (constituency.getDistrict() != null) {
            response.setDistrictId(constituency.getDistrict().getId());
            response.setDistrictName(constituency.getDistrict().getName());
            response.setDistrictCode(constituency.getDistrict().getCode());

            // Region information through district
            if (constituency.getDistrict().getRegion() != null) {
                response.setRegionId(constituency.getDistrict().getRegion().getId());
                response.setRegionName(constituency.getDistrict().getRegion().getName());
                response.setRegionCode(constituency.getDistrict().getRegion().getCode());
            }
        }

        // Audit fields
        if (constituency.getCreatedBy() != null) {
            response.setCreatedById(constituency.getCreatedBy().getId());
            response.setCreatedByUsername(constituency.getCreatedBy().getUsername());
        }
        if (constituency.getUpdatedBy() != null) {
            response.setUpdatedById(constituency.getUpdatedBy().getId());
            response.setUpdatedByUsername(constituency.getUpdatedBy().getUsername());
        }
        response.setCreatedAt(constituency.getCreatedAt());
        response.setUpdatedAt(constituency.getUpdatedAt());

        return response;
    }
}
