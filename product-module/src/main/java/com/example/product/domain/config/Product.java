package com.example.product.domain.config;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
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
}
