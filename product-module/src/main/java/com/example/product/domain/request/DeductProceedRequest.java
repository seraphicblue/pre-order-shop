package com.example.product.domain.request;


import lombok.Getter;

import java.math.BigDecimal;
@Getter
public class DeductProceedRequest {

    private String productId;
    private BigDecimal paymentAmount;
    private String progress;


}