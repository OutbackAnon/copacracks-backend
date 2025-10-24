package com.copacracks.application.usecases.impl;

import com.copacracks.application.dto.NewUserDto;
import com.copacracks.application.usecases.CreateUserCase;
import com.copacracks.domain.model.user.Password;
import com.copacracks.domain.model.user.RawPassword;
import com.copacracks.domain.model.user.User;
import com.copacracks.domain.repository.UserRepository;
import com.copacracks.domain.security.PasswordEncoder;
import com.copacracks.infrastructure.config.AppConfig;
import com.google.inject.Inject;

public class CreateUserCaseImpl implements CreateUserCase {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final AppConfig appConfig;

	@Inject
	public CreateUserCaseImpl(
			final UserRepository userRepository,
			final PasswordEncoder passwordEncoder,
			final AppConfig appConfig) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.appConfig = appConfig;
	}

	@Override
	public void execute(final NewUserDto userDto) {
		User.verifyPasswordStrength(userDto.rawPassword());

		final String securityPepper = appConfig.env().getSecurityPepper();

		final RawPassword rawPassword = new RawPassword(userDto.rawPassword());
		final Password password = Password.fromRaw(rawPassword, passwordEncoder, securityPepper);

		final User user = new User(userDto.username(), password, userDto.email());

		userRepository.save(user);
	}
}
