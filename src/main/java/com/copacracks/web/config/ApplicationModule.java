package com.copacracks.web.config;

import com.copacracks.domain.event.EventStore;
import com.copacracks.domain.repository.UserRepository;
import com.copacracks.infrastructure.config.DatabaseProvider;
import com.copacracks.infrastructure.event.JdbcEventStore;
import com.copacracks.infrastructure.repository.JdbcUserRepository;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import javax.sql.DataSource;

public class ApplicationModule extends AbstractModule {
  @Override
  protected void configure() {
    // Bind interfaces to implementations
    bind(UserRepository.class).to(JdbcUserRepository.class);
    bind(EventStore.class).to(JdbcEventStore.class);
  }

  @Provides
  @Singleton
  public DataSource provideDataSource() {
    return new DatabaseProvider().get();
  }
}
