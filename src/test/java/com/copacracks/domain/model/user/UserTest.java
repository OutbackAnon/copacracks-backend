package com.copacracks.domain.model.user;

import static org.junit.jupiter.api.Assertions.*;

import com.copacracks.domain.exception.UserValidationException;
import org.junit.jupiter.api.Test;

class UserTest {
  @Test
  void shouldCreateValidUser() {
    // Given
    String username = "john_doe";
    String password = "SecurePass123!";
    String email = "john@example.com";

    // When
    User user = new User(username, password, email);

    // Then
    assertNotNull(user);
    assertEquals(username, user.getUsername());
    assertEquals(email, user.getEmail());
    assertTrue(user.isNew());
  }

  @Test
  void shouldValidatePassword() {
    // Given & When & Then
    assertThrows(
        UserValidationException.class,
        () -> {
          new User("john_doe", "weak", "john@example.com");
        });

    assertThrows(
        UserValidationException.class,
        () -> {
          new User("john_doe", "nouppercase123!", "john@example.com");
        });

    assertThrows(
        UserValidationException.class,
        () -> {
          new User("john_doe", "NOLOWERCASE123!", "john@example.com");
        });

    assertThrows(
        UserValidationException.class,
        () -> {
          new User("john_doe", "NoNumbers!", "john@example.com");
        });

    assertThrows(
        UserValidationException.class,
        () -> {
          new User("john_doe", "NoSpecial123", "john@example.com");
        });
  }

  @Test
  void shouldValidateEmail() {
    // Given & When & Then
    assertThrows(
        UserValidationException.class,
        () -> {
          new User("john_doe", "SecurePass123!", "invalid-email");
        });

    assertThrows(
        UserValidationException.class,
        () -> {
          new User("john_doe", "SecurePass123!", "");
        });

    assertThrows(
        UserValidationException.class,
        () -> {
          new User("john_doe", "SecurePass123!", null);
        });
  }

  @Test
  void shouldValidateUsername() {
    // Given & When & Then
    assertThrows(
        UserValidationException.class,
        () -> {
          new User("", "SecurePass123!", "john@example.com");
        });

    assertThrows(
        UserValidationException.class,
        () -> {
          new User("ab", "SecurePass123!", "john@example.com");
        });

    assertThrows(
        UserValidationException.class,
        () -> {
          new User("a".repeat(51), "SecurePass123!", "john@example.com");
        });

    assertThrows(
        UserValidationException.class,
        () -> {
          new User("john-doe", "SecurePass123!", "john@example.com");
        });
  }

  @Test
  void shouldChangePassword() {
      // Given
      User user = new User("john_doe", "SecurePass123!", "john@example.com");
      String newPassword = "NewSecurePass456!";
  
      // When
      User updatedUser = user.withNewPassword(newPassword);
  
      // Then
      assertTrue(updatedUser.isPasswordValid(newPassword));
      assertFalse(updatedUser.isPasswordValid("SecurePass123!"));
      
      assertFalse(user.isPasswordValid(newPassword));
      assertTrue(user.isPasswordValid("SecurePass123!"));
      
      assertNotSame(user, updatedUser);
      
      assertEquals(user.getId(), updatedUser.getId());
      assertEquals(user.getUsername(), updatedUser.getUsername());
      assertEquals(user.getEmail(), updatedUser.getEmail());
  }

  @Test
  void shouldChangeEmail() {
    // Given
    User user = new User("john_doe", "SecurePass123!", "john@example.com");
    String newEmail = "john.doe@company.com";

    // When
    User updatedUser = user.withNewEmail(newEmail);

    // Then
    assertEquals(newEmail, updatedUser.getEmail());
    // O usuário original não deve ser alterado
    assertEquals("john@example.com", user.getEmail());
  }

  @Test
  void shouldChangeUsername() {
    // Given
    User user = new User("john_doe", "SecurePass123!", "john@example.com");
    String newUsername = "jane_doe";

    // When
    User updatedUser = user.withNewUsername(newUsername);

    // Then
    assertEquals(newUsername, updatedUser.getUsername());
    // O usuário original não deve ser alterado
    assertEquals("john_doe", user.getUsername());
  }

  @Test
  void shouldCreateUserWithId() {
    // Given
    Long id = 1L;
    String username = "john_doe";
    String password = "SecurePass123!";
    String email = "john@example.com";

    // When
    User user = new User(id, username, password, email);

    // Then
    assertEquals(id, user.getId());
    assertFalse(user.isNew());
  }
}
