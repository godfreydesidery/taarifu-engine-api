package com.taarifu_engine_api.modules.auth.service;

import com.taarifu_engine_api.config.JwtService;
import com.taarifu_engine_api.modules.auth.domain.dto.AuthRequestDto;
import com.taarifu_engine_api.modules.auth.domain.dto.AuthResponseDto;
import com.taarifu_engine_api.modules.common.exception.ApiException;
import com.taarifu_engine_api.modules.userandrole.domain.entity.User;
import com.taarifu_engine_api.modules.userandrole.domain.enums.UserType;
import com.taarifu_engine_api.modules.userandrole.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

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
        
        // Generate JWT tokens
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        
        authResponse.setAccessToken(accessToken);
        authResponse.setRefreshToken(refreshToken);
        authResponse.setExpiresIn(jwtService.getAccessTokenExpiration());
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
}
