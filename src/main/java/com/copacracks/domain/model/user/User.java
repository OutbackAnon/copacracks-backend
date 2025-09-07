package com.copacracks.domain.model.user;

import com.copacracks.domain.exception.UserValidationException;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Rich domain model representing a user entity following Domain-Driven Design principles.
 *
 * <p>This class encapsulates business rules and validations related to users. It serves as an
 * immutable aggregate root that ensures data integrity through value objects and proper validation.
 *
 * <p>The User class supports multiple construction scenarios:
 *
 * <ul>
 *   <li>Creating new users with plain text passwords
 *   <li>Reconstructing existing users with hashed passwords
 *   <li>Creating users with pre-validated value objects
 * </ul>
 *
 * <p>All modification operations return new User instances, maintaining immutability.
 */
public class User {
	private final Long id;
	private final Username username;
	private final Password password;
	private final Email email;
	private final String hashedPassword;
	private final LocalDateTime createdAt;

	/**
	 * Constructor for creating a new user with plain text password.
	 *
	 * <p>This constructor is typically used when creating new users through registration processes.
	 * The creation timestamp is automatically set to now.
	 *
	 * @param username the username string value
	 * @param password the plain text password string
	 * @param email the email address string
	 * @throws UserValidationException if any of the parameters fail validation
	 */
	public User(final String username, final String password, final String email) {
		this(null, username, password, email, null, LocalDateTime.now());
	}

	/**
	 * Constructor for creating a user with complete information including hashed password.
	 *
	 * <p>This constructor allows setting both plain text password and hashed password, typically used
	 * during user creation processes where both values are available.
	 *
	 * @param id the unique identifier, may be null for new users
	 * @param username the username string value
	 * @param password the plain text password string
	 * @param email the email address string
	 * @param hashedPassword the hashed password string, may be null
	 * @param createdAt the creation timestamp
	 * @throws UserValidationException if any of the parameters fail validation
	 */
	public User(
			final Long id,
			final String username,
			final String password,
			final String email,
			final String hashedPassword,
			final LocalDateTime createdAt) {
		this.id = id;
		this.username = new Username(username);
		this.password = new Password(password);
		this.email = new Email(email);
		this.hashedPassword = hashedPassword;
		this.createdAt = createdAt;
	}

	/**
	 * Constructor for reconstructing existing users with hashed passwords only.
	 *
	 * <p>This constructor is typically used when loading users from persistence where only the hashed
	 * password is available and the plain text password is not needed.
	 *
	 * @param id the unique identifier
	 * @param username the username string value
	 * @param hashedPassword the hashed password string
	 * @param email the email address string
	 * @param createdAt the creation timestamp
	 * @throws UserValidationException if any of the parameters fail validation
	 */
	public User(
			final Long id,
			final String username,
			final String hashedPassword,
			final String email,
			final LocalDateTime createdAt) {
		this.id = id;
		this.username = new Username(username);
		this.password = null;
		this.email = new Email(email);
		this.hashedPassword = hashedPassword;
		this.createdAt = createdAt;
	}

	/**
	 * Constructor for creating users with pre-validated value objects.
	 *
	 * <p>This constructor accepts already validated value objects, avoiding redundant validation when
	 * the objects are known to be valid.
	 *
	 * @param id the unique identifier, may be null for new users
	 * @param username the validated username value object
	 * @param password the validated password value object, may be null
	 * @param email the validated email value object
	 * @param hashedPassword the hashed password string, may be null
	 * @param createdAt the creation timestamp
	 */
	public User(
			final Long id,
			final Username username,
			final Password password,
			final Email email,
			final String hashedPassword,
			final LocalDateTime createdAt) {
		this.id = id;
		this.username = username;
		this.password = password;
		this.email = email;
		this.hashedPassword = hashedPassword;
		this.createdAt = createdAt;
	}

	/**
	 * Validates if the provided plain text password matches the stored password.
	 *
	 * <p>This method safely handles cases where either the provided password or the stored password
	 * might be null, returning false in such cases.
	 *
	 * @param plainPassword the plain text password to validate
	 * @return {@code true} if the password is valid and matches the stored password, {@code false}
	 *     otherwise, including when any password is null
	 */
	public boolean isPasswordValid(final String plainPassword) {
		boolean result = false;

		if (plainPassword != null && this.password != null) {
			result = this.password.value().matches(plainPassword);
		}

		return result;
	}

	/**
	 * Creates a new User instance with a different password.
	 *
	 * <p>This method maintains immutability by returning a new User instance with the updated
	 * password while keeping all other fields unchanged.
	 *
	 * @param newPassword the new plain text password
	 * @return a new User instance with the updated password
	 * @throws UserValidationException if the new password fails validation
	 */
	public User withNewPassword(final String newPassword) {
		return new User(
				this.id,
				this.username,
				new Password(newPassword),
				this.email,
				this.hashedPassword,
				this.createdAt);
	}

