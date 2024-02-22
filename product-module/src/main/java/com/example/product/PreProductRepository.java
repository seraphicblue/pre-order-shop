package com.example.product;

import com.example.product.domain.PreProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PreProductRepository extends JpaRepository<PreProduct, Long> {
}
