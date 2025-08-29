package com.copacracks.infrastructure.security;

import com.copacracks.application.security.PasswordHasher;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.StringJoiner;
import org.bouncycastle.crypto.generators.Argon2BytesGenerator;
import org.bouncycastle.crypto.params.Argon2Parameters;

public class PasswordHasherImpl implements PasswordHasher {
  @Override
  public String createHash(String rawPassword) {
    byte[] salt = generatedSalt();
    return genereteArgonHash(rawPassword, toStringHex(salt));
  }

  @Override
  public String createHash(String rawPassword, String salt) {
    return genereteArgonHash(rawPassword, salt);
  }

  @Override
  public boolean verify(String rawPassword, String hashedPassword) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'verify'");
  }

  private byte[] generatedSalt() {
    SecureRandom secureRandom = new SecureRandom();
    byte[] salt = new byte[16];
    secureRandom.nextBytes(salt);

    return salt;
  }

  private String genereteArgonHash(String rawPassword, String salt) {
    Argon2Parameters.Builder builder =
        new Argon2Parameters.Builder(Argon2Parameters.ARGON2_id)
            .withVersion(Argon2Parameters.ARGON2_VERSION_13)
            .withIterations(2)
            .withMemoryAsKB(66536)
            .withParallelism(1)
            .withSalt(salt.getBytes(StandardCharsets.UTF_8));

    Argon2BytesGenerator generator = new Argon2BytesGenerator();
    generator.init(builder.build());

    byte[] result = new byte[32];

    generator.generateBytes(rawPassword.getBytes(StandardCharsets.UTF_8), result, 0, result.length);

    StringJoiner sj = new StringJoiner("::");
    sj.add(salt);
    sj.add(toStringHex(result));

    return sj.toString();
  }

  private String toStringHex(byte[] bytes) {
    return String.format("%040x", new BigInteger(1, bytes));
  }
}
