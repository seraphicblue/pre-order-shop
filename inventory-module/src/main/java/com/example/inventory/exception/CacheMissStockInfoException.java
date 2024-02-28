package com.example.inventory.exception;

import org.springframework.http.HttpStatus;

public class CacheMissStockInfoException extends RuntimeException {
    private final ErrorCode errorCode;

    public CacheMissStockInfoException(ErrorCode errorCode, Long productId) {

        super(errorCode.getMessage() + productId);
        this.errorCode = errorCode;
    }

    public HttpStatus getHttpStatus() {

        return errorCode.getHttpStatus();
    }
}
