package com.example.product;

import com.example.product.domain.config.Product;
import com.example.product.dto.ProductDto;
import com.example.product.dto.StockStatusDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ProductService {

    private final RedisTemplate<String, String> redisTemplate;
    private final ProductRepository productRepository;


    // 재고 차감
    public void deductStockFromRedis(String productId, BigDecimal paymentAmount) {
        updateStock(productId, paymentAmount.negate());
    }

    // 재고 증가
    public void plusStockFromRedis(String productId, BigDecimal paymentAmount) {
        updateStock(productId, paymentAmount);
    }

    private void updateStock(String productId, BigDecimal amount) {
        String stockKey = "product:" + productId;
        ValueOperations<String, String> ops = redisTemplate.opsForValue();

        // 현재 재고 가져오기
        String currentStockString = ops.get(stockKey);
        if (currentStockString == null) {
            throw new RuntimeException("해당 제품의 재고 정보를 찾을 수 없습니다: " + productId);
        }
        BigDecimal currentStock = new BigDecimal(currentStockString);

        // 새로운 재고 계산
        BigDecimal newStock = currentStock.add(amount);

        // 재고 부족 예외 처리
        if (newStock.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("재고가 부족합니다.");
        }

        // 새로운 재고 값 Redis에 저장
        ops.set(stockKey, newStock.toString());
    }

    // 전체 상품 가져오기
    public List<ProductDto> getAllProducts() {
        List<ProductDto> products = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        productRepository.findAll().forEach(product -> {
            ProductDto dto = new ProductDto(
                    product.getProductId(),
                    product.getProductName(),
                    product.getStock(),
                    product.getPrice(),
                    product.getExecutionTime());
            dto.updatePurchasable(now); // 구매 가능 여부 업데이트
            products.add(dto);
        });

        return products;
    }

    // 현재 재고 체크 후 DTO 빌드
    public StockStatusDto checkStock(Long productId) {
        BigDecimal currentStock = getCurrentStock(productId.toString());
        return StockStatusDto.builder()
                .productId(productId)
                .currentStock(currentStock)
                .build();
    }

    //현재 재고 redis 에서 확인하기
    private BigDecimal getCurrentStock(String productId) {
        String stockKey = "product:" + productId;
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        String currentStockString = ops.get(stockKey);
        if (currentStockString == null) {
            throw new RuntimeException("해당 제품의 재고 정보가 존재하지 않습니다: " + productId);
        }
        return new BigDecimal(currentStockString);
    }

    // 상품 상세 조회
    public ProductDto getProductDetail(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("해당 상품이 존재 하지 않습니다.: " + productId));

        LocalDateTime now = LocalDateTime.now();
        ProductDto dto = new ProductDto(
                product.getProductId(),
                product.getProductName(),
                product.getStock(),
                product.getPrice(),
                product.getExecutionTime());
        dto.updatePurchasable(now);

        return dto;
    }

    // MySQL에서 재고 차감
    public void deductStockFromMysql(Long productId, BigDecimal amount) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("상품을 찾을 수 없습니다.: " + productId));
        BigDecimal newStock = product.getStock().subtract(amount);
        if (newStock.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("재고가 부족합니다.");
        }
        product.updateStock(newStock);
        productRepository.save(product);
    }

    // MySQL에서 재고 증가
    public void plusStockToMysql(Long productId, BigDecimal amount) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("상품을 찾을 수 없습니다.: " + productId));
        BigDecimal newStock = product.getStock().add(amount);
        product.updateStock(newStock);
        productRepository.save(product);
    }


}
