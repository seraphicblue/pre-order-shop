package com.example.product.service.exception;

import org.springframework.http.HttpStatus;

public class InsufficientStockException extends RuntimeException {
    private final ErrorCode errorCode;

    public InsufficientStockException(ErrorCode errorCode, String productId) {
        super(errorCode.getMessage() + ": " + productId);
        this.errorCode = errorCode;
    }

    public HttpStatus getHttpStatus() {
        return errorCode.getHttpStatus();
    }
}
