package com.example.inventory.controller;

import com.example.inventory.Inventory;
import com.example.inventory.InventoryService;
import com.example.inventory.request.InventoryCreateRequest;
import com.example.inventory.request.StockAdjustmentRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class InventoryController {
    private final InventoryService inventoryService;

    //재고 등록
    @PostMapping("/create")
    public ResponseEntity<?> createInventory(@RequestBody InventoryCreateRequest request) {
        Inventory inventory = inventoryService.createInventory(request.getProductId(), request.getStockQuantity());
        return ResponseEntity.ok(inventory);
    }
    //재고 차감
    @PostMapping("/internal/inventory/deduct")
    public void deductStock(@RequestBody StockAdjustmentRequest request) {
        inventoryService.updateStock(request.getProductId(), request.getPaymentAmount().negate());
    }
    //재고 증가
    @PostMapping("/internal/inventory/plus")
    public void plusStock(@RequestBody StockAdjustmentRequest request) {
        inventoryService.updateStock(request.getProductId(), request.getPaymentAmount().negate());
    }
}
