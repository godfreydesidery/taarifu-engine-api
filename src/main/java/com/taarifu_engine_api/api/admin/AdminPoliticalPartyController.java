package com.taarifu_engine_api.api.admin;

import com.taarifu_engine_api.modules.common.domain.util.PageResponseWrapper;
import com.taarifu_engine_api.modules.common.domain.util.ResponseWrapper;
import com.taarifu_engine_api.modules.politicalparty.domain.dto.CreatePoliticalPartyRequestDto;
import com.taarifu_engine_api.modules.politicalparty.domain.dto.PoliticalPartyResponseDto;
import com.taarifu_engine_api.modules.politicalparty.domain.dto.UpdatePoliticalPartyRequestDto;
import com.taarifu_engine_api.modules.politicalparty.service.PoliticalPartyService;
import com.taarifu_engine_api.modules.politicalparty.service.PoliticalPartyService.PoliticalPartyStats;
import jakarta.validation.Valid;
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
 * Admin controller for Political Party management
 */
@RestController
@RequestMapping("/admin/v1/political-parties")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin(origins = "*")
public class AdminPoliticalPartyController {

    private final PoliticalPartyService politicalPartyService;

    /**
     * Create a new political party
     */
    @PostMapping
    public ResponseEntity<ResponseWrapper<PoliticalPartyResponseDto>> createPoliticalParty(
            @Valid @RequestBody CreatePoliticalPartyRequestDto request) {
        log.info("Admin creating political party: {}", request.getName());
        
        PoliticalPartyResponseDto response = politicalPartyService.createPoliticalParty(request);
        ResponseWrapper<PoliticalPartyResponseDto> wrapper = new ResponseWrapper<>(true, HttpStatus.CREATED.value(), "Political party created successfully", response);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(wrapper);
    }

    /**
     * Update political party by ID
     */
    @PutMapping("/{id}")
    public ResponseEntity<ResponseWrapper<PoliticalPartyResponseDto>> updatePoliticalParty(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePoliticalPartyRequestDto request) {
        log.info("Admin updating political party with ID: {}", id);
        
        PoliticalPartyResponseDto response = politicalPartyService.updatePoliticalParty(id, request);
        ResponseWrapper<PoliticalPartyResponseDto> wrapper = new ResponseWrapper<>(true, HttpStatus.OK.value(), "Political party updated successfully", response);
        
        return ResponseEntity.ok(wrapper);
    }

    /**
     * Update political party by UID
     */
    @PutMapping("/uid/{uid}")
    public ResponseEntity<ResponseWrapper<PoliticalPartyResponseDto>> updatePoliticalPartyByUid(
            @PathVariable String uid,
            @Valid @RequestBody UpdatePoliticalPartyRequestDto request) {
        log.info("Admin updating political party with UID: {}", uid);
        
        PoliticalPartyResponseDto response = politicalPartyService.updatePoliticalPartyByUid(uid, request);
        ResponseWrapper<PoliticalPartyResponseDto> wrapper = new ResponseWrapper<>(true, HttpStatus.OK.value(), "Political party updated successfully", response);
        
        return ResponseEntity.ok(wrapper);
    }

    /**
     * Get political party by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ResponseWrapper<PoliticalPartyResponseDto>> getPoliticalPartyById(@PathVariable Long id) {
        log.debug("Admin fetching political party with ID: {}", id);
        
        PoliticalPartyResponseDto response = politicalPartyService.getPoliticalPartyById(id);
        ResponseWrapper<PoliticalPartyResponseDto> wrapper = new ResponseWrapper<>(true, HttpStatus.OK.value(), "Political party fetched successfully", response);
        
        return ResponseEntity.ok(wrapper);
    }

    /**
     * Get political party by UID
     */
    @GetMapping("/uid/{uid}")
    public ResponseEntity<ResponseWrapper<PoliticalPartyResponseDto>> getPoliticalPartyByUid(@PathVariable String uid) {
        log.debug("Admin fetching political party with UID: {}", uid);
        
        PoliticalPartyResponseDto response = politicalPartyService.getPoliticalPartyByUid(uid);
        ResponseWrapper<PoliticalPartyResponseDto> wrapper = new ResponseWrapper<>(true, HttpStatus.OK.value(), "Political party fetched successfully", response);
        
        return ResponseEntity.ok(wrapper);
    }

