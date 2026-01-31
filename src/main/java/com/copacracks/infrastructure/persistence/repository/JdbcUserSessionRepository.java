package com.copacracks.infrastructure.persistence.repository;

import com.copacracks.domain.model.auth.UserSession;
import com.copacracks.domain.repository.UserSessionRepository;
import com.copacracks.infrastructure.mapper.UserSessionMapper;
import com.copacracks.infrastructure.persistence.entity.UserSessionEntity;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.Optional;
import java.util.UUID;
import javax.sql.DataSource;

@Singleton
public class JdbcUserSessionRepository extends BaseJdbcRepository implements UserSessionRepository {
	private static final String INSERT_SESSION =
			"INSERT INTO user_sessions (id, user_id, refresh_token_hash, created_at, expires_at, revoked, locale, device_info, ip_address) "
					+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
	private static final String FIND_BY_ID =
			"SELECT id, username, password, email FROM user_sessions WHERE id = ?";

	@Inject
	public JdbcUserSessionRepository(DataSource dataSource) {
		super(dataSource);
	}

	@Override
	public UUID save(UserSession userSession) {
		final UserSessionEntity userSessionEntity = UserSessionMapper.fromModel(userSession);
		executeInsert(
				INSERT_SESSION,
				stmt -> {
					stmt.setString(1, userSessionEntity.id());
					stmt.setLong(2, userSessionEntity.userId());
					stmt.setString(3, userSessionEntity.refreshToken());
					stmt.setTimestamp(4, userSessionEntity.createdAt());
					stmt.setTimestamp(5, userSessionEntity.expiresAt());
					stmt.setBoolean(6, userSessionEntity.revoked());
					stmt.setString(7, userSessionEntity.locale());
					stmt.setString(8, userSessionEntity.deviceInfo());
					stmt.setString(9, userSessionEntity.ipAddress());
				});

		return userSession.id();
	}

	@Override
	public Optional<UserSession> findById(UUID id) {
		return null;
	}
}
