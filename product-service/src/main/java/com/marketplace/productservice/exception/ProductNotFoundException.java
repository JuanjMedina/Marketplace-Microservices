package com.marketplace.productservice.exception;

import org.springframework.http.HttpStatus;

public class ProductNotFoundException extends RuntimeException {
    private final HttpStatus status;

    public ProductNotFoundException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
