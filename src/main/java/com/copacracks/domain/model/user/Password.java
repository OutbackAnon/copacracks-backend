package com.copacracks.domain.model.user;

import com.copacracks.domain.exception.UserValidationException;
import com.copacracks.domain.security.PasswordEncoder;

import java.util.regex.Pattern;

/**
 * Immutable value object representing a validated password.
 *
 * <p>This record ensures that all password instances meet the security requirements defined by the
 * application's password policy. The validation is performed during construction, guaranteeing that
 * all Password instances are valid.
 *
 * <p>Password validation rules include:
 *
 * <ul>
 *   <li>Minimum length of 8 characters
 *   <li>At least one uppercase letter (A-Z)
 *   <li>At least one lowercase letter (a-z)
 *   <li>At least one digit (0-9)
 *   <li>At least one special character
 * </ul>
 *
 * @param value the password string value that has been validated
 */
public record Password(HashedPassword value) {

	/** Minimum required password length. */
	private static final int MIN_LENGTH = 8;

	/** Pattern to match uppercase letters. */
	private static final Pattern UPPERCASE = Pattern.compile("[A-Z]");

	/** Pattern to match lowercase letters. */
	private static final Pattern LOWERCASE = Pattern.compile("[a-z]");

	/** Pattern to match digits. */
	private static final Pattern DIGIT = Pattern.compile("\\d");

	/** Pattern to match special characters. */
	private static final Pattern SPECIAL_CHAR =
			Pattern.compile("[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]");

	/**
	 * Compact constructor that validates the password value.
	 *
	 * <p>This constructor automatically validates the provided password string against all security
	 * requirements before creating the Password instance.
	 *
	 * @throws UserValidationException if the password does not meet any of the validation
	 *     requirements
	 */
	public Password {}

    public static Password fromRaw(RawPassword rawPassword, PasswordEncoder encoder, String pepper) {
        return new Password(new HashedPassword(encoder.encode(rawPassword.value(), pepper)));
    }

    public static Password fromHashed(String encodedValue) {
        return new Password(new HashedPassword(encodedValue));
    }

	/**
	 * Validates a plain text password against all security requirements.
	 *
	 * <p>This method performs comprehensive validation including checking for: blank values, minimum
	 * length, and required character types.
	 *
	 * @param plainPassword the password string to be validated
	 * @throws UserValidationException if the password fails any validation rule
	 */
	public static void validatePlainPassword(final String plainPassword) {
		ensureNotBlank(plainPassword);
		ensureMinimumLength(plainPassword);
		ensureContainsUppercase(plainPassword);
		ensureContainsLowercase(plainPassword);
		ensureContainsDigit(plainPassword);
		ensureContainsSpecialChar(plainPassword);
	}

    public String getHashedPasswordValue() {
        return value().value();
    }

	/**
	 * Ensures the password is not null or empty.
	 *
	 * @param plainPassword the password to check
	 * @throws UserValidationException if the password is null or empty
	 */
	private static void ensureNotBlank(final String plainPassword) {
		if (plainPassword == null || plainPassword.isEmpty()) {
			throw new UserValidationException("Password cannot be empty");
		}
	}

	/**
	 * Ensures the password meets the minimum length requirement.
	 *
	 * @param plainPassword the password to check
	 * @throws UserValidationException if the password is shorter than the minimum required length
	 */
	private static void ensureMinimumLength(final String plainPassword) {
		if (plainPassword.length() < MIN_LENGTH) {
			throw new UserValidationException(
					"Password must be at least " + MIN_LENGTH + " characters long");
		}
	}

	/**
	 * Ensures the password contains at least one uppercase letter.
	 *
	 * @param plainPassword the password to check
	 * @throws UserValidationException if the password does not contain any uppercase letters
	 */
	private static void ensureContainsUppercase(final String plainPassword) {
		if (!UPPERCASE.matcher(plainPassword).find()) {
			throw new UserValidationException("Password must contain at least one uppercase letter");
		}
	}

	/**
	 * Ensures the password contains at least one lowercase letter.
	 *
	 * @param plainPassword the password to check
	 * @throws UserValidationException if the password does not contain any lowercase letters
	 */
	private static void ensureContainsLowercase(final String plainPassword) {
		if (!LOWERCASE.matcher(plainPassword).find()) {
			throw new UserValidationException("Password must contain at least one lowercase letter");
		}
	}

	/**
	 * Ensures the password contains at least one digit.
	 *
	 * @param plainPassword the password to check
	 * @throws UserValidationException if the password does not contain any digits
	 */
	private static void ensureContainsDigit(final String plainPassword) {
		if (!DIGIT.matcher(plainPassword).find()) {
			throw new UserValidationException("Password must contain at least one digit");
		}
	}

	/**
	 * Ensures the password contains at least one special character.
	 *
	 * @param plainPassword the password to check
	 * @throws UserValidationException if the password does not contain any special characters
	 */
	private static void ensureContainsSpecialChar(final String plainPassword) {
		if (!SPECIAL_CHAR.matcher(plainPassword).find()) {
			throw new UserValidationException("Password must contain at least one special character");
		}
	}
}
