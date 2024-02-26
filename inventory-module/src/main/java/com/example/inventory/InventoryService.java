package com.example.inventory;


import com.example.inventory.exception.CacheMissStockInfoException;
import com.example.inventory.exception.InventoryNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class InventoryService {
    private final InventoryRepository inventoryRepository;
    private final RedisTemplate<String, String> redisTemplate;

    //재고 등록
    public Inventory createInventory(Long productId, BigDecimal stockQuantity) {
        Inventory inventory = Inventory.builder()
                .productId(productId)
                .stockQuantity(stockQuantity)
                .lastUpdate(LocalDateTime.now())
                .build();
        inventoryRepository.save(inventory);

        registerProductStockInRedis(productId, stockQuantity);
        return inventory;
    }

    // 상품의 재고를 업데이트할 때 호출
    public void updateStock(Long productId, BigDecimal amount) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new InventoryNotFoundException("재고를 찾을 수 없습니다: " + productId));

        // 재고 수량 업데이트
        BigDecimal newStockQuantity = inventory.getStockQuantity().add(amount);
        inventory.updateStockQuantity(newStockQuantity);

        // MySQL 데이터베이스에 저장
        inventoryRepository.save(inventory);

        // Redis에도 재고 업데이트
        updateStockInRedis(productId, newStockQuantity);
    }

    // 현재 재고를 Redis에서 확인
    private BigDecimal getCurrentStock(Long productId) {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        String currentStockString = ops.get(buildRedisKey(productId));
        if (currentStockString == null) {
            throw new CacheMissStockInfoException("재고 캐시정보가 존재하지 않습니다: " + productId);
        }
        return new BigDecimal(currentStockString);
    }

    // Redis에 재고 업데이트
    private void updateStockInRedis(Long productId, BigDecimal newStock) {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        ops.set(buildRedisKey(productId), newStock.toString());
    }

    // Redis에 초기 재고 등록
    private void registerProductStockInRedis(Long productId, BigDecimal stock) {
        redisTemplate.opsForValue().set(buildRedisKey(productId), stock.toString());
    }

    // Redis 키 생성 메서드
    private String buildRedisKey(Long productId) {
        return "inventory:" + productId;
    }
    //재고 에러 메서드


}
