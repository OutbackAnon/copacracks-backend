package com.copacracks.application.usecases.model;

public record UsecaseRequest<T>(T body, long userId, String correlationId, String local) {}
