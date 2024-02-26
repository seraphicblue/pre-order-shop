package com.example.payment;

import com.example.payment.request.StockAdjustmentRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "inventory-module", url = "http://localhost:8089")
public interface InventoryServiceClient {
    @PostMapping("/internal/inventory/deduct")
    void deductStock(@RequestBody StockAdjustmentRequest request);

    @PostMapping("/internal/inventory/plus")
    void plusStock(@RequestBody StockAdjustmentRequest request);
}
