package com.example.payment.dto;


import lombok.Getter;

import java.math.BigDecimal;
@Getter
public class OrderDto {
    private String productId;
    private String payerId;
    private BigDecimal amount;
    private String productType;

}
