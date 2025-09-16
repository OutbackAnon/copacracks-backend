package com.copacracks.infrastructure.controller;

import com.copacracks.application.dto.CreateUserRequestDto;
import com.copacracks.application.usecases.CreateUserCase;
import com.copacracks.domain.exception.UserValidationException;
import com.copacracks.infrastructure.dto.ErrorResponse;
import com.copacracks.infrastructure.dto.UserResponse;
import com.copacracks.infrastructure.exception.ControllerException;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/** REST controller for managing user-related HTTP operations. */
@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class UserController {

	private final CreateUserCase createUserCase;

	/**
	 * Registers a new user in the system.
	 *
	 * <p>This method handles HTTP POST requests for user registration. It processes the incoming user
	 * registration data, validates it through the business logic layer, and returns an appropriate
	 * HTTP response.
	 *
	 * <p>The method handles the following scenarios:
	 *
	 * <ul>
	 *   <li>Successful registration: Returns HTTP 201 Created with user details
	 *   <li>Validation errors: Returns HTTP 400 Bad Request with error details
	 *   <li>Internal errors: Returns HTTP 500 Internal Server Error with generic error message
	 * </ul>
	 *
	 * @param ctx the Javalin HTTP context containing the request data and response object
	 */
	public void registerUser(final Context ctx) {
		try {
			final CreateUserRequestDto request = ctx.bodyAsClass(CreateUserRequestDto.class);

			createUserCase.execute(request);

			ctx.status(HttpStatus.CREATED);
			ctx.json(new UserResponse(1L, request.username(), request.email()));

		} catch (UserValidationException e) {
			if (log.isErrorEnabled()) {
				log.error("User validation error: {}", e.getMessage());
			}
			ctx.status(HttpStatus.BAD_REQUEST);
			ctx.json(new ErrorResponse("VALIDATION_ERROR", e.getMessage()));
		} catch (ControllerException e) {
			if (log.isErrorEnabled()) {
				log.error("Error registering user", e);
			}
			ctx.status(HttpStatus.INTERNAL_SERVER_ERROR);
			ctx.json(new ErrorResponse("INTERNAL_ERROR", "Internal server error"));
		}
	}
}
