package com.example.product;

import com.example.product.domain.NormalProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NormalProductRepository extends JpaRepository<NormalProduct, Long> {
}
