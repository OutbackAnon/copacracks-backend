    package com.copacracks;

import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.path;
import static io.javalin.apibuilder.ApiBuilder.post;

import com.copacracks.infrastructure.controller.AuthController;
import com.copacracks.infrastructure.controller.UserController;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.javalin.apibuilder.EndpointGroup;
import lombok.RequiredArgsConstructor;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class Routes implements EndpointGroup {
	private final UserController userController;
    private final AuthController authController;

	@Override
	public void addEndpoints() {
		path("/api", () -> {
            path("/health", () -> {
                        get(ctx -> ctx.json("OK"));
                    });

            path("/users", () -> {
                        post(userController::registerUser);
                    });
            path("/auth", () -> {
                post("/login", authController::login);
            });
        });
	}
}
