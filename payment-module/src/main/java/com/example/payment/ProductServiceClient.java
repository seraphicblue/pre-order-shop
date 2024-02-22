package com.example.payment;

import com.example.payment.request.DeductRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "product-module", url = "http://localhost:8087")
public interface ProductServiceClient {
    @PostMapping("/internal/products/deduct")
    void deductStockFromRedis(@RequestBody DeductRequest request);

    @PostMapping("/internal/products/plus")
    void plusStockFromRedis(@RequestBody DeductRequest request);
}

