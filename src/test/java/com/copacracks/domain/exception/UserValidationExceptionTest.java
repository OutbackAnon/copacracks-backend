package com.copacracks.domain.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link UserValidationException}.
 *
 * Testing library/framework:
 * - Using JUnit 5 (Jupiter): org.junit.jupiter.api.*
 *
 * This suite validates:
 * - Message propagation via single-argument constructor
 * - Message and cause propagation via two-argument constructor
 * - Behavior with null message and/or null cause
 * - Type hierarchy (is a RuntimeException)
 * - toString formatting contains the message when present
 */
class UserValidationExceptionTest {

  @Test
  @DisplayName("should be a RuntimeException")
  void shouldBeRuntimeException() {
    UserValidationException ex = new UserValidationException("msg");
    assertTrue(ex instanceof RuntimeException, "UserValidationException must extend RuntimeException");
  }

  @Nested
  @DisplayName("single-argument constructor (message)")
  class SingleArgConstructor {

    @Test
    @DisplayName("propagates non-null message and has no cause")
    void propagatesMessageNoCause() {
      String message = "Invalid user data: missing email";
      UserValidationException ex = new UserValidationException(message);

      assertEquals(message, ex.getMessage(), "Message should match provided value");
      assertNull(ex.getCause(), "Cause should be null when not provided");
    }

    @Test
    @DisplayName("accepts null message and has no cause")
    void acceptsNullMessage() {
      UserValidationException ex = new UserValidationException(null);

      assertNull(ex.getMessage(), "Message should be null when constructed with null");
      assertNull(ex.getCause(), "Cause should be null when not provided");
    }

    @Test
    @DisplayName("toString contains class name and message when message present")
    void toStringContainsClassNameAndMessage() {
      String message = "Something went wrong";
      UserValidationException ex = new UserValidationException(message);

      String text = ex.toString();
      assertNotNull(text);
      assertTrue(text.contains(UserValidationException.class.getName()), "toString should contain fully qualified class name");
      assertTrue(text.contains(message), "toString should contain the exception message");
    }

    @Test
    @DisplayName("toString contains class name when message is null")
    void toStringContainsClassNameWhenMessageNull() {
      UserValidationException ex = new UserValidationException(null);

      String text = ex.toString();
      assertNotNull(text);
      assertTrue(text.contains(UserValidationException.class.getName()), "toString should contain fully qualified class name");
    }
  }

  @Nested
  @DisplayName("two-argument constructor (message, cause)")
  class TwoArgConstructor {

    @Test
    @DisplayName("propagates non-null message and non-null cause")
    void propagatesMessageAndCause() {
      String message = "Failed validation: username too short";
      Throwable cause = new IllegalArgumentException("length < 3");
      UserValidationException ex = new UserValidationException(message, cause);

      assertEquals(message, ex.getMessage(), "Message should match provided value");
      assertSame(cause, ex.getCause(), "Cause should be the same instance provided");
    }

    @Test
    @DisplayName("accepts null message and non-null cause")
    void acceptsNullMessageWithCause() {
      Throwable cause = new NullPointerException("field was null");
      UserValidationException ex = new UserValidationException(null, cause);

      assertNull(ex.getMessage(), "Message should be null when constructed with null");
      assertSame(cause, ex.getCause(), "Cause should be the same instance provided");
    }

    @Test
    @DisplayName("accepts non-null message and null cause")
    void acceptsNullCauseWithMessage() {
      String message = "Email format invalid";
      UserValidationException ex = new UserValidationException(message, null);

      assertEquals(message, ex.getMessage(), "Message should match provided value");
      assertNull(ex.getCause(), "Cause should be null when constructed with null cause");
    }

    @Test
    @DisplayName("accepts null message and null cause")
    void acceptsNullMessageAndNullCause() {
      UserValidationException ex = new UserValidationException(null, null);

      assertNull(ex.getMessage(), "Message should be null when constructed with null");
      assertNull(ex.getCause(), "Cause should be null when constructed with null cause");
    }

    @Test
    @DisplayName("toString contains class name and message when message present")
    void toStringContainsClassNameAndMessage() {
      String message = "Generic validation failure";
      Throwable cause = new RuntimeException("root cause");
      UserValidationException ex = new UserValidationException(message, cause);

      String text = ex.toString();
      assertNotNull(text);
      assertTrue(text.contains(UserValidationException.class.getName()), "toString should contain fully qualified class name");
      assertTrue(text.contains(message), "toString should contain the exception message");
    }

    @Test
    @DisplayName("toString contains class name when message is null")
    void toStringContainsClassNameWhenMessageNull() {
      Throwable cause = new RuntimeException("root cause");
      UserValidationException ex = new UserValidationException(null, cause);

      String text = ex.toString();
      assertNotNull(text);
      assertTrue(text.contains(UserValidationException.class.getName()), "toString should contain fully qualified class name");
    }
  }
}