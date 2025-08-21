package com.copacracks.application.service;

import com.copacracks.application.command.RegisterUserCommand;
import com.copacracks.application.handle.GetUserByIdQueryHandler;
import com.copacracks.application.handle.RegisterUserCommandHandler;
import com.copacracks.application.query.GetUserByIdQuery;
import com.copacracks.domain.model.user.User;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class UserService {
  private final RegisterUserCommandHandler registerUserCommandHandler;
  private final GetUserByIdQueryHandler getUserByIdQueryHandler;

  public Long registerUser(RegisterUserCommand command) {
    return registerUserCommandHandler.handle(command);
  }

  public Optional<User> getUserById(Long id) {
    return getUserByIdQueryHandler.handle(new GetUserByIdQuery(id));
  }
}
