package com.taarifu_engine_api.api.admin;

import com.taarifu_engine_api.modules.common.domain.util.ResponseWrapper;
import com.taarifu_engine_api.modules.common.domain.util.PageResponseWrapper;
import com.taarifu_engine_api.modules.location.village.domain.dto.CreateVillageRequest;
import com.taarifu_engine_api.modules.location.village.domain.dto.VillageResponse;
import com.taarifu_engine_api.modules.location.village.domain.dto.UpdateVillageRequest;
import com.taarifu_engine_api.modules.location.village.service.VillageService;
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
 * REST controller for Village management operations
 * Accessible only to admin users
 */
@RestController
@RequestMapping("/admin/v1/villages")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class AdminVillageController {

    private final VillageService villageService;

    /**
     * Create a new village
     */
    @PostMapping
    public ResponseEntity<ResponseWrapper<VillageResponse>> createVillage(
            @Valid @RequestBody CreateVillageRequest request) {
        log.info("Admin creating new village: {}", request.getName());
        
        VillageResponse response = villageService.createVillage(request);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseWrapper<>(
                        true,
                        HttpStatus.CREATED.value(),
                        "Village created successfully",
                        response
                ));
    }

    /**
     * Update an existing village by UID
     */
    @PutMapping("/uid/{uid}")
    public ResponseEntity<ResponseWrapper<VillageResponse>> updateVillage(
            @PathVariable String uid,
            @Valid @RequestBody UpdateVillageRequest request) {
        log.info("Admin updating village with UID: {}", uid);
        
        VillageResponse response = villageService.updateVillageByUid(uid, request);
        
        return ResponseEntity.ok(new ResponseWrapper<>(
                true,
                HttpStatus.OK.value(),
                "Village updated successfully",
                response
        ));
    }

    /**
     * Get village by UID
     */
    @GetMapping("/uid/{uid}")
    public ResponseEntity<ResponseWrapper<VillageResponse>> getVillageByUid(@PathVariable String uid) {
        log.debug("Admin fetching village with UID: {}", uid);
        
        VillageResponse response = villageService.getVillageByUid(uid);
        
        return ResponseEntity.ok(new ResponseWrapper<>(
                true,
                HttpStatus.OK.value(),
                "Village retrieved successfully",
                response
        ));
    }

    /**
     * Get village by code
     */
    @GetMapping("/code/{code}")
    public ResponseEntity<ResponseWrapper<VillageResponse>> getVillageByCode(@PathVariable String code) {
        log.debug("Admin fetching village with code: {}", code);
        
        VillageResponse response = villageService.getVillageByCode(code);
        
        return ResponseEntity.ok(new ResponseWrapper<>(
                true,
                HttpStatus.OK.value(),
                "Village retrieved successfully",
                response
        ));
    }

    /**
     * Get all villages with pagination
     */
    @GetMapping
    public ResponseEntity<PageResponseWrapper<VillageResponse>> getAllVillages(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        log.debug("Admin fetching all villages - page: {}, size: {}", page, size);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") 
                ? Sort.by(sortBy).descending() 
                : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<VillageResponse> response = villageService.getAllVillages(pageable);
        
        return ResponseEntity.ok(PageResponseWrapper.fromPage(response, "Villages retrieved successfully"));
    }

    /**
     * Get all active villages
     */
    @GetMapping("/active")
    public ResponseEntity<PageResponseWrapper<VillageResponse>> getActiveVillages(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        log.debug("Admin fetching active villages - page: {}, size: {}", page, size);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") 
                ? Sort.by(sortBy).descending() 
                : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<VillageResponse> response = villageService.getActiveVillages(pageable);
        
        return ResponseEntity.ok(PageResponseWrapper.fromPage(response, "Active villages retrieved successfully"));
    }

    /**
     * Get villages by ward UID
     */
    @GetMapping("/ward/uid/{wardUid}")
    public ResponseEntity<PageResponseWrapper<VillageResponse>> getVillagesByWardUid(
            @PathVariable String wardUid,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        log.debug("Admin fetching villages for ward UID: {}", wardUid);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") 
                ? Sort.by(sortBy).descending() 
                : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<VillageResponse> response = villageService.getVillagesByWardUid(wardUid, pageable);
        
        return ResponseEntity.ok(PageResponseWrapper.fromPage(response, "Villages retrieved successfully"));
    }

    /**
     * Get active villages by ward UID
     */
    @GetMapping("/ward/uid/{wardUid}/active")
    public ResponseEntity<PageResponseWrapper<VillageResponse>> getActiveVillagesByWardUid(
            @PathVariable String wardUid,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        log.debug("Admin fetching active villages for ward UID: {}", wardUid);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") 
                ? Sort.by(sortBy).descending() 
                : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<VillageResponse> response = villageService.getActiveVillagesByWardUid(wardUid, pageable);
        
        return ResponseEntity.ok(PageResponseWrapper.fromPage(response, "Active villages retrieved successfully"));
    }

    /**
     * Search villages
     */
    @GetMapping("/search")
    public ResponseEntity<PageResponseWrapper<VillageResponse>> searchVillages(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        log.debug("Admin searching villages with term: {}", q);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") 
                ? Sort.by(sortBy).descending() 
                : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<VillageResponse> response = villageService.searchVillages(q, pageable);
        
        return ResponseEntity.ok(PageResponseWrapper.fromPage(response, "Village search completed successfully"));
    }

    /**
     * Search villages by ward UID
     */
    @GetMapping("/ward/uid/{wardUid}/search")
    public ResponseEntity<PageResponseWrapper<VillageResponse>> searchVillagesByWardUid(
            @PathVariable String wardUid,
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        log.debug("Admin searching villages for ward UID: {} with term: {}", wardUid, q);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") 
                ? Sort.by(sortBy).descending() 
                : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<VillageResponse> response = villageService.searchVillagesByWardUid(wardUid, q, pageable);
        
        return ResponseEntity.ok(PageResponseWrapper.fromPage(response, "Village search completed successfully"));
    }

    /**
     * Get villages by status
     */
    @GetMapping("/status/{isActive}")
    public ResponseEntity<PageResponseWrapper<VillageResponse>> getVillagesByStatus(
            @PathVariable Boolean isActive,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        log.debug("Admin fetching villages by status: {}", isActive);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") 
                ? Sort.by(sortBy).descending() 
                : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<VillageResponse> response = villageService.getVillagesByStatus(isActive, pageable);
        
        return ResponseEntity.ok(PageResponseWrapper.fromPage(response, "Villages retrieved successfully"));
    }

    /**
     * Toggle village status (activate/deactivate)
     */
    @PatchMapping("/uid/{uid}/toggle-status")
    public ResponseEntity<ResponseWrapper<VillageResponse>> toggleVillageStatus(@PathVariable String uid) {
        log.info("Admin toggling status for village with UID: {}", uid);
        
        VillageResponse response = villageService.toggleVillageStatusByUid(uid);
        
        return ResponseEntity.ok(new ResponseWrapper<>(
                true,
                HttpStatus.OK.value(),
                "Village status toggled successfully",
                response
        ));
    }

    /**
     * Soft delete village by UID
     */
    @DeleteMapping("/uid/{uid}")
    public ResponseEntity<ResponseWrapper<Void>> deleteVillage(@PathVariable String uid) {
        log.info("Admin soft deleting village with UID: {}", uid);
        
        villageService.deleteVillageByUid(uid);
        
        return ResponseEntity.ok(new ResponseWrapper<>(
                true,
                HttpStatus.OK.value(),
                "Village deleted successfully",
                null
        ));
    }

    /**
     * Get village statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<ResponseWrapper<VillageService.VillageStats>> getVillageStats() {
        log.debug("Admin fetching village statistics");
        
        VillageService.VillageStats stats = villageService.getVillageStats();
        
        return ResponseEntity.ok(new ResponseWrapper<>(
                true,
                HttpStatus.OK.value(),
                "Village statistics retrieved successfully",
                stats
        ));
    }

    /**
     * Get village statistics by ward UID
     */
    @GetMapping("/ward/uid/{wardUid}/stats")
    public ResponseEntity<ResponseWrapper<VillageService.VillageStats>> getVillageStatsByWardUid(
            @PathVariable String wardUid) {
        log.debug("Admin fetching village statistics for ward UID: {}", wardUid);
        
        VillageService.VillageStats stats = villageService.getVillageStatsByWardUid(wardUid);
        
        return ResponseEntity.ok(new ResponseWrapper<>(
                true,
                HttpStatus.OK.value(),
                "Village statistics retrieved successfully",
                stats
        ));
    }
}
