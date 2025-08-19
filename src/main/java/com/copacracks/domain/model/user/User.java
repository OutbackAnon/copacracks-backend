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
 * Checks if the provided password matches the stored password.
 * This method handles cases where the provided password might be null.
 *
 * @param plainPassword the plain text password to be validated.
 * @return {@code true} if the provided password is valid and matches the stored password;
 * {@code false} otherwise, including if the provided or stored password is null.
 */
  public boolean isPasswordValid(final String plainPassword) {
    boolean result = false;

    if (plainPassword != null && this.password != null) {
      result = this.password.value().matches(plainPassword);
  }

    return result;
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

  public String getEmail() {
    return email.value();
  }

  @Override
  public boolean equals(final Object obj) {
      boolean result = false;
      
      if (this == obj) {
          result = true;
      } else if (obj != null && getClass() == obj.getClass()) {
          final User user = (User) obj;
          
          // Se ambos têm ID, compara apenas por ID
          if (id != null && user.id != null) {
              result = Objects.equals(id, user.id);
          } else {
              // Se não têm ID, todos os campos devem ser iguais (AND, não OR)
              result = Objects.equals(username, user.username) && 
                      Objects.equals(password, user.password) &&
                      Objects.equals(email, user.email);
          }
      }
      
      return result;
  }

  @Override
  public int hashCode() {
      final int result;
      
      if (id != null) {
          result = Objects.hash(id);
      } else {
          result = Objects.hash(username, password, email);
      }
      
      return result;
  }

  @Override
  public String toString() {
    return String.format(
        "User{id=%s, username='%s', email='%s'}", id, username.value(), email.value());
  }
}
