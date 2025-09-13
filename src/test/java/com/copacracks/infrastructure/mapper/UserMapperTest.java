package com.copacracks.infrastructure.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.copacracks.domain.model.user.User;
import com.copacracks.infrastructure.persistence.entity.UserEntity;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.time.Instant;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;

@DisplayName("UserMapper Tests")
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class UserMapperTest {
	private static final String VALID_USERNAME = "testuser";
	private static final String VALID_EMAIL = "test@example.com";
	private static final String VALID_PASSWORD = "plainPassword";
	private static final String VALID_HASHED_PASSWORD = "$2a$10$hashedPassword";
	private static final Long VALID_ID = 1L;
	private static final Instant VALID_CREATE_AT = Instant.now();

	@Nested
	@DisplayName("fromModel() Tests")
	public class FromModelTests {

		@Test
		@DisplayName("Should successfully convert User to UserEntity")
		public void shouldConvertUserToUserEntity() {
			// Given
			User user = createValidUser();

			// When
			UserEntity result = UserMapper.fromModel(user);

			// Then
			assertThat(result).isNotNull();
			assertThat(result.getUsername()).isEqualTo(VALID_USERNAME);
			assertThat(result.getEmail()).isEqualTo(VALID_EMAIL);
			assertThat(result.getPassword()).isEqualTo(VALID_HASHED_PASSWORD);
			assertThat(result.getCreatedAt()).isEqualTo(Timestamp.from(VALID_CREATE_AT));
			// ID should not be mapped in fromModel (handled by persistence layer)
			assertThat(result.getId()).isNull();
		}

		@Test
		@DisplayName("Should preserve timestamp precision during conversion")
		public void shouldPreserveTimestampPrecision() {
			// Given
			Instant preciseInstant = Instant.parse("2023-12-25T10:30:45.123456789Z");
			User user = createUserWithCreatedAt(preciseInstant);

			// When
			UserEntity userEntity = UserMapper.fromModel(user);

			// Then
			assertThat(userEntity.getCreatedAt().toInstant()).isEqualTo(preciseInstant);
		}

		@Test
		@DisplayName("Should handle edge case timestamps correctly")
		public void shouldHandleEdgeCaseTimestamps() {
			// Given - Test with epoch time
			Instant epochTime = Instant.EPOCH;
			User user = createUserWithCreatedAt(epochTime);

			// When
			UserEntity result = UserMapper.fromModel(user);

			// Then
			assertThat(result.getCreatedAt().toInstant()).isEqualTo(epochTime);
		}

		@ParameterizedTest
		@NullSource
		@DisplayName("Should throw IllegalArgumentException when user is null")
		public void shouldThrowExceptionWhenUserIsNull(User user) {
			// When & Then
			assertThatThrownBy(() -> UserMapper.fromModel(user)).isInstanceOf(NullPointerException.class);
		}

		@Test
		@DisplayName("Should throw NullPointerException when createAt is null")
		public void shouldThrowExceptionWhenCreateAtIsNull() {
			// Given
			User user = createUserWithNullCreatedAt();

			// When & Then
			assertThatThrownBy(() -> UserMapper.fromModel(user)).isInstanceOf(NullPointerException.class);
		}

		private User createValidUser() {
			return new User(
					VALID_ID,
					VALID_USERNAME,
					VALID_PASSWORD,
					VALID_EMAIL,
					VALID_HASHED_PASSWORD,
					VALID_CREATE_AT);
		}

		private User createUserWithCreatedAt(Instant createdAt) {
			return new User(
					VALID_ID, VALID_USERNAME, VALID_PASSWORD, VALID_EMAIL, VALID_HASHED_PASSWORD, createdAt);
		}

		private User createUserWithNullCreatedAt() {
			return new User(
					VALID_ID, VALID_USERNAME, VALID_PASSWORD, VALID_EMAIL, VALID_HASHED_PASSWORD, null);
		}
	}

	@Nested
	@DisplayName("toModel() Tests")
	public class ToModelTests {

		@Test
		@DisplayName("Should successfully convert UserEntity to User")
		public void shouldConvertUserEntityToUser() {
			// Given
			UserEntity userEntity = createValidUserEntity();

			// When
			User result = UserMapper.toModel(userEntity);

			// Then
			assertThat(result).isNotNull();
			assertThat(result.getId()).isEqualTo(VALID_ID);
			assertThat(result.getUsername()).isEqualTo(VALID_USERNAME);
			assertThat(result.getEmail()).isEqualTo(VALID_EMAIL);
			assertThat(result.getHashedPassword()).isEqualTo(VALID_HASHED_PASSWORD);
			assertThat(result.getCreateAt()).isEqualTo(VALID_CREATE_AT);
		}

		@Test
		@DisplayName("Should preserve timestamp precision during reverse conversion")
		public void shouldPreserveTimestampPrecisionInReverseConversion() {
			// Given
			Instant preciseInstant = Instant.parse("2023-12-25T10:30:45.123456789Z");
			UserEntity userEntity = createUserEntityWithCreatedAt(Timestamp.from(preciseInstant));

			// When
			User result = UserMapper.toModel(userEntity);

			// Then
			assertThat(result.getCreateAt()).isEqualTo(preciseInstant);
		}

		@Test
		@DisplayName("Should handle minimum timestamp value")
		public void shouldHandleMinimumTimestamp() {
			// Given
			Timestamp minTimestamp = new Timestamp(Long.MIN_VALUE);
			UserEntity userEntity = createUserEntityWithCreatedAt(minTimestamp);

			// When
			User result = UserMapper.toModel(userEntity);

			// Then
			assertThat(result.getCreateAt()).isEqualTo(minTimestamp.toInstant());
		}

		@ParameterizedTest
		@NullSource
		@DisplayName("Should throw exception when userEntity is null")
		public void shouldThrowExceptionWhenUserEntityIsNull(UserEntity userEntity) {
			// When & Then
			assertThatThrownBy(() -> UserMapper.toModel(userEntity))
					.isInstanceOf(NullPointerException.class);
		}

		@Test
		@DisplayName("Should throw NullPointerException when createdAt is null")
		public void shouldThrowExceptionWhenCreatedAtIsNullInEntity() {
			// Given
			UserEntity userEntity = createUserEntityWithNullCreatedAt();

			// When & Then
			assertThatThrownBy(() -> UserMapper.toModel(userEntity))
					.isInstanceOf(NullPointerException.class);
		}

		private UserEntity createValidUserEntity() {
			return UserEntity.builder()
					.id(VALID_ID)
					.username(VALID_USERNAME)
					.email(VALID_EMAIL)
					.password(VALID_HASHED_PASSWORD)
					.createdAt(Timestamp.from(VALID_CREATE_AT))
					.build();
		}

		private UserEntity createUserEntityWithCreatedAt(Timestamp createdAt) {
			return UserEntity.builder()
					.id(VALID_ID)
					.username(VALID_USERNAME)
					.email(VALID_EMAIL)
					.password(VALID_HASHED_PASSWORD)
					.createdAt(createdAt)
					.build();
		}

		private UserEntity createUserEntityWithNullCreatedAt() {
			return UserEntity.builder()
					.id(VALID_ID)
					.username(VALID_USERNAME)
					.email(VALID_EMAIL)
					.password(VALID_HASHED_PASSWORD)
					.createdAt(null)
					.build();
		}
	}

	@Nested
	@DisplayName("Bidirectional Mapping Tests")
	public class BidirectionalMappingTests {

		@Test
		@DisplayName("Should maintain data consistency in round-trip conversion")
		public void shouldMaintainDataConsistencyInRoundTrip() {
			// Given
			User originalUser = createOriginalUser();

			// When - Convert to entity and back to model
			UserEntity entity = UserMapper.fromModel(originalUser);
			User convertedUser = UserMapper.toModel(entity);

			// Then - Verify data consistency (excluding plain password which is lost)
			assertThat(convertedUser.getUsername()).isEqualTo(originalUser.getUsername());
			assertThat(convertedUser.getEmail()).isEqualTo(originalUser.getEmail());
			assertThat(convertedUser.getHashedPassword()).isEqualTo(originalUser.getHashedPassword());
			assertThat(convertedUser.getCreateAt()).isEqualTo(originalUser.getCreateAt());
		}

		@Test
		@DisplayName("Should handle multiple round-trip conversions consistently")
		public void shouldHandleMultipleRoundTripConversionsConsistently() {
			// Given
			User originalUser = createOriginalUser();

			// When - Perform multiple round-trip conversions
			User firstRoundTrip = UserMapper.toModel(UserMapper.fromModel(originalUser));
			User secondRoundTrip = UserMapper.toModel(UserMapper.fromModel(firstRoundTrip));

			// Then - Results should be identical
			assertThat(secondRoundTrip.getUsername()).isEqualTo(firstRoundTrip.getUsername());
			assertThat(secondRoundTrip.getEmail()).isEqualTo(firstRoundTrip.getEmail());
			assertThat(secondRoundTrip.getHashedPassword()).isEqualTo(firstRoundTrip.getHashedPassword());
			assertThat(secondRoundTrip.getCreateAt()).isEqualTo(firstRoundTrip.getCreateAt());
		}

		private User createOriginalUser() {
			return new User(
					VALID_ID,
					VALID_USERNAME,
					VALID_PASSWORD,
					VALID_EMAIL,
					VALID_HASHED_PASSWORD,
					VALID_CREATE_AT);
		}
	}

	@Nested
	@DisplayName("Utility Class Structure Tests")
	public class UtilityClassStructureTests {

		@Test
		@DisplayName("Should be a utility class with private constructor")
		public void shouldBeUtilityClassWithPrivateConstructor() {
			// When & Then
			assertThatThrownBy(
							() -> {
								Constructor<UserMapper> constructor = UserMapper.class.getDeclaredConstructor();
								constructor.setAccessible(true);
								constructor.newInstance();
							})
					.isInstanceOf(InvocationTargetException.class)
					.hasCauseInstanceOf(UnsupportedOperationException.class);
		}

		@Test
		@DisplayName("Should have private constructor that is not accessible")
		public void shouldHavePrivateConstructor() throws NoSuchMethodException {
			// Given
			Constructor<UserMapper> constructor = UserMapper.class.getDeclaredConstructor();

			// Then
			assertThat(constructor.canAccess(null)).isFalse();
		}

		@Test
		@DisplayName("Should be final class")
		public void shouldBeFinalClass() {
			// When & Then
			assertThat(UserMapper.class).isFinal();
		}
	}

	@Nested
	@DisplayName("Edge Cases and Boundary Tests")
	public class EdgeCasesTests {

		@Test
		@DisplayName("Should handle very long usernames and emails")
		public void shouldHandleLongStrings() {
			// Given
			String longUsername = "a".repeat(1000);
			String longEmail = "a".repeat(500) + "@" + "b".repeat(500) + ".com";
			User user =
					new User(
							VALID_ID,
							longUsername,
							VALID_PASSWORD,
							longEmail,
							VALID_HASHED_PASSWORD,
							VALID_CREATE_AT);

			// When
			UserEntity entity = UserMapper.fromModel(user);
			User convertedUser = UserMapper.toModel(entity);

			// Then
			assertThat(convertedUser.getUsername()).isEqualTo(longUsername);
			assertThat(convertedUser.getEmail()).isEqualTo(longEmail);
		}

		@Test
		@DisplayName("Should handle special characters in user data")
		public void shouldHandleSpecialCharacters() {
			// Given
			String specialUsername = "user@#$%^&*()";
			String specialEmail = "test+special@domain-name.co.uk";
			User user =
					new User(
							VALID_ID,
							specialUsername,
							VALID_PASSWORD,
							specialEmail,
							VALID_HASHED_PASSWORD,
							VALID_CREATE_AT);

			// When
			UserEntity entity = UserMapper.fromModel(user);
			User convertedUser = UserMapper.toModel(entity);

			// Then
			assertThat(convertedUser.getUsername()).isEqualTo(specialUsername);
			assertThat(convertedUser.getEmail()).isEqualTo(specialEmail);
		}

		@Test
		@DisplayName("Should handle unicode characters")
		public void shouldHandleUnicodeCharacters() {
			// Given
			String unicodeUsername = "用户名测试";
			String unicodeEmail = "тест@домен.рф";
			User user =
					new User(
							VALID_ID,
							unicodeUsername,
							VALID_PASSWORD,
							unicodeEmail,
							VALID_HASHED_PASSWORD,
							VALID_CREATE_AT);

			// When
			UserEntity entity = UserMapper.fromModel(user);
			User convertedUser = UserMapper.toModel(entity);

			// Then
			assertThat(convertedUser.getUsername()).isEqualTo(unicodeUsername);
			assertThat(convertedUser.getEmail()).isEqualTo(unicodeEmail);
		}
	}
}
