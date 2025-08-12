package com.copacracks.domain.model.user;

import com.copacracks.domain.exception.UserValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class PasswordTest {

    private static final String VALID_PASSWORD = "Abcdef1!"; // 8 chars, meets all rules

    @Test
    @DisplayName("fromPlainText should create hashed value and salt, with salt being 16 decoded bytes")
    void fromPlainText_createsHashedAndSalt() {
        Password pwd = Password.fromPlainText(VALID_PASSWORD);

        assertNotNull(pwd, "Password instance should not be null");
        assertNotNull(pwd.hashedValue(), "Hashed value should not be null");
        assertNotNull(pwd.salt(), "Salt should not be null");
        assertFalse(pwd.hashedValue().isEmpty(), "Hashed value should not be empty");
        assertFalse(pwd.salt().isEmpty(), "Salt should not be empty");
        assertNotEquals(VALID_PASSWORD, pwd.hashedValue(), "Hashed value must not equal the plain password");

        // Salt should be base64 that decodes to 16 bytes
        byte[] saltBytes = Base64.getDecoder().decode(pwd.salt());
        assertEquals(16, saltBytes.length, "Salt must decode to 16 bytes");
    }

    @Test
    @DisplayName("fromPlainText should reject invalid passwords per validation rules")
    void fromPlainText_rejectsInvalidPasswords() {
        assertThrows(UserValidationException.class, () -> Password.fromPlainText(null), "Null should be rejected");
        assertThrows(UserValidationException.class, () -> Password.fromPlainText(""), "Empty should be rejected");
        assertThrows(UserValidationException.class, () -> Password.fromPlainText("Abc1!a"), "Too short (<8) should be rejected");
        assertThrows(UserValidationException.class, () -> Password.fromPlainText("abcdef1!"), "Missing uppercase should be rejected");
        assertThrows(UserValidationException.class, () -> Password.fromPlainText("ABCDEFG1!"), "Missing lowercase should be rejected");
        assertThrows(UserValidationException.class, () -> Password.fromPlainText("Abcdefghi"), "Missing digit should be rejected");
        assertThrows(UserValidationException.class, () -> Password.fromPlainText("Abcdefg1"), "Missing special character should be rejected");
    }

    @Test
    @DisplayName("matches returns true for correct password, false for wrong or null")
    void matches_behavior() {
        Password pwd = Password.fromPlainText(VALID_PASSWORD);

        assertTrue(pwd.matches(VALID_PASSWORD), "Correct plain password should match");
        assertFalse(pwd.matches("Wrong1!"), "Incorrect password should not match");
        assertFalse(pwd.matches(null), "Null input should not match");
    }

    @Test
    @DisplayName("fromHash recreates a Password that matches the original plain password")
    void fromHash_recreatesAndMatches() {
        Password original = Password.fromPlainText(VALID_PASSWORD);

        Password reconstructed = Password.fromHash(original.hashedValue(), original.salt());
        assertTrue(reconstructed.matches(VALID_PASSWORD), "Reconstructed password should match with same salt");

        // Using the same hash with a different salt must fail
        String otherSalt = Password.fromPlainText("Another1!").salt();
        Password withWrongSalt = Password.fromHash(original.hashedValue(), otherSalt);
        assertFalse(withWrongSalt.matches(VALID_PASSWORD), "Mismatched salt should cause match to fail");
    }

    @Test
    @DisplayName("Canonical constructor enforces non-null, non-empty hashedValue and salt")
    void canonicalConstructor_validation() {
        assertThrows(UserValidationException.class, () -> new Password(null, "salt"), "Null hash should be rejected");
        assertThrows(UserValidationException.class, () -> new Password("", "salt"), "Empty hash should be rejected");
        assertThrows(UserValidationException.class, () -> new Password("hash", null), "Null salt should be rejected");
        assertThrows(UserValidationException.class, () -> new Password("hash", ""), "Empty salt should be rejected");
    }

    @Test
    @DisplayName("isValid returns true for all constructed instances")
    void isValid_alwaysTrue() {
        Password pwd = Password.fromPlainText(VALID_PASSWORD);
        assertTrue(pwd.isValid(), "isValid should always be true for constructed instances");
    }

    @Test
    @DisplayName("isValidPlainPassword returns expected booleans for valid and invalid inputs")
    void isValidPlainPassword_expectedBooleans() {
        assertTrue(Password.isValidPlainPassword(VALID_PASSWORD), "Valid password should be true");

        assertFalse(Password.isValidPlainPassword(null), "Null should be false");
        assertFalse(Password.isValidPlainPassword(""), "Empty should be false");
        assertFalse(Password.isValidPlainPassword("Abc1!a"), "Too short should be false");
        assertFalse(Password.isValidPlainPassword("abcdef1!"), "Missing uppercase should be false");
        assertFalse(Password.isValidPlainPassword("ABCDEFG1!"), "Missing lowercase should be false");
        assertFalse(Password.isValidPlainPassword("Abcdefghi"), "Missing digit should be false");
        assertFalse(Password.isValidPlainPassword("Abcdefg1"), "Missing special should be false");
    }

    @Test
    @DisplayName("toString masks hashed value and salt")
    void toString_masksSensitiveData() {
        Password pwd = Password.fromPlainText(VALID_PASSWORD);
        assertEquals("Password{hashed='***', salt='***'}", pwd.toString(), "toString should mask sensitive fields");
    }
}