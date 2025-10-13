package com.copacracks.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.io.InputStream;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AppConfigYaml {
    private Database database;

    public static AppConfigYaml load() {
        try {
            String path = "configs/local-config.yaml";
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            mapper.findAndRegisterModules();

            InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);

            return mapper.readValue(inputStream, AppConfigYaml.class);
        } catch (IOException err) {
            err.printStackTrace();
        }
        return null;
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
