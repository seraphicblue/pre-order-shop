package com.example.product.controller.request;

import lombok.Builder;
import lombok.Getter;
@Builder
@Getter
public class ProductRequest {
    private final String productId;
    private final int quantity;

}
