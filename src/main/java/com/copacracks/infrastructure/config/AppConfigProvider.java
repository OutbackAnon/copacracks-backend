package com.copacracks.infrastructure.config;

import com.google.inject.Provider;

public final class AppConfigProvider implements Provider<AppConfig> {
	private static final AppEnv env = AppEnv.load();
    private static final AppConfigYaml configs = AppConfigYaml.load();

	@Override
	public AppConfig get() {
        return AppConfig.builder()
				.env(env)
                .config(configs)
				.build();
	}
}
