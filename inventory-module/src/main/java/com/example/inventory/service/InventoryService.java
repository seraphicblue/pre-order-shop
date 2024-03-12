package com.example.inventory.service;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import com.example.inventory.repository.InventoryRepository;
import com.example.inventory.entity.Inventory;
import com.example.inventory.exception.CacheMissStockInfoException;
import com.example.inventory.exception.ErrorCode;
import com.example.inventory.exception.InventoryNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final RedissonClient redissonClient;

    //재고 등록
    public Inventory createInventory(Long productId, BigDecimal stockQuantity) {

        Inventory inventory = Inventory.builder()
                .productId(productId)
                .stockQuantity(stockQuantity)
                .lastUpdate(LocalDateTime.now())
                .build();

        inventoryRepository.save(inventory);

        registerProductInventoryInRedis(productId, stockQuantity);

        return inventory;
    }

    // 상품의 재고를 업데이트할 때 호출
    public void updateInventory(Long productId, BigDecimal amount) {
        // 락 키 생성
        String lockKey = "lock:inventory:" + productId;
        RLock lock = redissonClient.getLock(lockKey);

        try {
            // 분산 락 획득 시도, 최대 10초 대기, 락 유지 시간 30초
            boolean isLocked = lock.tryLock(10, 30, TimeUnit.SECONDS);

            if (!isLocked) {
                throw new RuntimeException("락을 가져올수 없었습니다.");
            }

            Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new InventoryNotFoundException(
                        ErrorCode.PRODUCT_NOT_FOUND, productId));

        // 재고 수량 업데이트
        BigDecimal newStockQuantity = inventory.getStockQuantity().add(amount);

        //재고 변경
        inventory.updatedStockQuantity(newStockQuantity);

        // 변경내역 MySQL 데이터베이스에 저장
        inventoryRepository.save(inventory);

        // Redis에도 재고 업데이트
        updateInventoryInRedis(productId, newStockQuantity);
        }catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("락 중 인터럽트 발생", e);
        } finally {
            // 분산 락 해제
            lock.unlock();
        }
    }

    // 현재 재고를 Redis에서 확인
    public BigDecimal getCurrentInventory(Long productId) {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        String currentStockString = ops.get(buildRedisKey(productId));

        if (currentStockString == null) {
            throw new CacheMissStockInfoException(ErrorCode.CACHE_MISS_STOCK_INFO, productId);
        }
        return new BigDecimal(currentStockString);
    }

    // Redis에 재고 업데이트
    private void updateInventoryInRedis(Long productId, BigDecimal newStock) {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        ops.set(buildRedisKey(productId), newStock.toString());
    }

    // Redis에 초기 재고 등록
    private void registerProductInventoryInRedis(Long productId, BigDecimal stock) {
        redisTemplate.opsForValue().set(buildRedisKey(productId), stock.toString());
    }

    // Redis 키 생성 메서드
    private String buildRedisKey(Long productId) {

        return "inventory:" + productId;
    }

}
