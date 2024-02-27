package com.example.product;


import com.example.product.request.InventoryCreateRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;


@FeignClient(name = "inventory-module", url = "http://localhost:8089")
public interface InventoryClient {
    @PostMapping("interner/inventory/update")
    void updateStock(@RequestBody InventoryCreateRequest request);
}