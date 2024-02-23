package com.example.product;

import com.example.product.request.DeductRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class InternalProductController {

    private final ProductService productService;

    @PostMapping("/internal/products/deduct/redis")
    public void deductStockFromRedis(@RequestBody DeductRequest request) {
        productService.deductStockFromRedis(request.getProductId(), request.getPaymentAmount());
    }

    @PostMapping("/internal/products/plus/redis")
    public void plusStockFromRedis(@RequestBody DeductRequest request) {
        productService.plusStockFromRedis(request.getProductId(), request.getPaymentAmount());
    }

    @PostMapping("/internal/products/deduct/mysql")
    public void deductProductFromMysql(@RequestBody DeductRequest request) {
        productService.deductStockFromMysql(Long.valueOf(request.getProductId()), request.getPaymentAmount());
    }

    @PostMapping("/internal/products/plus/mysql")
    public void plusProductfromMysql(@RequestBody DeductRequest request) {
        productService.plusStockToMysql(Long.valueOf(request.getProductId()), request.getPaymentAmount());
    }
}
