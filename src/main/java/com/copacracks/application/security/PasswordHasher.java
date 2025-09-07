package com.copacracks.application.security;

/**
 * Interface for password hashing and verification operations.
 *
 * <p>This interface provides a contract for secure password handling, including hashing plain text
 * passwords and verifying passwords against their hashed representations. Implementations should
 * use cryptographically secure hashing algorithms suitable for password storage.
 *
 * <p>The interface supports both automatic salt generation and explicit salt specification,
 * allowing for flexible password security strategies. All implementations should follow security
 * best practices including:
 *
 * <ul>
 *   <li>Using strong, slow hashing algorithms (e.g., bcrypt, scrypt, Argon2)
 *   <li>Generating cryptographically secure random salts
 *   <li>Using appropriate computational cost parameters
 *   <li>Handling timing attacks through constant-time comparisons
 * </ul>
 *
 * <p>Implementations should be thread-safe to support concurrent password operations in
 * multi-threaded environments.
 */
public interface PasswordHasher {

  /**
   * Creates a hash of the provided raw password using an automatically generated salt.
   *
   * <p>This method generates a cryptographically secure random salt and uses it to hash the
   * provided password. The resulting hash typically includes both the salt and algorithm
   * parameters, making it self-contained for later verification.
   *
   * <p>This is the recommended method for most password hashing scenarios as it ensures each
   * password gets a unique, randomly generated salt.
   *
   * @param rawPassword the plain text password to hash, must not be null or empty
   * @return the hashed password string, typically including salt and algorithm metadata
   * @throws IllegalArgumentException if rawPassword is null or empty
   * @throws RuntimeException if the hashing operation fails due to cryptographic errors
   */
  String createHash(String rawPassword);

  /**
   * Creates a hash of the provided raw password using the specified salt.
   *
   * <p>This method allows explicit control over the salt used in the hashing process. It should be
   * used when salt reuse is required for specific scenarios, though automatic salt generation is
   * generally preferred for security reasons.
   *
   * <p>The provided salt should be cryptographically secure and sufficiently random to prevent
   * rainbow table attacks.
   *
   * @param rawPassword the plain text password to hash, must not be null or empty
   * @param salt the salt to use for hashing, must not be null or empty
   * @return the hashed password string using the provided salt
   * @throws IllegalArgumentException if rawPassword or salt is null or empty
   * @throws RuntimeException if the hashing operation fails due to cryptographic errors
   */
  String createHash(String rawPassword, String salt);

  /**
   * Verifies if a raw password matches the provided hashed password.
   *
   * <p>This method performs a secure comparison between the provided plain text password and its
   * hashed representation. It extracts the salt and algorithm parameters from the hashed password,
   * re-hashes the raw password, and performs a constant-time comparison to prevent timing attacks.
   *
   * <p>The verification process should be resistant to timing attacks by ensuring that the
   * comparison time is constant regardless of where differences occur in the compared values.
   *
   * @param rawPassword the plain text password to verify, must not be null
   * @param hashedPassword the hashed password to compare against, must not be null
   * @return {@code true} if the raw password matches the hashed password, {@code false} otherwise
   * @throws IllegalArgumentException if rawPassword or hashedPassword is null
   * @throws RuntimeException if the verification operation fails due to malformed hash data or
   *     cryptographic errors
   */
  boolean verify(String rawPassword, String hashedPassword);
}
