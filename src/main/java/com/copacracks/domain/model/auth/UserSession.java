package com.copacracks.domain.model.auth;

import java.time.Instant;
import java.util.UUID;

public record UserSession(
        UUID id,
        Long userId,
        String refreshToken,
        boolean revoked,
        String deviceInfo,
        String locale,
        String ipAddress,
        Instant createdAt,
        Instant expiresAt

) {
    public static UserSession createSession(Long userId, String refreshToken, boolean revoked, String deviceInfo, String locale, String ipAddress) {
        return new UserSession(
                UUID.randomUUID(),
                userId,
                refreshToken,
                revoked,
                deviceInfo,
                locale,
                ipAddress,
                Instant.now(),
                Instant.now()
        );
    }
}
