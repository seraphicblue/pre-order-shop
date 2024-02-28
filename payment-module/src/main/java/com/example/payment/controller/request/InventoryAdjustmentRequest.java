package com.example.payment.controller.request;

import com.example.payment.controller.dto.PaymentStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class InventoryAdjustmentRequest {

    private Long productId;
    private BigDecimal paymentAmount;
    private PaymentStatus paymentStatus;

    public void updatePaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }


    public static InventoryAdjustmentRequest createInventoryAdjustmentRequest(Long productId, BigDecimal amount, PaymentStatus status) {
        return InventoryAdjustmentRequest.builder()
                .productId(productId)
                .paymentAmount(amount)
                .paymentStatus(status)
                .build();
    }
}
