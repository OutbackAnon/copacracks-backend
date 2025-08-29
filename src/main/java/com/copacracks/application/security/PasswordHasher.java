package com.copacracks.application.security;

public interface PasswordHasher {
  String createHash(String rawPassword);

  String createHash(String rawPassword, String salt);

  boolean verify(String rawPassword, String hashedPassword);
}
