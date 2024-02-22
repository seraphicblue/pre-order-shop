package com.example.payment.request;


import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
@Builder
@Getter
public class DeductProceedRequest {

    private String productId;
    private BigDecimal paymentAmount;
    private String progress;


}