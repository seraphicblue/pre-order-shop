package com.example.payment.service.exception;


import org.springframework.http.HttpStatus;

import java.math.BigDecimal;

public class InvalidFinalQuantityException extends RuntimeException {
    private final ErrorCode errorCode;

    public InvalidFinalQuantityException(ErrorCode errorCode, BigDecimal finalQuantity) {
        super(errorCode.getMessage() + ": " + finalQuantity);
        this.errorCode = errorCode;
    }

    public HttpStatus getHttpStatus() {
        return errorCode.getHttpStatus();
    }
}

