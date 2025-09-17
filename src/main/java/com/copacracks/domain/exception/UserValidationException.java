package com.copacracks.domain.exception;

/**
 * Exception thrown when there are validation problems with the user.
 *
 * <p>This runtime exception is used to indicate that user data or user-related operations have
 * failed validation checks. It extends {@link RuntimeException} to provide unchecked exception
 * behavior for validation failures.
 */
public class UserValidationException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a new UserValidationException with the specified detail message.
	 *
	 * <p>The detail message is saved for later retrieval by the {@link #getMessage()} method.
	 *
	 * @param message the detail message explaining the validation failure. The detail message is
	 *     saved for later retrieval by the {@link #getMessage()} method.
	 */
	public UserValidationException(final String message) {
		super(message);
	}

	/**
	 * Constructs a new UserValidationException with the specified detail message and cause.
	 *
	 * <p>The detail message is saved for later retrieval by the {@link #getMessage()} method, and the
	 * cause is saved for later retrieval by the {@link #getCause()} method.
	 *
	 * @param message the detail message explaining the validation failure (which is saved for later
	 *     retrieval by the {@link #getMessage()} method)
	 * @param cause the cause of the validation failure (which is saved for later retrieval by the
	 *     {@link #getCause()} method). A null value is permitted, and indicates that the cause is
	 *     nonexistent or unknown.
	 */
	public UserValidationException(final String message, final Throwable cause) {
		super(message, cause);
	}
}
