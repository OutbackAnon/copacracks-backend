package com.copacracks.infrastructure.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AppEnvTest {
	@Test
	@DisplayName(
			"It should load values from environment variables when they exist.\" (or) \"It must load values from environment variables when they exist.")
	void shouldLoadFromEnvironmentVariables() {
		Dotenv mockDotenv = mock(Dotenv.class);
		when(mockDotenv.get("SECURITY_PEPPER", "security_pepper")).thenReturn("custom_pepper");
		when(mockDotenv.get("APP_ENV", "local")).thenReturn("prod");
		when(mockDotenv.get("DB_URL", "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1"))
				.thenReturn("jdbc:postgresql://localhost:5432/mydb");
		when(mockDotenv.get("DB_USERNAME", "admin")).thenReturn("dbuser");
		when(mockDotenv.get("DB_PASSWORD", "")).thenReturn("dbpass123");

		try (MockedStatic<Dotenv> dotenvStatic = mockStatic(Dotenv.class)) {
			DotenvBuilder builder = mock(DotenvBuilder.class);
			when(Dotenv.configure()).thenReturn(builder);
			when(builder.ignoreIfMissing()).thenReturn(builder);
			when(builder.ignoreIfMalformed()).thenReturn(builder);
			when(builder.load()).thenReturn(mockDotenv);

			AppEnv env = AppEnv.load();

			assertThat("custom_pepper").isEqualTo(env.getSecurityPepper());
			assertThat("prod").isEqualTo(env.getAppEnv());
			assertThat("jdbc:postgresql://localhost:5432/mydb").isEqualTo(env.getDbUrl());
			assertThat("dbuser").isEqualTo(env.getDbUsername());
			assertThat("dbpass123").isEqualTo(env.getDbPassword());
		}
	}

	@Test
	@DisplayName(
			"It should use default values when variables do not exist.\" (or) \"It must use default values when variables do not exist.")
	void shouldUseDefaultValues() {
		Dotenv mockDotenv = mock(Dotenv.class);
		when(mockDotenv.get("SECURITY_PEPPER", "security_pepper")).thenReturn("security_pepper");
		when(mockDotenv.get("APP_ENV", "local")).thenReturn("local");
		when(mockDotenv.get("DB_URL", "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1"))
				.thenReturn("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
		when(mockDotenv.get("DB_USERNAME", "admin")).thenReturn("admin");
		when(mockDotenv.get("DB_PASSWORD", "")).thenReturn("");

		try (MockedStatic<Dotenv> dotenvStatic = mockStatic(Dotenv.class)) {
			DotenvBuilder builder = mock(DotenvBuilder.class);
			when(Dotenv.configure()).thenReturn(builder);
			when(builder.ignoreIfMissing()).thenReturn(builder);
			when(builder.ignoreIfMalformed()).thenReturn(builder);
			when(builder.load()).thenReturn(mockDotenv);

			AppEnv env = AppEnv.load();

			assertThat("security_pepper").isEqualTo(env.getSecurityPepper());
			assertThat("local").isEqualTo(env.getAppEnv());
			assertThat("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1").isEqualTo(env.getDbUrl());
			assertThat("admin").isEqualTo(env.getDbUsername());
			assertThat("").isEqualTo(env.getDbPassword());
		}
	}
}
