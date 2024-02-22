package com.example.payment;


import com.example.payment.request.DeductCompleteRequest;
import com.example.payment.request.DeductInitRequest;
import com.example.payment.request.DeductProceedRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "product-module", url = "http://localhost:8087")
public interface ProductServiceClient {

    @PostMapping("/internal/products/deductinit")
    void deductInitStockFromRedis(@RequestBody DeductInitRequest request);

    @PostMapping("/internal/products/deductprocced")
    void deductProceedStockFromRedis(@RequestBody DeductProceedRequest request);

    @PostMapping("/internal/products/deductcomplete")
    void deductCompleteStockFromRedis(@RequestBody DeductCompleteRequest request);
}
