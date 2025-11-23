package com.copacracks.infrastructure.mapper;

import com.copacracks.domain.model.auth.UserSession;
import com.copacracks.infrastructure.persistence.entity.UserSessionEntity;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

public class UserSessionMapper {
    public static UserSessionEntity fromModel(final UserSession userSession) {
        final Instant createAt = userSession.createdAt();
        final Instant expiresAt = userSession.expiresAt();

        final Timestamp createAtTimestamp = Timestamp.from(createAt);
        final Timestamp expiresAtTimestamp = Timestamp.from(expiresAt);

        return UserSessionEntity.builder()
                .id(userSession.id().toString())
                .userId(userSession.userId())
                .createdAt(createAtTimestamp)
                .expiresAt(expiresAtTimestamp)
                .deviceInfo(userSession.deviceInfo())
                .ipAddress(userSession.ipAddress())
                .locale(userSession.locale())
                .refreshToken(userSession.refreshToken())
                .revoked(userSession.revoked())
                .build();
    }

    public static UserSession fromEntity(final UserSessionEntity userSessionEntity) {
        final Timestamp entityCreatedAt = userSessionEntity.createdAt();
        final Timestamp entityExpiresAt = userSessionEntity.expiresAt();

        final Instant createdAtInstant = entityCreatedAt.toInstant();
        final Instant entityExpiresAtInstant = entityExpiresAt.toInstant();
        final UUID id = UUID.fromString(userSessionEntity.id());

        return new UserSession(
            id,
            userSessionEntity.userId(),
            userSessionEntity.refreshToken(),
            userSessionEntity.revoked(),
            userSessionEntity.deviceInfo(),
            userSessionEntity.locale(),
            userSessionEntity.ipAddress(),
            createdAtInstant,
            entityExpiresAtInstant
        );
    }
}
