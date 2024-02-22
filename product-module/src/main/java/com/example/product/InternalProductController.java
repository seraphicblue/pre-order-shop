package com.example.product;

import com.example.product.domain.request.DeductCompleteRequest;
import com.example.product.domain.request.DeductInitRequest;
import com.example.product.domain.request.DeductProceedRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class InternalProductController {

    private final ProductService productService;

    @PostMapping("/internal/products/deductinit")
    public void deductInitStockFromRedis(@RequestBody DeductInitRequest request) {
        productService.deductinitStockFromRedis(request.getProductId(), request.getInitiated());
    }

    @PostMapping("/internal/products/deductprocced")
    public void deductProceedStockFromRedis(@RequestBody DeductProceedRequest request) {
        productService.deductproccedStockFromRedis(request.getProductId(), request.getPaymentAmount(), request.getProgress());
    }

    @PostMapping("/internal/products/deductcomplete")
    public void deductCompleteStockFromRedis(@RequestBody DeductCompleteRequest request) {
        productService.deductcompeleteStockFromRedis(request.getProductId(), request.getPaymentAmount(), request.getCompleted());
    }
}
