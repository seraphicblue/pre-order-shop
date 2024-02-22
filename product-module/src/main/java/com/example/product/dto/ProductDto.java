package com.example.product.dto;

import lombok.Getter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class ProductDto {
    private Long id;
    private String productName;
    private Integer stock;
    private BigDecimal price;
    private LocalDateTime executionTime;

    public ProductDto(Long id, String productName, Integer stock, BigDecimal price, LocalDateTime executionTime) {
        this.id = id;
        this.productName = productName;
        this.stock = stock;
        this.price = price;
        this.executionTime = executionTime;
    }
}
