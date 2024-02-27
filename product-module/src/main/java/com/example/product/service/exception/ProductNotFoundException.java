package com.example.product.service.exception;

import org.springframework.http.HttpStatus;

public class ProductNotFoundException  extends RuntimeException {
    private final ErrorCode errorCode;

    public ProductNotFoundException (ErrorCode errorCode, String productId) {

        super(errorCode.getMessage() + productId);
        this.errorCode = errorCode;
    }

    public HttpStatus getHttpStatus() {
        return errorCode.getHttpStatus();
    }
}
