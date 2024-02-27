package com.example.payment.service;

import com.example.payment.controller.request.StockAdjustmentRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "inventory-module", url = "http://localhost:8089")
public interface InventoryServiceClient {
    //실시간 재고 차감
    @PostMapping("/internal/inventory/deduct")
    void deductStock(@RequestBody StockAdjustmentRequest request);
    //실시간 재고 추가
    @PostMapping("/internal/inventory/plus")
    void plusStock(@RequestBody StockAdjustmentRequest request);
}
