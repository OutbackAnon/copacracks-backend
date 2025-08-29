package com.copacracks.infrastructure.config;

import com.copacracks.application.security.PasswordHasher;
import com.copacracks.application.usecases.CreateUserCase;
import com.copacracks.application.usecases.impl.CreateUserCaseImpl;
import com.copacracks.domain.repository.UserRepository;
import com.copacracks.infrastructure.repository.JdbcUserRepository;
import com.copacracks.infrastructure.security.PasswordHasherImpl;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import javax.sql.DataSource;

public class ApplicationModule extends AbstractModule {
  @Override
  protected void configure() {
    // Bind interfaces to implementations
    bind(UserRepository.class).to(JdbcUserRepository.class);
    bind(CreateUserCase.class).to(CreateUserCaseImpl.class);
    bind(PasswordHasher.class).to(PasswordHasherImpl.class);
  }

  @Provides
  @Singleton
  public DataSource provideDataSource() {
    return new DatabaseProvider().get();
  }
}
