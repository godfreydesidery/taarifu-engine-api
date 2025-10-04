package com.taarifu_engine_api.api.admin;

import com.taarifu_engine_api.modules.common.domain.util.ResponseWrapper;
import com.taarifu_engine_api.modules.common.domain.util.PageResponseWrapper;
import com.taarifu_engine_api.modules.location.district.domain.dto.CreateDistrictRequest;
import com.taarifu_engine_api.modules.location.district.domain.dto.DistrictResponse;
import com.taarifu_engine_api.modules.location.district.domain.dto.UpdateDistrictRequest;
import com.taarifu_engine_api.modules.location.district.service.DistrictService;
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
 * REST controller for District management operations
 * Accessible only to admin users
 */
@RestController
@RequestMapping("/admin/v1/districts")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class AdminDistrictController {

    private final DistrictService districtService;

    /**
     * Create a new district
     */
    @PostMapping
    public ResponseEntity<ResponseWrapper<DistrictResponse>> createDistrict(
            @Valid @RequestBody CreateDistrictRequest request) {
        log.info("Admin creating new district: {}", request.getName());
        
        DistrictResponse response = districtService.createDistrict(request);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseWrapper<>(
                        true,
                        HttpStatus.CREATED.value(),
                        "District created successfully",
                        response
                ));
    }

    /**
     * Update an existing district by UID
     */
    @PutMapping("/uid/{uid}")
    public ResponseEntity<ResponseWrapper<DistrictResponse>> updateDistrict(
            @PathVariable String uid,
            @Valid @RequestBody UpdateDistrictRequest request) {
        log.info("Admin updating district with UID: {}", uid);
        
        DistrictResponse response = districtService.updateDistrictByUid(uid, request);
        
        return ResponseEntity.ok(new ResponseWrapper<>(
                true,
                HttpStatus.OK.value(),
                "District updated successfully",
                response
        ));
    }

    /**
     * Get district by UID
     */
    @GetMapping("/uid/{uid}")
    public ResponseEntity<ResponseWrapper<DistrictResponse>> getDistrictByUid(@PathVariable String uid) {
        log.debug("Admin fetching district with UID: {}", uid);
        
        DistrictResponse response = districtService.getDistrictByUid(uid);
        
        return ResponseEntity.ok(new ResponseWrapper<>(
                true,
                HttpStatus.OK.value(),
                "District retrieved successfully",
                response
        ));
    }

    /**
     * Get district by code
     */
    @GetMapping("/code/{code}")
    public ResponseEntity<ResponseWrapper<DistrictResponse>> getDistrictByCode(@PathVariable String code) {
        log.debug("Admin fetching district with code: {}", code);
        
        DistrictResponse response = districtService.getDistrictByCode(code);
        
        return ResponseEntity.ok(new ResponseWrapper<>(
                true,
                HttpStatus.OK.value(),
                "District retrieved successfully",
                response
        ));
    }

    /**
     * Get all districts with pagination
     */
    @GetMapping
    public ResponseEntity<PageResponseWrapper<DistrictResponse>> getAllDistricts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        log.debug("Admin fetching all districts - page: {}, size: {}", page, size);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") 
                ? Sort.by(sortBy).descending() 
                : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<DistrictResponse> response = districtService.getAllDistricts(pageable);
        
        return ResponseEntity.ok(PageResponseWrapper.fromPage(response, "Districts retrieved successfully"));
    }

    /**
     * Get all active districts
     */
    @GetMapping("/active")
    public ResponseEntity<PageResponseWrapper<DistrictResponse>> getActiveDistricts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        log.debug("Admin fetching active districts - page: {}, size: {}", page, size);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") 
                ? Sort.by(sortBy).descending() 
                : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<DistrictResponse> response = districtService.getActiveDistricts(pageable);
        
        return ResponseEntity.ok(PageResponseWrapper.fromPage(response, "Active districts retrieved successfully"));
    }

    /**
     * Get districts by region UID
     */
    @GetMapping("/region/uid/{regionUid}")
    public ResponseEntity<PageResponseWrapper<DistrictResponse>> getDistrictsByRegionUid(
            @PathVariable String regionUid,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        log.debug("Admin fetching districts for region UID: {}", regionUid);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") 
                ? Sort.by(sortBy).descending() 
                : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<DistrictResponse> response = districtService.getDistrictsByRegionUid(regionUid, pageable);
        
        return ResponseEntity.ok(PageResponseWrapper.fromPage(response, "Districts retrieved successfully"));
    }

    /**
     * Get active districts by region UID
     */
    @GetMapping("/region/uid/{regionUid}/active")
    public ResponseEntity<PageResponseWrapper<DistrictResponse>> getActiveDistrictsByRegionUid(
            @PathVariable String regionUid,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        log.debug("Admin fetching active districts for region UID: {}", regionUid);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") 
                ? Sort.by(sortBy).descending() 
                : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<DistrictResponse> response = districtService.getActiveDistrictsByRegionUid(regionUid, pageable);
        
        return ResponseEntity.ok(PageResponseWrapper.fromPage(response, "Active districts retrieved successfully"));
    }

    /**
     * Search districts
     */
    @GetMapping("/search")
    public ResponseEntity<PageResponseWrapper<DistrictResponse>> searchDistricts(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        log.debug("Admin searching districts with term: {}", q);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") 
                ? Sort.by(sortBy).descending() 
                : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<DistrictResponse> response = districtService.searchDistricts(q, pageable);
        
        return ResponseEntity.ok(PageResponseWrapper.fromPage(response, "District search completed successfully"));
    }

    /**
     * Search districts by region UID
     */
    @GetMapping("/region/uid/{regionUid}/search")
    public ResponseEntity<PageResponseWrapper<DistrictResponse>> searchDistrictsByRegionUid(
            @PathVariable String regionUid,
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        log.debug("Admin searching districts for region UID: {} with term: {}", regionUid, q);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") 
                ? Sort.by(sortBy).descending() 
                : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<DistrictResponse> response = districtService.searchDistrictsByRegionUid(regionUid, q, pageable);
        
        return ResponseEntity.ok(PageResponseWrapper.fromPage(response, "District search completed successfully"));
    }

    /**
     * Get districts by status
     */
    @GetMapping("/status/{isActive}")
    public ResponseEntity<PageResponseWrapper<DistrictResponse>> getDistrictsByStatus(
            @PathVariable Boolean isActive,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        log.debug("Admin fetching districts by status: {}", isActive);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") 
                ? Sort.by(sortBy).descending() 
                : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<DistrictResponse> response = districtService.getDistrictsByStatus(isActive, pageable);
        
        return ResponseEntity.ok(PageResponseWrapper.fromPage(response, "Districts retrieved successfully"));
    }

    /**
     * Toggle district status (activate/deactivate)
     */
    @PatchMapping("/uid/{uid}/toggle-status")
    public ResponseEntity<ResponseWrapper<DistrictResponse>> toggleDistrictStatus(@PathVariable String uid) {
        log.info("Admin toggling status for district with UID: {}", uid);
        
        DistrictResponse response = districtService.toggleDistrictStatusByUid(uid);
        
        return ResponseEntity.ok(new ResponseWrapper<>(
                true,
                HttpStatus.OK.value(),
                "District status toggled successfully",
                response
        ));
    }

    /**
     * Soft delete district by UID
     */
    @DeleteMapping("/uid/{uid}")
    public ResponseEntity<ResponseWrapper<Void>> deleteDistrict(@PathVariable String uid) {
        log.info("Admin soft deleting district with UID: {}", uid);
        
        districtService.deleteDistrictByUid(uid);
        
        return ResponseEntity.ok(new ResponseWrapper<>(
                true,
                HttpStatus.OK.value(),
                "District deleted successfully",
                null
        ));
    }

    /**
     * Get district statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<ResponseWrapper<DistrictService.DistrictStats>> getDistrictStats() {
        log.debug("Admin fetching district statistics");
        
        DistrictService.DistrictStats stats = districtService.getDistrictStats();
        
        return ResponseEntity.ok(new ResponseWrapper<>(
                true,
                HttpStatus.OK.value(),
                "District statistics retrieved successfully",
                stats
        ));
    }

    /**
     * Get district statistics by region UID
     */
    @GetMapping("/region/uid/{regionUid}/stats")
    public ResponseEntity<ResponseWrapper<DistrictService.DistrictStats>> getDistrictStatsByRegionUid(
            @PathVariable String regionUid) {
        log.debug("Admin fetching district statistics for region UID: {}", regionUid);
        
        DistrictService.DistrictStats stats = districtService.getDistrictStatsByRegionUid(regionUid);
        
        return ResponseEntity.ok(new ResponseWrapper<>(
                true,
                HttpStatus.OK.value(),
                "District statistics retrieved successfully",
                stats
        ));
    }
}
