package com.example.product.dto;

import com.example.product.domain.config.ProductType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Getter
@Builder
public class ProductDto {
    private Long productId;
    private String productName;
    private BigDecimal stock;
    private BigDecimal price;
    private LocalDateTime executionTime;
    private ProductType productType;
    private boolean isPurchasable; // 구매 가능 여부를 나타내는 필드

    // 구매 가능 여부 업데이트 메소드
    public void updatePurchasable(LocalDateTime now) {
        // executionTime이 null이 아니고 현재 시간(now) 이후인 경우에만 구매 불가능으로 설정
        if (this.executionTime != null && this.executionTime.isAfter(now)) {
            this.isPurchasable = false;
        } else {
            // 그 외의 경우에는 구매 가능으로 설정
            this.isPurchasable = true;
        }
    }

}
