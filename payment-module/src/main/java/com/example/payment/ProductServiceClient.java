package com.example.payment;

import com.example.payment.request.DeductRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;

@FeignClient(name = "product-module", url = "http://localhost:8087")
public interface ProductServiceClient {
    @PostMapping("/internal/products/deduct/redis")
    void deductStockFromRedis(@RequestBody DeductRequest request);

    @PostMapping("/internal/products/plus/redis")
    void plusStockFromRedis(@RequestBody DeductRequest request);

    @PostMapping("/internal/products/deduct/mysql")
    void deductProductFromMysql(@RequestBody DeductRequest request);

    @PostMapping("/internal/products/plus/mysql")
    void plusProductFromMysql(@RequestBody DeductRequest request);


}

