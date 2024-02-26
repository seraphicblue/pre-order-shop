package com.example.product;

import com.example.product.domain.config.Product;
import com.example.product.dto.ProductDto;
import com.example.product.dto.StockStatusDto;
import com.example.product.mapper.ProductMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ProductService {

    private final RedisTemplate<String, String> redisTemplate;
    private final ProductRepository productRepository;

    private final InventoryClient inventoryClient; // Feign Client 추가

    // 상품 등록
    public ProductDto registerProduct(ProductDto productDto) {
        Product product = ProductMapper.fromDto(productDto);
        product = productRepository.save(product);

        // 재고 등록 요청을 InventoryService로 전송
        inventoryClient.updateStock(product.getProductId(), product.getStock());
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
                .orElseThrow(() -> new EntityNotFoundException("제품을 찾을 수 없습니다.: " + productId));
        return ProductMapper.toDto(product);
    }


}
