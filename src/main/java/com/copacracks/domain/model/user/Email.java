package com.copacracks.domain.model.user;

import com.copacracks.domain.exception.UserValidationException;

import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Value Object para representar um email usando record. Encapsula a lógica de validação e
 * formatação de email.
 */
public record Email(String value) {
  private static final Pattern EMAIL_PATTERN =
      Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");

  /**
   * Construtor canônico com validação. Todas as instâncias de Email passarão por esta validação,
   * independentemente de como forem criadas.
   */
  public Email(final String value) {
    validateEmail(value);
    this.value = value.trim().toLowerCase(Locale.ROOT);
  }

  /**
   * Verifica se o email é válido.
   *
   * @return true se o email for válido
   */
  public boolean isValid() {
    return value != null && !value.isEmpty();
  }

  /**
   * Valida o email fornecido.
   *
   * @param email valor a ser validado
   * @throws UserValidationException se o email for inválido
   */
  private void validateEmail(final String email) {
    if (email == null || email.isBlank()) {
      throw new UserValidationException("Email não pode ser vazio");
    }

    final String trimmedEmail = email.trim();
    if (!EMAIL_PATTERN.matcher(trimmedEmail).matches()) {
      throw new UserValidationException("Email deve ter um formato válido");
    }
  }

  public String getDomain() {
    return value.substring(value.indexOf('@') + 1);
  }

  public String getUsername() {
    return value.substring(0, value.indexOf('@'));
  }
}
