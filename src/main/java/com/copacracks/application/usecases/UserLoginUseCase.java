package com.copacracks.application.usecases;

import com.copacracks.domain.model.auth.UserAuth;
import com.copacracks.domain.model.user.PartialUser;

@FunctionalInterface
public interface UserLoginUseCase {
    UserAuth execute(PartialUser partialUser);
}
