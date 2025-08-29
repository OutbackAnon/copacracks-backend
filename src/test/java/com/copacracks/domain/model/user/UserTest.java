package com.copacracks.domain.model.user;

import static org.junit.jupiter.api.Assertions.*;

import com.copacracks.domain.exception.UserValidationException;
import java.time.LocalDateTime;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

class UserTest {
  private static final String USERNAME = "john_doe";
  private static final String PASSWORD = "SecurePass123!";
  private static final String EMAIL = "john@example.com";

  public UserTest() {}

  private boolean creationThrowsFor(
      final String username, final String password, final String email) {
    boolean thrown;
    try {
      new User(username, password, email);
      thrown = false;
    } catch (UserValidationException ex) {
      thrown = true;
    }
    return thrown;
  }

  @Test
  void shouldCreateValidUser() {
    // Given
    final String username = USERNAME;
    final String password = PASSWORD;
    final String email = EMAIL;

    // When
    final User user = new User(username, password, email);

    // Then
    assertTrue(
        user != null
            && username.equals(user.getUsername())
            && email.equals(user.getEmail())
            && user.isNew(),
        "Deve criar usuário válido com propriedades corretas e estado 'novo'.");
  }

  @Test
  void shouldValidatePassword() {
    // Given & When & Then
    final boolean allInvalidThrow =
        Stream.of("weak", "nouppercase123!", "NOLOWERCASE123!", "NoNumbers!", "NoSpecial123")
            .allMatch(pwd -> creationThrowsFor(USERNAME, pwd, EMAIL));

    assertTrue(allInvalidThrow, "Todas as senhas inválidas devem lançar UserValidationException.");
  }

  @Test
  void shouldValidateEmail() {
    // Given & When & Then
    final boolean allInvalidThrow =
        Stream.of("invalid-email", "", (String) null)
            .allMatch(mail -> creationThrowsFor(USERNAME, PASSWORD, mail));

    assertTrue(allInvalidThrow, "E-mails inválidos devem lançar UserValidationException.");
  }

  @Test
  void shouldValidateUsername() {
    // Given & When & Then
    final boolean allInvalidThrow =
        Stream.of("", "ab", "a".repeat(51), "john-doe")
            .allMatch(name -> creationThrowsFor(name, PASSWORD, EMAIL));

    assertTrue(allInvalidThrow, "Usernames inválidos devem lançar UserValidationException.");
  }

  @Test
  void shouldChangePassword() {
    // Given
    final User user = new User(USERNAME, PASSWORD, EMAIL);
    final String newPassword = "NewSecurePass456!";

    // When
    final User updatedUser = user.withNewPassword(newPassword);

    // Then
    assertTrue(
        updatedUser.isPasswordValid(newPassword)
            && !updatedUser.isPasswordValid(PASSWORD)
            && !user.isPasswordValid(newPassword)
            && user.isPasswordValid(PASSWORD)
            && !user.equals(updatedUser)
            && user.getUsername().equals(updatedUser.getUsername())
            && user.getEmail().equals(updatedUser.getEmail()),
        "Alterar a senha deve produzir um novo usuário com mesma identidade e credenciais coerentes.");
  }

  @Test
  void shouldChangeEmail() {
    // Given
    final User user = new User(USERNAME, PASSWORD, EMAIL);
    final String newEmail = "john.doe@company.com";

    // When
    final User updatedUser = user.withNewEmail(newEmail);

    // Then
    assertTrue(
        newEmail.equals(updatedUser.getEmail()) && EMAIL.equals(user.getEmail()),
        "Alterar e-mail deve retornar novo usuário e manter e-mail original intacto.");
  }

  @Test
  void shouldChangeUsername() {
    // Given
    final User user = new User(USERNAME, PASSWORD, EMAIL);
    final String newUsername = "jane_doe";

    // When
    final User updatedUser = user.withNewUsername(newUsername);

    // Then
    assertTrue(
        newUsername.equals(updatedUser.getUsername()) && USERNAME.equals(user.getUsername()),
        "Alterar username deve retornar novo usuário e manter username original intacto.");
  }

  @Test
  void shouldCreateUserWithId() {
    // Given
    final Long id = 1L;
    final String username = USERNAME;
    final String password = PASSWORD;
    final String email = EMAIL;

    // When
    final User user = new User(id, username, password, email, null, LocalDateTime.now());

    // Then
    assertTrue(
        id.equals(user.getId()) && !user.isNew(),
        "Usuário criado com ID não deve ser 'novo' e deve manter o mesmo ID.");
  }
}
