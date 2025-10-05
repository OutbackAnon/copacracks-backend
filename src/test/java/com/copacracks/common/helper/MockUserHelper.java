package com.copacracks.common.helper;

import com.copacracks.domain.model.user.HashedPassword;
import com.copacracks.domain.model.user.Password;
import com.copacracks.domain.model.user.User;
import lombok.Builder;

import java.time.Instant;

public class MockUserHelper {
    public static final String VALID_USERNAME = "john_doe";
    public static final String VALID_EMAIL = "john@example.com";
    public static final String VALID_PASSWORD = "ValidPass123!";
    public static final String VALID_HASHED_PASSWORD = "$2a$10$hashedPassword";
    public static final Long VALID_ID = 1L;
    public static final Instant VALID_CREATE_AT = Instant.now();

    public static User createValidUser() {
        return new User(VALID_ID, VALID_USERNAME, createPassword(), VALID_EMAIL, VALID_CREATE_AT);
    }

    public static User createUserWithCreatedAt(Instant createdAt) {
        return new User(VALID_ID, VALID_USERNAME, createPassword(), VALID_EMAIL, createdAt);
    }

    public static User createUserWithNullCreatedAt() {
        return new User(VALID_ID, VALID_USERNAME, createPassword(), VALID_EMAIL, null);
    }

    public static UserBuilder.UserBuilderBuilder createUserBuilder() {
        return UserBuilder.builder()
                .id(VALID_ID)
                .username(VALID_USERNAME)
                .password(VALID_PASSWORD)
                .email(VALID_EMAIL)
                .createdAt(VALID_CREATE_AT);
    }

    private static Password createPassword() {
        return new Password(new HashedPassword(VALID_HASHED_PASSWORD));
    }

    public static Password createPassword(String password) {
        return new Password(new HashedPassword(password));
    }

    @Builder
    public record UserBuilder(Long id, String username, String password, String email, Instant createdAt) {
        public User createUser() {
            return new User(id(), username(), createPassword(password()), email(), createdAt());
        }
    }
}
