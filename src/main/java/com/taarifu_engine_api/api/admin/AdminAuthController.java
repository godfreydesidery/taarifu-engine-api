package com.taarifu_engine_api.api.admin;

import com.taarifu_engine_api.modules.auth.domain.dto.AuthRequestDto;
import com.taarifu_engine_api.modules.auth.domain.dto.AuthResponseDto;
import com.taarifu_engine_api.modules.auth.domain.dto.ForgotPasswordRequestDto;
import com.taarifu_engine_api.modules.auth.domain.dto.ForgotPasswordResponseDto;
import com.taarifu_engine_api.modules.auth.domain.dto.ResetPasswordWithTokenDto;
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

    /**
     * Initiates a password reset process by sending a reset token via email.
     *
     * @param request the forgot password request containing email
     * @return response indicating success and next steps
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<ResponseWrapper<ForgotPasswordResponseDto>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequestDto request) {
        log.info("Forgot password request for email: {}", request.getEmail());
        
        ForgotPasswordResponseDto response = authService.forgotPassword(request);
        
        ResponseWrapper<ForgotPasswordResponseDto> wrapper = new ResponseWrapper<>(
                true, 200, "Password reset instructions sent", response
        );
        
        return ResponseEntity.ok(wrapper);
    }

    /**
     * Resets a user's password using a valid reset token.
     *
     * @param request the reset password request containing token and new password
     * @return response indicating success
     */
    @PostMapping("/reset-password")
    public ResponseEntity<ResponseWrapper<ForgotPasswordResponseDto>> resetPasswordWithToken(
            @Valid @RequestBody ResetPasswordWithTokenDto request) {
        log.info("Password reset with token request");
        
        ForgotPasswordResponseDto response = authService.resetPasswordWithToken(request);
        
        ResponseWrapper<ForgotPasswordResponseDto> wrapper = new ResponseWrapper<>(
                true, 200, "Password reset successfully", response
        );
        
        return ResponseEntity.ok(wrapper);
    }

    /**
     * Validates a password reset token without resetting the password.
     *
     * @param token the reset token to validate
     * @return true if token is valid, false otherwise
     */
    @GetMapping("/validate-reset-token/{token}")
    public ResponseEntity<ResponseWrapper<Boolean>> validatePasswordResetToken(@PathVariable String token) {
        log.info("Validating password reset token");
        
        boolean isValid = authService.validatePasswordResetToken(token);
        
        ResponseWrapper<Boolean> wrapper = new ResponseWrapper<>(
                true, 200, 
                isValid ? "Token is valid" : "Token is invalid or expired", 
                isValid
        );
        
        return ResponseEntity.ok(wrapper);
    }
}
