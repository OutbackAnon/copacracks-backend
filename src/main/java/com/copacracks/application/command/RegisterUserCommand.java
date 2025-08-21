package com.copacracks.application.command;

public record RegisterUserCommand(String username, String password, String email) {}
