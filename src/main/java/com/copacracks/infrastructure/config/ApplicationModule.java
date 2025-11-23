package com.copacracks.infrastructure.config;

import com.copacracks.Routes;
import com.copacracks.application.usecases.CreateUserCase;
import com.copacracks.application.usecases.UserLoginUseCase;
import com.copacracks.application.usecases.impl.CreateUserCaseImpl;
import com.copacracks.application.usecases.impl.UserLoginUserCaseImpl;
import com.copacracks.domain.repository.UserRepository;
import com.copacracks.domain.repository.UserSessionRepository;
import com.copacracks.domain.security.JwtTokenGenerator;
import com.copacracks.domain.security.PasswordEncoder;
import com.copacracks.infrastructure.controller.AuthController;
import com.copacracks.infrastructure.controller.UserController;
import com.copacracks.infrastructure.persistence.repository.JdbcUserRepository;
import com.copacracks.infrastructure.persistence.repository.JdbcUserSessionRepository;
import com.copacracks.infrastructure.security.JwtTokenGeneratorImpl;
import com.copacracks.infrastructure.security.PasswordEncoderImpl;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import javax.sql.DataSource;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ApplicationModule extends AbstractModule {
	@Override
	protected void configure() {
		bind(Routes.class).asEagerSingleton();
		bind(UserController.class).asEagerSingleton();
		bind(AuthController.class).asEagerSingleton();

		// Bind interfaces to implementations
		bind(UserRepository.class).to(JdbcUserRepository.class);
		bind(UserSessionRepository.class).to(JdbcUserSessionRepository.class);
		bind(CreateUserCase.class).to(CreateUserCaseImpl.class);
		bind(UserLoginUseCase.class).to(UserLoginUserCaseImpl.class);
		bind(PasswordEncoder.class).to(PasswordEncoderImpl.class);
		bind(JwtTokenGenerator.class).to(JwtTokenGeneratorImpl.class);
	}

	@Provides
	@Singleton
	public DataSource provideDataSource(AppConfig appConfig) {
		return new DatabaseProvider(appConfig).get();
	}

	@Provides
	@Singleton
	public AppConfig provideAppConfig() {
		return new AppConfigProvider().get();
	}
}
