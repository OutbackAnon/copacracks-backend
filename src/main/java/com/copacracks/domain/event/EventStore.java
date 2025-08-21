package com.copacracks.domain.event;

import java.util.List;

public interface EventStore {
  void saveEvents(String aggregateId, List<DomainEvent> events, Long expectedVersion);

  List<DomainEvent> getEventsForAggregate(String aggregateId);
}
