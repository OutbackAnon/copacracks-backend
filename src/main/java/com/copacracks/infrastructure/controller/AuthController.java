package com.copacracks.infrastructure.controller;

import com.copacracks.application.usecases.UserLoginUseCase;
import com.copacracks.application.usecases.model.UsecaseResponse;
import com.copacracks.domain.model.user.PartialUser;
import com.copacracks.infrastructure.dto.UserLoginDto;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.javalin.http.Context;
import io.javalin.http.Cookie;
import lombok.RequiredArgsConstructor;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class AuthController {
    private final UserLoginUseCase userLoginUseCase;

    public void login(final Context ctx) {
        final UserLoginDto payload = ctx.bodyAsClass(UserLoginDto.class);
        PartialUser partialUser = new PartialUser(payload.username(), payload.password());

        UsecaseResponse<Void> usecaseResponse = userLoginUseCase.execute(partialUser);
        String accessToken = usecaseResponse.getMetadata().get("ACCESS_TOKEN");
        String refreshToken = usecaseResponse.getMetadata().get("REFRESH_TOKEN");

        final Cookie accessTokenCookie = new Cookie("access_token", accessToken, "/", 60 * 60 * 24, true, 0, true);
        final Cookie refreshTokenCookie = new Cookie("refresh_token", refreshToken, "/", 60 * 60 * 24, true, 0, true);

        ctx.cookie(accessTokenCookie);
        ctx.cookie(refreshTokenCookie);
        ctx.status(200).result("Usuario logado com sucesso!");
    }

}
