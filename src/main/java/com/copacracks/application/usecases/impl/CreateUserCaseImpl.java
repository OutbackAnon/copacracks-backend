package com.copacracks.application.usecases.impl;

import com.copacracks.application.dto.CreateUserRequestDto;
import com.copacracks.application.security.PasswordHasher;
import com.copacracks.application.usecases.CreateUserCase;
import com.copacracks.domain.model.user.User;
import com.copacracks.domain.repository.UserRepository;
import com.google.inject.Inject;

public class CreateUserCaseImpl implements CreateUserCase {
  private UserRepository userRepository;
  private PasswordHasher passwordHasher;

  @Inject
  public CreateUserCaseImpl(UserRepository userRepository, PasswordHasher passwordHasher) {
    this.userRepository = userRepository;
    this.passwordHasher = passwordHasher;
  }

  @Override
  public void execute(CreateUserRequestDto userDto) {
    var user = new User(userDto.username(), userDto.password(), userDto.email());
    var hash = passwordHasher.createHash(userDto.password());

    var userWithhashedPassword = user.withHashedPassword(hash);

    userRepository.save(userWithhashedPassword);
  }
}
