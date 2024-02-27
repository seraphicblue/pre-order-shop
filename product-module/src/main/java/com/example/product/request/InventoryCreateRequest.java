package com.example.product.request;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class InventoryCreateRequest {
    private Long productId;
    private BigDecimal stockQuantity;
}
