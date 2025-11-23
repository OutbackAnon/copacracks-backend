package com.copacracks.infrastructure.persistence.entity;

import lombok.Builder;

import java.sql.Timestamp;
import java.util.UUID;

@Builder
public record UserSessionEntity(
        String id,
        Long userId,
        String refreshToken,
        Timestamp createdAt,
        Timestamp expiresAt,
        boolean revoked,
        String locale,
        String deviceInfo,
        String ipAddress) {
}
