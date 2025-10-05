package com.taarifu_engine_api.modules.auth.service;

import com.taarifu_engine_api.modules.auth.domain.dto.AuthRequestDto;
import com.taarifu_engine_api.modules.auth.domain.dto.AuthResponseDto;
import com.taarifu_engine_api.modules.auth.domain.dto.ForgotPasswordRequestDto;
import com.taarifu_engine_api.modules.auth.domain.dto.ForgotPasswordResponseDto;
import com.taarifu_engine_api.modules.auth.domain.dto.ResetPasswordWithTokenDto;
import com.taarifu_engine_api.modules.userandrole.domain.entity.User;
import com.taarifu_engine_api.modules.userandrole.domain.enums.UserType;

/**
 * Service interface for authentication operations.
 * Handles user authentication, token generation, and authorization logic.
 */
public interface AuthService {

    /**
     * Authenticate user and generate tokens for admin users.
     *
     * @param request authentication request containing credentials
     * @return authentication response with user info and tokens
     * @throws IllegalArgumentException if credentials are invalid
     * @throws SecurityException if user is not authorized for admin access
     */
    AuthResponseDto authenticateAdmin(AuthRequestDto request);

    /**
     * Authenticate user and generate tokens for any user type.
     *
     * @param request authentication request containing credentials
     * @return authentication response with user info and tokens
     * @throws IllegalArgumentException if credentials are invalid
     */
    AuthResponseDto authenticateUser(AuthRequestDto request);

    /**
     * Authenticate user and generate tokens for specific user type.
     *
     * @param request authentication request containing credentials
     * @param expectedUserType the expected user type for authorization
     * @return authentication response with user info and tokens
     * @throws IllegalArgumentException if credentials are invalid
     * @throws SecurityException if user type doesn't match expected type
     */
    AuthResponseDto authenticateUser(AuthRequestDto request, UserType expectedUserType);

    /**
     * Validate user credentials without generating tokens.
     *
     * @param usernameOrEmail username or email
     * @param password plain text password
     * @return authenticated user if valid, null if invalid
     */
    User validateCredentials(String usernameOrEmail, String password);

    /**
     * Check if user can authenticate (account is active, etc.).
     *
     * @param user the user to check
     * @return true if user can authenticate, false otherwise
     */
    boolean canUserAuthenticate(User user);

    /**
     * Refresh access token using refresh token.
     *
     * @param refreshToken the refresh token
     * @return new authentication response with fresh tokens
     * @throws SecurityException if refresh token is invalid or expired
     */
    AuthResponseDto refreshToken(String refreshToken);

    /**
     * Logout user by invalidating tokens (if token blacklisting is implemented).
     *
     * @param accessToken the access token to invalidate
     */
    void logout(String accessToken);

    /**
     * Initiates a password reset process by sending a reset token via email.
     *
     * @param request the forgot password request containing email
     * @return response indicating success and next steps
     */
    ForgotPasswordResponseDto forgotPassword(ForgotPasswordRequestDto request);

    /**
     * Resets a user's password using a valid reset token.
     *
     * @param request the reset password request containing token and new password
     * @return response indicating success
     * @throws IllegalArgumentException if token is invalid or expired
     */
    ForgotPasswordResponseDto resetPasswordWithToken(ResetPasswordWithTokenDto request);

    /**
     * Validates a password reset token without resetting the password.
     *
     * @param token the reset token to validate
     * @return true if token is valid, false otherwise
     */
    boolean validatePasswordResetToken(String token);
}
