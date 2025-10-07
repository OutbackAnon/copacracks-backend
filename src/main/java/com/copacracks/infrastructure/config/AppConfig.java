package com.copacracks.infrastructure.config;

import lombok.Builder;

@Builder
public record AppConfig(Env env) {
	public boolean isProduction() {
		return "prod".equalsIgnoreCase(env.appEnv());
	}

	public boolean isDevelopment() {
		return "local".equalsIgnoreCase(env.appEnv());
	}

	@Builder
	public record Env(String securityPepper, String appEnv) {}
}
