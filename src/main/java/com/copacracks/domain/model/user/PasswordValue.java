package com.copacracks.domain.model.user;

public sealed interface PasswordValue permits HashedPassword, RawPassword {
    String value();
}
