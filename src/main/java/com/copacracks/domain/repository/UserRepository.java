package com.copacracks.domain.repository;

import com.copacracks.domain.model.user.User;
import com.copacracks.infrastructure.persistence.entity.UserEntity;

import java.util.Optional;

/**
 * Repository interface for managing User entities.
 *
 * <p>This interface provides the contract for data access operations related to User entities. It
 * defines methods for creating, retrieving, and querying user data from the underlying data storage
 * mechanism.
 *
 * <p>Implementations of this interface should handle the persistence layer operations and provide
 * appropriate error handling for data access failures.
 */
public interface UserRepository {
	/**
	 * Saves a user entity to the repository.
	 *
	 * <p>This method persists the given user to the underlying data storage. If the user already
	 * exists, it will be updated with the new information. If the user is new, it will be created in
	 * the repository.
	 *
	 * @param user the user entity to be saved, must not be null
	 * @return the saved user entity, potentially with generated fields like ID
	 * @throws IllegalArgumentException if the user parameter is null
	 */
	Long save(User user);

	/**
	 * Retrieves a user by their unique identifier.
	 *
	 * <p>This method searches for a user with the specified ID in the repository.
	 *
	 * @param id the unique identifier of the user to retrieve, must not be null
	 * @return an {@link Optional} containing the user if found, or empty if not found
	 * @throws IllegalArgumentException if the id parameter is null
	 */
	Optional<UserEntity> findById(Long id);

	/**
	 * Retrieves a user by their username.
	 *
	 * <p>This method searches for a user with the specified username in the repository. Username
	 * lookups are typically case-sensitive depending on the implementation.
	 *
	 * @param username the username of the user to retrieve, must not be null or empty
	 * @return an {@link Optional} containing the user if found, or empty if not found
	 * @throws IllegalArgumentException if the username parameter is null or empty
	 */
	Optional<UserEntity> findByUsername(String username);

	/**
	 * Checks if a user with the specified username exists in the repository.
	 *
	 * <p>This method provides a way to verify username availability without retrieving the full user
	 * entity, which can be more efficient for validation purposes.
	 *
	 * @param username the username to check for existence, must not be null or empty
	 * @return {@code true} if a user with the given username exists, {@code false} otherwise
	 * @throws IllegalArgumentException if the username parameter is null or empty
	 */
	boolean existsByUsername(String username);
}
