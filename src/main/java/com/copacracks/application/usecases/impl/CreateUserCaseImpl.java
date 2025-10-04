package com.copacracks.application.usecases.impl;

import com.copacracks.application.dto.NewUserDto;
import com.copacracks.application.security.PasswordHasher;
import com.copacracks.application.usecases.CreateUserCase;
import com.copacracks.domain.model.user.User;
import com.copacracks.domain.repository.UserRepository;
import com.google.inject.Inject;

public class CreateUserCaseImpl implements CreateUserCase {
	private final UserRepository userRepository;
	private final PasswordHasher passwordHasher;

	@Inject
	public CreateUserCaseImpl(
			final UserRepository userRepository, final PasswordHasher passwordHasher) {
		this.userRepository = userRepository;
		this.passwordHasher = passwordHasher;
	}

	@Override
	public void execute(final NewUserDto userDto) {
		User.verifyPasswordStrength(userDto.rawPassword());

		final String hashedPassword = passwordHasher.createHash(userDto.rawPassword());
		final User user = new User(userDto.username(), hashedPassword, userDto.email());

		userRepository.save(user);
	}
}
