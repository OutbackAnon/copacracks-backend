package com.copacracks.infrastructure.config;

import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.SQLException;
import javax.sql.DataSource;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;
import org.h2.tools.Server;

/**
 * Database configuration and DataSource provider for the CopaCracks application.
 *
 * <p>This class is responsible for configuring and providing database connections through a
 * HikariCP connection pool. It also handles database schema migrations using Flyway during
 * application startup.
 *
 * <p>Key responsibilities:
 *
 * <ul>
 *   <li>Configure HikariCP connection pool with optimal settings
 *   <li>Provide DataSource instances for dependency injection
 *   <li>Execute database migrations automatically on startup
 *   <li>Handle database configuration errors gracefully
 * </ul>
 *
 * <p>The provider is configured as a singleton to ensure only one DataSource instance is created
 * and shared across the application.
 *
 * @author CopaCracks Team
 * @version 1.0
 * @since 1.0
 */
@Slf4j
@Singleton
@AllArgsConstructor
public class DatabaseProvider implements Provider<DataSource> {
	//    private static final String DB_URL = "jdbc:postgresql://localhost:5432/copacracks";

	private AppConfig appConfig;

	@Override
	public DataSource get() {
		final HikariConfig config = new HikariConfig();
		config.setJdbcUrl(appConfig.env().getDbUrl());
		config.setUsername(appConfig.env().getDbUsername());
		config.setPassword(appConfig.env().getDbPassword());
		//        config.setDriverClassName("org.postgresql.Driver");
		config.setDriverClassName(appConfig.config().getDatabase().driver());
		config.setMaximumPoolSize(appConfig.config().getDatabase().maximumPoolSize());
		config.setMinimumIdle(appConfig.config().getDatabase().minimumIdle());
		config.setConnectionTimeout(appConfig.config().getDatabase().connectionTimeout());
		config.setIdleTimeout(appConfig.config().getDatabase().idleTimeout());
		config.setMaxLifetime(appConfig.config().getDatabase().maxLifetime());

		try {
			Server webServer = Server.createWebServer("-webPort", "8082", "-tcpAllowOthers").start();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

		final DataSource dataSource = new HikariDataSource(config);

		runMigration(dataSource);

		return dataSource;
	}

	private void runMigration(final DataSource dataSource) {
		try {
			final Flyway flyway =
					Flyway.configure()
							.dataSource(dataSource)
							.locations("classpath:db/migration")
							.cleanDisabled(appConfig.config().getDatabase().flyway().cleanOnDisabled())
							.load();

			if (appConfig.config().getDatabase().flyway().cleanMigrationOnStart()) {
				flyway.clean();
			}
			flyway.migrate();
			log.info("Database migrations executed successfully");
		} catch (FlywayException e) {
			log.error("Error running database migrations", e);
			throw new FlywayException("Failed to run database migrations", e);
		}
	}
}
