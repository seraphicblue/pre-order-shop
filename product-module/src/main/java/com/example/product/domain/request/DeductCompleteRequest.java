package com.example.product.domain.request;

import lombok.Getter;

import java.math.BigDecimal;
@Getter
public class DeductCompleteRequest {

    private String productId;
    private BigDecimal paymentAmount;
    private String completed;


}