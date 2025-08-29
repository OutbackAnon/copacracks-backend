package com.copacracks.infrastructure.repository;

import com.copacracks.domain.model.user.User;
import com.copacracks.domain.repository.UserRepository;
import com.copacracks.infrastructure.mapper.UserMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Optional;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
public class JdbcUserRepository extends AbstractJdbcRepository implements UserRepository {

  private static final String INSERT_USER =
      "INSERT INTO users (username, password_hash, email, created_at) VALUES (?, ?, ?, ?) RETURNING id";

  private static final String FIND_BY_ID =
      "SELECT id, username, password_hash, email FROM users WHERE id = ?";

  private static final String FIND_BY_USERNAME =
      "SELECT id, username, password_hash, email FROM users WHERE username = ?";

  private static final String EXISTS_BY_USERNAME = "SELECT 1 FROM users WHERE username = ? LIMIT 1";

  @Inject
  public JdbcUserRepository(DataSource dataSource) {
    super(dataSource);
  }

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
    return executeSingleResultQuery(
        FIND_BY_ID, stmt -> stmt.setLong(1, id), this::mapResultSetToUser);
  }

  @Override
  public Optional<User> findByUsername(String username) {
    return executeSingleResultQuery(
        FIND_BY_USERNAME, stmt -> stmt.setString(1, username), this::mapResultSetToUser);
  }

  @Override
  public boolean existsByUsername(String username) {
    return executeBooleanQuery(EXISTS_BY_USERNAME, stmt -> stmt.setString(1, username));
  }

  private User insertUser(User user) {
    var mappedUser = UserMapper.fromModel(user);
    Long generateId =
        executeInsertAndReturnId(
            INSERT_USER,
            stmt -> {
              stmt.setString(1, mappedUser.getUsername());
              stmt.setString(2, mappedUser.getPassword());
              stmt.setString(3, mappedUser.getEmail());
              stmt.setTimestamp(4, mappedUser.getCreatedAt());
            });

    return new User(
        generateId,
        user.getUsername(),
        user.getHashedPassword(),
        user.getEmail(),
        user.getCreateAt());
  }

  private User mapResultSetToUser(ResultSet rs) throws SQLException {
    return new User(
        rs.getLong("id"),
        rs.getString("username"),
        rs.getString("password_raw"),
        rs.getString("email"),
        rs.getString("password_hash"),
        rs.getObject("created_at", LocalDateTime.class));
  }
}
