package com.example.inventory.controller;

import com.example.inventory.entity.Inventory;
import com.example.inventory.service.InventoryService;
import com.example.inventory.controller.request.InventoryCreateRequest;
import com.example.inventory.controller.request.StockAdjustmentRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    //product 상품 등록시 -> 재고 등록
    @PostMapping("interner/inventory/update")
    public ResponseEntity<?> createInventory(@RequestBody InventoryCreateRequest request) {
        Inventory inventory = inventoryService.createInventory(request.getProductId(), request.getStockQuantity());
        return ResponseEntity.ok(inventory);
    }
    //payment 결제 화면 진입, 결제 중 -> 재고 차감
    @PostMapping("/internal/inventory/deduct")
    public void deductStock(@RequestBody StockAdjustmentRequest request) {
        inventoryService.updateStock(request.getProductId(), request.getPaymentAmount().negate());
    }

    //payment 결제 화면 이탈, 결제 중 취소 -> 재고 증가
    @PostMapping("/internal/inventory/plus")
    public void plusStock(@RequestBody StockAdjustmentRequest request) {
        inventoryService.updateStock(request.getProductId(), request.getPaymentAmount().negate());
    }
}
