package com.example.product.domain;

import lombok.Builder;
import lombok.Getter;
@Builder
@Getter
public class ProductRequest {
    private final String productId;
    private final int quantity;

}
