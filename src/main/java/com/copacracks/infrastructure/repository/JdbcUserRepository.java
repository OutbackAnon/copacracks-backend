package com.copacracks.infrastructure.repository;

import com.copacracks.domain.model.user.User;
import com.copacracks.domain.repository.UserRepository;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class JdbcUserRepository implements UserRepository {
  private final DataSource dataSource;

  private static final String INSERT_USER =
      "INSERT INTO users (username, password_hash, email, created_at) VALUES (?, ?, ?, NOW()) RETURNING id";

  private static final String FIND_BY_ID =
      "SELECT id, username, password_hash, email FROM users WHERE id = ?";

  private static final String FIND_BY_USERNAME =
      "SELECT id, username, password_hash, email FROM users WHERE username = ?";

  private static final String FIND_BY_EMAIL =
      "SELECT id, username, password_hash, email FROM users WHERE email = ?";

  private static final String EXISTS_BY_USERNAME = "SELECT 1 FROM users WHERE username = ? LIMIT 1";

  private static final String EXISTS_BY_EMAIL = "SELECT 1 FROM users WHERE email = ? LIMIT 1";

  @Override
  public User save(User user) {
    if (user.isNew()) {
      return insertUser(user);
    } else {
      throw new UnsupportedOperationException("User update not implemented yet");
    }
  }

  @Override
  public Optional<User> findById(Long id) {
    try (Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement(FIND_BY_ID)) {

      stmt.setLong(1, id);
      ResultSet rs = stmt.executeQuery();

      return rs.next() ? Optional.of(mapResultSetToUser(rs)) : Optional.empty();

    } catch (SQLException e) {
      log.error("Error finding user by id: {}", id, e);
      throw new RuntimeException("Database error", e);
    }
  }

  @Override
  public Optional<User> findByUsername(String username) {
    try (Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement(FIND_BY_USERNAME)) {

      stmt.setString(1, username);
      ResultSet rs = stmt.executeQuery();

      return rs.next() ? Optional.of(mapResultSetToUser(rs)) : Optional.empty();

    } catch (SQLException e) {
      log.error("Error finding user by username: {}", username, e);
      throw new RuntimeException("Database error", e);
    }
  }

  @Override
  public Optional<User> findByEmail(String email) {
    try (Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement(FIND_BY_EMAIL)) {

      stmt.setString(1, email);
      ResultSet rs = stmt.executeQuery();

      return rs.next() ? Optional.of(mapResultSetToUser(rs)) : Optional.empty();

    } catch (SQLException e) {
      log.error("Error finding user by email: {}", email, e);
      throw new RuntimeException("Database error", e);
    }
  }

  @Override
  public boolean existsByUsername(String username) {
    try (Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement(EXISTS_BY_USERNAME)) {

      stmt.setString(1, username);
      ResultSet rs = stmt.executeQuery();

      return rs.next();

    } catch (SQLException e) {
      log.error("Error checking if username exists: {}", username, e);
      throw new RuntimeException("Database error", e);
    }
  }

  @Override
  public boolean existsByEmail(String email) {
    try (Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement(EXISTS_BY_EMAIL)) {

      stmt.setString(1, email);
      ResultSet rs = stmt.executeQuery();

      return rs.next();

    } catch (SQLException e) {
      log.error("Error checking if email exists: {}", email, e);
      throw new RuntimeException("Database error", e);
    }
  }

  private User insertUser(User user) {
    try (Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement(INSERT_USER)) {

      stmt.setString(1, user.getUsername());
      stmt.setString(2, hashPassword(user)); // Você precisa implementar hash da senha
      stmt.setString(3, user.getEmail());

      ResultSet rs = stmt.executeQuery();
      if (rs.next()) {
        Long id = rs.getLong("id");
        // Retorna um novo User com o ID gerado
        return new User(id, user.getUsername(), "hashedPassword", user.getEmail());
      }

      throw new RuntimeException("Failed to insert user");

    } catch (SQLException e) {
      log.error("Error inserting user: {}", user.getUsername(), e);
      throw new RuntimeException("Database error", e);
    }
  }

  private User mapResultSetToUser(ResultSet rs) throws SQLException {
    return new User(
        rs.getLong("id"),
        rs.getString("username"),
        rs.getString("password_hash"),
        rs.getString("email"));
  }

  private String hashPassword(User user) {
    // TODO: Implementar hash real da senha (BCrypt, etc.)
    return "hashed_" + user.getUsername();
  }
}
