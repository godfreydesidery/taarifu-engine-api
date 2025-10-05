package com.taarifu_engine_api.modules.auth.service;

import com.taarifu_engine_api.config.JwtService;
import com.taarifu_engine_api.modules.auth.domain.dto.AuthRequestDto;
import com.taarifu_engine_api.modules.auth.domain.dto.AuthResponseDto;
import com.taarifu_engine_api.modules.auth.domain.dto.ForgotPasswordRequestDto;
import com.taarifu_engine_api.modules.auth.domain.dto.ForgotPasswordResponseDto;
import com.taarifu_engine_api.modules.auth.domain.dto.ResetPasswordWithTokenDto;
import com.taarifu_engine_api.modules.common.domain.enums.PasswordStrength;
import com.taarifu_engine_api.modules.common.domain.util.PasswordStrengthCalculator;
import com.taarifu_engine_api.modules.common.exception.ApiException;
import com.taarifu_engine_api.modules.notification.domain.enums.EmailType;
import com.taarifu_engine_api.modules.notification.service.EmailService;
import com.taarifu_engine_api.modules.userandrole.domain.entity.User;
import com.taarifu_engine_api.modules.userandrole.domain.enums.UserType;
import com.taarifu_engine_api.modules.userandrole.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailService emailService;

    @Override
    public AuthResponseDto authenticateAdmin(AuthRequestDto request) {
        log.info("Admin authentication attempt for: {}", request.getUsernameOrEmail());
        
        // Validate credentials and get user
        User user = validateCredentials(request.getUsernameOrEmail(), request.getPassword());
        if (user == null) {
            throw new ApiException("Invalid credentials", HttpStatus.UNAUTHORIZED);
        }

        // Check if user is admin
        if (user.getUserType() != UserType.ADMIN) {
            log.warn("Admin authentication failed: User is not admin - {}", user.getUsername());
            throw new ApiException("Access denied. Admin privileges required.", HttpStatus.FORBIDDEN);
        }

        // Check if user can authenticate
        if (!canUserAuthenticate(user)) {
            throw new ApiException("Account is inactive or suspended", HttpStatus.FORBIDDEN);
        }

        // Update last login and generate tokens
        updateLastLogin(user);
        AuthResponseDto response = createAuthResponse(user);
        
        log.info("Admin authentication successful for: {}", user.getUsername());
        return response;
    }

    @Override
    public AuthResponseDto authenticateUser(AuthRequestDto request) {
        log.info("User authentication attempt for: {}", request.getUsernameOrEmail());
        
        // Validate credentials and get user
        User user = validateCredentials(request.getUsernameOrEmail(), request.getPassword());
        if (user == null) {
            throw new ApiException("Invalid credentials", HttpStatus.UNAUTHORIZED);
        }

        // Check if user can authenticate
        if (!canUserAuthenticate(user)) {
            throw new ApiException("Account is inactive or suspended", HttpStatus.FORBIDDEN);
        }

        // Update last login and generate tokens
        updateLastLogin(user);
        AuthResponseDto response = createAuthResponse(user);
        
        log.info("User authentication successful for: {}", user.getUsername());
        return response;
    }

    @Override
    public AuthResponseDto authenticateUser(AuthRequestDto request, UserType expectedUserType) {
        log.info("User authentication attempt for: {} (expected type: {})", 
                request.getUsernameOrEmail(), expectedUserType);
        
        // Validate credentials and get user
        User user = validateCredentials(request.getUsernameOrEmail(), request.getPassword());
        if (user == null) {
            throw new ApiException("Invalid credentials", HttpStatus.UNAUTHORIZED);
        }

        // Check if user type matches expected type
        if (user.getUserType() != expectedUserType) {
            log.warn("User authentication failed: User type mismatch - expected: {}, actual: {}", 
                    expectedUserType, user.getUserType());
            throw new ApiException("Access denied. Invalid user type for this operation.", HttpStatus.FORBIDDEN);
        }

        // Check if user can authenticate
        if (!canUserAuthenticate(user)) {
            throw new ApiException("Account is inactive or suspended", HttpStatus.FORBIDDEN);
        }

        // Update last login and generate tokens
        updateLastLogin(user);
        AuthResponseDto response = createAuthResponse(user);
        
        log.info("User authentication successful for: {} (type: {})", 
                user.getUsername(), user.getUserType());
        return response;
    }

    @Override
    public User validateCredentials(String usernameOrEmail, String password) {
        // Find user by username or email
        Optional<User> userOpt = userRepository.findByUsername(usernameOrEmail)
                .or(() -> userRepository.findByEmail(usernameOrEmail));

        if (userOpt.isEmpty()) {
            log.warn("Authentication failed: User not found - {}", usernameOrEmail);
            return null;
        }

        User user = userOpt.get();

        // Verify password
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            log.warn("Authentication failed: Invalid password for user - {}", user.getUsername());
            return null;
        }

        return user;
    }

    @Override
    public boolean canUserAuthenticate(User user) {
        return user.isActive();
    }

    @Override
    public AuthResponseDto refreshToken(String refreshToken) {
        try {
            log.info("Token refresh attempt");
            
            // Validate refresh token and extract user info
            String username = jwtService.extractUsername(refreshToken);
            String tokenType = jwtService.extractTokenType(refreshToken);
            
            if (!"refresh".equals(tokenType)) {
                throw new ApiException("Invalid token type for refresh operation", HttpStatus.BAD_REQUEST);
            }

            // Validate token
            if (!jwtService.validateToken(refreshToken, username)) {
                throw new ApiException("Invalid or expired refresh token", HttpStatus.UNAUTHORIZED);
            }

            // Find user and generate new tokens
            Optional<User> userOpt = userRepository.findByUsername(username);
            if (userOpt.isEmpty()) {
                throw new ApiException("User not found", HttpStatus.NOT_FOUND);
            }

            User user = userOpt.get();
            if (!canUserAuthenticate(user)) {
                throw new ApiException("Account is inactive or suspended", HttpStatus.FORBIDDEN);
            }

            // Generate new tokens
            AuthResponseDto response = createAuthResponse(user);
            
            log.info("Token refresh successful for: {}", user.getUsername());
            return response;

        } catch (ApiException e) {
            // Re-throw ApiException as-is
            throw e;
        } catch (Exception e) {
            log.error("Token refresh failed: {}", e.getMessage());
            throw new ApiException("Token refresh failed: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void logout(String accessToken) {
        // For stateless JWT, we don't need to do anything on the server side
        // Tokens will naturally expire
        // If token blacklisting is needed in the future, it can be implemented here
        log.info("User logout - token will expire naturally");
    }

    /**
     * Create authentication response with user info and tokens
     */
    private AuthResponseDto createAuthResponse(User user) {
        AuthResponseDto authResponse = new AuthResponseDto();
        
        // Set user information
        authResponse.setUid(user.getUid());
        authResponse.setUsername(user.getUsername());
        authResponse.setEmail(user.getEmail());
        authResponse.setUserType(user.getUserType());
        authResponse.setStatus(user.getStatus());
        authResponse.setPasswordStrength(user.getPasswordStrength());
        authResponse.setRequirePasswordChange(user.getRequirePasswordChange());
        authResponse.setLastLoginAt(user.getLastLoginAt());
        authResponse.setCreatedAt(user.getCreatedAt());
        
        // Generate JWT tokens based on password change requirement
        if (user.getRequirePasswordChange()) {
            // Limited access token (5 minutes) - no refresh token - only password change allowed
            String limitedAccessToken = jwtService.generateLimitedAccessToken(user);
            authResponse.setAccessToken(limitedAccessToken);
            authResponse.setRefreshToken(null); // No refresh token for limited access
            authResponse.setExpiresIn(300L); // 5 minutes
            log.info("Generated limited access token for user requiring password change: {}", user.getUsername());
        } else {
            // Full access token with refresh token
            String accessToken = jwtService.generateAccessToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);
            authResponse.setAccessToken(accessToken);
            authResponse.setRefreshToken(refreshToken);
            authResponse.setExpiresIn(jwtService.getAccessTokenExpiration());
            log.info("Generated full access token for user: {}", user.getUsername());
        }
        
        authResponse.setTokenType("Bearer");
        
        return authResponse;
    }

    /**
     * Update user's last login timestamp
     */
    private void updateLastLogin(User user) {
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);
    }

    @Override
    public ForgotPasswordResponseDto forgotPassword(ForgotPasswordRequestDto request) {
        log.info("Processing forgot password request for email: {}", request.getEmail());
        
        // Find user by email
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
        
        // Always return success for security (prevents email enumeration)
        ForgotPasswordResponseDto response = new ForgotPasswordResponseDto();
        response.setSuccess(true);
        response.setMessage("If an account with that email exists, password reset instructions have been sent.");
        response.setEmail(request.getEmail());
        response.setCheckEmail(true);
        response.setTokenExpirationMinutes(60); // 1 hour expiration
        
        if (userOpt.isEmpty()) {
            log.warn("Forgot password request for non-existent email: {}", request.getEmail());
            return response;
        }
        
        User user = userOpt.get();
        
        // Check if user is active
        if (!user.isActive()) {
            log.warn("Forgot password request for inactive user: {}", user.getEmail());
            return response;
        }
        
        // Generate secure reset token
        String resetToken = generateSecureResetToken();
        LocalDateTime expirationTime = LocalDateTime.now().plusHours(1);
        
        // Save reset token to user
        user.setPasswordResetToken(resetToken);
        user.setPasswordResetTokenExpiresAt(expirationTime);
        userRepository.save(user);
        
        log.info("Generated password reset token for user: {}", user.getUsername());
        
        // Send password reset email
        sendPasswordResetEmail(user, resetToken);
        
        return response;
    }

    @Override
    public ForgotPasswordResponseDto resetPasswordWithToken(ResetPasswordWithTokenDto request) {
        log.info("Processing password reset with token");
        
        // Validate passwords match
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new ApiException("Passwords do not match", HttpStatus.BAD_REQUEST);
        }
        
        // Find user by reset token
        Optional<User> userOpt = userRepository.findByPasswordResetToken(request.getToken());
        
        if (userOpt.isEmpty()) {
            throw new ApiException("Invalid or expired reset token", HttpStatus.BAD_REQUEST);
        }
        
        User user = userOpt.get();
        
        // Validate token is not expired
        if (user.isPasswordResetTokenExpired()) {
            // Clear expired token
            user.clearPasswordResetToken();
            userRepository.save(user);
            throw new ApiException("Reset token has expired. Please request a new password reset.", HttpStatus.BAD_REQUEST);
        }
        
        // Validate password strength
        PasswordStrength strength = PasswordStrengthCalculator.calculatePasswordStrength(request.getNewPassword());
        if (user.isAdmin() && !strength.equals(PasswordStrength.STRONG)) {
            throw new ApiException(
                "Admin passwords must be STRONG. " + PasswordStrengthCalculator.getAdminPasswordRequirements(),
                HttpStatus.BAD_REQUEST
            );
        }
        
        // Update password
        String hashedPassword = passwordEncoder.encode(request.getNewPassword());
        user.setPasswordHash(hashedPassword);
        user.setPasswordStrength(strength);
        user.setRequirePasswordChange(false); // Password reset completed
        user.clearPasswordResetToken(); // Clear the reset token
        
        // TEMPORARY: Update raw password for testing (remove in production)
        user.setRawPassword(request.getNewPassword());
        
        userRepository.save(user);
        
        log.info("Password reset successfully for user: {}", user.getUsername());
        
        // Send password changed notification
        sendPasswordChangedEmail(user);
        
        ForgotPasswordResponseDto response = new ForgotPasswordResponseDto();
        response.setSuccess(true);
        response.setMessage("Password has been reset successfully. You can now log in with your new password.");
        response.setEmail(user.getEmail());
        response.setCheckEmail(false);
        response.setTokenExpirationMinutes(0);
        
        return response;
    }

    @Override
    public boolean validatePasswordResetToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            return false;
        }
        
        Optional<User> userOpt = userRepository.findByPasswordResetToken(token);
        if (userOpt.isEmpty()) {
            return false;
        }
        
        User user = userOpt.get();
        return user.hasValidPasswordResetToken();
    }

    /**
     * Generate a secure random reset token
     */
    private String generateSecureResetToken() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    /**
     * Send password reset email to user
     */
    private void sendPasswordResetEmail(User user, String resetToken) {
        try {
            // Create reset link (you'll need to configure the frontend URL)
            String resetLink = "http://localhost:3000/reset-password?token=" + resetToken;
            
            // Set email content with reset link
            String htmlContent = String.format("""
                <html>
                <body>
                    <h2>Password Reset Request</h2>
                    <p>Hello %s,</p>
                    <p>You have requested to reset your password. Click the link below to reset your password:</p>
                    <p><a href="%s" style="background-color: #007bff; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px;">Reset Password</a></p>
                    <p>This link will expire in 1 hour.</p>
                    <p>If you did not request this password reset, please ignore this email.</p>
                    <br>
                    <p>Best regards,<br>Taarifu Engine Team</p>
                </body>
                </html>
                """, user.getUsername(), resetLink);
            
            // Send email using simple method
            emailService.sendSimpleEmailAsync(
                user.getEmail(), 
                "Password Reset Request", 
                htmlContent, 
                EmailType.FORGOT_PASSWORD
            );
            
            log.info("Password reset email sent to: {}", user.getEmail());
        } catch (Exception e) {
            log.error("Failed to send password reset email to: {}", user.getEmail(), e);
            // Don't throw exception - email failure shouldn't break the flow
        }
    }

    /**
     * Send password changed notification email
     */
    private void sendPasswordChangedEmail(User user) {
        try {
            String htmlContent = String.format("""
                <html>
                <body>
                    <h2>Password Changed Successfully</h2>
                    <p>Hello %s,</p>
                    <p>Your password has been successfully changed.</p>
                    <p>If you did not make this change, please contact support immediately.</p>
                    <br>
                    <p>Best regards,<br>Taarifu Engine Team</p>
                </body>
                </html>
                """, user.getUsername());
            
            // Send email using simple method
            emailService.sendSimpleEmailAsync(
                user.getEmail(), 
                "Password Changed Successfully", 
                htmlContent, 
                EmailType.PASSWORD_CHANGED
            );
            
            log.info("Password changed notification sent to: {}", user.getEmail());
        } catch (Exception e) {
            log.error("Failed to send password changed notification to: {}", user.getEmail(), e);
            // Don't throw exception - email failure shouldn't break the flow
        }
    }
}
