package com.copacracks.domain.model.user;

import com.copacracks.domain.exception.UserValidationException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.regex.Pattern;

/**
 * Value Object para representar uma senha usando record. Encapsula a lógica de validação e
 * criptografia de senha. Garante que todas as instâncias sejam válidas através do construtor
 * canônico.
 */
public record Password(String hashedValue, String salt) {

  private static final int MIN_LENGTH = 8;
  private static final int SALT_LENGTH = 16;
  private static final Pattern UPPERCASE = Pattern.compile("[A-Z]");
  private static final Pattern LOWERCASE = Pattern.compile("[a-z]");
  private static final Pattern DIGIT = Pattern.compile("\\d");
  private static final Pattern SPECIAL_CHAR =
      Pattern.compile("[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]");

  /**
   * Construtor canônico com validação. Todas as instâncias de Password passarão por esta validação,
   * independentemente de como forem criadas.
   */
  public Password {
    if (hashedValue == null || hashedValue.isEmpty()) {
      throw new UserValidationException("Valor criptografado não pode ser vazio");
    }
    if (salt == null || salt.isEmpty()) {
      throw new UserValidationException("Salt não pode ser vazio");
    }
  }

  /**
   * Cria uma nova instância de Password a partir de uma senha em texto plano. Valida a senha e a
   * criptografa automaticamente.
   *
   * @param plainPassword senha em texto plano
   * @return nova instância de Password com senha criptografada
   * @throws UserValidationException se a senha for inválida
   */
  public static Password fromPlainText(final String plainPassword) {
    validatePlainPassword(plainPassword);
    final String salt = generateSalt();
    final String hashedValue = hashPassword(plainPassword, salt);
    return new Password(hashedValue, salt);
  }

  /**
   * Cria uma instância de Password a partir de valores já criptografados. Útil para carregar senhas
   * do banco de dados.
   *
   * @param hashedValue valor já criptografado
   * @param salt salt usado na criptografia
   * @return nova instância de Password
   * @throws UserValidationException se os valores forem inválidos
   */
  public static Password fromHash(final String hashedValue, final String salt) {
    return new Password(hashedValue, salt);
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
   * Gera um salt aleatório.
   *
   * @return salt gerado
   */
  private static String generateSalt() {
    final SecureRandom random = new SecureRandom();
    final byte[] saltBytes = new byte[SALT_LENGTH];
    random.nextBytes(saltBytes);
    return Base64.getEncoder().encodeToString(saltBytes);
  }

  /**
   * Criptografa a senha usando SHA-256 com salt.
   *
   * @param plainPassword senha em texto plano
   * @param salt salt a ser usado
   * @return senha criptografada
   */
  private static String hashPassword(final String plainPassword, final String salt) {
    try {
      final MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
      final String saltedPassword = plainPassword + salt;
      final byte[] hashedBytes = messageDigest.digest(saltedPassword.getBytes(StandardCharsets.UTF_8));
      return Base64.getEncoder().encodeToString(hashedBytes);
    } catch (NoSuchAlgorithmException e) {
      throw new UserValidationException("Erro ao criptografar senha", e);
    }
  }

  /**
   * Verifica se a senha fornecida corresponde a esta senha.
   *
   * @param plainPassword senha em texto plano a ser verificada
   * @return true se a senha corresponder, false caso contrário
   */
  public boolean matches(final String plainPassword) {
    if (plainPassword == null) {
      return false;
    }
    final String hashedInput = hashPassword(plainPassword, this.salt);
    return this.hashedValue.equals(hashedInput);
  }

  /**
   * Verifica se a senha é válida. Como todas as instâncias passam pela validação no construtor,
   * este método sempre retornará true para instâncias existentes.
   *
   * @return sempre true para instâncias válidas de Password
   */
  public boolean isValid() {
    return true; // Se chegou até aqui, é válido
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

  @Override
  public String toString() {
    return "Password{hashed='***', salt='***'}";
  }
}
