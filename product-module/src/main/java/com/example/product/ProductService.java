package com.example.product;

import com.example.product.dto.ProductDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Service
public class ProductService {

    private final RedisTemplate<String, String> redisTemplate;
    private final PreProductRepository preProductRepository;
    private final NormalProductRepository normalProductRepository;

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
        List<ProductDto> preProducts = preProductRepository.findAll().stream()
                .map(p -> new ProductDto(p.getPreId(), p.getPreProductName(), p.getPreStock(), p.getPrePrice(), p.getPreExecutionTime()))
                .toList();

        List<ProductDto> normalProducts = normalProductRepository.findAll().stream()
                .map(n -> new ProductDto(n.getNormalId(), n.getNormalProductName(), n.getNormalStock(), n.getNormalPrice(), null))
                .toList();

        return Stream.concat(preProducts.stream(), normalProducts.stream())
                .collect(Collectors.toList());
    }
}
