package com.copacracks.domain.repository;

import com.copacracks.infrastructure.persistence.entity.BaseRepository;
import com.copacracks.infrastructure.persistence.entity.UserSessionEntity;

import java.util.UUID;

public interface UserSessionRepository extends BaseRepository<UserSessionEntity, UUID> {
}
