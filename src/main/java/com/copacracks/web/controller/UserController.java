package com.copacracks.web.controller;

import com.copacracks.application.command.RegisterUserCommand;
import com.copacracks.application.service.UserService;
import com.copacracks.domain.exception.UserValidationException;
import com.copacracks.domain.model.user.User;
import com.copacracks.web.dto.ErrorResponse;
import com.copacracks.web.dto.RegisterUserRequest;
import com.copacracks.web.dto.UserResponse;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class UserController {
  private final UserService userService;

  public void registerUser(Context ctx) {
    try {
      RegisterUserRequest request = ctx.bodyAsClass(RegisterUserRequest.class);

      RegisterUserCommand command =
          new RegisterUserCommand(request.username(), request.password(), request.email());

      Long userId = userService.registerUser(command);

      ctx.status(HttpStatus.CREATED);
      ctx.json(new UserResponse(userId, request.username(), request.email()));

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

  public void getUserById(Context ctx) {
    try {
      Long userId = Long.parseLong(ctx.pathParam("id"));
      Optional<User> userOpt = userService.getUserById(userId);

      if (userOpt.isPresent()) {
        User user = userOpt.get();
        UserResponse response = new UserResponse(user.getId(), user.getUsername(), user.getEmail());
        ctx.json(response);
      } else {
        ctx.status(HttpStatus.NOT_FOUND);
        ctx.json(new ErrorResponse("NOT_FOUND", "Usuário não encontrado"));
      }

    } catch (NumberFormatException e) {
      ctx.status(HttpStatus.BAD_REQUEST);
      ctx.json(new ErrorResponse("INVALID_ID", "ID inválido"));
    } catch (Exception e) {
      log.error("Error getting user by id", e);
      ctx.status(HttpStatus.INTERNAL_SERVER_ERROR);
      ctx.json(new ErrorResponse("INTERNAL_ERROR", "Erro interno do servidor"));
    }
  }
}
