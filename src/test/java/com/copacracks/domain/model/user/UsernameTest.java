package com.copacracks.domain.model.user;

import com.copacracks.domain.exception.UserValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Username value object.
 *
 * Testing framework: JUnit 5 (Jupiter)
 *
 * Coverage:
 * - Happy paths and valid username formats
 * - Edge cases for min/max length boundaries (including trimming)
 * - Invalid characters and Unicode handling
 * - Null/empty/whitespace-only input handling
 * - Record semantics: equals, hashCode, toString
 * - isValid() behavior for constructed instances
 */
@DisplayName("Username Value Object Tests")
class UsernameTest {

  // -------------------------
  // Valid username scenarios
  // -------------------------
  @Nested
  @DisplayName("Valid Username Creation")
  class ValidUsernameTests {

    @Test
    @DisplayName("Creates username with minimum length (3 chars)")
    void createsUsernameWithMinimumLength() {
      Username username = new Username("abc");
      assertNotNull(username);
      assertEquals("abc", username.value());
      assertTrue(username.isValid());
    }

    @Test
    @DisplayName("Creates username with maximum length (50 chars)")
    void createsUsernameWithMaximumLength() {
      String fiftyChars = "a".repeat(50);
      Username username = new Username(fiftyChars);
      assertNotNull(username);
      assertEquals(fiftyChars, username.value());
      assertTrue(username.isValid());
    }

    @Test
    @DisplayName("Creates username with alphanumeric characters")
    void createsUsernameWithAlphanumeric() {
      Username username = new Username("User123");
      assertNotNull(username);
      assertEquals("User123", username.value());
      assertTrue(username.isValid());
    }

    @Test
    @DisplayName("Creates username with underscores")
    void createsUsernameWithUnderscores() {
      Username username = new Username("user_name_123");
      assertNotNull(username);
      assertEquals("user_name_123", username.value());
      assertTrue(username.isValid());
    }

    @Test
    @DisplayName("Trims whitespace around a valid username")
    void trimsWhitespace() {
      Username username = new Username("  validUser123  ");
      assertNotNull(username);
      assertEquals("validUser123", username.value());
      assertTrue(username.isValid());
    }

    @Test
    @DisplayName("Accepts a variety of valid formats (letters, digits, underscores, mixed case)")
    void acceptsVarietyOfValidFormats() {
      String[] validUsernames = {
          "abc", "ABC", "123", "___",
          "user", "USER", "User",
          "test_user", "TEST_USER", "Test_User",
          "user123", "USER456", "User789",
          "user_123", "USER_456", "User_789",
          "test_user_123", "TEST_USER_456", "Test_User_789",
          "CamelCaseUser_123"
      };
      for (String v : validUsernames) {
        Username username = new Username(v);
        assertEquals(v.trim(), username.value(), "Should store trimmed value");
        assertTrue(username.isValid(), "Valid usernames should be marked valid");
      }
    }

    @Test
    @DisplayName("Allows only numbers or only underscores")
    void allowsOnlyNumbersOrOnlyUnderscores() {
      Username onlyNumbers = new Username("123456");
      assertEquals("123456", onlyNumbers.value());
      assertTrue(onlyNumbers.isValid());

      Username onlyUnderscores = new Username("___");
      assertEquals("___", onlyUnderscores.value());
      assertTrue(onlyUnderscores.isValid());
    }

    @Test
    @DisplayName("Allows leading, trailing, and consecutive underscores")
    void allowsVariousUnderscorePositions() {
      assertEquals("_user123", new Username("_user123").value());
      assertEquals("user123_", new Username("user123_").value());
      assertEquals("user___123", new Username("user___123").value());
    }

    @Test
    @DisplayName("Usernames equal if values are equal after trimming")
    void equalityConsidersTrimmedValue() {
      Username a = new Username("abc");
      Username b = new Username("  abc  ");
      assertEquals(a, b, "Trimmed values should be equal");
      assertEquals(a.hashCode(), b.hashCode(), "Equal values should share hash code");
    }

    @Test
    @DisplayName("Exactly 50 underscores is valid")
    void exactlyFiftyUnderscoresIsValid() {
      String fiftyUnderscores = "_".repeat(50);
      Username u = new Username(fiftyUnderscores);
      assertEquals(fiftyUnderscores, u.value());
      assertTrue(u.isValid());
    }
  }

  // -------------------------
  // Invalid username scenarios
  // -------------------------
  @Nested
  @DisplayName("Invalid Username Validation")
  class InvalidUsernameTests {

    @Test
    @DisplayName("Throws for null username")
    void throwsForNull() {
      UserValidationException ex =
          assertThrows(UserValidationException.class, () -> new Username(null));
      assertEquals("Nome de usuário não pode ser vazio", ex.getMessage());
    }

    @Test
    @DisplayName("Throws for empty username")
    void throwsForEmpty() {
      UserValidationException ex =
          assertThrows(UserValidationException.class, () -> new Username(""));
      assertEquals("Nome de usuário não pode ser vazio", ex.getMessage());
    }

    @Test
    @DisplayName("Throws for whitespace-only username")
    void throwsForWhitespaceOnly() {
      UserValidationException ex =
          assertThrows(UserValidationException.class, () -> new Username("   "));
      assertEquals("Nome de usuário não pode ser vazio", ex.getMessage());
    }

