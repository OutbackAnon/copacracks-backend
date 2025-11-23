package com.copacracks.infrastructure.persistence.entity;

import java.util.Optional;

public interface BaseRepository <T, K> {
    K save(T entity);
    Optional<T> findById(K id);
}
