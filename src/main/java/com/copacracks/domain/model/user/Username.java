package com.copacracks.domain.model.user;

import com.copacracks.domain.exception.UserValidationException;
import java.util.regex.Pattern;

/**
 * Value Object para representar um nome de usuário usando record. Encapsula a lógica de validação
 * de username.
 */
public record Username(String value) {
  private static final int MIN_LENGTH = 3;
  private static final int MAX_LENGTH = 50;
  private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]+$");

  /**
   * Construtor canônico com validação. Todas as instâncias de Username passarão por esta validação,
   * independentemente de como forem criadas.
   */
  public Username(final String value) {
    validateUsername(value);
    this.value = value.trim();
  }

  /**
   * Verifica se o username é válido.
   *
   * @return true se o username for válido
   */
  public boolean isValid() {
    return value != null && !value.isEmpty();
  }

  /**
   * Valida o username fornecido.
   *
   * @param username valor a ser validado
   * @throws UserValidationException se o username for inválido
   */
  private void validateUsername(final String username) {
    ensureNotBlank(username);

    final String trimmedUsername = username.trim();

    ensureLengthWithinBounds(trimmedUsername);
    ensureMatchesPattern(trimmedUsername);
  }

  private static void ensureNotBlank(final String username) {
    if (username == null || username.isBlank()) {
      throw new UserValidationException("Nome de usuário não pode ser vazio");
    }
  }

  private static void ensureLengthWithinBounds(final String trimmedUsername) {
    final int length = trimmedUsername.length();
    if (length < MIN_LENGTH) {
      throw new UserValidationException(
          "Nome de usuário deve ter pelo menos " + MIN_LENGTH + " caracteres");
    }
    if (length > MAX_LENGTH) {
      throw new UserValidationException(
          "Nome de usuário deve ter no máximo " + MAX_LENGTH + " caracteres");
    }
  }

  private static void ensureMatchesPattern(final String trimmedUsername) {
    if (!USERNAME_PATTERN.matcher(trimmedUsername).matches()) {
      throw new UserValidationException(
          "Nome de usuário deve conter apenas letras, números e underscore");
    }
  }
}
