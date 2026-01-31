package com.copacracks.domain.repository;

import com.copacracks.domain.model.auth.UserSession;
import com.copacracks.infrastructure.persistence.entity.BaseRepository;
import java.util.UUID;

public interface UserSessionRepository extends BaseRepository<UserSession, UUID> {}
