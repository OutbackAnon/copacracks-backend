package com.copacracks.domain.event;

import java.time.Instant;
import java.util.UUID;

public abstract class DomainEvent {
  private final UUID eventId;
  private final Instant occurredOn;
  private final String aggregateId;
  private final Long version;

  protected DomainEvent(String aggregateId, Long version) {
    this.eventId = UUID.randomUUID();
    this.occurredOn = Instant.now();
    this.aggregateId = aggregateId;
    this.version = version;
  }

  public UUID getEventId() {
    return eventId;
  }

  public Instant getOccurredOn() {
    return occurredOn;
  }

  public String getAggregateId() {
    return aggregateId;
  }

  public Long getVersion() {
    return version;
  }
}
