package com.example.product;

import com.example.product.request.DeductRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class InternalProductController {

    private final ProductService productService;
    @PostMapping("/internal/products/deduct")
    void deductStockFromRedis(@RequestBody DeductRequest request){
        productService.deductStockFromRedis(request.getProductId(),
                request.getPaymentAmount());
    }

    @PostMapping("/internal/products/plus")
    void plusStockFromRedis(@RequestBody DeductRequest request){
        productService.plusStockFromRedis(request.getProductId(),
                request.getPaymentAmount());
    }

}
