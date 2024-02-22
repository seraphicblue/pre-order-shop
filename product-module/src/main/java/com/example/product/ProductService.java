package com.example.product;

import com.example.product.domain.NormalProduct;
import com.example.product.domain.PreProduct;
import com.example.product.dto.ProductDto;
import com.example.product.dto.StockStatusDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        List<ProductDto> products = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        preProductRepository.findAll().forEach(preProduct -> {

            ProductDto dto = new ProductDto(preProduct.getPreId(), preProduct.getPreProductName(),
                    preProduct.getPreStock(), preProduct.getPrePrice(), preProduct.getPreExecutionTime());

            dto.updatePurchasable(now); // 구매 가능 여부 업데이트
            products.add(dto);
        });

        normalProductRepository.findAll().forEach(normalProduct -> {

            ProductDto dto = new ProductDto(normalProduct.getNormalId(), normalProduct.getNormalProductName(),
                    normalProduct.getNormalStock(), normalProduct.getNormalPrice(), null);

            dto.updatePurchasable(now); // 일반 상품은 항상 구매 가능
            products.add(dto);
        });

        return products;
    }

    //현재 재고가 있는지 체크후 dto 빌드
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

    //상품 조회 하기
    public ProductDto getProductDetail(Long productId) {
        LocalDateTime now = LocalDateTime.now();
        // 예약 상품 리포지토리에서 먼저 조회 시도
        Optional<PreProduct> preProductOpt = preProductRepository.findById(productId);
        if (preProductOpt.isPresent()) {
            PreProduct preProduct = preProductOpt.get();
            ProductDto dto = new ProductDto(
                    preProduct.getPreId(),
                    preProduct.getPreProductName(),
                    preProduct.getPreStock(),
                    preProduct.getPrePrice(),
                    preProduct.getPreExecutionTime());
            dto.updatePurchasable(now);
            return dto;
        }

        // 일반 상품 리포지토리에서 조회 시도
        Optional<NormalProduct> normalProductOpt = normalProductRepository.findById(productId);
        if (normalProductOpt.isPresent()) {
            NormalProduct normalProduct = normalProductOpt.get();
            ProductDto dto = new ProductDto(
                    normalProduct.getNormalId(),
                    normalProduct.getNormalProductName(),
                    normalProduct.getNormalStock(),
                    normalProduct.getNormalPrice(),
                    null); // 일반 상품은 executionTime 없음
            dto.updatePurchasable(now); // 일반 상품은 항상 구매 가능
            return dto;
        }

        throw new RuntimeException("상품을 찾을 수 없습니다. ID: " + productId);
    }

}
