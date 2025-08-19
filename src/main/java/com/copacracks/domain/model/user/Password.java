package com.copacracks.domain.model.user;

import com.copacracks.domain.exception.UserValidationException;
import java.util.regex.Pattern;

public record Password(String value) {

  private static final int MIN_LENGTH = 8;
  private static final Pattern UPPERCASE = Pattern.compile("[A-Z]");
  private static final Pattern LOWERCASE = Pattern.compile("[a-z]");
  private static final Pattern DIGIT = Pattern.compile("\\d");
  private static final Pattern SPECIAL_CHAR =
      Pattern.compile("[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]");

  public Password {
    validatePlainPassword(value);
  }

  /**
   * Valida a senha em texto plano.
   *
   * @param plainPassword senha a ser validada
   * @throws UserValidationException se a senha for inválida
   */
  private static void validatePlainPassword(final String plainPassword) {
    ensureNotBlank(plainPassword);
    ensureMinimumLength(plainPassword);
    ensureContainsUppercase(plainPassword);
    ensureContainsLowercase(plainPassword);
    ensureContainsDigit(plainPassword);
    ensureContainsSpecialChar(plainPassword);
  }

  private static void ensureNotBlank(final String plainPassword) {
    if (plainPassword == null || plainPassword.isEmpty()) {
      throw new UserValidationException("Senha não pode ser vazia");
    }
  }

  private static void ensureMinimumLength(final String plainPassword) {
    if (plainPassword.length() < MIN_LENGTH) {
      throw new UserValidationException("Senha deve ter pelo menos " + MIN_LENGTH + " caracteres");
    }
  }

  private static void ensureContainsUppercase(final String plainPassword) {
    if (!UPPERCASE.matcher(plainPassword).find()) {
      throw new UserValidationException("Senha deve conter pelo menos uma letra maiúscula");
    }
  }

  private static void ensureContainsLowercase(final String plainPassword) {
    if (!LOWERCASE.matcher(plainPassword).find()) {
      throw new UserValidationException("Senha deve conter pelo menos uma letra minúscula");
    }
  }

  private static void ensureContainsDigit(final String plainPassword) {
    if (!DIGIT.matcher(plainPassword).find()) {
      throw new UserValidationException("Senha deve conter pelo menos um número");
    }
  }

  private static void ensureContainsSpecialChar(final String plainPassword) {
    if (!SPECIAL_CHAR.matcher(plainPassword).find()) {
      throw new UserValidationException("Senha deve conter pelo menos um caractere especial");
    }
  }
}
