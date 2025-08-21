package com.copacracks.domain.event.user;

import com.copacracks.domain.event.DomainEvent;

public class UserRegisteredEvent extends DomainEvent {
  private final String username;
  private final String email;

  public UserRegisteredEvent(String aggregateId, Long version, String username, String email) {
    super(aggregateId, version);
    this.username = username;
    this.email = email;
  }

  public String getUsername() {
    return username;
  }

  public String getEmail() {
    return email;
  }
}
