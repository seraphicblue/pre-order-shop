package com.example.product;

import com.example.product.dto.ProductDto;
import com.example.product.dto.StockStatusDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/product")
public class ProductController {
    private final ProductService productService;

    // 전체 상품 가져오기
    @GetMapping("/list")
    public ResponseEntity<List<ProductDto>> getProductList() {
        List<ProductDto> productList = productService.getAllProducts();
        return ResponseEntity.ok(productList);
    }

    // 상품 재고 확인하기
    @GetMapping("/{productId}/stock")
    public ResponseEntity<StockStatusDto> getCurrentStock(@PathVariable Long productId) {
        StockStatusDto stockStatus = productService.checkStock(productId);
        return ResponseEntity.ok(stockStatus);
    }

    // 상품 상세 조회
    @GetMapping("/{productId}")
    public ResponseEntity<ProductDto> getProductDetail(@PathVariable Long productId) {
        ProductDto productDetail = productService.getProductDetail(productId);
        return ResponseEntity.ok(productDetail);
    }
}
