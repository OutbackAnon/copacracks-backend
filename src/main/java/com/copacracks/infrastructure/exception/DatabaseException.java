package com.copacracks.infrastructure.exception;

/**
 * Custom exception for database-related errors.
 *
 * <p>This exception provides more specific error handling than generic RuntimeException and allows
 * callers to handle database errors differently from other runtime errors.
 */
public final class DatabaseException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a new database exception with the specified detail message.
	 *
	 * @param message the detail message
	 */
	public DatabaseException(final String message) {
		super(message);
	}

	/**
	 * Constructs a new database exception with the specified detail message and cause.
	 *
	 * @param message the detail message
	 * @param cause the cause
	 */
	public DatabaseException(final String message, final Throwable cause) {
		super(message, cause);
	}
}
