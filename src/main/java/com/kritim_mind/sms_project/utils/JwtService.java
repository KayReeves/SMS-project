package com.kritim_mind.sms_project.config;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtDecoder jwtDecoder; // Provided by Spring Security

    /**
     * Extracts username (subject) from JWT token
     */
    public String extractUsername(String token) {
        try {
            Jwt jwt = jwtDecoder.decode(token);
            return jwt.getSubject();
        } catch (JwtException e) {
            return null;
        }
    }

    /**
     * Checks if token is valid for the given user
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            Jwt jwt = jwtDecoder.decode(token);
            String username = jwt.getSubject();
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(jwt));
        } catch (JwtException e) {
            return false;
        }
    }

    /**
     * Checks if token is expired
     */
    private boolean isTokenExpired(Jwt jwt) {
        return jwt.getExpiresAt() != null && jwt.getExpiresAt().isBefore(java.time.Instant.now());
    }

    /**
     * Optional: Extract custom claims, e.g., roles or adminId
     */
    public Object extractClaim(String token, String claimName) {
        try {
            Jwt jwt = jwtDecoder.decode(token);
            return jwt.getClaim(claimName);
        } catch (JwtException e) {
            return null;
        }
    }
}
