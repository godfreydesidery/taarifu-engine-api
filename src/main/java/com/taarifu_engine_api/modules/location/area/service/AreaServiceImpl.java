package com.taarifu_engine_api.modules.location.area.service;

import com.taarifu_engine_api.modules.common.domain.enums.AreaType;
import com.taarifu_engine_api.modules.common.domain.util.PageResponseWrapper;
import com.taarifu_engine_api.modules.common.exception.ApiException;
import com.taarifu_engine_api.modules.location.area.domain.dto.AreaResponse;
import com.taarifu_engine_api.modules.location.area.domain.entity.Area;
import com.taarifu_engine_api.modules.location.area.repository.AreaRepository;
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

/**
 * Service implementation for Area operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AreaServiceImpl implements AreaService {

    private final AreaRepository areaRepository;
    private final UserRepository userRepository;

    @Override
    public Area createArea(AreaType areaType, Long areaId, String name) {
        log.info("Creating area for type: {} with areaId: {} and name: {}", areaType, areaId, name);

        // Check if area already exists
        if (areaRepository.existsByAreaTypeAndAreaId(areaType, areaId)) {
            log.warn("Area already exists for type: {} with areaId: {}", areaType, areaId);
            return areaRepository.findByAreaTypeAndAreaId(areaType, areaId)
                    .orElseThrow(() -> new ApiException("Area not found", HttpStatus.NOT_FOUND));
        }

        // Get current authenticated user
        User currentUser = getCurrentAuthenticatedUser();

        // Create new area
        Area area = new Area();
        area.ensureUid();
        area.setAreaType(areaType);
        area.setAreaId(areaId);
        area.setName(name);
        area.setAreaCode(getNextSequenceNumber());
        area.setCreatedBy(currentUser);
        area.setUpdatedBy(currentUser);

        Area savedArea = areaRepository.save(area);
        log.info("Created area with code: {} for type: {} with areaId: {} and name: {}", 
                savedArea.getCode(), areaType, areaId, name);

        return savedArea;
    }

    @Override
    public Area updateArea(AreaType areaType, Long areaId, String name) {
        log.info("Updating area for type: {} with areaId: {} and name: {}", areaType, areaId, name);

        // Find existing area
        Area area = areaRepository.findByAreaTypeAndAreaId(areaType, areaId)
                .orElseThrow(() -> new ApiException("Area not found for type: " + areaType + " with areaId: " + areaId, HttpStatus.NOT_FOUND));

        // Get current authenticated user
        User currentUser = getCurrentAuthenticatedUser();

        // Update the area
        area.setName(name);
        area.setUpdatedBy(currentUser);

        Area savedArea = areaRepository.save(area);
        log.info("Updated area with code: {} for type: {} with areaId: {} and name: {}", 
                savedArea.getCode(), areaType, areaId, name);

        return savedArea;
    }

    @Override
    @Transactional(readOnly = true)
    public Area findByUid(String uid) {
        return areaRepository.findByUid(uid)
                .orElseThrow(() -> new ApiException("Area not found with UID: " + uid, HttpStatus.NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
    public Area findByCode(String code) {
        return areaRepository.findByCode(code)
                .orElseThrow(() -> new ApiException("Area not found with code: " + code, HttpStatus.NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
    public Area findByAreaTypeAndAreaId(AreaType areaType, Long areaId) {
        return areaRepository.findByAreaTypeAndAreaId(areaType, areaId)
                .orElseThrow(() -> new ApiException("Area not found for type: " + areaType + " with areaId: " + areaId, HttpStatus.NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseWrapper<AreaResponse> getAllAreas(Pageable pageable) {
        Page<Area> areas = areaRepository.findAllByOrderByCodeAsc(pageable);
        Page<AreaResponse> areaResponses = areas.map(this::mapToAreaResponse);
        return PageResponseWrapper.fromPage(areaResponses, "Areas fetched successfully");
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseWrapper<AreaResponse> getAreasByType(AreaType areaType, Pageable pageable) {
        Page<Area> areas = areaRepository.findByAreaTypeOrderByCodeAsc(areaType, pageable);
        Page<AreaResponse> areaResponses = areas.map(this::mapToAreaResponse);
        return PageResponseWrapper.fromPage(areaResponses, "Areas fetched successfully");
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseWrapper<AreaResponse> searchAreas(String searchTerm, Pageable pageable) {
        Page<Area> areas = areaRepository.searchAreas(searchTerm, pageable);
        Page<AreaResponse> areaResponses = areas.map(this::mapToAreaResponse);
        return PageResponseWrapper.fromPage(areaResponses, "Areas fetched successfully");
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseWrapper<AreaResponse> searchAreasByType(AreaType areaType, String searchTerm, Pageable pageable) {
        Page<Area> areas = areaRepository.searchAreasByType(areaType, searchTerm, pageable);
        Page<AreaResponse> areaResponses = areas.map(this::mapToAreaResponse);
        return PageResponseWrapper.fromPage(areaResponses, "Areas fetched successfully");
    }

    @Override
    @Transactional(readOnly = true)
    public AreaResponse getAreaResponseByUid(String uid) {
        Area area = findByUid(uid);
        return mapToAreaResponse(area);
    }

    @Override
    @Transactional(readOnly = true)
    public AreaResponse getAreaResponseByCode(String code) {
        Area area = findByCode(code);
        return mapToAreaResponse(area);
    }

    @Override
    @Transactional(readOnly = true)
    public AreaResponse getAreaResponseByTypeAndId(AreaType areaType, Long areaId) {
        Area area = findByAreaTypeAndAreaId(areaType, areaId);
        return mapToAreaResponse(area);
    }

    @Override
    @Transactional(readOnly = true)
    public AreaStats getAreaStats() {
        long totalAreas = areaRepository.count();
        long regionCount = areaRepository.countByAreaType(AreaType.REGION);
        long districtCount = areaRepository.countByAreaType(AreaType.DISTRICT);
        long wardCount = areaRepository.countByAreaType(AreaType.WARD);
        long villageCount = areaRepository.countByAreaType(AreaType.VILLAGE);
        long hamletCount = areaRepository.countByAreaType(AreaType.HAMLET);
        long constituencyCount = areaRepository.countByAreaType(AreaType.CONSTITUENCY);

        return new AreaStats(
                totalAreas,
                regionCount,
                districtCount,
                wardCount,
                villageCount,
                hamletCount,
                constituencyCount
        );
    }

    @Override
    public String generateNextAreaCode() {
        int nextSequence = getNextSequenceNumber();
        return String.format("AR%011d", nextSequence);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByAreaTypeAndAreaId(AreaType areaType, Long areaId) {
        return areaRepository.existsByAreaTypeAndAreaId(areaType, areaId);
    }

    @Override
    public void deleteByAreaTypeAndAreaId(AreaType areaType, Long areaId) {
        log.info("Deleting area for type: {} with areaId: {}", areaType, areaId);
        areaRepository.findByAreaTypeAndAreaId(areaType, areaId)
                .ifPresent(area -> {
                    areaRepository.delete(area);
                    log.info("Deleted area with code: {}", area.getCode());
                });
    }

    /**
     * Maps Area entity to AreaResponse DTO
     */
    private AreaResponse mapToAreaResponse(Area area) {
        if (area == null) {
            return null;
        }

        AreaResponse response = new AreaResponse();
        response.setId(area.getId());
        response.setUid(area.getUid());
        response.setCode(area.getCode());
        response.setAreaType(area.getAreaType());
        response.setAreaId(area.getAreaId());
        response.setName(area.getName());
        response.setCreatedAt(area.getCreatedAt());
        response.setUpdatedAt(area.getUpdatedAt());

        return response;
    }

    /**
     * Get the next sequence number for area code generation
     */
    private int getNextSequenceNumber() {
        return areaRepository.findTopByOrderByCodeDesc()
                .map(area -> {
                    String code = area.getCode();
                    if (code != null && code.startsWith("AR") && code.length() == 13) {
                        try {
                            return Integer.parseInt(code.substring(2)) + 1;
                        } catch (NumberFormatException e) {
                            log.warn("Invalid area code format: {}", code);
                        }
                    }
                    return 1;
                })
                .orElse(1);
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
