package com.copacracks.infrastructure.persistence.repository;

import com.copacracks.infrastructure.exception.DatabaseException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import javax.sql.DataSource;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Abstract base class for JDBC-based repository implementations.
 *
 * <p>This class provides common database operations and utilities for concrete repository
 * implementations. It encapsulates JDBC boilerplate code and provides a higher-level API for
 * database interactions with proper resource management and error handling.
 *
 * <p>Key features:
 *
 * <ul>
 *   <li>Automatic connection and statement resource management
 *   <li>Consistent error handling and logging
 *   <li>Support for insert operations with auto-generated key retrieval
 *   <li>Generic query execution with custom result mapping
 *   <li>Boolean query execution for existence checks
 * </ul>
 *
 * <p>Subclasses should extend this class to implement specific repository contracts while
 * leveraging the common database operations provided.
 *
 * @author CopaCracks Team
 * @version 1.0
 * @since 1.0
 */
@Slf4j
@AllArgsConstructor
public class BaseJdbcRepository {

	/** The data source used for obtaining database connections. */
	protected final DataSource dataSource;

	/**
	 * Executes an INSERT statement and returns the auto-generated primary key.
	 *
	 * <p>This method handles the complete lifecycle of an insert operation:
	 *
	 * <ul>
	 *   <li>Obtains a database connection
	 *   <li>Prepares the statement with auto-generated key retrieval
	 *   <li>Sets parameters using the provided consumer
	 *   <li>Executes the insert and retrieves the generated ID
	 *   <li>Properly closes all resources
	 * </ul>
	 *
	 * @param sql the INSERT SQL statement to execute
	 * @param paramsSetter consumer that sets the prepared statement parameters
	 * @return the auto-generated primary key as a Long value
	 * @throws RuntimeException if a database error occurs during the operation
	 * @throws IllegalArgumentException if sql is null or empty
	 * @throws NullPointerException if paramsSetter is null
	 */
	protected Long executeInsertAndReturnId(
			final String sql, final PreparedStatementConsumer paramsSetter) {
		try (Connection conn = dataSource.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			paramsSetter.accept(stmt);

			final int rowsAffected = stmt.executeUpdate();
			if (rowsAffected == 0) {
				throw new DatabaseException("Insert failed, no rows affected.");
			}

			try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					return generatedKeys.getLong(1);
				} else {
					throw new DatabaseException("Insert failed, no ID obtained.");
				}
			}
		} catch (SQLException e) {
			log.error("Database error during insert operation", e);
			throw new DatabaseException("Database error", e);
		}
	}

	/**
	 * Executes a SELECT query that expects at most one result.
	 *
	 * <p>This method is designed for queries that should return either zero or one row, such as
	 * finding entities by unique identifiers. The result is wrapped in an {@link Optional} to handle
	 * the case where no matching record is found.
	 *
	 * <p>The method handles:
	 *
	 * <ul>
	 *   <li>Connection and statement resource management
	 *   <li>Parameter setting via the provided consumer
	 *   <li>Result set mapping using the provided mapper function
	 *   <li>Proper cleanup of database resources
	 * </ul>
	 *
	 * @param <T> the type of the result object
	 * @param sql the SELECT SQL query to execute
	 * @param paramsSetter consumer that sets the prepared statement parameters
	 * @param mapper function that maps the ResultSet to the desired object type
	 * @return an {@link Optional} containing the mapped result, or empty if no result found
	 * @throws RuntimeException if a database error occurs during the operation
	 * @throws IllegalArgumentException if sql is null or empty
	 * @throws NullPointerException if paramsSetter or mapper is null
	 */
	protected <T> Optional<T> executeSingleResultQuery(
			final String sql,
			final PreparedStatementConsumer paramsSetter,
			final ResultSetMapper<T> mapper) {
		try (Connection conn = dataSource.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {
			paramsSetter.accept(stmt);
			final ResultSet rs = stmt.executeQuery();

			return rs.next() ? Optional.of(mapper.map(rs)) : Optional.empty();
		} catch (SQLException e) {
			log.error("Database error during query execution", e);
			throw new DatabaseException("Database error", e);
		}
	}

	/**
	 * Executes a query that returns a boolean result based on row existence.
	 *
	 * <p>This method is typically used for existence checks, such as verifying if a username already
	 * exists in the database. It returns {@code true} if the query returns at least one row, {@code
	 * false} otherwise.
	 *
	 * <p>Common use cases include:
	 *
	 * <ul>
	 *   <li>Checking if a record exists with specific criteria
	 *   <li>Validating uniqueness constraints
	 *   <li>Performing authorization checks
	 * </ul>
	 *
	 * @param sql the SELECT SQL query to execute
	 * @param paramsSetter consumer that sets the prepared statement parameters
	 * @return {@code true} if the query returns at least one row, {@code false} otherwise
	 * @throws RuntimeException if a database error occurs during the operation
	 * @throws IllegalArgumentException if sql is null or empty
	 * @throws NullPointerException if paramsSetter is null
	 */
	protected boolean executeBooleanQuery(
			final String sql, final PreparedStatementConsumer paramsSetter) {
		try (Connection conn = dataSource.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {
			paramsSetter.accept(stmt);
			try (ResultSet rs = stmt.executeQuery()) {
				return rs.next();
			}
		} catch (SQLException e) {
			log.error("Database error during boolean query", e);
			throw new DatabaseException("Database error", e);
		}
	}

	/**
	 * Functional interface for setting parameters on a PreparedStatement.
	 *
	 * <p>This interface allows callers to provide parameter-setting logic without dealing with the
	 * underlying JDBC resource management. The implementation should set all required parameters on
	 * the provided PreparedStatement.
	 *
	 * <p>Example usage:
	 *
	 * <pre>{@code
	 * stmt -> {
	 * 	stmt.setString(1, username);
	 * 	stmt.setString(2, email);
	 * }
	 * }</pre>
	 */
	@FunctionalInterface
	protected interface PreparedStatementConsumer {

		/**
		 * Sets parameters on the provided PreparedStatement.
		 *
		 * @param stmt the PreparedStatement to configure with parameters
		 * @throws SQLException if a database access error occurs or the parameter index is invalid
		 */
		void accept(PreparedStatement stmt) throws SQLException;
	}

	/**
	 * Functional interface for mapping ResultSet rows to domain objects.
	 *
	 * <p>This interface allows callers to provide custom mapping logic for converting ResultSet data
	 * into domain objects. The mapper should extract all necessary data from the current row of the
	 * ResultSet.
	 *
	 * <p>Example usage:
	 *
	 * <pre>{@code
	 * rs -> new User(rs.getLong("id"), rs.getString("username"), rs.getString("email"))
	 * }</pre>
	 *
	 * @param <T> the type of object to be created from the ResultSet
	 */
	@FunctionalInterface
	protected interface ResultSetMapper<T> {

		/**
		 * Maps the current row of a ResultSet to an object of type T.
		 *
		 * @param rs the ResultSet positioned at the row to be mapped
		 * @return the mapped object of type T
		 * @throws SQLException if a database access error occurs or the column name/index is invalid
		 */
		T map(ResultSet rs) throws SQLException;
	}
}
