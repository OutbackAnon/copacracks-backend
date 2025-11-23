package com.copacracks.application.usecases.model;

public interface UseCase <I, O> {
    UsecaseResponse<O> execute(UsecaseRequest<I> input);
}
