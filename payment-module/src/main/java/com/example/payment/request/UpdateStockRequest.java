
package com.example.payment.request;


import com.example.payment.dto.PaymentStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Builder
@Getter
public class UpdateStockRequest {

    private Long productId;
    private BigDecimal paymentAmount;




}
