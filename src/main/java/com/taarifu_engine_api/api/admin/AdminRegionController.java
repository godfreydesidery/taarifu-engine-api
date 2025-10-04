package com.taarifu_engine_api.api.admin;

import com.taarifu_engine_api.modules.common.domain.util.ResponseWrapper;
import com.taarifu_engine_api.modules.common.domain.util.PageResponseWrapper;
import com.taarifu_engine_api.modules.location.region.domain.dto.CreateRegionRequest;
import com.taarifu_engine_api.modules.location.region.domain.dto.RegionResponse;
import com.taarifu_engine_api.modules.location.region.domain.dto.UpdateRegionRequest;
import com.taarifu_engine_api.modules.location.region.service.RegionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for Region management operations
 * Accessible only to admin users
 */
@RestController
@RequestMapping("/admin/v1/regions")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class AdminRegionController {

    private final RegionService regionService;

    /**
     * Create a new region
     */
    @PostMapping
    public ResponseEntity<ResponseWrapper<RegionResponse>> createRegion(
            @Valid @RequestBody CreateRegionRequest request) {
        log.info("Admin creating new region: {}", request.getName());
        
        RegionResponse response = regionService.createRegion(request);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseWrapper<>(
                        true,
                        HttpStatus.CREATED.value(),
                        "Region created successfully",
                        response
                ));
    }

    /**
     * Update an existing region by UID
     */
    @PutMapping("/uid/{uid}")
    public ResponseEntity<ResponseWrapper<RegionResponse>> updateRegion(
            @PathVariable String uid,
            @Valid @RequestBody UpdateRegionRequest request) {
        log.info("Admin updating region with UID: {}", uid);
        
        RegionResponse response = regionService.updateRegionByUid(uid, request);
        
        return ResponseEntity.ok(new ResponseWrapper<>(
                true,
                HttpStatus.OK.value(),
                "Region updated successfully",
                response
        ));
    }

    /**
     * Get region by UID
     */
    @GetMapping("/uid/{uid}")
    public ResponseEntity<ResponseWrapper<RegionResponse>> getRegionByUid(@PathVariable String uid) {
        log.debug("Admin fetching region with UID: {}", uid);
        
        RegionResponse response = regionService.getRegionByUid(uid);
        
        return ResponseEntity.ok(new ResponseWrapper<>(
                true,
                HttpStatus.OK.value(),
                "Region retrieved successfully",
                response
        ));
    }

    /**
     * Get region by code
     */
    @GetMapping("/code/{code}")
    public ResponseEntity<ResponseWrapper<RegionResponse>> getRegionByCode(@PathVariable String code) {
        log.debug("Admin fetching region with code: {}", code);
        
        RegionResponse response = regionService.getRegionByCode(code);
        
        return ResponseEntity.ok(new ResponseWrapper<>(
                true,
                HttpStatus.OK.value(),
                "Region retrieved successfully",
                response
        ));
    }

    /**
     * Get all regions with pagination
     */
    @GetMapping
    public ResponseEntity<PageResponseWrapper<RegionResponse>> getAllRegions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        log.debug("Admin fetching all regions - page: {}, size: {}", page, size);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") 
                ? Sort.by(sortBy).descending() 
                : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<RegionResponse> response = regionService.getAllRegions(pageable);
        
        return ResponseEntity.ok(PageResponseWrapper.fromPage(response, "Regions retrieved successfully"));
    }

    /**
     * Get all active regions
     */
    @GetMapping("/active")
    public ResponseEntity<PageResponseWrapper<RegionResponse>> getActiveRegions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        log.debug("Admin fetching active regions - page: {}, size: {}", page, size);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") 
                ? Sort.by(sortBy).descending() 
                : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<RegionResponse> response = regionService.getActiveRegions(pageable);
        
        return ResponseEntity.ok(PageResponseWrapper.fromPage(response, "Active regions retrieved successfully"));
    }

    /**
     * Search regions
     */
    @GetMapping("/search")
    public ResponseEntity<PageResponseWrapper<RegionResponse>> searchRegions(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        log.debug("Admin searching regions with term: {}", q);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") 
                ? Sort.by(sortBy).descending() 
                : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<RegionResponse> response = regionService.searchRegions(q, pageable);
        
        return ResponseEntity.ok(PageResponseWrapper.fromPage(response, "Region search completed successfully"));
    }

    /**
     * Get regions by status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<PageResponseWrapper<RegionResponse>> getRegionsByStatus(
            @PathVariable Boolean status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        log.debug("Admin fetching regions by status: {}", status);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") 
                ? Sort.by(sortBy).descending() 
                : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<RegionResponse> response = regionService.getRegionsByStatus(status, pageable);
        
        return ResponseEntity.ok(PageResponseWrapper.fromPage(response, "Regions retrieved successfully"));
    }

    /**
     * Toggle region status (activate/deactivate)
     */
    @PatchMapping("/uid/{uid}/toggle-status")
    public ResponseEntity<ResponseWrapper<RegionResponse>> toggleRegionStatus(@PathVariable String uid) {
        log.info("Admin toggling status for region with UID: {}", uid);
        
        RegionResponse response = regionService.toggleRegionStatusByUid(uid);
        
        return ResponseEntity.ok(new ResponseWrapper<>(
                true,
                HttpStatus.OK.value(),
                "Region status toggled successfully",
                response
        ));
    }

    /**
     * Soft delete region by UID
     */
    @DeleteMapping("/uid/{uid}")
    public ResponseEntity<ResponseWrapper<Void>> deleteRegion(@PathVariable String uid) {
        log.info("Admin soft deleting region with UID: {}", uid);
        
        regionService.deleteRegionByUid(uid);
        
        return ResponseEntity.ok(new ResponseWrapper<>(
                true,
                HttpStatus.OK.value(),
                "Region deleted successfully",
                null
        ));
    }

    /**
     * Get region statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<ResponseWrapper<RegionService.RegionStats>> getRegionStats() {
        log.debug("Admin fetching region statistics");
        
        RegionService.RegionStats stats = regionService.getRegionStats();
        
        return ResponseEntity.ok(new ResponseWrapper<>(
                true,
                HttpStatus.OK.value(),
                "Region statistics retrieved successfully",
                stats
        ));
    }
}
