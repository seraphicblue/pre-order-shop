
package com.example.payment.controller.request;


import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Builder
@Getter
public class UpdateStockRequest {

    private Long productId;
    private BigDecimal paymentAmount;




}
