package com.copacracks.domain.exception;

/** Exceção lançada quando há problemas de validação com o usuário. */
public class UserValidationException extends RuntimeException {

  public UserValidationException(String message) {
    super(message);
  }

  public UserValidationException(String message, Throwable cause) {
    super(message, cause);
  }
}
