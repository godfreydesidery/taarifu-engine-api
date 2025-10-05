package com.taarifu_engine_api.config;

import com.taarifu_engine_api.modules.userandrole.domain.entity.User;
import com.taarifu_engine_api.modules.userandrole.domain.enums.UserType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@Slf4j
public class JwtService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    /**
     * Generate access token for user
     */
    public String generateAccessToken(User user) {
        return generateToken(user, accessTokenExpiration, "access");
    }

    /**
     * Generate limited access token for user (when password change is required)
     */
    public String generateLimitedAccessToken(User user) {
        return generateLimitedToken(user, 300, "limited_access"); // 5 minutes
    }

    /**
     * Generate refresh token for user
     */
    public String generateRefreshToken(User user) {
        return generateToken(user, refreshTokenExpiration, "refresh");
    }

    /**
     * Generate JWT token with custom expiration and token type
     */
    private String generateToken(User user, long expirationTime, String tokenType) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("uid", user.getUid());
        claims.put("username", user.getUsername());
        claims.put("email", user.getEmail());
        claims.put("userType", user.getUserType().name());
        claims.put("status", user.getStatus().name());
        claims.put("passwordStrength", user.getPasswordStrength().name());
        claims.put("tokenType", tokenType);

        return createToken(claims, user.getUsername(), expirationTime);
    }

    /**
     * Generate limited JWT token with restricted access
     */
    private String generateLimitedToken(User user, long expirationTime, String tokenType) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("uid", user.getUid());
        claims.put("username", user.getUsername());
        claims.put("email", user.getEmail());
        claims.put("userType", user.getUserType().name());
        claims.put("status", user.getStatus().name());
        claims.put("passwordStrength", user.getPasswordStrength().name());
        claims.put("tokenType", tokenType);
        claims.put("requirePasswordChange", user.getRequirePasswordChange());
        claims.put("limitedAccess", true); // Flag for limited access

        return createToken(claims, user.getUsername(), expirationTime);
    }

    /**
     * Create JWT token with claims
     */
    private String createToken(Map<String, Object> claims, String subject, long expirationTime) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expirationTime * 1000);

        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(getSignKey())
                .compact();
    }

    /**
     * Extract username from token
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extract UID from token
     */
    public String extractUid(String token) {
        return extractClaim(token, claims -> claims.get("uid", String.class));
    }

    /**
     * Extract user type from token
     */
    public UserType extractUserType(String token) {
        String userTypeStr = extractClaim(token, claims -> claims.get("userType", String.class));
        return UserType.valueOf(userTypeStr);
    }

    /**
     * Extract token type from token
     */
    public String extractTokenType(String token) {
        return extractClaim(token, claims -> claims.get("tokenType", String.class));
    }

    /**
     * Check if token has limited access
     */
    public Boolean hasLimitedAccess(String token) {
        return extractClaim(token, claims -> claims.get("limitedAccess", Boolean.class));
    }

    /**
     * Check if token requires password change
     */
    public Boolean requiresPasswordChange(String token) {
        return extractClaim(token, claims -> claims.get("requirePasswordChange", Boolean.class));
    }

    /**
     * Extract expiration date from token
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Check if token is expired
     */
    public Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Validate token for user
     */
    public Boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }

    /**
     * Extract specific claim from token
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extract all claims from token
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Get signing key from secret
     */
    private SecretKey getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Get access token expiration time in seconds
     */
    public long getAccessTokenExpiration() {
        return accessTokenExpiration;
    }

    /**
     * Get refresh token expiration time in seconds
     */
    public long getRefreshTokenExpiration() {
        return refreshTokenExpiration;
    }
}
