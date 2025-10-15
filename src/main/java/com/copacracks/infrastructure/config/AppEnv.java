package com.copacracks.infrastructure.config;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class AppEnv {
    private final String securityPepper;
    private final String appEnv;
    private final String dbUrl;
    private final String dbUsername;
    private final String dbPassword;

    public static AppEnv load() {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().ignoreIfMalformed().load();

        return new AppEnv(
            dotenv.get("SECURITY_PEPPER", "security_pepper"),
            dotenv.get("APP_ENV", "local"),
            dotenv.get("DB_URL", "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1"),
            dotenv.get("DB_USERNAME", "admin"),
            dotenv.get("DB_PASSWORD", "")
        );
    }
}
