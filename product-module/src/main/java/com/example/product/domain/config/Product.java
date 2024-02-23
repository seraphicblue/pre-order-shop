package com.example.product.domain.config;

import com.example.product.dto.ProductDto;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@NoArgsConstructor(access = AccessLevel.PROTECTED) // 기본 생성자 추가
@AllArgsConstructor // 전체 생성자 추가
@Getter
@Entity
@Builder
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long productId;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "stock")
    private BigDecimal stock;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "execution_time")
    private LocalDateTime executionTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "product_type")
    private ProductType productType;


    public void updateStock(BigDecimal newStock) {
        this.stock = newStock;
    }

    // Product 엔티티를 ProductDto로 변환
    public static ProductDto toDto(Product product) {
        return ProductDto.builder()
                .productId(product.getProductId())
                .productName(product.getProductName())
                .stock(product.getStock())
                .price(product.getPrice())
                .executionTime(product.getExecutionTime())
                .productType(product.getProductType())
                .build();
    }
}

