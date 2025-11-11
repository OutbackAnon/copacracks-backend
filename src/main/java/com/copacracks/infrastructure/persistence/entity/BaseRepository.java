package com.copacracks.infrastructure.persistence.entity;

public interface BaseRepository <T, K> {
    K save(T entity);
    T findById(K id);
}
