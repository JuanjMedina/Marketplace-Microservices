package com.marketplace.userservice.exception;

public class KeycloakOperationException extends RuntimeException {
    public KeycloakOperationException(String message) {
        super(message);
    }
}
