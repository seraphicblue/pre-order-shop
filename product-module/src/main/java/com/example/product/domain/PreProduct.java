package com.example.product.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "pre_products")
public class PreProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pre_id")
    private Long preId;

    @Column(name = "pre_product_name")
    private String preProductName;

    @Column(name = "pre_execution_time")
    private LocalDateTime preExecutionTime;

    @Column(name = "pre_stock")
    private Integer preStock;

    @Column(name = "pre_price")
    private BigDecimal prePrice;

}
