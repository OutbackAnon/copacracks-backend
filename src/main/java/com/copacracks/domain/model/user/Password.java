package com.copacracks.domain.model.user;

import com.copacracks.domain.exception.UserValidationException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Value Object para representar uma senha usando record. Encapsula a lógica de validação e
 * criptografia de senha. Garante que todas as instâncias sejam válidas através do construtor
 * canônico.
 */
public record Password(String hashedValue, String salt) {

  private static final int MIN_LENGTH = 8;
  private static final int SALT_LENGTH = 16;

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
  public static Password fromPlainText(String plainPassword) {
    validatePlainPassword(plainPassword);
    String salt = generateSalt();
    String hashedValue = hashPassword(plainPassword, salt);
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
  public static Password fromHash(String hashedValue, String salt) {
    return new Password(hashedValue, salt);
  }

  /**
   * Valida a senha em texto plano.
   *
   * @param plainPassword senha a ser validada
   * @throws UserValidationException se a senha for inválida
   */
  private static void validatePlainPassword(String plainPassword) {
    if (plainPassword == null || plainPassword.isEmpty()) {
      throw new UserValidationException("Senha não pode ser vazia");
    }

    if (plainPassword.length() < MIN_LENGTH) {
      throw new UserValidationException("Senha deve ter pelo menos " + MIN_LENGTH + " caracteres");
    }

    if (!plainPassword.matches(".*[A-Z].*")) {
      throw new UserValidationException("Senha deve conter pelo menos uma letra maiúscula");
    }

    if (!plainPassword.matches(".*[a-z].*")) {
      throw new UserValidationException("Senha deve conter pelo menos uma letra minúscula");
    }

    if (!plainPassword.matches(".*\\d.*")) {
      throw new UserValidationException("Senha deve conter pelo menos um número");
    }

    if (!plainPassword.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*")) {
      throw new UserValidationException("Senha deve conter pelo menos um caractere especial");
    }
  }

  /**
   * Gera um salt aleatório.
   *
   * @return salt gerado
   */
  private static String generateSalt() {
    SecureRandom random = new SecureRandom();
    byte[] saltBytes = new byte[SALT_LENGTH];
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
  private static String hashPassword(String plainPassword, String salt) {
    try {
      MessageDigest md = MessageDigest.getInstance("SHA-256");
      String saltedPassword = plainPassword + salt;
      byte[] hashedBytes = md.digest(saltedPassword.getBytes());
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
  public boolean matches(String plainPassword) {
    if (plainPassword == null) {
      return false;
    }
    String hashedInput = hashPassword(plainPassword, this.salt);
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
  public static boolean isValidPlainPassword(String plainPassword) {
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
