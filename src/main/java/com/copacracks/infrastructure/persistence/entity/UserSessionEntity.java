package com.copacracks.infrastructure.persistence.entity;

import java.sql.Timestamp;
import lombok.Builder;

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
		String ipAddress) {}
