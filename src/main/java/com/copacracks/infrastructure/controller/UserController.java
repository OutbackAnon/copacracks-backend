package com.copacracks.infrastructure.controller;

import com.copacracks.application.dto.CreateUserRequestDto;
import com.copacracks.application.usecases.CreateUserCase;
import com.copacracks.domain.exception.UserValidationException;
import com.copacracks.infrastructure.dto.ErrorResponse;
import com.copacracks.infrastructure.dto.UserResponse;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class UserController {
  private final CreateUserCase createUserCase;

  public void registerUser(Context ctx) {
    try {
      CreateUserRequestDto request = ctx.bodyAsClass(CreateUserRequestDto.class);

      createUserCase.execute(request);

      ctx.status(HttpStatus.CREATED);
      ctx.json(new UserResponse(1L, request.username(), request.email()));

    } catch (UserValidationException e) {
      log.warn("User validation error: {}", e.getMessage());
      ctx.status(HttpStatus.BAD_REQUEST);
      ctx.json(new ErrorResponse("VALIDATION_ERROR", e.getMessage()));
    } catch (Exception e) {
      log.error("Error registering user", e);
      ctx.status(HttpStatus.INTERNAL_SERVER_ERROR);
      ctx.json(new ErrorResponse("INTERNAL_ERROR", "Erro interno do servidor"));
    }
  }
}
