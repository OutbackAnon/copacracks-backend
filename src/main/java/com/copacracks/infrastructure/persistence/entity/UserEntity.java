package com.copacracks.infrastructure.persistence.entity;

import java.sql.Timestamp;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserEntity {
  private final Long id;
  private final String username;
  private final String password;
  private final String email;
  private final Timestamp createdAt;
}
