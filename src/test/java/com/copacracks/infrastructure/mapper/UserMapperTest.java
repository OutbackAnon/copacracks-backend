package com.copacracks.infrastructure.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.copacracks.common.helper.MockUserHelper;
import com.copacracks.domain.model.user.User;
import com.copacracks.infrastructure.persistence.entity.UserEntity;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.sql.Timestamp;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;

@DisplayName("UserMapper Tests")
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class UserMapperTest {
	@Nested
	@DisplayName("fromModel() Tests")
	public class FromModelTests {

		@Test
		@DisplayName("Should successfully convert User to UserEntity")
		public void shouldConvertUserToUserEntity() {
			// Given
			User user = MockUserHelper.createValidUser();

			// When
			UserEntity result = UserMapper.fromModel(user);

			// Then
			assertThat(result).isNotNull();
			assertThat(result.getUsername()).isEqualTo(MockUserHelper.VALID_USERNAME);
			assertThat(result.getEmail()).isEqualTo(MockUserHelper.VALID_EMAIL);
			assertThat(result.getPassword()).isEqualTo(MockUserHelper.VALID_HASHED_PASSWORD);
			assertThat(result.getCreatedAt()).isEqualTo(Timestamp.from(MockUserHelper.VALID_CREATE_AT));
			// ID should not be mapped in fromModel (handled by persistence layer)
			assertThat(result.getId()).isNull();
		}

		@Test
		@DisplayName("Should preserve timestamp precision during conversion")
		public void shouldPreserveTimestampPrecision() {
			// Given
			Instant preciseInstant = Instant.parse("2023-12-25T10:30:45.123456789Z");
			User user = MockUserHelper.createUserWithCreatedAt(preciseInstant);

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
			User user = MockUserHelper.createUserWithCreatedAt(epochTime);

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
			User user = MockUserHelper.createUserWithNullCreatedAt();

			// When & Then
			assertThatThrownBy(() -> UserMapper.fromModel(user)).isInstanceOf(NullPointerException.class);
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
			assertThat(result.getId()).isEqualTo(MockUserHelper.VALID_ID);
			assertThat(result.getUsername()).isEqualTo(MockUserHelper.VALID_USERNAME);
			assertThat(result.getEmail()).isEqualTo(MockUserHelper.VALID_EMAIL);
			assertThat(result.getCreateAt()).isEqualTo(MockUserHelper.VALID_CREATE_AT);
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
					.id(MockUserHelper.VALID_ID)
					.username(MockUserHelper.VALID_USERNAME)
					.email(MockUserHelper.VALID_EMAIL)
					.password(MockUserHelper.VALID_HASHED_PASSWORD)
					.createdAt(Timestamp.from(MockUserHelper.VALID_CREATE_AT))
					.build();
		}

		private UserEntity createUserEntityWithCreatedAt(Timestamp createdAt) {
			return UserEntity.builder()
					.id(MockUserHelper.VALID_ID)
					.username(MockUserHelper.VALID_USERNAME)
					.email(MockUserHelper.VALID_EMAIL)
					.password(MockUserHelper.VALID_HASHED_PASSWORD)
					.createdAt(createdAt)
					.build();
		}

		private UserEntity createUserEntityWithNullCreatedAt() {
			return UserEntity.builder()
					.id(MockUserHelper.VALID_ID)
					.username(MockUserHelper.VALID_USERNAME)
					.email(MockUserHelper.VALID_EMAIL)
					.password(MockUserHelper.VALID_HASHED_PASSWORD)
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
			User originalUser = MockUserHelper.createValidUser();

			// When - Convert to entity and back to model
			UserEntity entity = UserMapper.fromModel(originalUser);
			User convertedUser = UserMapper.toModel(entity);

			// Then - Verify data consistency (excluding plain password which is lost)
			assertThat(convertedUser.getUsername()).isEqualTo(originalUser.getUsername());
			assertThat(convertedUser.getEmail()).isEqualTo(originalUser.getEmail());
			assertThat(convertedUser.getCreateAt()).isEqualTo(originalUser.getCreateAt());
		}

		@Test
		@DisplayName("Should handle multiple round-trip conversions consistently")
		public void shouldHandleMultipleRoundTripConversionsConsistently() {
			// Given
			User originalUser = MockUserHelper.createValidUser();

			// When - Perform multiple round-trip conversions
			User firstRoundTrip = UserMapper.toModel(UserMapper.fromModel(originalUser));
			User secondRoundTrip = UserMapper.toModel(UserMapper.fromModel(firstRoundTrip));

			// Then - Results should be identical
			assertThat(secondRoundTrip.getUsername()).isEqualTo(firstRoundTrip.getUsername());
			assertThat(secondRoundTrip.getEmail()).isEqualTo(firstRoundTrip.getEmail());
			assertThat(secondRoundTrip.getCreateAt()).isEqualTo(firstRoundTrip.getCreateAt());
		}

		//		private User createOriginalUser() {
		//			return new User(VALID_ID, VALID_USERNAME, VALID_PASSWORD, VALID_EMAIL, VALID_CREATE_AT);
		//		}
	}

	@Nested
	@DisplayName("Utility Class Structure Tests")
	public class UtilityClassStructureTests {

		@Test
		@DisplayName("Should be a utility class with private constructor")
		public void shouldBeUtilityClassWithPrivateConstructor() throws Exception {
			// Given
			Constructor<UserMapper> constructor = UserMapper.class.getDeclaredConstructor();

			// When
			constructor.setAccessible(true);
			UserMapper instance = constructor.newInstance();

			// Then
			assertThat(instance).isNotNull();
			assertThat(Modifier.isPrivate(constructor.getModifiers())).isTrue();
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
		@DisplayName("Should handle maximum length usernames and emails")
		public void shouldHandleMaximumLengthStrings() {
			// Given - Username max 50 chars, Email with valid format
			String maxUsername = "a".repeat(50); // Maximum allowed length
			String validLongEmail = "test" + "a".repeat(60) + "@example.com"; // Valid but long email

			User user =
					MockUserHelper.createUserBuilder()
							.username(maxUsername)
							.email(validLongEmail)
							.build()
							.createUser();

			// When
			UserEntity entity = UserMapper.fromModel(user);
			User convertedUser = UserMapper.toModel(entity);

			// Then
			assertThat(convertedUser.getUsername()).isEqualTo(maxUsername);
			assertThat(convertedUser.getEmail()).isEqualTo(validLongEmail.toLowerCase());
		}

		@Test
		@DisplayName("Should handle valid special characters in user data")
		public void shouldHandleValidSpecialCharacters() {
			// Given - Username with underscore (valid), Email with plus and hyphen (valid)
			String validUsername = "user_name123";
			String validEmail = "test+special@domain-name.co.uk";
			User user =
					MockUserHelper.createUserBuilder()
							.username(validUsername)
							.email(validEmail)
							.build()
							.createUser();

			// When
			UserEntity entity = UserMapper.fromModel(user);
			User convertedUser = UserMapper.toModel(entity);

			// Then
			assertThat(convertedUser.getUsername()).isEqualTo(validUsername);
			assertThat(convertedUser.getEmail()).isEqualTo(validEmail.toLowerCase());
		}

		@Test
		@DisplayName("Should handle mixed case and numbers in usernames")
		public void shouldHandleMixedCaseAndNumbers() {
			// Given - Valid username with mixed case and numbers
			String mixedCaseUsername = "User123_Test";
			String mixedCaseEmail = "Test.User123@Example.Com";

			User user =
					MockUserHelper.createUserBuilder()
							.username(mixedCaseUsername)
							.email(mixedCaseEmail)
							.build()
							.createUser();

			// When
			UserEntity entity = UserMapper.fromModel(user);
			User convertedUser = UserMapper.toModel(entity);

			// Then
			assertThat(convertedUser.getUsername()).isEqualTo(mixedCaseUsername);
			assertThat(convertedUser.getEmail()).isEqualTo(mixedCaseEmail.toLowerCase());
		}
	}
}
