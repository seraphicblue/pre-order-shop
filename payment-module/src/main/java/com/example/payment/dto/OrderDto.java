package com.example.payment.dto;

import lombok.Getter;

import java.math.BigDecimal;
@Getter
public class OrderDto {
    private String productId; // 상품명
    private String payerId; // 주문자
    private BigDecimal amount; //수량
    private PaymentStatus paymentStatus;//상품 진행 상태

}
