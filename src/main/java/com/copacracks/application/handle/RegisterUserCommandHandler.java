package com.copacracks.application.handle;

import com.copacracks.application.command.RegisterUserCommand;
import com.copacracks.domain.event.EventStore;
import com.copacracks.domain.event.user.UserRegisteredEvent;
import com.copacracks.domain.exception.UserValidationException;
import com.copacracks.domain.model.user.User;
import com.copacracks.domain.repository.UserRepository;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class RegisterUserCommandHandler {
  private final UserRepository userRepository;
  private final EventStore eventStore;

  public Long handle(RegisterUserCommand command) {
    if (userRepository.existsByEmail(command.email())) {
      throw new UserValidationException("Email já existe" + command.email());
    }

    if (userRepository.existsByUsername(command.username())) {
      throw new UserValidationException("Usename já existe" + command.username());
    }

    User user = new User(command.username(), command.password(), command.email());

    User savedUser = userRepository.save(user);

    UserRegisteredEvent event =
        new UserRegisteredEvent(
            savedUser.getId().toString(), 1L, savedUser.getUsername(), savedUser.getEmail());

    eventStore.saveEvents(savedUser.getId().toString(), Arrays.asList(event), 0L);

    return savedUser.getId();
  }
}