    /**
     * Get political party by code
     */
    @GetMapping("/code/{code}")
    public ResponseEntity<ResponseWrapper<PoliticalPartyResponseDto>> getPoliticalPartyByCode(@PathVariable String code) {
        log.debug("Admin fetching political party with code: {}", code);
        
        PoliticalPartyResponseDto response = politicalPartyService.getPoliticalPartyByCode(code);
        ResponseWrapper<PoliticalPartyResponseDto> wrapper = new ResponseWrapper<>(true, HttpStatus.OK.value(), "Political party fetched successfully", response);
        
        return ResponseEntity.ok(wrapper);
    }

    /**
     * Get political party by name
     */
    @GetMapping("/name/{name}")
    public ResponseEntity<ResponseWrapper<PoliticalPartyResponseDto>> getPoliticalPartyByName(@PathVariable String name) {
        log.debug("Admin fetching political party with name: {}", name);
        
        PoliticalPartyResponseDto response = politicalPartyService.getPoliticalPartyByName(name);
        ResponseWrapper<PoliticalPartyResponseDto> wrapper = new ResponseWrapper<>(true, HttpStatus.OK.value(), "Political party fetched successfully", response);
        
        return ResponseEntity.ok(wrapper);
    }

    /**
     * Get political party by abbreviation
     */
    @GetMapping("/abbreviation/{abbreviation}")
    public ResponseEntity<ResponseWrapper<PoliticalPartyResponseDto>> getPoliticalPartyByAbbreviation(@PathVariable String abbreviation) {
        log.debug("Admin fetching political party with abbreviation: {}", abbreviation);
        
        PoliticalPartyResponseDto response = politicalPartyService.getPoliticalPartyByAbbreviation(abbreviation);
        ResponseWrapper<PoliticalPartyResponseDto> wrapper = new ResponseWrapper<>(true, HttpStatus.OK.value(), "Political party fetched successfully", response);
        
        return ResponseEntity.ok(wrapper);
    }

