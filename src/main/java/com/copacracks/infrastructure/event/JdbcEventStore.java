package com.copacracks.infrastructure.event;

import com.copacracks.domain.event.DomainEvent;
import com.copacracks.domain.event.EventStore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class JdbcEventStore implements EventStore {
  private final DataSource dataSource;
  private final ObjectMapper objectMapper = new ObjectMapper();

  private static final String INSERT_EVENT =
      "INSERT INTO events (aggregate_id, event_type, event_data, version, occurred_on) VALUES (?, ?, ?::jsonb, ?, ?)";

  private static final String FIND_EVENTS =
      "SELECT event_type, event_data, version, occurred_on FROM events WHERE aggregate_id = ? ORDER BY version";

  @Override
  public void saveEvents(String aggregateId, List<DomainEvent> events, Long expectedVersion) {
    try (Connection conn = dataSource.getConnection()) {
      conn.setAutoCommit(false);

      try (PreparedStatement stmt = conn.prepareStatement(INSERT_EVENT)) {
        for (DomainEvent event : events) {
          stmt.setString(1, aggregateId);
          stmt.setString(2, event.getClass().getSimpleName());
          stmt.setString(3, objectMapper.writeValueAsString(event));
          stmt.setLong(4, event.getVersion());
          stmt.setTimestamp(5, Timestamp.from(event.getOccurredOn()));
          stmt.addBatch();
        }

        stmt.executeBatch();
        conn.commit();

      } catch (Exception e) {
        conn.rollback();
        throw e;
      }

    } catch (Exception e) {
      log.error("Error saving events for aggregate: {}", aggregateId, e);
      throw new RuntimeException("Database error", e);
    }
  }

  @Override
  public List<DomainEvent> getEventsForAggregate(String aggregateId) {
    // Implementação simplificada - em produção você desserializaria os eventos
    return new ArrayList();
  }
}
