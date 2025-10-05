package com.copacracks.infrastructure.config;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.Builder;

@Builder
public record AppConfig(String securityPepper, String appEnv) {
	public boolean isProduction() {
		return "prod".equalsIgnoreCase(appEnv);
	}

	public boolean isDevelopment() {
		return "local".equalsIgnoreCase(appEnv);
	}

	private static Dotenv getDotenv() {
		return Dotenv.configure().ignoreIfMissing().ignoreIfMalformed().load();
	}
}
