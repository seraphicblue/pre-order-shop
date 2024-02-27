package com.example.product.controller.request;



import com.example.product.entity.PaymentStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Builder
@Getter
public class DeductRequest {

    private String productId;
    private BigDecimal paymentAmount;
    private PaymentStatus paymentStatus;


    public void updatePaymentStatus(PaymentStatus paymentStatus) {

        this.paymentStatus = paymentStatus;
    }
}