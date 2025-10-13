package com.copacracks;

import com.copacracks.infrastructure.controller.UserController;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.javalin.apibuilder.ApiBuilder;
import io.javalin.apibuilder.EndpointGroup;
import io.javalin.config.JavalinConfig;
import io.javalin.config.RouterConfig;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import static io.javalin.apibuilder.ApiBuilder.*;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class Routes implements EndpointGroup {
    private final UserController userController;

    @Override
    public void addEndpoints() {
        path("/api", () -> {
            path("/health", () -> {
                get(ctx -> ctx.json("OK"));
            });

            path("/users", () -> {
                post(userController::registerUser);
            });
        });
    }
}
