package com.taarifu_engine_api.api.admin;

import com.taarifu_engine_api.modules.common.domain.util.ResponseWrapper;
import com.taarifu_engine_api.modules.common.domain.util.PageResponseWrapper;
import com.taarifu_engine_api.modules.location.ward.domain.dto.CreateWardRequest;
import com.taarifu_engine_api.modules.location.ward.domain.dto.WardResponse;
import com.taarifu_engine_api.modules.location.ward.domain.dto.UpdateWardRequest;
import com.taarifu_engine_api.modules.location.ward.service.WardService;
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
 * REST controller for Ward management operations
 * Accessible only to admin users
 */
@RestController
@RequestMapping("/admin/v1/wards")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class AdminWardController {

    private final WardService wardService;

    /**
     * Create a new ward
     */
    @PostMapping
    public ResponseEntity<ResponseWrapper<WardResponse>> createWard(
            @Valid @RequestBody CreateWardRequest request) {
        log.info("Admin creating new ward: {}", request.getName());
        
        WardResponse response = wardService.createWard(request);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseWrapper<>(
                        true,
                        HttpStatus.CREATED.value(),
                        "Ward created successfully",
                        response
                ));
    }

    /**
     * Update an existing ward by UID
     */
    @PutMapping("/uid/{uid}")
    public ResponseEntity<ResponseWrapper<WardResponse>> updateWard(
            @PathVariable String uid,
            @Valid @RequestBody UpdateWardRequest request) {
        log.info("Admin updating ward with UID: {}", uid);
        
        WardResponse response = wardService.updateWardByUid(uid, request);
        
        return ResponseEntity.ok(new ResponseWrapper<>(
                true,
                HttpStatus.OK.value(),
                "Ward updated successfully",
                response
        ));
    }

    /**
     * Get ward by UID
     */
    @GetMapping("/uid/{uid}")
    public ResponseEntity<ResponseWrapper<WardResponse>> getWardByUid(@PathVariable String uid) {
        log.debug("Admin fetching ward with UID: {}", uid);
        
        WardResponse response = wardService.getWardByUid(uid);
        
        return ResponseEntity.ok(new ResponseWrapper<>(
                true,
                HttpStatus.OK.value(),
                "Ward retrieved successfully",
                response
        ));
    }

    /**
     * Get ward by code
     */
    @GetMapping("/code/{code}")
    public ResponseEntity<ResponseWrapper<WardResponse>> getWardByCode(@PathVariable String code) {
        log.debug("Admin fetching ward with code: {}", code);
        
        WardResponse response = wardService.getWardByCode(code);
        
        return ResponseEntity.ok(new ResponseWrapper<>(
                true,
                HttpStatus.OK.value(),
                "Ward retrieved successfully",
                response
        ));
    }

    /**
     * Get all wards with pagination
     */
    @GetMapping
    public ResponseEntity<PageResponseWrapper<WardResponse>> getAllWards(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        log.debug("Admin fetching all wards - page: {}, size: {}", page, size);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") 
                ? Sort.by(sortBy).descending() 
                : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<WardResponse> response = wardService.getAllWards(pageable);
        
        return ResponseEntity.ok(PageResponseWrapper.fromPage(response, "Wards retrieved successfully"));
    }

    /**
     * Get all active wards
     */
    @GetMapping("/active")
    public ResponseEntity<PageResponseWrapper<WardResponse>> getActiveWards(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        log.debug("Admin fetching active wards - page: {}, size: {}", page, size);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") 
                ? Sort.by(sortBy).descending() 
                : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<WardResponse> response = wardService.getActiveWards(pageable);
        
        return ResponseEntity.ok(PageResponseWrapper.fromPage(response, "Active wards retrieved successfully"));
    }

    /**
     * Get wards by district UID
     */
    @GetMapping("/district/uid/{districtUid}")
    public ResponseEntity<PageResponseWrapper<WardResponse>> getWardsByDistrictUid(
            @PathVariable String districtUid,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        log.debug("Admin fetching wards for district UID: {}", districtUid);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") 
                ? Sort.by(sortBy).descending() 
                : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<WardResponse> response = wardService.getWardsByDistrictUid(districtUid, pageable);
        
        return ResponseEntity.ok(PageResponseWrapper.fromPage(response, "Wards retrieved successfully"));
    }

    /**
     * Get active wards by district UID
     */
    @GetMapping("/district/uid/{districtUid}/active")
    public ResponseEntity<PageResponseWrapper<WardResponse>> getActiveWardsByDistrictUid(
            @PathVariable String districtUid,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        log.debug("Admin fetching active wards for district UID: {}", districtUid);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") 
                ? Sort.by(sortBy).descending() 
                : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<WardResponse> response = wardService.getActiveWardsByDistrictUid(districtUid, pageable);
        
        return ResponseEntity.ok(PageResponseWrapper.fromPage(response, "Active wards retrieved successfully"));
    }

    /**
     * Search wards
     */
    @GetMapping("/search")
    public ResponseEntity<PageResponseWrapper<WardResponse>> searchWards(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        log.debug("Admin searching wards with term: {}", q);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") 
                ? Sort.by(sortBy).descending() 
                : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<WardResponse> response = wardService.searchWards(q, pageable);
        
        return ResponseEntity.ok(PageResponseWrapper.fromPage(response, "Ward search completed successfully"));
    }

    /**
     * Search wards by district UID
     */
    @GetMapping("/district/uid/{districtUid}/search")
    public ResponseEntity<PageResponseWrapper<WardResponse>> searchWardsByDistrictUid(
            @PathVariable String districtUid,
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        log.debug("Admin searching wards for district UID: {} with term: {}", districtUid, q);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") 
                ? Sort.by(sortBy).descending() 
                : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<WardResponse> response = wardService.searchWardsByDistrictUid(districtUid, q, pageable);
        
        return ResponseEntity.ok(PageResponseWrapper.fromPage(response, "Ward search completed successfully"));
    }

    /**
     * Get wards by status
     */
    @GetMapping("/status/{isActive}")
    public ResponseEntity<PageResponseWrapper<WardResponse>> getWardsByStatus(
            @PathVariable Boolean isActive,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        log.debug("Admin fetching wards by status: {}", isActive);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") 
                ? Sort.by(sortBy).descending() 
                : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<WardResponse> response = wardService.getWardsByStatus(isActive, pageable);
        
        return ResponseEntity.ok(PageResponseWrapper.fromPage(response, "Wards retrieved successfully"));
    }

    /**
     * Toggle ward status (activate/deactivate)
     */
    @PatchMapping("/uid/{uid}/toggle-status")
    public ResponseEntity<ResponseWrapper<WardResponse>> toggleWardStatus(@PathVariable String uid) {
        log.info("Admin toggling status for ward with UID: {}", uid);
        
        WardResponse response = wardService.toggleWardStatusByUid(uid);
        
        return ResponseEntity.ok(new ResponseWrapper<>(
                true,
                HttpStatus.OK.value(),
                "Ward status toggled successfully",
                response
        ));
    }

    /**
     * Soft delete ward by UID
     */
    @DeleteMapping("/uid/{uid}")
    public ResponseEntity<ResponseWrapper<Void>> deleteWard(@PathVariable String uid) {
        log.info("Admin soft deleting ward with UID: {}", uid);
        
        wardService.deleteWardByUid(uid);
        
        return ResponseEntity.ok(new ResponseWrapper<>(
                true,
                HttpStatus.OK.value(),
                "Ward deleted successfully",
                null
        ));
    }

    /**
     * Get ward statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<ResponseWrapper<WardService.WardStats>> getWardStats() {
        log.debug("Admin fetching ward statistics");
        
        WardService.WardStats stats = wardService.getWardStats();
        
        return ResponseEntity.ok(new ResponseWrapper<>(
                true,
                HttpStatus.OK.value(),
                "Ward statistics retrieved successfully",
                stats
        ));
    }

    /**
     * Get ward statistics by district UID
     */
    @GetMapping("/district/uid/{districtUid}/stats")
    public ResponseEntity<ResponseWrapper<WardService.WardStats>> getWardStatsByDistrictUid(
            @PathVariable String districtUid) {
        log.debug("Admin fetching ward statistics for district UID: {}", districtUid);
        
        WardService.WardStats stats = wardService.getWardStatsByDistrictUid(districtUid);
        
        return ResponseEntity.ok(new ResponseWrapper<>(
                true,
                HttpStatus.OK.value(),
                "Ward statistics retrieved successfully",
                stats
        ));
    }
}
