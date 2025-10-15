package com.copacracks.infrastructure.config;

import com.copacracks.infrastructure.exception.ConfigurationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AppConfigYaml {
    private static final Logger logger = LoggerFactory.getLogger(AppConfigYaml.class);

    private Database database;

    public static AppConfigYaml load(String configPath) {
        try {
            logger.info("Loading configuration from: {}", configPath);

            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            mapper.findAndRegisterModules();

            InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(configPath);

            if (inputStream == null) {
                logger.error("Configuration file not found: {}", configPath);
                throw new ConfigurationException(
                        "Configuration file not found: " + configPath
                );
            }

            AppConfigYaml config = mapper.readValue(inputStream, AppConfigYaml.class);
            logger.info("Configuration loaded successfully");

            return config;
        } catch (IOException err) {
            logger.error("Failed to parse configuration file: {}", configPath, err);
            throw new ConfigurationException("Failed to load configuration from: " + configPath, err);
        }
    }

    public record Database(
            String driver,
            int maximumPoolSize,
            int minimumIdle,
            int idleTimeout,
            int connectionTimeout,
            int maxLifetime,
            Flyway flyway,
            Server server
    ) {}

    public record Flyway(
            boolean cleanOnDisabled,
            boolean cleanMigrationOnStart
    ) {}

    public record Server(String port) {}
}
