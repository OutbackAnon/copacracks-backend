package com.copacracks.infrastructure.mapper;

import com.copacracks.domain.model.user.User;
import com.copacracks.infrastructure.persistence.entity.UserEntity;
import java.sql.Timestamp;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Utility class for mapping between User domain model and UserEntity persistence model.
 *
 * <p>This mapper provides bidirectional conversion between the rich domain model ({@link User}) and
 * the persistence entity ({@link UserEntity}). It handles the necessary data transformations and
 * type conversions required for persistence operations.
 *
 * <p>The mapper follows these conversion rules:
 *
 * <ul>
 *   <li>Domain to Entity: Converts LocalDateTime to Timestamp using UTC timezone
 *   <li>Entity to Domain: Uses hashed password constructor to avoid plain text validation
 *   <li>Maintains data integrity during the conversion process
 * </ul>
 *
 * <p>This class is stateless and thread-safe, providing only static utility methods.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings({"PMD.ReplaceJavaUtilDate", "PMD.LawOfDemeter"})
public final class UserMapper {

	/**
	 * Converts a User domain model to a UserEntity for persistence.
	 *
	 * <p>This method transforms the rich domain model into a simple data structure suitable for
	 * database persistence. The conversion includes:
	 *
	 * <ul>
	 *   <li>Extracting primitive values from value objects
	 *   <li>Converting LocalDateTime to SQL Timestamp using UTC timezone
	 *   <li>Using the hashed password instead of plain text password
	 * </ul>
	 *
	 * <p>Note: The ID field is not mapped as it's typically handled by the persistence layer during
	 * insert operations.
	 *
	 * @param user the domain model user to convert, must not be null
	 * @return a new UserEntity instance populated with data from the domain model
	 * @throws IllegalArgumentException if user is null
	 * @throws NullPointerException if user.getCreateAt() returns null
	 */
	public static UserEntity fromModel(final User user) {
		final Instant createAt = user.getCreateAt();
		final Timestamp timestamp = Timestamp.from(createAt);

		return UserEntity.builder()
				.username(user.getUsername())
				.password(user.getHashedPassword())
				.email(user.getEmail())
				.createdAt(timestamp)
				.build();
	}

	/**
	 * Converts a UserEntity from persistence to a User domain model.
	 *
	 * <p>This method reconstructs the rich domain model from the persistence entity. The conversion
	 * includes:
	 *
	 * <ul>
	 *   <li>Converting SQL Timestamp back to LocalDateTime
	 *   <li>Using the hashed password constructor to avoid plain text validation
	 *   <li>Preserving the entity ID for existing users
	 * </ul>
	 *
	 * <p>The plain text password parameter is set to null since only the hashed password is available
	 * from the persistence layer. This uses the specialized User constructor that accepts hashed
	 * passwords.
	 *
	 * @param userEntity the persistence entity to convert, must not be null
	 * @return a new User domain model instance populated with data from the entity
	 * @throws IllegalArgumentException if userEntity is null
	 * @throws NullPointerException if any required entity field is null
	 */
	public static User toModel(final UserEntity userEntity) {
		final Timestamp entityCreatedAt = userEntity.getCreatedAt();
		final Instant createdAtInstant = entityCreatedAt.toInstant();

		return new User(
				userEntity.getId(),
				userEntity.getUsername(),
				userEntity.getPassword(),
				userEntity.getEmail(),
				createdAtInstant);
	}
}
