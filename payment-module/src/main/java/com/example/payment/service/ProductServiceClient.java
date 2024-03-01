
package com.example.payment.service;

import com.example.payment.controller.request.UpdateStockRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "product-module", url = "http://localhost:8087")
public interface ProductServiceClient {

    @PostMapping("/interner/products/deduct")
    void deductProduct(@RequestBody UpdateStockRequest deductrequest);

    @PostMapping("/interner/products/plus")
    void plusProduct(@RequestBody UpdateStockRequest plusrequest);


}


