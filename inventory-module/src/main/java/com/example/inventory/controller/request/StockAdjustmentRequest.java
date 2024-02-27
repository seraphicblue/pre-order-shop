package com.example.inventory.controller.request;

import com.example.inventory.entity.PaymentStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Builder
@Getter
public class StockAdjustmentRequest {

    private Long productId;
    private BigDecimal paymentAmount;
    private PaymentStatus paymentStatus;


    public void updatePaymentStatus(PaymentStatus paymentStatus) {

        this.paymentStatus = paymentStatus;
    }
}
