package com.copacracks.application.usecases.model;

import java.util.Map;
import java.util.Optional;

public class UsecaseResponse<T> {
    private final T body;
    private final Map<String, String> metadata;
    private final UsecaseStatus status;

    public UsecaseResponse(T body, Map<String, String> metadata, UsecaseStatus status) {
        this.body = body;
        this.metadata = metadata;
        this.status = status;
    }

    public UsecaseResponse(Map<String, String> metadata, UsecaseStatus status) {
        this(null, metadata, status);
    }

    public UsecaseResponse(T body, UsecaseStatus status) {
        this(body, Map.of(), status);
    }

    public UsecaseResponse(UsecaseStatus status) {
        this(null, Map.of(), status);
    }

    public Optional<T> getBody() {
        return Optional.ofNullable(body);
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public UsecaseStatus getStatus() {
        return status;
    }
}

