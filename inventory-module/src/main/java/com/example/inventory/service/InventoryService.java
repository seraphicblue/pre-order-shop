package com.example.inventory.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.transaction.annotation.Propagation;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final RedissonClient redissonClient;

    //재고 등록
    @Transactional
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
    @Transactional
    public void updateInventory(Long productId, BigDecimal amount) {
        final String lockKey = "lock:inventory:" + productId;
        final RLock lock = redissonClient.getLock(lockKey);


        try {
            // 분산 락을 시도합니다. 최대 1초간 대기하고, 락을 얻으면 3초 동안 유지합니다.
            if (!lock.tryLock(10, 30, TimeUnit.SECONDS)) {
                throw new RuntimeException("락 획득 실패.");
            }
            BigDecimal currentInventory = getCurrentInventory(productId);
            BigDecimal newStockQuantity = currentInventory.add(amount);



            log.info("[{}] 현재 핸들러: {} & 현재 남은 재고량: {} 개", LocalDateTime.now(), Thread.currentThread().getName(), newStockQuantity);

            updateInventoryInDatabaseAndRedis(productId, newStockQuantity);


        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();
            throw new RuntimeException("락 획득 중단됨.", e);
        } finally {
            // 예외가 발생하더라도 항상 finally 블록에서 락을 해제하여 락이 안전하게 해제되도록 보장합니다.
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    // 현재 재고를 Redis에서 확인하고, 필요시 데이터베이스에서 업데이트 후 Redis에 반영
    private void updateInventoryInDatabaseAndRedis(Long productId, BigDecimal newStockQuantity) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new InventoryNotFoundException(ErrorCode.PRODUCT_NOT_FOUND, productId));

        inventory.updatedStockQuantity(newStockQuantity);
        inventoryRepository.save(inventory);
        updateInventoryInRedis(productId, newStockQuantity);
    }


    public void updateInventory2(Long productId, BigDecimal amount) {

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
