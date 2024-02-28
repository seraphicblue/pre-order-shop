package com.example.payment.service.exception;


import org.springframework.http.HttpStatus;

public class PaymentNotFoundException extends RuntimeException {
    private final ErrorCode errorCode;

    public PaymentNotFoundException(ErrorCode errorCode, Long paymentId) {
        super(errorCode.getMessage() + paymentId);
        this.errorCode = errorCode;
    }
    public HttpStatus getHttpStatus() {

        return errorCode.getHttpStatus();
    }

}
