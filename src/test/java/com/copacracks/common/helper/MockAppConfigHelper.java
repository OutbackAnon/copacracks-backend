package com.copacracks.common.helper;

import com.copacracks.infrastructure.config.AppConfig;

public class MockAppConfigHelper {
	public static AppConfig createAppConfig() {
		return AppConfig.builder()
				.env(AppConfig.Env.builder().appEnv("local").securityPepper("security_pepper").build())
				.build();
	}
}
