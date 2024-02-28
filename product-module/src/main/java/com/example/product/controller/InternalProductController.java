
package com.example.product.controller;

import com.example.product.service.ProductService;
import com.example.product.controller.request.UpdateStockRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/interner/products")
public class InternalProductController {

    private final ProductService productService;

    //payment 최종 결제시 -> 재고 차감
    @PostMapping("/deduct")
    public void deductProduct(@RequestBody UpdateStockRequest request) {
        productService.deductProduct(request.getProductId(), request.getPaymentAmount());
    }

    //payment 최종 결제 취소시 -> 재고 증가
    @PostMapping("/plus")
    public void plusProduct(@RequestBody UpdateStockRequest request) {
        productService.plusProduct(request.getProductId(), request.getPaymentAmount());
    }
}

