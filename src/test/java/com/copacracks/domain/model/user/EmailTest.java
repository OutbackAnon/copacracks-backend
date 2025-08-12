package com.copacracks.domain.model.user;

import com.copacracks.domain.exception.UserValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for Email value object.
 *
 * Notes on framework:
 * - Using JUnit 5 (Jupiter) assertions and @Test annotations.
 * - No additional dependencies required; uses standard JUnit assertions.
 * - If project includes AssertJ/Mockito, these can be introduced later to match repo conventions.
 */
class EmailTest {

  @Nested
  @DisplayName("Constructor and normalization")
  class ConstructorNormalization {

    @Test
    @DisplayName("Should normalize value by trimming spaces and lowercasing")
    void shouldNormalizeValue() {
      Email email = new Email("  USER.Name+tag@Example.COM  ");
      assertEquals("user.name+tag@example.com", email.value(), "Email must be trimmed and lowercased");
    }

    @Test
    @DisplayName("Should accept typical valid emails per regex")
    void shouldAcceptValidEmails() {
      String[] valid = {
          "a@b.co",
          "john.doe@example.com",
          "user_name@example.co.uk",
          "user-name+tag@sub.domain.org",
          "u123@domain.io",
          "firstname.lastname@domain.travel"
      };
      for (String v : valid) {
        Email email = new Email(v);
        assertTrue(email.isValid(), "Constructed valid email should be valid");
        assertEquals(v.trim().toLowerCase(), email.value());
      }
    }
  }

  @Nested
  @DisplayName("Validation: constructor failures")
  class ConstructorFailures {

    @Test
    @DisplayName("Null email should throw UserValidationException")
    void nullEmailThrows() {
      UserValidationException ex = assertThrows(UserValidationException.class, () -> new Email(null));
      assertTrue(ex.getMessage().contains("Email não pode ser vazio"));
    }

    @Test
    @DisplayName("Blank email (only spaces) should throw UserValidationException")
    void blankEmailThrows() {
      UserValidationException ex = assertThrows(UserValidationException.class, () -> new Email("   "));
      assertTrue(ex.getMessage().contains("Email não pode ser vazio"));
    }

    @Test
    @DisplayName("Missing at-sign should throw UserValidationException")
    void missingAtThrows() {
      UserValidationException ex = assertThrows(UserValidationException.class, () -> new Email("user.domain.com"));
      assertTrue(ex.getMessage().contains("Email deve ter um formato válido"));
    }

    @Test
    @DisplayName("Missing domain should throw UserValidationException")
    void missingDomainThrows() {
      UserValidationException ex = assertThrows(UserValidationException.class, () -> new Email("user@"));
      assertTrue(ex.getMessage().contains("Email deve ter um formato válido"));
    }

    @Test
    @DisplayName("Missing local part should throw UserValidationException")
    void missingLocalThrows() {
      UserValidationException ex = assertThrows(UserValidationException.class, () -> new Email("@domain.com"));
      assertTrue(ex.getMessage().contains("Email deve ter um formato válido"));
    }

    @Test
    @DisplayName("TLD shorter than 2 characters should throw UserValidationException")
    void shortTldThrows() {
      UserValidationException ex = assertThrows(UserValidationException.class, () -> new Email("user@domain.c"));
      assertTrue(ex.getMessage().contains("Email deve ter um formato válido"));
    }

    @Test
    @DisplayName("Email with spaces inside should throw UserValidationException")
    void spacesInsideThrows() {
      UserValidationException ex = assertThrows(UserValidationException.class, () -> new Email("user name@domain.com"));
      assertTrue(ex.getMessage().contains("Email deve ter um formato válido"));
    }

    @Test
    @DisplayName("Email with invalid characters should throw UserValidationException")
    void invalidCharsThrows() {
      UserValidationException ex = assertThrows(UserValidationException.class, () -> new Email("user()@domain.com"));
      assertTrue(ex.getMessage().contains("Email deve ter um formato válido"));
    }

    @Test
    @DisplayName("Email with trailing dot in domain should throw UserValidationException")
    void trailingDotDomainThrows() {
      UserValidationException ex = assertThrows(UserValidationException.class, () -> new Email("user@domain.com."));
      assertTrue(ex.getMessage().contains("Email deve ter um formato válido"));
    }
  }

  @Nested
  @DisplayName("Accessors: getDomain and getUsername")
  class Accessors {

    @Test
    @DisplayName("getDomain returns substring after '@'")
    void getDomainReturnsDomain() {
      Email email = new Email("user.name+tag@Sub.Domain.COM");
      assertEquals("sub.domain.com", email.getDomain());
    }

    @Test
    @DisplayName("getUsername returns substring before '@'")
    void getUsernameReturnsLocalPart() {
      Email email = new Email("User.Name+tag@domain.com");
      assertEquals("user.name+tag", email.getUsername());
    }

    @Test
    @DisplayName("Edge case: minimal valid email like a@b.co")
    void edgeCaseMinimalValid() {
      Email email = new Email("a@b.co");
      assertEquals("a", email.getUsername());
      assertEquals("b.co", email.getDomain());
    }
  }

  @Nested
  @DisplayName("isValid behavior")
  class IsValidBehavior {

    @Test
    @DisplayName("isValid is true for constructed valid emails")
    void isValidTrueForConstructed() {
      Email email = new Email("valid@example.org");
      assertTrue(email.isValid());
    }

    @Test
    @DisplayName("Even if constructed value is valid, confirm method logic only checks non-empty")
    void isValidChecksNonEmptyOnly() {
      Email email = new Email("UPPER@EXAMPLE.ORG");
      // Construction ensures trimming/lowercasing and regex check already passed.
      assertTrue(email.isValid());
      assertEquals("upper@example.org", email.value());
    }
  }
}