package com.example.product.service;

import com.example.product.entity.Product;
import com.example.product.controller.dto.ProductDto;
import com.example.product.service.exception.InvalidStockOperationException;
import com.example.product.service.exception.ProductNotFoundException;
import com.example.product.service.mapper.ProductMapper;
import com.example.product.repository.ProductRepository;
import com.example.product.controller.request.InventoryCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.product.service.exception.ErrorCode.INVALID_STOCK_OPERATION;
import static com.example.product.service.exception.ErrorCode.PRODUCT_NOT_FOUND;

@RequiredArgsConstructor
@Service
public class ProductService {

    private final RedisTemplate<String, String> redisTemplate;
    private final ProductRepository productRepository;

    private final InventoryClient inventoryClient; // Feign Client 추가

    // 상품 등록
    public ProductDto registerProduct(ProductDto productDto) {
        // DTO에서 Entity로 변환
        Product product = ProductMapper.fromDto(productDto);
        // 상품 정보 저장
        product = productRepository.save(product);

        // InventoryCreateRequest 객체 생성
        InventoryCreateRequest request = InventoryCreateRequest.builder()
                .productId(product.getProductId())
                .stockQuantity(product.getStock())
                .build();

        // 재고 등록 요청을 InventoryService로 전송
        inventoryClient.updateStock(request);

        // Entity를 DTO로 변환하여 반환
        return ProductMapper.toDto(product);
    }


    // 전체 상품 조회
    public List<ProductDto> getAllProducts() {
        return productRepository.findAll().stream()
                .map(ProductMapper::toDto)
                .collect(Collectors.toList());
    }

    // 상품 상세 조회
    public ProductDto getProductDetail(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(PRODUCT_NOT_FOUND ,": " + productId));
        return ProductMapper.toDto(product);
    }

    public void deductProduct(Long productId, BigDecimal amount) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(PRODUCT_NOT_FOUND ,": " + productId));
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {

            throw new InvalidStockOperationException(INVALID_STOCK_OPERATION, ": " + amount);
        }
        BigDecimal newStock = product.getStock().subtract(amount);
        if (newStock.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("재고가 충분하지 않습니다.: " + productId);
        }
        product.updateStock(newStock);
        productRepository.save(product);
    }


    public void plusProduct(Long productId, BigDecimal amount) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(PRODUCT_NOT_FOUND ,": " + productId));
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {

            throw new InvalidStockOperationException(INVALID_STOCK_OPERATION, ": " + amount);
        }
        BigDecimal newStock = product.getStock().add(amount);
        product.updateStock(newStock);
        productRepository.save(product);
    }
}


