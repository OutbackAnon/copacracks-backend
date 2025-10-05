package com.copacracks.infrastructure.persistence.entity;

import java.sql.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
@SuppressWarnings({"PMD.ReplaceJavaUtilDate"})
public class UserEntity {
	private final Long id;
	private final String username;
	private final String password;
	private final String email;
	private final Timestamp createdAt;
}
