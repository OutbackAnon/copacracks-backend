package com.copacracks.infrastructure.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import javax.sql.DataSource;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public abstract class AbstractJdbcRepository {
  protected final DataSource dataSource;

  protected Long executeInsertAndReturnId(String sql, PreparedStatementConsumer paramsSetter) {
    try (Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
      paramsSetter.accept(stmt);

      int rowsAffected = stmt.executeUpdate();
      if (rowsAffected == 0) {
        throw new SQLException("Insert failed, no rows affected.");
      }

      try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
        if (generatedKeys.next()) {
          return generatedKeys.getLong(1);
        } else {
          throw new SQLException("Insert failed, no ID obtained.");
        }
      }
    } catch (SQLException e) {
      log.error("Database error durging insert operation", e);
      throw new RuntimeException("Database error", e);
    }
  }

  protected <T> Optional<T> executeSingleResultQuery(
      String sql, PreparedStatementConsumer paramsSetter, ResultSetMapper<T> mapper) {
    try (Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
      paramsSetter.accept(stmt);
      ResultSet rs = stmt.executeQuery();

      return rs.next() ? Optional.of(mapper.map(rs)) : Optional.empty();
    } catch (SQLException e) {
      log.error("Database error during query execution", e);
      throw new RuntimeException("Database error", e);
    }
  }

  protected boolean executeBooleanQuery(String sql, PreparedStatementConsumer paramsSetter) {
    try (Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
      paramsSetter.accept(stmt);
      ResultSet rs = stmt.executeQuery();
      return rs.next();
    } catch (SQLException e) {
      log.error("Database error during boolean query", e);
      throw new RuntimeException("Database error", e);
    }
  }

  @FunctionalInterface
  protected interface PreparedStatementConsumer {
    void accept(PreparedStatement stmt) throws SQLException;
  }

  @FunctionalInterface
  protected interface ResultSetMapper<T> {
    T map(ResultSet rs) throws SQLException;
  }
}
