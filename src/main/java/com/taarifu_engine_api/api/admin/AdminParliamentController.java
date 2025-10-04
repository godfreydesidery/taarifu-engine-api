package com.taarifu_engine_api.api.admin;

import com.taarifu_engine_api.modules.common.domain.util.PageResponseWrapper;
import com.taarifu_engine_api.modules.common.domain.util.ResponseWrapper;
import com.taarifu_engine_api.modules.parliament.domain.dto.CreateParliamentRequestDto;
import com.taarifu_engine_api.modules.parliament.domain.dto.ParliamentResponseDto;
import com.taarifu_engine_api.modules.parliament.domain.dto.UpdateParliamentRequestDto;
import com.taarifu_engine_api.modules.parliament.service.ParliamentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Admin controller for Parliament management
 */
@RestController
@RequestMapping("/admin/v1/parliaments")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('ADMIN')")
public class AdminParliamentController {

    private final ParliamentService parliamentService;

    /**
     * Create a new parliament
     */
    @PostMapping
    public ResponseEntity<ResponseWrapper<ParliamentResponseDto>> createParliament(
            @RequestBody CreateParliamentRequestDto request) {
        
        log.info("Admin creating new parliament: {}", request.getName());

        ParliamentResponseDto parliament = parliamentService.createParliament(request);
        ResponseWrapper<ParliamentResponseDto> response = new ResponseWrapper<>(
                true, HttpStatus.CREATED.value(), "Parliament created successfully", parliament);

        log.info("Successfully created parliament with ID: {} for admin", parliament.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get all parliaments with pagination
     */
    @GetMapping
    public ResponseEntity<PageResponseWrapper<ParliamentResponseDto>> getAllParliaments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "startDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        log.info("Admin requesting all parliaments - page: {}, size: {}, sortBy: {}, sortDir: {}", 
                page, size, sortBy, sortDir);

        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        PageResponseWrapper<ParliamentResponseDto> response = parliamentService.getAllParliaments(pageable);
        
        log.info("Retrieved {} parliaments for admin", response.getData().size());
        return ResponseEntity.ok(response);
    }

    /**
     * Get parliament by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ResponseWrapper<ParliamentResponseDto>> getParliamentById(@PathVariable Long id) {
        log.info("Admin requesting parliament by ID: {}", id);

        ParliamentResponseDto parliament = parliamentService.getParliamentById(id);
        ResponseWrapper<ParliamentResponseDto> response = new ResponseWrapper<>(
                true, 200, "Parliament retrieved successfully", parliament);

        log.info("Retrieved parliament with ID: {} for admin", id);
        return ResponseEntity.ok(response);
    }

    /**
     * Get parliament by UID
     */
    @GetMapping("/uid/{uid}")
    public ResponseEntity<ResponseWrapper<ParliamentResponseDto>> getParliamentByUid(@PathVariable String uid) {
        log.info("Admin requesting parliament by UID: {}", uid);

        ParliamentResponseDto parliament = parliamentService.getParliamentByUid(uid);
        ResponseWrapper<ParliamentResponseDto> response = new ResponseWrapper<>(
                true, 200, "Parliament retrieved successfully", parliament);

        log.info("Retrieved parliament with UID: {} for admin", uid);
        return ResponseEntity.ok(response);
    }

    /**
     * Get parliament by code
     */
    @GetMapping("/code/{code}")
    public ResponseEntity<ResponseWrapper<ParliamentResponseDto>> getParliamentByCode(@PathVariable String code) {
        log.info("Admin requesting parliament by code: {}", code);

        ParliamentResponseDto parliament = parliamentService.getParliamentByCode(code);
        ResponseWrapper<ParliamentResponseDto> response = new ResponseWrapper<>(
                true, 200, "Parliament retrieved successfully", parliament);

        log.info("Retrieved parliament with code: {} for admin", code);
        return ResponseEntity.ok(response);
    }

    /**
     * Get current parliament
     */
    @GetMapping("/current")
    public ResponseEntity<ResponseWrapper<ParliamentResponseDto>> getCurrentParliament() {
        log.info("Admin requesting current parliament");

        ParliamentResponseDto parliament = parliamentService.getCurrentParliament();
        ResponseWrapper<ParliamentResponseDto> response = new ResponseWrapper<>(
                true, 200, "Current parliament retrieved successfully", parliament);

        log.info("Retrieved current parliament for admin");
        return ResponseEntity.ok(response);
    }

    /**
     * Get active parliaments with pagination
     */
    @GetMapping("/active")
    public ResponseEntity<PageResponseWrapper<ParliamentResponseDto>> getActiveParliaments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "startDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        log.info("Admin requesting active parliaments - page: {}, size: {}", page, size);

        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        PageResponseWrapper<ParliamentResponseDto> response = parliamentService.getActiveParliaments(pageable);
        
