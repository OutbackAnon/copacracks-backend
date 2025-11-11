package com.copacracks.infrastructure.security;

import com.copacracks.domain.security.JwtTokenGenerator;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.checkerframework.checker.units.qual.C;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

public class JwtTokenGeneratorImpl implements JwtTokenGenerator {
    private static final SecretKey SECRET_KEY = Jwts.SIG.HS256.key().build();
    private static final SecretKey REFRESH_SECRET_KEY = Jwts.SIG.HS256.key().build();

    private static final long JWT_EXPIRATION = 0;
    private static final long REFRESH_EXPIRATION = 0;

    @Override
    public String generateAccessToken(String username) {
        return generateAccessToken(username, null);
    }

    @Override
    public String generateAccessToken(String username, Map<String, Object> extraClaims) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION))
                .id(UUID.randomUUID().toString())
                .signWith(SECRET_KEY)
                .claims(extraClaims)
                .compact();
    }

    @Override
    public String generateRefreshToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + REFRESH_EXPIRATION))
                .id(UUID.randomUUID().toString())
                .signWith(REFRESH_SECRET_KEY)
                .compact();
    }

    @Override
    public Date extractExpiration(String token) {
        return extractClaim(token, SECRET_KEY, Claims::getExpiration);
    }

    @Override
    public String extractUsername(String token) {
        return extractClaim(token, SECRET_KEY, Claims::getSubject);
    }

    public String extractUsernameFromRefreshToken(String token) {
        return extractClaim(token, REFRESH_SECRET_KEY, Claims::getSubject);
    }

    @Override
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    @Override
    public boolean validateAccessToken(String token, String username) {
        return false;
    }

    @Override
    public boolean validateRefreshToken(String token, String username) {
        return false;
    }

    public <T> T extractClaim(String token, SecretKey key, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token, key);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token, SecretKey key) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