	/**
	 * Creates a new User instance with a different email address.
	 *
	 * <p>This method maintains immutability by returning a new User instance with the updated email
	 * while keeping all other fields unchanged.
	 *
	 * @param newEmail the new email address
	 * @return a new User instance with the updated email
	 * @throws UserValidationException if the new email fails validation
	 */
	public User withNewEmail(final String newEmail) {
		return new User(
				this.id,
				this.username,
				this.password,
				new Email(newEmail),
				this.hashedPassword,
				this.createdAt);
	}

	/**
	 * Creates a new User instance with a different username.
	 *
	 * <p>This method maintains immutability by returning a new User instance with the updated
	 * username while keeping all other fields unchanged.
	 *
	 * @param newUsername the new username
	 * @return a new User instance with the updated username
	 * @throws UserValidationException if the new username fails validation
	 */
	public User withNewUsername(final String newUsername) {
		return new User(
				this.id,
				new Username(newUsername),
				this.password,
				this.email,
				this.hashedPassword,
				this.createdAt);
	}

	/**
	 * Creates a new User instance with a different hashed password.
	 *
	 * <p>This method is typically used when updating the hashed password after password hashing
	 * operations.
	 *
	 * @param hashedPassword the new hashed password
	 * @return a new User instance with the updated hashed password
	 */
	public User withHashedPassword(final String hashedPassword) {
		return new User(
				this.id, this.username, this.password, this.email, hashedPassword, this.createdAt);
	}

	/**
	 * Checks if this user is new (has no assigned ID yet).
	 *
	 * <p>This method is useful for determining whether a user needs to be inserted or updated in the
	 * persistence layer.
	 *
	 * @return {@code true} if the user is new (ID is null), {@code false} otherwise
	 */
	public boolean isNew() {
		return this.id == null;
	}

	/**
	 * Returns the unique identifier of this user.
	 *
	 * @return the user ID, or null if this is a new user
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Returns the username as a string value.
	 *
	 * @return the username string
	 */
	public String getUsername() {
		return username.value();
	}

	/**
	 * Returns the email address as a string value.
	 *
	 * @return the email address string
	 */
	public String getEmail() {
		return email.value();
	}

	/**
	 * Returns the creation timestamp of this user.
	 *
	 * @return the creation date and time
	 */
	public LocalDateTime getCreateAt() {
		return createdAt;
	}

	/**
	 * Returns the hashed password string.
	 *
	 * @return the hashed password, or null if not set
	 */
	public String getHashedPassword() {
		return hashedPassword;
	}

	/**
	 * Compares this user with another object for equality.
	 *
	 * <p>The equality logic follows these rules:
	 *
	 * <ul>
	 *   <li>If both users have IDs, they are equal if their IDs match
	 *   <li>If neither has an ID, they are equal if username, password, and email all match
	 *   <li>If only one has an ID, they are not equal
	 * </ul>
	 *
	 * @param obj the object to compare with
	 * @return {@code true} if the objects are equal according to the rules above
	 */
	@Override
	public boolean equals(final Object obj) {
		boolean result = false;

		if (this == obj) {
			result = true;
		} else if (obj != null && getClass() == obj.getClass()) {
			final User user = (User) obj;

			// If both have IDs, compare only by ID
			if (id != null && user.id != null) {
				result = Objects.equals(id, user.id);
			} else {
				// If they don't have IDs, all fields must be equal
				result =
						Objects.equals(username, user.username)
								&& Objects.equals(password, user.password)
								&& Objects.equals(email, user.email);
			}
		}

		return result;
	}

	/**
	 * Returns a hash code for this user.
	 *
	 * <p>The hash code is calculated based on:
	 *
	 * <ul>
	 *   <li>Only the ID if it exists
	 *   <li>Username, password, and email if no ID exists
	 * </ul>
	 *
	 * @return the hash code value
	 */
	@Override
	public int hashCode() {
		final int result;

		if (id != null) {
			result = Objects.hash(id);
		} else {
			result = Objects.hash(username, password, email);
		}

		return result;
	}

	/**
	 * Returns a string representation of this user.
	 *
	 * <p>The string includes the ID, username, and email but excludes sensitive information like
	 * passwords.
	 *
	 * @return a string representation in the format "User{id=..., username='...', email='...'}"
	 */
	@Override
	public String toString() {
		return String.format(
				"User{id=%s, username='%s', email='%s'}", id, username.value(), email.value());
	}
}
