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
    if (plainPassword == null || plainPassword.isEmpty()) {
      throw new UserValidationException("Senha não pode ser vazia");
    }

    if (plainPassword.length() < MIN_LENGTH) {
      throw new UserValidationException("Senha deve ter pelo menos " + MIN_LENGTH + " caracteres");
    }

    if (!UPPERCASE.matcher(plainPassword).find()) {
      throw new UserValidationException("Senha deve conter pelo menos uma letra maiúscula");
    }

    if (!LOWERCASE.matcher(plainPassword).find()) {
      throw new UserValidationException("Senha deve conter pelo menos uma letra minúscula");
    }

    if (!DIGIT.matcher(plainPassword).find()) {
      throw new UserValidationException("Senha deve conter pelo menos um número");
    }

    if (!SPECIAL_CHAR.matcher(plainPassword).find()) {
      throw new UserValidationException("Senha deve conter pelo menos um caractere especial");
    }
  }

  /**
   * Método utilitário para validar uma senha em texto plano sem criar a instância. Útil para
   * pré-validação em formulários, APIs, etc.
   *
   * @param plainPassword senha a ser testada
   * @return true se a senha for válida
   */
  public static boolean isValidPlainPassword(final String plainPassword) {
    try {
      validatePlainPassword(plainPassword);
      return true;
    } catch (UserValidationException e) {
      return false;
    }
  }
}
