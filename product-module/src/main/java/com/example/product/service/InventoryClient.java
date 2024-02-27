package com.example.product.service;


import com.example.product.controller.request.InventoryCreateRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(name = "inventory-module", url = "http://localhost:8089")
public interface InventoryClient {
    @PostMapping("interner/inventory/update")
    void updateStock(@RequestBody InventoryCreateRequest request);
}