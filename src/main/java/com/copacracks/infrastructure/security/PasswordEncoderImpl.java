package com.copacracks.infrastructure.security;

import com.copacracks.domain.security.PasswordEncoder;
import com.copacracks.infrastructure.config.AppConfig;
import com.google.inject.Inject;
import com.password4j.Password;

public class PasswordEncoderImpl implements PasswordEncoder {
	private final String securityPepper;

	@Inject
	public PasswordEncoderImpl(AppConfig config) {
		this.securityPepper = config.securityPepper();
	}

	@Override
	public String encode(String rawPassword, String pepper) {
		return Password.hash(rawPassword)
				.addRandomSalt(32)
				.addPepper(securityPepper)
				.withArgon2()
				.getResult();
	}

	@Override
	public boolean verify(String rawPassword, String hashedPassword, String pepper) {
		return Password.check(rawPassword, hashedPassword).addPepper(securityPepper).withArgon2();
	}
}
