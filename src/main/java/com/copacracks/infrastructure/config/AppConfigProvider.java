package com.copacracks.infrastructure.config;

import com.google.inject.Provider;

import java.util.Locale;

public final class AppConfigProvider implements Provider<AppConfig> {
	private static final AppEnv env = AppEnv.load();
    private static final AppConfigYaml configs = AppConfigYaml.load(resolveConfigFile(env.getAppEnv()));

	@Override
	public AppConfig get() {
        return AppConfig.builder()
				.env(env)
                .config(configs)
				.build();
	}

    private static String resolveConfigFile(String appEnv) {
        return switch (appEnv.toLowerCase(Locale.ROOT)) {
            case "prod" -> mountFilePath("prod-config");
            default -> mountFilePath("local-config");
        };
    }

    private static String mountFilePath(String fileName) {
        return String.format("configs/%s.yaml", fileName);
    }
}
