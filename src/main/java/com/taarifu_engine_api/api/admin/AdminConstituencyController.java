package com.taarifu_engine_api.api.admin;

import com.taarifu_engine_api.modules.common.domain.util.ResponseWrapper;
import com.taarifu_engine_api.modules.common.domain.util.PageResponseWrapper;
import com.taarifu_engine_api.modules.location.constituency.domain.dto.CreateConstituencyRequest;
import com.taarifu_engine_api.modules.location.constituency.domain.dto.ConstituencyResponse;
import com.taarifu_engine_api.modules.location.constituency.domain.dto.UpdateConstituencyRequest;
import com.taarifu_engine_api.modules.location.constituency.service.ConstituencyService;
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
 * REST controller for Constituency management operations
 * Accessible only to admin users
 */
@RestController
@RequestMapping("/admin/v1/constituencies")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class AdminConstituencyController {

    private final ConstituencyService constituencyService;

    /**
     * Create a new constituency
     */
    @PostMapping
    public ResponseEntity<ResponseWrapper<ConstituencyResponse>> createConstituency(
            @Valid @RequestBody CreateConstituencyRequest request) {
        log.info("Admin creating new constituency: {}", request.getName());
        
        ConstituencyResponse response = constituencyService.createConstituency(request);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseWrapper<>(
                        true,
                        HttpStatus.CREATED.value(),
                        "Constituency created successfully",
                        response
                ));
    }

    /**
     * Update an existing constituency by UID
     */
    @PutMapping("/uid/{uid}")
    public ResponseEntity<ResponseWrapper<ConstituencyResponse>> updateConstituency(
            @PathVariable String uid,
            @Valid @RequestBody UpdateConstituencyRequest request) {
        log.info("Admin updating constituency with UID: {}", uid);
        
        ConstituencyResponse response = constituencyService.updateConstituencyByUid(uid, request);
        
        return ResponseEntity.ok(new ResponseWrapper<>(
                true,
                HttpStatus.OK.value(),
                "Constituency updated successfully",
                response
        ));
    }

    /**
     * Get constituency by UID
     */
    @GetMapping("/uid/{uid}")
    public ResponseEntity<ResponseWrapper<ConstituencyResponse>> getConstituencyByUid(@PathVariable String uid) {
        log.debug("Admin fetching constituency with UID: {}", uid);
        
        ConstituencyResponse response = constituencyService.getConstituencyByUid(uid);
        
        return ResponseEntity.ok(new ResponseWrapper<>(
                true,
                HttpStatus.OK.value(),
                "Constituency retrieved successfully",
                response
        ));
    }

    /**
     * Get constituency by code
     */
    @GetMapping("/code/{code}")
    public ResponseEntity<ResponseWrapper<ConstituencyResponse>> getConstituencyByCode(@PathVariable String code) {
        log.debug("Admin fetching constituency with code: {}", code);
        
        ConstituencyResponse response = constituencyService.getConstituencyByCode(code);
        
        return ResponseEntity.ok(new ResponseWrapper<>(
                true,
                HttpStatus.OK.value(),
                "Constituency retrieved successfully",
                response
        ));
    }

    /**
     * Get all constituencies with pagination
     */
    @GetMapping
    public ResponseEntity<PageResponseWrapper<ConstituencyResponse>> getAllConstituencies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        log.debug("Admin fetching all constituencies - page: {}, size: {}", page, size);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") 
                ? Sort.by(sortBy).descending() 
                : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ConstituencyResponse> response = constituencyService.getAllConstituencies(pageable);
        
        return ResponseEntity.ok(PageResponseWrapper.fromPage(response, "Constituencies retrieved successfully"));
    }

    /**
     * Get all active constituencies
     */
    @GetMapping("/active")
    public ResponseEntity<PageResponseWrapper<ConstituencyResponse>> getActiveConstituencies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        log.debug("Admin fetching active constituencies - page: {}, size: {}", page, size);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") 
                ? Sort.by(sortBy).descending() 
                : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ConstituencyResponse> response = constituencyService.getActiveConstituencies(pageable);
        
        return ResponseEntity.ok(PageResponseWrapper.fromPage(response, "Active constituencies retrieved successfully"));
    }

    /**
     * Get constituencies by district UID
     */
    @GetMapping("/district/uid/{districtUid}")
    public ResponseEntity<PageResponseWrapper<ConstituencyResponse>> getConstituenciesByDistrictUid(
            @PathVariable String districtUid,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        log.debug("Admin fetching constituencies for district UID: {}", districtUid);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") 
                ? Sort.by(sortBy).descending() 
                : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ConstituencyResponse> response = constituencyService.getConstituenciesByDistrictUid(districtUid, pageable);
        
        return ResponseEntity.ok(PageResponseWrapper.fromPage(response, "Constituencies retrieved successfully"));
    }

    /**
     * Get active constituencies by district UID
     */
    @GetMapping("/district/uid/{districtUid}/active")
    public ResponseEntity<PageResponseWrapper<ConstituencyResponse>> getActiveConstituenciesByDistrictUid(
            @PathVariable String districtUid,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        log.debug("Admin fetching active constituencies for district UID: {}", districtUid);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") 
                ? Sort.by(sortBy).descending() 
                : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ConstituencyResponse> response = constituencyService.getActiveConstituenciesByDistrictUid(districtUid, pageable);
        
        return ResponseEntity.ok(PageResponseWrapper.fromPage(response, "Active constituencies retrieved successfully"));
    }

    /**
     * Search constituencies
     */
    @GetMapping("/search")
    public ResponseEntity<PageResponseWrapper<ConstituencyResponse>> searchConstituencies(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        log.debug("Admin searching constituencies with term: {}", q);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") 
                ? Sort.by(sortBy).descending() 
                : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ConstituencyResponse> response = constituencyService.searchConstituencies(q, pageable);
        
        return ResponseEntity.ok(PageResponseWrapper.fromPage(response, "Constituency search completed successfully"));
    }

    /**
     * Search constituencies by district UID
     */
    @GetMapping("/district/uid/{districtUid}/search")
    public ResponseEntity<PageResponseWrapper<ConstituencyResponse>> searchConstituenciesByDistrictUid(
            @PathVariable String districtUid,
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        log.debug("Admin searching constituencies for district UID: {} with term: {}", districtUid, q);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") 
                ? Sort.by(sortBy).descending() 
                : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ConstituencyResponse> response = constituencyService.searchConstituenciesByDistrictUid(districtUid, q, pageable);
        
        return ResponseEntity.ok(PageResponseWrapper.fromPage(response, "Constituency search completed successfully"));
    }

    /**
     * Get constituencies by status
     */
    @GetMapping("/status/{isActive}")
    public ResponseEntity<PageResponseWrapper<ConstituencyResponse>> getConstituenciesByStatus(
            @PathVariable Boolean isActive,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        log.debug("Admin fetching constituencies by status: {}", isActive);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") 
                ? Sort.by(sortBy).descending() 
                : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ConstituencyResponse> response = constituencyService.getConstituenciesByStatus(isActive, pageable);
        
        return ResponseEntity.ok(PageResponseWrapper.fromPage(response, "Constituencies retrieved successfully"));
    }

    /**
     * Toggle constituency status (activate/deactivate)
     */
    @PatchMapping("/uid/{uid}/toggle-status")
    public ResponseEntity<ResponseWrapper<ConstituencyResponse>> toggleConstituencyStatus(@PathVariable String uid) {
        log.info("Admin toggling status for constituency with UID: {}", uid);
        
        ConstituencyResponse response = constituencyService.toggleConstituencyStatusByUid(uid);
        
        return ResponseEntity.ok(new ResponseWrapper<>(
                true,
                HttpStatus.OK.value(),
                "Constituency status toggled successfully",
                response
        ));
    }

    /**
     * Soft delete constituency by UID
     */
    @DeleteMapping("/uid/{uid}")
    public ResponseEntity<ResponseWrapper<Void>> deleteConstituency(@PathVariable String uid) {
        log.info("Admin soft deleting constituency with UID: {}", uid);
        
        constituencyService.deleteConstituencyByUid(uid);
        
        return ResponseEntity.ok(new ResponseWrapper<>(
                true,
                HttpStatus.OK.value(),
                "Constituency deleted successfully",
                null
        ));
    }

    /**
     * Get constituency statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<ResponseWrapper<ConstituencyService.ConstituencyStats>> getConstituencyStats() {
        log.debug("Admin fetching constituency statistics");
        
        ConstituencyService.ConstituencyStats stats = constituencyService.getConstituencyStats();
        
        return ResponseEntity.ok(new ResponseWrapper<>(
                true,
                HttpStatus.OK.value(),
                "Constituency statistics retrieved successfully",
                stats
        ));
    }

    /**
     * Get constituency statistics by district UID
     */
    @GetMapping("/district/uid/{districtUid}/stats")
    public ResponseEntity<ResponseWrapper<ConstituencyService.ConstituencyStats>> getConstituencyStatsByDistrictUid(
            @PathVariable String districtUid) {
        log.debug("Admin fetching constituency statistics for district UID: {}", districtUid);
        
        ConstituencyService.ConstituencyStats stats = constituencyService.getConstituencyStatsByDistrictUid(districtUid);
        
        return ResponseEntity.ok(new ResponseWrapper<>(
                true,
                HttpStatus.OK.value(),
                "Constituency statistics retrieved successfully",
                stats
        ));
    }
}