        log.info("Retrieved {} active parliaments for admin", response.getData().size());
        return ResponseEntity.ok(response);
    }

    /**
     * Search parliaments
     */
    @GetMapping("/search")
    public ResponseEntity<PageResponseWrapper<ParliamentResponseDto>> searchParliaments(
            @RequestParam String searchTerm,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "startDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        log.info("Admin searching parliaments with term: {} - page: {}, size: {}", searchTerm, page, size);

        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        PageResponseWrapper<ParliamentResponseDto> response = parliamentService.searchParliaments(searchTerm, pageable);
        
        log.info("Found {} parliaments matching search term: {} for admin", response.getData().size(), searchTerm);
        return ResponseEntity.ok(response);
    }

    /**
     * Update parliament by ID
     */
    @PutMapping("/{id}")
    public ResponseEntity<ResponseWrapper<ParliamentResponseDto>> updateParliament(
            @PathVariable Long id,
            @RequestBody UpdateParliamentRequestDto request) {
        
        log.info("Admin updating parliament with ID: {}", id);

        ParliamentResponseDto parliament = parliamentService.updateParliament(id, request);
        ResponseWrapper<ParliamentResponseDto> response = new ResponseWrapper<>(
                true, 200, "Parliament updated successfully", parliament);

        log.info("Successfully updated parliament with ID: {} for admin", id);
        return ResponseEntity.ok(response);
    }

    /**
     * Update parliament by UID
     */
    @PutMapping("/uid/{uid}")
    public ResponseEntity<ResponseWrapper<ParliamentResponseDto>> updateParliamentByUid(
            @PathVariable String uid,
            @RequestBody UpdateParliamentRequestDto request) {
        
        log.info("Admin updating parliament with UID: {}", uid);

        ParliamentResponseDto parliament = parliamentService.updateParliamentByUid(uid, request);
        ResponseWrapper<ParliamentResponseDto> response = new ResponseWrapper<>(
                true, 200, "Parliament updated successfully", parliament);

        log.info("Successfully updated parliament with UID: {} for admin", uid);
        return ResponseEntity.ok(response);
    }

    /**
     * Set parliament as current by ID
     */
    @PutMapping("/{id}/set-current")
    public ResponseEntity<ResponseWrapper<ParliamentResponseDto>> setCurrentParliament(@PathVariable Long id) {
        log.info("Admin setting parliament with ID: {} as current", id);

        ParliamentResponseDto parliament = parliamentService.setCurrentParliament(id);
        ResponseWrapper<ParliamentResponseDto> response = new ResponseWrapper<>(
                true, 200, "Parliament set as current successfully", parliament);

        log.info("Successfully set parliament with ID: {} as current for admin", id);
        return ResponseEntity.ok(response);
    }

    /**
     * Set parliament as current by UID
     */
    @PutMapping("/uid/{uid}/set-current")
    public ResponseEntity<ResponseWrapper<ParliamentResponseDto>> setCurrentParliamentByUid(@PathVariable String uid) {
        log.info("Admin setting parliament with UID: {} as current", uid);

        ParliamentResponseDto parliament = parliamentService.setCurrentParliamentByUid(uid);
        ResponseWrapper<ParliamentResponseDto> response = new ResponseWrapper<>(
                true, 200, "Parliament set as current successfully", parliament);

        log.info("Successfully set parliament with UID: {} as current for admin", uid);
        return ResponseEntity.ok(response);
    }

    /**
     * Delete parliament by ID (soft delete)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseWrapper<Void>> deleteParliament(@PathVariable Long id) {
        log.info("Admin deleting parliament with ID: {}", id);

        parliamentService.deleteParliament(id);
        ResponseWrapper<Void> response = new ResponseWrapper<>(
                true, 200, "Parliament deleted successfully", null);

        log.info("Successfully deleted parliament with ID: {} for admin", id);
        return ResponseEntity.ok(response);
    }

    /**
     * Delete parliament by UID (soft delete)
     */
    @DeleteMapping("/uid/{uid}")
    public ResponseEntity<ResponseWrapper<Void>> deleteParliamentByUid(@PathVariable String uid) {
        log.info("Admin deleting parliament with UID: {}", uid);

        parliamentService.deleteParliamentByUid(uid);
        ResponseWrapper<Void> response = new ResponseWrapper<>(
                true, 200, "Parliament deleted successfully", null);

        log.info("Successfully deleted parliament with UID: {} for admin", uid);
        return ResponseEntity.ok(response);
    }

    /**
     * Get parliament statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<ResponseWrapper<ParliamentService.ParliamentStats>> getParliamentStats() {
        log.info("Admin requesting parliament statistics");

        ParliamentService.ParliamentStats stats = parliamentService.getParliamentStats();
        ResponseWrapper<ParliamentService.ParliamentStats> response = new ResponseWrapper<>(
                true, 200, "Parliament statistics retrieved successfully", stats);

        log.info("Retrieved parliament statistics for admin - total: {}, active: {}, current: {}", 
                stats.totalParliaments(), stats.activeParliaments(), stats.currentParliaments());
        
        return ResponseEntity.ok(response);
    }
}
