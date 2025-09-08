package com.copacracks.infrastructure.exception;

public class ControllerException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public ControllerException(final String message, final Throwable cause) {
		super(message, cause);
	}
}
