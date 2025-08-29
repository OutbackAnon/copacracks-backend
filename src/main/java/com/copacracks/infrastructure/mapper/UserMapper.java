package com.copacracks.infrastructure.mapper;

import com.copacracks.domain.model.user.User;
import com.copacracks.infrastructure.persistence.entity.UserEntity;
import java.sql.Timestamp;
import java.time.ZoneOffset;

public class UserMapper {
  public static UserEntity fromModel(User user) {
    var timestamp = Timestamp.from(user.getCreateAt().toInstant(ZoneOffset.UTC));

    return UserEntity.builder()
        .username(user.getUsername())
        .password(user.getHashedPassword())
        .email(user.getEmail())
        .createdAt(timestamp)
        .build();
  }

  public static User toModel(UserEntity userEntity) {
    return new User(
        userEntity.getId(),
        userEntity.getUsername(),
        null,
        userEntity.getEmail(),
        userEntity.getPassword(),
        userEntity.getCreatedAt().toLocalDateTime());
  }
}
