package com.example.product.service;

import com.example.product.entity.Product;
import com.example.product.entity.ProductType;
import com.example.product.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class ScheduledTasks {

    @Autowired
    private ProductRepository productRepository;

    // 매일 오후 2시에 실행되는 메서드
    @Scheduled(cron = "0 0 14 * * ?")
    public void activateReservedProducts() {
        LocalDateTime now = LocalDateTime.now();
        List<Product> productsToActivate = productRepository.findByProductTypeAndExecutionTimeBefore(ProductType.PRE, now);

        productsToActivate.forEach(product -> {
            Product updatedProduct = Product.builder()
                    .productId(product.getProductId())
                    .productName(product.getProductName())
                    .stock(product.getStock())
                    .price(product.getPrice())
                    .executionTime(product.getExecutionTime())
                    .productType(ProductType.NORMAL) // 상품 타입 업데이트
                    .build();

            productRepository.save(updatedProduct); // 업데이트된 상품 저장
        });
    }
}
