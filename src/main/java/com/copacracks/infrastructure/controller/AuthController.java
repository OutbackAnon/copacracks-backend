package com.copacracks.infrastructure.controller;

import com.copacracks.application.usecases.UserLoginUseCase;
import com.copacracks.domain.model.auth.UserAuth;
import com.copacracks.domain.model.user.PartialUser;
import com.copacracks.infrastructure.dto.UserLoginDto;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.javalin.http.Context;
import lombok.RequiredArgsConstructor;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class AuthController {
    private UserLoginUseCase userLoginUseCase;

    public void login(final Context context) {
        final UserLoginDto payload = context.bodyAsClass(UserLoginDto.class);
        PartialUser partialUser = new PartialUser(payload.username(), payload.password(), null);

        UserAuth userAuth = userLoginUseCase.execute(partialUser);

    }

}
