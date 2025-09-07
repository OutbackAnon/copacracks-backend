package com.copacracks.infrastructure.config;

import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;

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
public class DatabaseProvider implements Provider<DataSource> {
	private static final String DB_URL = "jdbc:postgresql://localhost:5432/copacracks";
	private static final String DB_USERNAME = "admin";
	private static final String DB_PASSWORD = "admin";

	@Override
	public DataSource get() {
		HikariConfig config = new HikariConfig();
		config.setJdbcUrl(DB_URL);
		config.setUsername(DB_USERNAME);
		config.setPassword(DB_PASSWORD);
		config.setDriverClassName("org.postgresql.Driver");
		config.setMaximumPoolSize(20);
		config.setMinimumIdle(5);
		config.setConnectionTimeout(30000);
		config.setIdleTimeout(600000);
		config.setMaxLifetime(1800000);

		DataSource dataSource = new HikariDataSource(config);

		runMigration(dataSource);

		return dataSource;
	}

	private void runMigration(DataSource dataSource) {
		try {
			Flyway flyway =
					Flyway.configure()
							.dataSource(dataSource)
							.locations("classpath:db/migration")
							.cleanDisabled(false)
							.load();

			flyway.clean();
			flyway.migrate();
			log.info("Database migrations executed successfully");
		} catch (Exception e) {
			log.error("Error running database migrations", e);
			throw new RuntimeException("Failed to run database migrations", e);
		}
	}
}
