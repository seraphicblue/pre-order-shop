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
    private boolean isPurchasable;

    public ProductDto(Long id, String productName, Integer stock, BigDecimal price, LocalDateTime executionTime) {
        this.id = id;
        this.productName = productName;
        this.stock = stock;
        this.price = price;
        this.executionTime = executionTime;
        this.isPurchasable = false; // 기본값은 false로 설정
    }

    public void updatePurchasable(LocalDateTime now) {
        if (this.executionTime != null) {
            this.isPurchasable = this.executionTime.isBefore(now);
        } else {
            this.isPurchasable = true; // executionTime이 null인 경우, 항상 구매 가능하다고 가정
        }
    }
}
