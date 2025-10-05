package com.copacracks.domain.model.user;

public record HashedPassword(String value) implements PasswordValue {
    public HashedPassword {
        if (!isValidHashedPassword(value)) {
            throw new IllegalArgumentException("Hashed password cannot be null or empty");
        }
    }

    private static boolean isValidHashedPassword(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
