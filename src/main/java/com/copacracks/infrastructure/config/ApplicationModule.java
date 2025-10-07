package com.copacracks.infrastructure.config;

import com.copacracks.application.usecases.CreateUserCase;
import com.copacracks.application.usecases.impl.CreateUserCaseImpl;
import com.copacracks.domain.repository.UserRepository;
import com.copacracks.domain.security.PasswordEncoder;
import com.copacracks.infrastructure.persistence.repository.JdbcUserRepository;
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
		// Bind interfaces to implementations
		bind(UserRepository.class).to(JdbcUserRepository.class);
		bind(CreateUserCase.class).to(CreateUserCaseImpl.class);
		bind(PasswordEncoder.class).to(PasswordEncoderImpl.class);
	}

	@Provides
	@Singleton
	public DataSource provideDataSource() {
		return new DatabaseProvider().get();
	}

	@Provides
	@Singleton
	public AppConfig provideAppConfig() {
		return new AppConfigProvider().get();
	}
}
