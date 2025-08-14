package com.copacracks.domain.exception;

/** Exceção lançada quando há problemas de validação com o usuário. */
public class UserValidationException extends RuntimeException {

  public UserValidationException(final String message) {
    super(message);
  }

  public UserValidationException(final String message, final Throwable cause) {
    super(message, cause);
  }
}
