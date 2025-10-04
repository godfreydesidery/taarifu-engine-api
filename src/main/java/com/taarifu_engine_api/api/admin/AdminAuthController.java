package com.taarifu_engine_api.api.admin;

import com.taarifu_engine_api.modules.auth.domain.dto.AuthRequestDto;
import com.taarifu_engine_api.modules.auth.domain.dto.AuthResponseDto;
import com.taarifu_engine_api.modules.auth.service.AuthService;
import com.taarifu_engine_api.modules.common.domain.util.ResponseWrapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AdminAuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ResponseWrapper<AuthResponseDto>> login(@Valid @RequestBody AuthRequestDto request) {
        log.info("Admin login attempt for username/email: {}", request.getUsernameOrEmail());
        
        // Use AuthService to handle authentication - exceptions will be handled by GlobalExceptionHandler
        AuthResponseDto authResponse = authService.authenticateAdmin(request);
        
        ResponseWrapper<AuthResponseDto> response = new ResponseWrapper<>(
                true, 200, "Login successful", authResponse
        );
        
        return ResponseEntity.ok(response);
    }
}