    @Test
    @DisplayName("Throws when shorter than minimum length (after trimming)")
    void throwsWhenTooShort() {
      UserValidationException ex =
          assertThrows(UserValidationException.class, () -> new Username("ab"));
      assertEquals("Nome de usuário deve ter pelo menos 3 caracteres", ex.getMessage());
    }

    @Test
    @DisplayName("Throws when longer than maximum length (after trimming)")
    void throwsWhenTooLong() {
      String fiftyOneChars = "a".repeat(51);
      UserValidationException ex =
          assertThrows(UserValidationException.class, () -> new Username(fiftyOneChars));
      assertEquals("Nome de usuário deve ter no máximo 50 caracteres", ex.getMessage());
    }

    @Test
    @DisplayName("Throws when trimmed value becomes too short")
    void throwsWhenTrimmedTooShort() {
      UserValidationException ex =
          assertThrows(UserValidationException.class, () -> new Username("  ab  "));
      assertEquals("Nome de usuário deve ter pelo menos 3 caracteres", ex.getMessage());
    }

    @Test
    @DisplayName("Throws when trimmed value becomes too long")
    void throwsWhenTrimmedTooLong() {
      String fiftyOne = "a".repeat(51);
      UserValidationException ex =
          assertThrows(UserValidationException.class, () -> new Username("  " + fiftyOne + "  "));
      assertEquals("Nome de usuário deve ter no máximo 50 caracteres", ex.getMessage());
    }

    @Test
    @DisplayName("Throws for invalid characters (symbols, spaces, punctuation)")
    void throwsForInvalidCharacters() {
      String[] invalids = {
          "user-name", "user.name", "user@name", "user name", "user!name",
          "user#name", "user$name", "user%name", "user&name", "user*name",
          "user+name", "user=name", "user/name", "user\\name", "user|name",
          "user<name", "user>name", "user?name", "user:name", "user;name",
          "user'name", "user\"name", "user,name", "user[name", "user]name",
          "user{name", "user}name", "user(name", "user)name", "user~name",
          "user`name"
      };
      for (String s : invalids) {
        UserValidationException ex =
            assertThrows(UserValidationException.class, () -> new Username(s));
        assertEquals("Nome de usuário deve conter apenas letras, números e underscore", ex.getMessage(),
            "Message should indicate only letters, numbers, underscore are allowed");
      }
    }

    @Test
    @DisplayName("Throws for emojis and non-ASCII letters")
    void throwsForEmojisAndUnicode() {
      // Emoji
      UserValidationException ex1 =
          assertThrows(UserValidationException.class, () -> new Username("user😀123"));
      assertEquals("Nome de usuário deve conter apenas letras, números e underscore", ex1.getMessage());

      // Accented character
      UserValidationException ex2 =
          assertThrows(UserValidationException.class, () -> new Username("usuário123"));
      assertEquals("Nome de usuário deve conter apenas letras, números e underscore", ex2.getMessage());
    }

    @Test
    @DisplayName("Throws for control characters like tab/newline")
    void throwsForControlCharacters() {
      UserValidationException ex1 =
          assertThrows(UserValidationException.class, () -> new Username("user\t123"));
      assertEquals("Nome de usuário deve conter apenas letras, números e underscore", ex1.getMessage());

      UserValidationException ex2 =
          assertThrows(UserValidationException.class, () -> new Username("user\n123"));
      assertEquals("Nome de usuário deve conter apenas letras, números e underscore", ex2.getMessage());
    }
  }

  // -------------------------
  // Record semantics and API
  // -------------------------
  @Nested
  @DisplayName("Record and API Behavior")
  class RecordSemanticsTests {

    @Test
    @DisplayName("equals true for same values")
    void equalsForSameValues() {
      Username u1 = new Username("testUser123");
      Username u2 = new Username("testUser123");
      assertEquals(u1, u2);
      assertEquals(u1.hashCode(), u2.hashCode());
    }

    @Test
    @DisplayName("equals false for different values")
    void notEqualsForDifferentValues() {
      Username u1 = new Username("testUser123");
      Username u2 = new Username("testUser456");
      assertNotEquals(u1, u2);
    }

    @Test
    @DisplayName("toString contains type and value")
    void toStringContainsTypeAndValue() {
      Username u = new Username("testUser123");
      String s = u.toString();
      assertNotNull(s);
      assertTrue(s.contains("Username"), "Should include record type name");
      assertTrue(s.contains("testUser123"), "Should include stored value");
    }

    @Test
    @DisplayName("isValid returns true for any constructed instance")
    void isValidAlwaysTrueForConstructedInstances() {
      Username[] values = {
          new Username("validUser123"),
          new Username("abc"),
          new Username("a".repeat(50)),
          new Username("_user_123_")
      };
      for (Username u : values) {
        assertTrue(u.isValid(), "Any constructed Username should be valid by definition");
        assertNotNull(u.value());
        assertFalse(u.value().isEmpty());
      }
    }
  }

  // -------------------------
  // Basic stress scenario
  // -------------------------
  @Nested
  @DisplayName("Stress Scenarios")
  class StressTests {

    @Test
    @DisplayName("Rapid creation of multiple usernames does not throw")
    void rapidCreationDoesNotThrow() {
      assertDoesNotThrow(() -> {
        for (int i = 0; i < 1_000; i++) {
          new Username("user" + i);
        }
      });
    }
  }
}