
package com.example.payment;

import com.example.payment.request.UpdateStockRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "product-module", url = "http://localhost:8087")
public interface ProductServiceClient {

    @PostMapping("/internal/products/deduct")
    void deductProduct(@RequestBody UpdateStockRequest deductrequest);

    @PostMapping("/internal/products/plus")
    void plusProduct(@RequestBody UpdateStockRequest plusrequest);


}


