package com.copacracks.domain.model.user;

public record HashedPassword(String value) implements PasswordValue {
}
