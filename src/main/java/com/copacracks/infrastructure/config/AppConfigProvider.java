package com.copacracks.infrastructure.config;

import com.google.inject.Provider;
import io.github.cdimascio.dotenv.Dotenv;

public class AppConfigProvider implements Provider<AppConfig> {
    private static final Dotenv dotenv = getDotenv();
    @Override
    public AppConfig get() {
        return AppConfig.builder()
                .appEnv(dotenv.get("APP_ENV", "local"))
                .securityPepper(dotenv.get("SECURITY_PAPER", "security_pepper"))
                .build();
    }

    private static Dotenv getDotenv() {
        return Dotenv.configure()
                .ignoreIfMissing()
                .ignoreIfMalformed()
                .load();
    }
}
