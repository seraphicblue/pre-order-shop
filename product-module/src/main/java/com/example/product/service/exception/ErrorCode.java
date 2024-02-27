package com.example.product.service.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    PRODUCT_NOT_FOUND("PRODUCT_NOT_FOUND", "상품을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    INVALID_STOCK_OPERATION("INVALID_STOCK_OPERATION", "잘못된 재고량 입니다.", HttpStatus.BAD_REQUEST),
    INSUFFICIENT_STOCK("INSUFFICIENT_STOCK", "재고가 충분하지 않습니다.", HttpStatus.CONFLICT); // 여기에 수정을 가했습니다.

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(String code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
