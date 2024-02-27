package com.example.product.repository;

import com.example.product.entity.Product;
import com.example.product.entity.ProductType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByProductTypeAndExecutionTimeBefore(ProductType productType, LocalDateTime executionTime);
}