    /**
     * Get all political parties with pagination
     */
    @GetMapping
    public ResponseEntity<PageResponseWrapper<PoliticalPartyResponseDto>> getAllPoliticalParties(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        log.debug("Admin fetching all political parties - page: {}, size: {}, sortBy: {}, sortDir: {}", page, size, sortBy, sortDir);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        PageResponseWrapper<PoliticalPartyResponseDto> response = politicalPartyService.getAllPoliticalParties(pageable);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get active political parties with pagination
     */
    @GetMapping("/active")
    public ResponseEntity<PageResponseWrapper<PoliticalPartyResponseDto>> getActivePoliticalParties(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        log.debug("Admin fetching active political parties - page: {}, size: {}, sortBy: {}, sortDir: {}", page, size, sortBy, sortDir);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        PageResponseWrapper<PoliticalPartyResponseDto> response = politicalPartyService.getActivePoliticalParties(pageable);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get registered political parties with pagination
     */
    @GetMapping("/registered")
    public ResponseEntity<PageResponseWrapper<PoliticalPartyResponseDto>> getRegisteredPoliticalParties(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        log.debug("Admin fetching registered political parties - page: {}, size: {}, sortBy: {}, sortDir: {}", page, size, sortBy, sortDir);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        PageResponseWrapper<PoliticalPartyResponseDto> response = politicalPartyService.getRegisteredPoliticalParties(pageable);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get operational political parties with pagination
     */
    @GetMapping("/operational")
    public ResponseEntity<PageResponseWrapper<PoliticalPartyResponseDto>> getOperationalPoliticalParties(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        log.debug("Admin fetching operational political parties - page: {}, size: {}, sortBy: {}, sortDir: {}", page, size, sortBy, sortDir);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        PageResponseWrapper<PoliticalPartyResponseDto> response = politicalPartyService.getOperationalPoliticalParties(pageable);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Search political parties with pagination
     */
    @GetMapping("/search")
    public ResponseEntity<PageResponseWrapper<PoliticalPartyResponseDto>> searchPoliticalParties(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        log.debug("Admin searching political parties with query: {} - page: {}, size: {}, sortBy: {}, sortDir: {}", q, page, size, sortBy, sortDir);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        PageResponseWrapper<PoliticalPartyResponseDto> response = politicalPartyService.searchPoliticalParties(q, pageable);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get political parties by founding year
     */
    @GetMapping("/founding-year/{year}")
    public ResponseEntity<PageResponseWrapper<PoliticalPartyResponseDto>> getPoliticalPartiesByFoundingYear(
            @PathVariable int year,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        log.debug("Admin fetching political parties by founding year: {} - page: {}, size: {}, sortBy: {}, sortDir: {}", year, page, size, sortBy, sortDir);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        PageResponseWrapper<PoliticalPartyResponseDto> response = politicalPartyService.getPoliticalPartiesByFoundingYear(year, pageable);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get political parties by ideology
     */
    @GetMapping("/ideology")
    public ResponseEntity<PageResponseWrapper<PoliticalPartyResponseDto>> getPoliticalPartiesByIdeology(
            @RequestParam String ideology,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        log.debug("Admin fetching political parties by ideology: {} - page: {}, size: {}, sortBy: {}, sortDir: {}", ideology, page, size, sortBy, sortDir);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        PageResponseWrapper<PoliticalPartyResponseDto> response = politicalPartyService.getPoliticalPartiesByIdeology(ideology, pageable);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Delete political party by ID (soft delete)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseWrapper<Void>> deletePoliticalParty(@PathVariable Long id) {
        log.info("Admin deleting political party with ID: {}", id);
        
        politicalPartyService.deletePoliticalParty(id);
        ResponseWrapper<Void> wrapper = new ResponseWrapper<>(true, HttpStatus.OK.value(), "Political party deleted successfully", null);
        
        return ResponseEntity.ok(wrapper);
    }

    /**
     * Delete political party by UID (soft delete)
     */
    @DeleteMapping("/uid/{uid}")
    public ResponseEntity<ResponseWrapper<Void>> deletePoliticalPartyByUid(@PathVariable String uid) {
        log.info("Admin deleting political party with UID: {}", uid);
        
        politicalPartyService.deletePoliticalPartyByUid(uid);
        ResponseWrapper<Void> wrapper = new ResponseWrapper<>(true, HttpStatus.OK.value(), "Political party deleted successfully", null);
        
        return ResponseEntity.ok(wrapper);
    }

    /**
     * Activate political party by ID
     */
    @PatchMapping("/{id}/activate")
    public ResponseEntity<ResponseWrapper<PoliticalPartyResponseDto>> activatePoliticalParty(@PathVariable Long id) {
        log.info("Admin activating political party with ID: {}", id);
        
        PoliticalPartyResponseDto response = politicalPartyService.activatePoliticalParty(id);
        ResponseWrapper<PoliticalPartyResponseDto> wrapper = new ResponseWrapper<>(true, HttpStatus.OK.value(), "Political party activated successfully", response);
        
        return ResponseEntity.ok(wrapper);
    }

    /**
     * Deactivate political party by ID
     */
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ResponseWrapper<PoliticalPartyResponseDto>> deactivatePoliticalParty(@PathVariable Long id) {
        log.info("Admin deactivating political party with ID: {}", id);
        
        PoliticalPartyResponseDto response = politicalPartyService.deactivatePoliticalParty(id);
        ResponseWrapper<PoliticalPartyResponseDto> wrapper = new ResponseWrapper<>(true, HttpStatus.OK.value(), "Political party deactivated successfully", response);
        
        return ResponseEntity.ok(wrapper);
    }

    /**
     * Register political party by ID
     */
    @PatchMapping("/{id}/register")
    public ResponseEntity<ResponseWrapper<PoliticalPartyResponseDto>> registerPoliticalParty(@PathVariable Long id) {
        log.info("Admin registering political party with ID: {}", id);
        
        PoliticalPartyResponseDto response = politicalPartyService.registerPoliticalParty(id);
        ResponseWrapper<PoliticalPartyResponseDto> wrapper = new ResponseWrapper<>(true, HttpStatus.OK.value(), "Political party registered successfully", response);
        
        return ResponseEntity.ok(wrapper);
    }

    /**
     * Deregister political party by ID
     */
    @PatchMapping("/{id}/deregister")
    public ResponseEntity<ResponseWrapper<PoliticalPartyResponseDto>> deregisterPoliticalParty(@PathVariable Long id) {
        log.info("Admin deregistering political party with ID: {}", id);
        
        PoliticalPartyResponseDto response = politicalPartyService.deregisterPoliticalParty(id);
        ResponseWrapper<PoliticalPartyResponseDto> wrapper = new ResponseWrapper<>(true, HttpStatus.OK.value(), "Political party deregistered successfully", response);
        
        return ResponseEntity.ok(wrapper);
    }

    /**
     * Get political party statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<ResponseWrapper<PoliticalPartyStats>> getPoliticalPartyStats() {
        log.debug("Admin fetching political party statistics");
        
        PoliticalPartyStats stats = politicalPartyService.getPoliticalPartyStats();
        ResponseWrapper<PoliticalPartyStats> wrapper = new ResponseWrapper<>(true, HttpStatus.OK.value(), "Political party statistics fetched successfully", stats);
        
        return ResponseEntity.ok(wrapper);
    }
}
