package com.example.product.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "normal_products")
public class NormalProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "normal_id")
    private Long normalId;

    @Column(name = "normal_product_name")
    private String normalProductName;

    @Column(name = "normal_stock")
    private Integer normalStock;

    @Column(name = "normal_price")
    private BigDecimal normalPrice;

}
