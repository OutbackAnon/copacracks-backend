package com.copacracks.infrastructure.security;

import com.copacracks.domain.security.PasswordEncoder;
import com.password4j.Password;

public class PasswordEncoderImpl implements PasswordEncoder {
	@Override
	public String encode(String rawPassword, String pepper) {
		return Password.hash(rawPassword).addRandomSalt(32).addPepper(pepper).withArgon2().getResult();
	}

	@Override
	public boolean verify(String rawPassword, String hashedPassword, String pepper) {
		return Password.check(rawPassword, hashedPassword).addPepper(pepper).withArgon2();
	}
}
