package com.example.product.exception;

import org.springframework.http.HttpStatus;

public class InvalidStockOperationException extends RuntimeException {
    private final ErrorCode errorCode;

    public InvalidStockOperationException(ErrorCode errorCode, String productId) {
        super(errorCode.getMessage() + productId);
        this.errorCode = errorCode;
    }

    public HttpStatus getHttpStatus() {
        return errorCode.getHttpStatus();
    }
}
