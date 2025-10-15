package com.copacracks.infrastructure.persistence.repository;

import com.copacracks.domain.model.user.User;
import com.copacracks.domain.repository.UserRepository;
import com.copacracks.infrastructure.mapper.UserMapper;
import com.copacracks.infrastructure.persistence.entity.UserEntity;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Optional;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;

/**
 * JDBC-based implementation of the UserRepository interface.
 *
 * <p>This repository provides persistent storage operations for User entities using direct JDBC
 * operations. It extends {@link BaseJdbcRepository} to leverage common database utilities while
 * implementing the specific business logic for user data access.
 *
 * <p>Key features:
 *
 * <ul>
 *   <li>User creation with auto-generated primary keys
 *   <li>User retrieval by ID and username
 *   <li>Username existence validation
 *   <li>Proper mapping between domain models and database entities
 *   <li>Comprehensive error handling and logging
 * </ul>
 *
 * <p>This implementation is thread-safe and designed as a singleton to be shared across the
 * application through dependency injection.
 */
@Slf4j
@Singleton
public class JdbcUserRepository extends BaseJdbcRepository implements UserRepository {

	/** SQL statement for inserting a new user with auto-generated ID. */
	private static final String INSERT_USER =
			"INSERT INTO users (username, password, email, created_at) " + "VALUES (?, ?, ?, ?)";

	/** SQL statement for finding a user by their unique identifier. */
	private static final String FIND_BY_ID =
			"SELECT id, username, password, email FROM users WHERE id = ?";

	/** SQL statement for finding a user by their username. */
	private static final String FIND_BY_USERNAME =
			"SELECT id, username, password, email FROM users " + "WHERE username = ?";

	/** SQL statement for checking if a username exists in the database. */
	private static final String EXISTS_BY_USERNAME = "SELECT 1 FROM users WHERE username = ? LIMIT 1";

	/**
	 * Constructs a new JdbcUserRepository with the specified DataSource.
	 *
	 * <p>This constructor is called by the dependency injection framework to provide the database
	 * connection source.
	 *
	 * @param dataSource the DataSource for obtaining database connections
	 */
	@Inject
	public JdbcUserRepository(final DataSource dataSource) {
		super(dataSource);
	}

	/**
	 * Saves a user entity to the database.
	 *
	 * <p>This implementation currently supports only user creation (insert) operations. For new users
	 * (those without an ID), it performs an insert operation and returns the user with the generated
	 * ID. Update operations are not yet implemented.
	 *
	 * @param user the user entity to save, must not be null
	 * @return the saved user with generated ID if it was a new user
	 * @throws UnsupportedOperationException if attempting to update an existing user
	 * @throws RuntimeException if a database error occurs during the operation
	 * @throws IllegalArgumentException if user is null
	 */
	@Override
	public Long save(final User user) {
		if (user.isNew()) {
			return insertUser(user);
		} else {
			throw new UnsupportedOperationException("User update not implemented yet");
		}
	}

	/**
	 * Retrieves a user by their unique identifier.
	 *
	 * <p>This method queries the database for a user with the specified ID and returns it wrapped in
	 * an Optional. If no user is found with the given ID, an empty Optional is returned.
	 *
	 * @param id the unique identifier of the user to retrieve, must not be null
	 * @return an {@link Optional} containing the user if found, empty otherwise
	 * @throws RuntimeException if a database error occurs during the operation
	 * @throws IllegalArgumentException if id is null
	 */
	@Override
	public Optional<UserEntity> findById(final Long id) {
		return executeSingleResultQuery(
				FIND_BY_ID, stmt -> stmt.setLong(1, id), this::mapResultSetToUser);
	}

	/**
	 * Retrieves a user by their username.
	 *
	 * <p>This method performs a case-sensitive search for a user with the specified username. The
	 * result is wrapped in an Optional to handle cases where no matching user is found.
	 *
	 * @param username the username to search for, must not be null or empty
	 * @return an {@link Optional} containing the user if found, empty otherwise
	 * @throws RuntimeException if a database error occurs during the operation
	 * @throws IllegalArgumentException if username is null or empty
	 */
	@Override
	public Optional<UserEntity> findByUsername(final String username) {
		return executeSingleResultQuery(
				FIND_BY_USERNAME, stmt -> stmt.setString(1, username), this::mapResultSetToUser);
	}

	/**
	 * Checks if a user with the specified username exists in the database.
	 *
	 * <p>This method provides an efficient way to verify username availability without retrieving the
	 * full user entity. It's particularly useful for validation during user registration processes.
	 *
	 * @param username the username to check for existence, must not be null
	 * @return {@code true} if a user with the username exists, {@code false} otherwise
	 * @throws RuntimeException if a database error occurs during the operation
	 * @throws IllegalArgumentException if username is null or empty
	 */
	@Override
	public boolean existsByUsername(final String username) {
		return executeBooleanQuery(EXISTS_BY_USERNAME, stmt -> stmt.setString(1, username));
	}

	/**
	 * Inserts a new user into the database and returns the user with generated ID.
	 *
	 * <p>This method performs the following operations:
	 *
	 * <ul>
	 *   <li>Maps the domain model to a database entity using UserMapper
	 *   <li>Executes the insert statement with parameter binding
	 *   <li>Retrieves the auto-generated primary key
	 *   <li>Constructs and returns a new User instance with the generated ID
	 * </ul>
	 *
	 * @param user the new user to insert, must not be null and must be new
	 * @return a new User instance with the generated database ID
	 * @throws RuntimeException if a database error occurs during insertion
	 */
	private Long insertUser(final User user) {
		final UserEntity mappedUser = UserMapper.fromModel(user);

		return executeInsertAndReturnId(
				INSERT_USER,
				stmt -> {
					stmt.setString(1, mappedUser.getUsername());
					stmt.setString(2, mappedUser.getPassword());
					stmt.setString(3, mappedUser.getEmail());
					stmt.setTimestamp(4, mappedUser.getCreatedAt());
				});
	}

	/**
	 * Maps a ResultSet row to a User domain model instance.
	 *
	 * <p>This method extracts user data from the current ResultSet position and constructs a User
	 * domain model. It uses the constructor that accepts hashed passwords to avoid validation of the
	 * encrypted password data.
	 *
	 * <p>Expected ResultSet columns:
	 *
	 * <ul>
	 *   <li>id - the user's unique identifier
	 *   <li>username - the user's username
	 *   <li>password_hash - the encrypted password
	 *   <li>email - the user's email address
	 *   <li>created_at - the user's creation timestamp
	 * </ul>
	 *
	 * @param rs the ResultSet positioned at the row to be mapped
	 * @return a new User instance populated with data from the ResultSet
	 * @throws SQLException if a database access error occurs or column is missing
	 */
	private UserEntity mapResultSetToUser(final ResultSet rs) throws SQLException {
		return new UserEntity(
				rs.getLong("id"),
				rs.getString("username"),
				rs.getString("password"),
				rs.getString("email"),
				rs.getObject("created_at", Timestamp.class));
	}
}
