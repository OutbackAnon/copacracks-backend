package com.copacracks;

import com.copacracks.infrastructure.config.ApplicationModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import io.javalin.Javalin;
import io.javalin.openapi.plugin.OpenApiPlugin;
import io.javalin.openapi.plugin.redoc.ReDocPlugin;
import io.javalin.plugin.bundled.CorsPluginConfig;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SuppressWarnings("PMD.UseUtilityClass")
public class Main {
	public static void main(String[] args) {
		final Injector injector = Guice.createInjector(new ApplicationModule());
		final Routes routes = injector.getInstance(Routes.class);

		final Javalin app = Javalin.create(
				config -> {
					config.bundledPlugins.enableCors(
							cors -> cors.addRule(CorsPluginConfig.CorsRule::anyHost));
					config.showJavalinBanner = true;
					config.router.apiBuilder(routes);
					config.registerPlugin(new OpenApiPlugin(pluginConfig -> {
						pluginConfig.withDefinitionConfiguration((version, definition) -> {
							definition.withInfo(info -> {
								info.setTitle("Javalin open api");
								info.setVersion("1.0.0");
							});
						});
					}));
					config.registerPlugin(new ReDocPlugin(pluginConfig -> {
						pluginConfig.setDocumentationPath("/openapi");
						pluginConfig.setUiPath("/redoc");
					}));
				});

		app.before(ctx -> log.info("{} {}", ctx.method(), ctx.path()));

		// Error handling
		app.exception(
				Exception.class,
				(e, ctx) -> {
					log.error("Unhandled exception", e);
					ctx.status(500);
					ctx.json("Internal server error");
				});

		// Iniciar servidor
		final int port = Integer.parseInt(System.getProperty("server.port", "8080"));
		app.start(port);

		log.info("Servidor iniciado na porta {}", port);
		log.info("Teste com: POST http://localhost:{}/api/users", port);
		log.info(
				"Body: {{\"username\":\"joao\",\"password\":\"senha123\",\"email\":\"joao@email.com\"}}");
	}
}
