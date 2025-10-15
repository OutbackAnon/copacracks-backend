package com.copacracks.infrastructure.exception;

import java.io.Serial;

public class ConfigurationException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public ConfigurationException(String message) {
        super(message);
    }

    public ConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}
