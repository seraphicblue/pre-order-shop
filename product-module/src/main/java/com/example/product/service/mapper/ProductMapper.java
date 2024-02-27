package com.example.product.service.mapper;

import com.example.product.entity.Product;
import com.example.product.controller.dto.ProductDto;

public class ProductMapper {

    // ProductDto에서 Product 엔티티로 변환
    public static Product fromDto(ProductDto dto) {
        return Product.builder()
                .productName(dto.getProductName())
                .stock(dto.getStock())
                .price(dto.getPrice())
                .executionTime(dto.getExecutionTime())
                .productType(dto.getProductType())
                .build();
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
