package com.taarifu_engine_api.api.mob;

import com.taarifu_engine_api.modules.auth.domain.dto.AuthRequestDto;
import com.taarifu_engine_api.modules.auth.domain.dto.AuthResponseDto;
import com.taarifu_engine_api.modules.auth.service.AuthService;
import com.taarifu_engine_api.modules.common.exception.ApiException;
import com.taarifu_engine_api.modules.common.domain.util.ResponseWrapper;
import com.taarifu_engine_api.modules.userandrole.domain.enums.UserType;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mob/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class MobAuthController {
    
    private final AuthService authService;
    
    /**
     * Login endpoint for mobile users (citizens)
     * Accepts username/email and password for authentication
     */
    @PostMapping("/login")
    public ResponseEntity<ResponseWrapper<AuthResponseDto>> login(@Valid @RequestBody AuthRequestDto request) {
        log.info("Mobile login attempt for: {}", request.getUsernameOrEmail());
        
        try {
            // Authenticate user (citizens are USER type)
            AuthResponseDto authResponse = authService.authenticateUser(request, UserType.USER);
            
            ResponseWrapper<AuthResponseDto> response = new ResponseWrapper<>(
                    true,
                    HttpStatus.OK.value(),
                    "Login successful",
                    authResponse
            );
            
            log.info("Mobile login successful for: {}", request.getUsernameOrEmail());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Mobile login failed for: {}", request.getUsernameOrEmail(), e);
            
            // Handle specific authentication errors
            if (e.getMessage().contains("Invalid credentials")) {
                throw new ApiException("Invalid username/email or password", HttpStatus.UNAUTHORIZED);
            } else if (e.getMessage().contains("Account is inactive")) {
                throw new ApiException("Account is inactive. Please contact support.", HttpStatus.FORBIDDEN);
            } else if (e.getMessage().contains("Invalid user type")) {
                throw new ApiException("Access denied. Invalid user type for mobile access.", HttpStatus.FORBIDDEN);
            } else {
                throw new ApiException("Login failed: " + e.getMessage(), HttpStatus.UNAUTHORIZED);
            }
        }
    }
    
    /**
     * Logout endpoint for mobile users
     * Invalidates the current session/token
     */
    @PostMapping("/logout")
    public ResponseEntity<ResponseWrapper<String>> logout(@RequestHeader("Authorization") String authorizationHeader) {
        log.info("Mobile logout request");
        
        try {
            // Extract token from Authorization header (Bearer token)
            String token = extractTokenFromHeader(authorizationHeader);
            authService.logout(token);
            
            ResponseWrapper<String> response = new ResponseWrapper<>(
                    true,
                    HttpStatus.OK.value(),
                    "Logout successful",
                    "Logout successful"
            );
            
            log.info("Mobile logout successful");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Mobile logout failed", e);
            throw new ApiException("Logout failed: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Refresh token endpoint for mobile users
     * Generates new access token using refresh token
     */
    @PostMapping("/refresh")
    public ResponseEntity<ResponseWrapper<AuthResponseDto>> refreshToken(@RequestBody RefreshTokenRequest request) {
        log.info("Mobile token refresh request");
        
        try {
            AuthResponseDto authResponse = authService.refreshToken(request.getRefreshToken());
            
            ResponseWrapper<AuthResponseDto> response = new ResponseWrapper<>(
                    true,
                    HttpStatus.OK.value(),
                    "Token refresh successful",
                    authResponse
            );
            
            log.info("Mobile token refresh successful");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Mobile token refresh failed", e);
            throw new ApiException("Token refresh failed: " + e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }
    
    /**
     * Extract JWT token from Authorization header
     */
    private String extractTokenFromHeader(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        throw new ApiException("Invalid authorization header", HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Simple DTO for refresh token requests
     */
    public static class RefreshTokenRequest {
        private String refreshToken;
        
        public String getRefreshToken() {
            return refreshToken;
        }
        
        public void setRefreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
        }
    }
}
