package com.example.inventory.controller;

import com.example.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    //실시간 재고 확인
    @GetMapping("/{productId}/stock")
    public ResponseEntity<BigDecimal> getCurrentStock(@PathVariable Long productId) {
        BigDecimal currentStock = inventoryService.getCurrentInventory(productId);
        return ResponseEntity.ok(currentStock);
    }
}
