package com.copacracks.common.helper;

import com.copacracks.infrastructure.config.AppConfig;
import com.copacracks.infrastructure.config.AppEnv;

public class MockAppConfigHelper {
	public static AppConfig createAppConfig() {
        AppEnv env = AppEnv.builder()
                .appEnv("local")
                .securityPepper("security_pepper")
                .dbUrl("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1")
                .dbUsername("admin")
                .dbPassword("")
                .build();

		return AppConfig.builder()
				.env(env)
				.build();
	}
}
