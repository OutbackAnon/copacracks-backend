package com.copacracks.domain.model.user;

public class PartialUser {
    private final Username username;
    private final RawPassword password;
    private final Email email;

    public PartialUser(String username, String email, String password) {
        this.username = new Username(username);
        this.password = new RawPassword(password);
        this.email = new Email(email);
    }

    public PartialUser(String username, String password) {
        this.username = new Username(username);
        this.password = new RawPassword(password);
        this.email = null;
    }

    public Email getEmail() {
        return email;
    }

    public RawPassword getPassword() {
        return password;
    }

    public Username getUsername() {
        return username;
    }
}
