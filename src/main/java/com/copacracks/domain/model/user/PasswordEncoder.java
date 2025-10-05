package com.copacracks.domain.model.user;

public interface PasswordEncoder {
	String encode(String rawPassword);

	boolean matches(String rawPassword, String encodedPassword);
}
