package com.copacracks.infrastructure.config;

import lombok.Builder;

@Builder
public record AppConfig(AppEnv env, AppConfigYaml config) {
	public boolean isProduction() {
		return "prod".equalsIgnoreCase(env.getAppEnv());
	}

	public boolean isDevelopment() {
		return "local".equalsIgnoreCase(env.getAppEnv());
	}
}
