package com.copacracks.application.usecases;

import com.copacracks.application.usecases.model.UsecaseResponse;
import com.copacracks.domain.model.user.PartialUser;

@FunctionalInterface
public interface UserLoginUseCase {
    UsecaseResponse<Void> execute(PartialUser partialUser);
}
