package com.taarifu_engine_api.api.admin;

import com.taarifu_engine_api.modules.common.domain.util.ResponseWrapper;
import com.taarifu_engine_api.modules.common.domain.util.PageResponseWrapper;
import com.taarifu_engine_api.modules.location.hamlet.domain.dto.CreateHamletRequest;
import com.taarifu_engine_api.modules.location.hamlet.domain.dto.HamletResponse;
import com.taarifu_engine_api.modules.location.hamlet.domain.dto.UpdateHamletRequest;
import com.taarifu_engine_api.modules.location.hamlet.service.HamletService;
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
 * REST controller for Hamlet management operations
 * Accessible only to admin users
 */
@RestController
@RequestMapping("/admin/v1/hamlets")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class AdminHamletController {

    private final HamletService hamletService;

    /**
     * Create a new hamlet
     */
    @PostMapping
    public ResponseEntity<ResponseWrapper<HamletResponse>> createHamlet(
            @Valid @RequestBody CreateHamletRequest request) {
        log.info("Admin creating new hamlet: {}", request.getName());
        
        HamletResponse response = hamletService.createHamlet(request);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseWrapper<>(
                        true,
                        HttpStatus.CREATED.value(),
                        "Hamlet created successfully",
                        response
                ));
    }

    /**
     * Update an existing hamlet by UID
     */
    @PutMapping("/uid/{uid}")
    public ResponseEntity<ResponseWrapper<HamletResponse>> updateHamlet(
            @PathVariable String uid,
            @Valid @RequestBody UpdateHamletRequest request) {
        log.info("Admin updating hamlet with UID: {}", uid);
        
        HamletResponse response = hamletService.updateHamletByUid(uid, request);
        
        return ResponseEntity.ok(new ResponseWrapper<>(
                true,
                HttpStatus.OK.value(),
                "Hamlet updated successfully",
                response
        ));
    }

    /**
     * Get hamlet by UID
     */
    @GetMapping("/uid/{uid}")
    public ResponseEntity<ResponseWrapper<HamletResponse>> getHamletByUid(@PathVariable String uid) {
        log.debug("Admin fetching hamlet with UID: {}", uid);
        
        HamletResponse response = hamletService.getHamletByUid(uid);
        
        return ResponseEntity.ok(new ResponseWrapper<>(
                true,
                HttpStatus.OK.value(),
                "Hamlet retrieved successfully",
                response
        ));
    }

    /**
     * Get hamlet by code
     */
    @GetMapping("/code/{code}")
    public ResponseEntity<ResponseWrapper<HamletResponse>> getHamletByCode(@PathVariable String code) {
        log.debug("Admin fetching hamlet with code: {}", code);
        
        HamletResponse response = hamletService.getHamletByCode(code);
        
        return ResponseEntity.ok(new ResponseWrapper<>(
                true,
                HttpStatus.OK.value(),
                "Hamlet retrieved successfully",
                response
        ));
    }

    /**
     * Get all hamlets with pagination
     */
    @GetMapping
    public ResponseEntity<PageResponseWrapper<HamletResponse>> getAllHamlets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        log.debug("Admin fetching all hamlets - page: {}, size: {}", page, size);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") 
                ? Sort.by(sortBy).descending() 
                : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<HamletResponse> response = hamletService.getAllHamlets(pageable);
        
        return ResponseEntity.ok(PageResponseWrapper.fromPage(response, "Hamlets retrieved successfully"));
    }

    /**
     * Get all active hamlets
     */
    @GetMapping("/active")
    public ResponseEntity<PageResponseWrapper<HamletResponse>> getActiveHamlets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        log.debug("Admin fetching active hamlets - page: {}, size: {}", page, size);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") 
                ? Sort.by(sortBy).descending() 
                : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<HamletResponse> response = hamletService.getActiveHamlets(pageable);
        
        return ResponseEntity.ok(PageResponseWrapper.fromPage(response, "Active hamlets retrieved successfully"));
    }

    /**
     * Get hamlets by village UID
     */
    @GetMapping("/village/uid/{villageUid}")
    public ResponseEntity<PageResponseWrapper<HamletResponse>> getHamletsByVillageUid(
            @PathVariable String villageUid,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        log.debug("Admin fetching hamlets for village UID: {}", villageUid);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") 
                ? Sort.by(sortBy).descending() 
                : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<HamletResponse> response = hamletService.getHamletsByVillageUid(villageUid, pageable);
        
        return ResponseEntity.ok(PageResponseWrapper.fromPage(response, "Hamlets retrieved successfully"));
    }

    /**
     * Get active hamlets by village UID
     */
    @GetMapping("/village/uid/{villageUid}/active")
    public ResponseEntity<PageResponseWrapper<HamletResponse>> getActiveHamletsByVillageUid(
            @PathVariable String villageUid,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        log.debug("Admin fetching active hamlets for village UID: {}", villageUid);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") 
                ? Sort.by(sortBy).descending() 
                : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<HamletResponse> response = hamletService.getActiveHamletsByVillageUid(villageUid, pageable);
        
        return ResponseEntity.ok(PageResponseWrapper.fromPage(response, "Active hamlets retrieved successfully"));
    }

    /**
     * Search hamlets
     */
    @GetMapping("/search")
    public ResponseEntity<PageResponseWrapper<HamletResponse>> searchHamlets(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        log.debug("Admin searching hamlets with term: {}", q);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") 
                ? Sort.by(sortBy).descending() 
                : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<HamletResponse> response = hamletService.searchHamlets(q, pageable);
        
        return ResponseEntity.ok(PageResponseWrapper.fromPage(response, "Hamlet search completed successfully"));
    }

    /**
     * Search hamlets by village UID
     */
    @GetMapping("/village/uid/{villageUid}/search")
    public ResponseEntity<PageResponseWrapper<HamletResponse>> searchHamletsByVillageUid(
            @PathVariable String villageUid,
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        log.debug("Admin searching hamlets for village UID: {} with term: {}", villageUid, q);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") 
                ? Sort.by(sortBy).descending() 
                : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<HamletResponse> response = hamletService.searchHamletsByVillageUid(villageUid, q, pageable);
        
        return ResponseEntity.ok(PageResponseWrapper.fromPage(response, "Hamlet search completed successfully"));
    }

    /**
     * Get hamlets by status
     */
    @GetMapping("/status/{isActive}")
    public ResponseEntity<PageResponseWrapper<HamletResponse>> getHamletsByStatus(
            @PathVariable Boolean isActive,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        log.debug("Admin fetching hamlets by status: {}", isActive);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") 
                ? Sort.by(sortBy).descending() 
                : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<HamletResponse> response = hamletService.getHamletsByStatus(isActive, pageable);
        
        return ResponseEntity.ok(PageResponseWrapper.fromPage(response, "Hamlets retrieved successfully"));
    }

    /**
     * Toggle hamlet status (activate/deactivate)
     */
    @PatchMapping("/uid/{uid}/toggle-status")
    public ResponseEntity<ResponseWrapper<HamletResponse>> toggleHamletStatus(@PathVariable String uid) {
        log.info("Admin toggling status for hamlet with UID: {}", uid);
        
        HamletResponse response = hamletService.toggleHamletStatusByUid(uid);
        
        return ResponseEntity.ok(new ResponseWrapper<>(
                true,
                HttpStatus.OK.value(),
                "Hamlet status toggled successfully",
                response
        ));
    }

    /**
     * Soft delete hamlet by UID
     */
    @DeleteMapping("/uid/{uid}")
    public ResponseEntity<ResponseWrapper<Void>> deleteHamlet(@PathVariable String uid) {
        log.info("Admin soft deleting hamlet with UID: {}", uid);
        
        hamletService.deleteHamletByUid(uid);
        
        return ResponseEntity.ok(new ResponseWrapper<>(
                true,
                HttpStatus.OK.value(),
                "Hamlet deleted successfully",
                null
        ));
    }

    /**
     * Get hamlet statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<ResponseWrapper<HamletService.HamletStats>> getHamletStats() {
        log.debug("Admin fetching hamlet statistics");
        
        HamletService.HamletStats stats = hamletService.getHamletStats();
        
        return ResponseEntity.ok(new ResponseWrapper<>(
                true,
                HttpStatus.OK.value(),
                "Hamlet statistics retrieved successfully",
                stats
        ));
    }

    /**
     * Get hamlet statistics by village UID
     */
    @GetMapping("/village/uid/{villageUid}/stats")
    public ResponseEntity<ResponseWrapper<HamletService.HamletStats>> getHamletStatsByVillageUid(
            @PathVariable String villageUid) {
        log.debug("Admin fetching hamlet statistics for village UID: {}", villageUid);
        
        HamletService.HamletStats stats = hamletService.getHamletStatsByVillageUid(villageUid);
        
        return ResponseEntity.ok(new ResponseWrapper<>(
                true,
                HttpStatus.OK.value(),
                "Hamlet statistics retrieved successfully",
                stats
        ));
    }
}
