package com.copacracks.domain.exception;

import java.io.Serial;

public class UserAuthException extends RuntimeException {
	@Serial private static final long serialVersionUID = 1L;

	public UserAuthException(final String message) {
		super(message);
	}

	public UserAuthException(final String message, final Throwable cause) {
		super(message, cause);
	}
}
