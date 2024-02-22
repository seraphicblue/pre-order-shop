package com.example.product;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Objects;
@RequiredArgsConstructor
@Service
public class ProductService {

    private final RedisTemplate<String, String> redisTemplate;
    private final StringRedisTemplate stringRedisTemplate;


    public void deductinitStockFromRedis(String productId, String initiated) {
        String stockKey = "product:" + productId;
        String currentStockString = redisTemplate.opsForValue().get(stockKey);
        System.out.println(currentStockString);
        // 현재 재고를 가져와 BigDecimal로 변환
        BigDecimal currentStock = new BigDecimal(currentStockString);

        // 감소할 값을 BigDecimal로 변환
        BigDecimal decreaseAmount = new BigDecimal("1");

        // 현재 재고에서 감소할 값을 뺀 새로운 재고를 계산
        BigDecimal newStock = currentStock.subtract(decreaseAmount);

        // 새로운 재고를 Redis에 설정
        redisTemplate.opsForValue().set(stockKey, newStock.toString());
    }

    public void deductproccedStockFromRedis(String productId, BigDecimal paymentAmount, String progress) {
        String stockKey = "product:" + productId;
        // 결제 처리 중 재고 감소
        BigDecimal deductedStock = new BigDecimal(Objects.requireNonNull(redisTemplate.opsForValue().get(stockKey)));
        BigDecimal newStock = deductedStock.subtract(paymentAmount);

        if (newStock.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("재고가 부족합니다.");
        }
        redisTemplate.opsForValue().set(stockKey, newStock.toString());

    }

    public void deductcompeleteStockFromRedis(String productId, BigDecimal paymentAmount, String completed) {
        String stockKey = "product:" + productId;
        // 결제 완료 시 재고 감소
        BigDecimal deductedStock = new BigDecimal(Objects.requireNonNull(redisTemplate.opsForValue().get(stockKey)));
        BigDecimal newStock = deductedStock.subtract(paymentAmount);

        if (newStock.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("재고가 부족합니다.");
        }
        redisTemplate.opsForValue().set(stockKey, newStock.toString());

    }


}
