import com.example.inventory.entity.Inventory;
import com.example.inventory.repository.InventoryRepository;
import com.example.inventory.service.InventoryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private RedissonClient redissonClient;

    @Mock
    private RLock lock;

    @InjectMocks
    private InventoryService inventoryService;
    @SuppressWarnings("unchecked")
    @Test
    void testUpdateInventory() {
        Long productId = 1L;
        BigDecimal amount = new BigDecimal("10");
        Inventory inventory = new Inventory(productId, new BigDecimal("100"));
        String lockKey = "lock:inventory:" + productId;

        when(redissonClient.getLock(lockKey)).thenReturn(lock);
        try {
            when(lock.tryLock(anyLong(), anyLong(), any())).thenReturn(true);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        when(inventoryRepository.findByProductId(productId)).thenReturn(Optional.of(inventory));

        ValueOperations<String, String> valueOperations = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        inventoryService.updateInventory(productId, amount);

        verify(inventoryRepository).save(any(Inventory.class));
        verify(valueOperations).set(anyString(), anyString());
        verify(lock).unlock();
    }
}
