package com.copacracks.domain.security;

import java.util.Date;
import java.util.Map;

public interface JwtTokenGenerator {
    String generateAccessToken(String username, Map<String, Object> extraClaims);
    String generateAccessToken(String username);
    String generateRefreshToken(String username);
    Date extractExpiration(String token);
    String extractUsername(String token);
    boolean isTokenExpired(String token);
    boolean validateAccessToken(String token, String username);
    boolean validateRefreshToken(String token, String username);
}
