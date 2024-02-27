package com.example.product.controller.request;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class InventoryCreateRequest {
    private Long productId;
    private BigDecimal stockQuantity;
}
