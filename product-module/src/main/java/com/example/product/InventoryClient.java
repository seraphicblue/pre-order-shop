package com.example.product;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

@FeignClient(name = "inventory-service", url = "http://localhost:8089")
public interface InventoryClient {
    @PostMapping("interner/inventory/update/{productId}")
    void updateStock(@PathVariable Long productId, @RequestParam BigDecimal amount);
}
