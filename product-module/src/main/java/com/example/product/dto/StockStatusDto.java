package com.example.product.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class StockStatusDto {
    private final Long productId;
    private final BigDecimal currentStock;
    
}

