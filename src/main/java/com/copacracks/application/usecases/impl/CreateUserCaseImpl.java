package com.copacracks.application.usecases.impl;

import com.copacracks.application.dto.NewUserDto;
import com.copacracks.application.usecases.CreateUserCase;
import com.copacracks.domain.model.user.Password;
import com.copacracks.domain.model.user.RawPassword;
import com.copacracks.domain.model.user.User;
import com.copacracks.domain.repository.UserRepository;
import com.copacracks.domain.security.PasswordEncoder;
import com.google.inject.Inject;

public class CreateUserCaseImpl implements CreateUserCase {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Inject
	public CreateUserCaseImpl(
			final UserRepository userRepository, final PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public void execute(final NewUserDto userDto) {
		User.verifyPasswordStrength(userDto.rawPassword());

		final RawPassword rawPassword = new RawPassword(userDto.rawPassword());
		final Password password = Password.fromRaw(rawPassword, passwordEncoder, "security_pepper");

		final User user = new User(userDto.username(), password, userDto.email());

		userRepository.save(user);
	}
}
