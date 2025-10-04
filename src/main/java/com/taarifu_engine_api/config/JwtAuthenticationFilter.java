package com.taarifu_engine_api.config;

import com.taarifu_engine_api.modules.userandrole.domain.entity.User;
import com.taarifu_engine_api.modules.userandrole.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * JWT Authentication Filter for processing JWT tokens in requests
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        // Skip JWT processing for public endpoints
        if (isPublicEndpoint(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Check if Authorization header exists and starts with "Bearer "
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.debug("No JWT token found in request to: {}", request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // Extract JWT token
            jwt = authHeader.substring(7);
            
            // Extract username from JWT
            username = jwtService.extractUsername(jwt);
            
            log.debug("Processing JWT for user: {} on endpoint: {}", username, request.getRequestURI());

            // If user is not authenticated and username is valid
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                
                // Find user in database
                User user = userRepository.findByUsername(username)
                        .orElse(null);

                if (user != null && jwtService.validateToken(jwt, username)) {
                    log.debug("Valid JWT token for user: {}", username);
                    
                    // Create authentication token with user details and authorities
                    List<SimpleGrantedAuthority> authorities = List.of(
                            new SimpleGrantedAuthority("ROLE_" + user.getUserType().name())
                    );
                    
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            user,
                            null,
                            authorities
                    );
                    
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    
                    log.debug("User {} authenticated successfully with role: {}", username, user.getUserType());
                } else {
                    log.warn("Invalid JWT token or user not found for username: {}", username);
                }
            }
        } catch (Exception e) {
            log.error("Error processing JWT token: {}", e.getMessage());
            // Continue filter chain even if JWT processing fails
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Check if the request is to a public endpoint that doesn't require authentication
     */
    private boolean isPublicEndpoint(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        
        return requestURI.startsWith("/health") ||
               requestURI.startsWith("/engine/status") ||
               requestURI.startsWith("/engine/info") ||
               requestURI.startsWith("/h2-console") ||
               requestURI.startsWith("/actuator") ||
               requestURI.equals("/admin/v1/auth/login");
    }
}
