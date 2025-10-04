package com.taarifu_engine_api.api.admin;

import com.taarifu_engine_api.modules.common.domain.enums.AreaType;
import com.taarifu_engine_api.modules.common.domain.util.PageResponseWrapper;
import com.taarifu_engine_api.modules.common.domain.util.ResponseWrapper;
import com.taarifu_engine_api.modules.location.area.domain.dto.AreaResponse;
import com.taarifu_engine_api.modules.location.area.service.AreaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Admin controller for Area management (Read-only operations)
 * Areas are automatically created when locations are created and cannot be manually managed
 */
@RestController
@RequestMapping("/admin/v1/areas")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('ADMIN')")
public class AdminAreaController {

    private final AreaService areaService;

    /**
     * Get all areas with pagination
     */
    @GetMapping
    public ResponseEntity<PageResponseWrapper<AreaResponse>> getAllAreas(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "code") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        log.info("Admin requesting all areas - page: {}, size: {}, sortBy: {}, sortDir: {}", 
                page, size, sortBy, sortDir);

        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        PageResponseWrapper<AreaResponse> response = areaService.getAllAreas(pageable);
        
        log.info("Retrieved {} areas for admin", response.getData().size());
        return ResponseEntity.ok(response);
    }

    /**
     * Get area by UID
     */
    @GetMapping("/uid/{uid}")
    public ResponseEntity<ResponseWrapper<AreaResponse>> getAreaByUid(@PathVariable String uid) {
        log.info("Admin requesting area by UID: {}", uid);

        AreaResponse area = areaService.getAreaResponseByUid(uid);
        ResponseWrapper<AreaResponse> response = new ResponseWrapper<>(
                true, 200, "Area retrieved successfully", area);

        log.info("Retrieved area with UID: {} for admin", uid);
        return ResponseEntity.ok(response);
    }

    /**
     * Get area by code
     */
    @GetMapping("/code/{code}")
    public ResponseEntity<ResponseWrapper<AreaResponse>> getAreaByCode(@PathVariable String code) {
        log.info("Admin requesting area by code: {}", code);

        AreaResponse area = areaService.getAreaResponseByCode(code);
        ResponseWrapper<AreaResponse> response = new ResponseWrapper<>(
                true, 200, "Area retrieved successfully", area);

        log.info("Retrieved area with code: {} for admin", code);
        return ResponseEntity.ok(response);
    }

    /**
     * Get areas by area type
     */
    @GetMapping("/type/{areaType}")
    public ResponseEntity<PageResponseWrapper<AreaResponse>> getAreasByType(
            @PathVariable AreaType areaType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "code") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        log.info("Admin requesting areas by type: {} - page: {}, size: {}", areaType, page, size);

        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        PageResponseWrapper<AreaResponse> response = areaService.getAreasByType(areaType, pageable);
        
        log.info("Retrieved {} areas of type {} for admin", response.getData().size(), areaType);
        return ResponseEntity.ok(response);
    }

    /**
     * Search areas
     */
    @GetMapping("/search")
    public ResponseEntity<PageResponseWrapper<AreaResponse>> searchAreas(
            @RequestParam String searchTerm,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "code") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        log.info("Admin searching areas with term: {} - page: {}, size: {}", searchTerm, page, size);

        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        PageResponseWrapper<AreaResponse> response = areaService.searchAreas(searchTerm, pageable);
        
        log.info("Found {} areas matching search term: {} for admin", response.getData().size(), searchTerm);
        return ResponseEntity.ok(response);
    }

    /**
     * Search areas by type
     */
    @GetMapping("/type/{areaType}/search")
    public ResponseEntity<PageResponseWrapper<AreaResponse>> searchAreasByType(
            @PathVariable AreaType areaType,
            @RequestParam String searchTerm,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "code") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        log.info("Admin searching areas of type: {} with term: {} - page: {}, size: {}", 
                areaType, searchTerm, page, size);

        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        PageResponseWrapper<AreaResponse> response = areaService.searchAreasByType(areaType, searchTerm, pageable);
        
        log.info("Found {} areas of type {} matching search term: {} for admin", 
                response.getData().size(), areaType, searchTerm);
        return ResponseEntity.ok(response);
    }

    /**
     * Get area statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<ResponseWrapper<AreaService.AreaStats>> getAreaStats() {
        log.info("Admin requesting area statistics");

        AreaService.AreaStats stats = areaService.getAreaStats();
        ResponseWrapper<AreaService.AreaStats> response = new ResponseWrapper<>(
                true, 200, "Area statistics retrieved successfully", stats);

        log.info("Retrieved area statistics for admin - total: {}, regions: {}, districts: {}, wards: {}, villages: {}, hamlets: {}, constituencies: {}", 
                stats.totalAreas(), stats.regionCount(), stats.districtCount(), 
                stats.wardCount(), stats.villageCount(), stats.hamletCount(), stats.constituencyCount());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get area by area type and area ID
     */
    @GetMapping("/type/{areaType}/area/{areaId}")
    public ResponseEntity<ResponseWrapper<AreaResponse>> getAreaByTypeAndId(
            @PathVariable AreaType areaType,
            @PathVariable Long areaId) {

        log.info("Admin requesting area by type: {} and areaId: {}", areaType, areaId);

        AreaResponse area = areaService.getAreaResponseByTypeAndId(areaType, areaId);
        ResponseWrapper<AreaResponse> response = new ResponseWrapper<>(
                true, 200, "Area retrieved successfully", area);

        log.info("Retrieved area with type: {} and areaId: {} for admin", areaType, areaId);
        return ResponseEntity.ok(response);
    }
}