package com.copacracks.domain.model.user;

public record RawPassword(String value) implements PasswordValue {
    public RawPassword {
        Password.validatePlainPassword(value);
    }
}
