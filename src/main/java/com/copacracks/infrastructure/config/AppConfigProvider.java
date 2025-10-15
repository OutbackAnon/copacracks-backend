package com.copacracks.infrastructure.config;

import com.google.inject.Provider;

import java.util.Locale;

public final class AppConfigProvider implements Provider<AppConfig> {
	@Override
	public AppConfig get() {
        final AppEnv env = AppEnv.load();
        final AppConfigYaml configs = AppConfigYaml.load(resolveConfigFile(env.getAppEnv()));

        return AppConfig.builder()
                .env(env)
                .config(configs)
                .build();
	}

    private String resolveConfigFile(String appEnv) {
        return switch (appEnv.toLowerCase(Locale.ROOT)) {
            case "prod" -> mountFilePath("prod-config");
            default -> mountFilePath("local-config");
        };
    }

    private String mountFilePath(String fileName) {
        return String.format("configs/%s.yaml", fileName);
    }
}
