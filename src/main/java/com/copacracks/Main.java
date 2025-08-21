package com.copacracks;

import com.copacracks.web.config.ApplicationModule;
import com.copacracks.web.controller.UserController;
import com.google.inject.Guice;
import com.google.inject.Injector;
import io.javalin.Javalin;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SuppressWarnings("PMD.UseUtilityClass")
public class Main {
  public static void main(String[] args) {
    Injector injector = Guice.createInjector(new ApplicationModule());

    UserController userController = injector.getInstance(UserController.class);

    Javalin app =
        Javalin.create(
            config -> {
              config.bundledPlugins.enableCors(
                  cors -> {
                    cors.addRule(it -> it.anyHost());
                  });
              config.showJavalinBanner = false;
            });

    app.before(
        ctx -> {
          log.info("{} {}", ctx.method(), ctx.path());
        });

    // Rotas
    app.post("/api/users", userController::registerUser);
    app.get("/api/users/{id}", userController::getUserById);

    // Health check
    app.get("/health", ctx -> ctx.json("OK"));

    // Error handling
    app.exception(
        Exception.class,
        (e, ctx) -> {
          log.error("Unhandled exception", e);
          ctx.status(500);
          ctx.json("Internal server error");
        });

    // Iniciar servidor
    int port = Integer.parseInt(System.getProperty("server.port", "8080"));
    app.start(port);

    log.info("Servidor iniciado na porta {}", port);
    log.info("Teste com: POST http://localhost:{}/api/users", port);
    log.info(
        "Body: {{\"username\":\"joao\",\"password\":\"senha123\",\"email\":\"joao@email.com\"}}");

    // Javalin.create().get("/", ctx -> ctx.result("Hello Copacracks Backend")).start(8080);
  }
}
