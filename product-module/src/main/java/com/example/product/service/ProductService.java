package com.example.product.service;

import com.example.product.controller.dto.StockStatusDto;
import com.example.product.entity.Product;
import com.example.product.controller.dto.ProductDto;
import com.example.product.service.exception.InsufficientStockException;
import com.example.product.service.exception.InvalidStockOperationException;
import com.example.product.service.exception.ProductNotFoundException;
import com.example.product.service.mapper.ProductMapper;
import com.example.product.repository.ProductRepository;
import com.example.product.controller.request.InventoryCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.product.entity.Product.createUpdatedProduct;
import static com.example.product.service.exception.ErrorCode.*;


@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final InventoryClient inventoryClient;

    // 상품 등록
    @Transactional
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
    @Transactional(readOnly = true)
    public List<ProductDto> getAllProducts() {
        return productRepository.findAll().stream()
                .map(ProductMapper::toDto)
                .collect(Collectors.toList());
    }

    // 상품 상세 조회
    @Transactional(readOnly = true)
    public ProductDto getProductDetail(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(PRODUCT_NOT_FOUND, ": " + productId));
        return ProductMapper.toDto(product);
    }

    //상품 재고 감소
    @Transactional
    public void deductProduct(Long productId, BigDecimal amount) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(PRODUCT_NOT_FOUND, ": " + productId));

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidStockOperationException(INVALID_STOCK_OPERATION, ": " + amount);
        }

        BigDecimal newStock = product.getStock().subtract(amount);

        if (newStock.compareTo(BigDecimal.ZERO) < 0) {
            throw new InsufficientStockException(INSUFFICIENT_STOCK, ": " + productId);
        }

        Product updatedProduct = createUpdatedProduct(product, newStock);

        productRepository.save(updatedProduct);
    }

    //상품 재고 증가
    @Transactional
    public void plusProduct(Long productId, BigDecimal amount) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(PRODUCT_NOT_FOUND, ": " + productId));

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidStockOperationException(INVALID_STOCK_OPERATION, ": " + amount);
        }

        BigDecimal newStock = product.getStock().add(amount);

        Product updatedProduct = createUpdatedProduct(product, newStock);

        productRepository.save(updatedProduct);
    }

    //결제 완료된 재고량 확인
    @Transactional(readOnly = true)
    public StockStatusDto checkStock(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(PRODUCT_NOT_FOUND, ": " + productId));

        // 찾은 Product 엔티티에서 재고 정보를 가져옴
        BigDecimal currentStock = product.getStock();

        // StockStatusDto를 생성하여 반환
        return StockStatusDto.builder()
                .productId(productId)
                .currentStock(currentStock)
                .build();

    }
}


