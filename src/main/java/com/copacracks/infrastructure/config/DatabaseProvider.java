package com.copacracks.infrastructure.config;

import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;

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
          Flyway.configure().dataSource(dataSource).locations("classpath:db/migration").load();

      flyway.migrate();
      log.info("Database migrations executed successfully");
    } catch (Exception e) {
      log.error("Error running database migrations", e);
      throw new RuntimeException("Failed to run database migrations", e);
    }
  }
}
