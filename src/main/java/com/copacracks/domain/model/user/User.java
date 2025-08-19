package com.copacracks.domain.model.user;

import com.copacracks.domain.exception.UserValidationException;
import java.util.Objects;

/**
 * Modelo de usuário seguindo princípios de modelo rico. Encapsula regras de negócio e validações
 * relacionadas ao usuário.
 */
public class User {
  private final Long id;
  private final Username username;
  private final Password password;
  private final Email email;

  /**
   * Construtor para criação de novo usuário.
   *
   * @param username nome de usuário
   * @param password senha
   * @param email email
   * @throws UserValidationException se os parâmetros forem inválidos
   */
  public User(final String username, final String password, final String email) {
    this(null, username, password, email);
  }

  /**
   * Construtor para usuário existente.
   *
   * @param id identificador único
   * @param username nome de usuário
   * @param password senha
   * @param email email
   * @throws UserValidationException se os parâmetros forem inválidos
   */
  public User(final Long id, final String username, final String password, final String email) {
    this.id = id;
    this.username = new Username(username);
    this.password = new Password(password);
    this.email = new Email(email);
  }

  /**
   * Construtor para usuário existente com Value Objects já criados.
   *
   * @param id identificador único
   * @param username nome de usuário
   * @param password senha já criptografada
   * @param email email já validado
   */
  public User(final Long id, final Username username, final Password password, final Email email) {
    this.id = id;
    this.username = username;
    this.password = password;
    this.email = email;
  }

  /**
   * Altera a senha do usuário.
   *
   * @param newPassword nova senha
   * @throws UserValidationException se a nova senha for inválida
   */
  public User withNewPassword(final String newPassword) {
    return new User(this.id, this.username, new Password(newPassword), this.email);
  }

  /**
   * Altera o email do usuário.
   *
   * @param newEmail novo email
   * @throws UserValidationException se o novo email for inválido
   */
  public User withNewEmail(final String newEmail) {
    return new User(this.id, this.username, this.password, new Email(newEmail));
  }

  /**
   * Altera o nome de usuário.
   *
   * @param newUsername novo nome de usuário
   * @throws UserValidationException se o novo nome de usuário for inválido
   */
  public User withNewUsername(final String newUsername) {
    return new User(this.id, new Username(newUsername), this.password, this.email);
  }


  /**
   * Verifica se o usuário é novo (ainda não possui ID).
   *
   * @return true se o usuário for novo, false caso contrário
   */
  public boolean isNew() {
    return this.id == null;
  }

  public Long getId() {
    return id;
  }

  public String getUsername() {
    return username.value();
  }

  public String getPassword() {
    return password.value();
  }

  public String getEmail() {
    return email.value();
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) return true;
    if (obj == null || getClass() != obj.getClass()) return false;

    final User user = (User) obj;

    if (id != null && user.id != null) {
      return Objects.equals(id, user.id);
    }

    return Objects.equals(username, user.username) || Objects.equals(email, user.email);
  }

  @Override
  public int hashCode() {
    if (id != null) {
      return Objects.hash(id);
    }
    return Objects.hash(username, email);
  }

  @Override
  public String toString() {
    return String.format(
        "User{id=%s, username='%s', email='%s'}", id, username.value(), email.value());
  }
}
