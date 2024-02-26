package com.example.payment;

import com.example.payment.dto.OrderDto;
import com.example.payment.dto.PaymentStatus;
import com.example.payment.request.StockAdjustmentRequest;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static com.example.payment.dto.PaymentStatus.*;


@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final InventoryServiceClient inventoryServiceClient;
    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);

    // 결제 화면 진입 시 재고 차감 (Redis)
    public Payment initiatePayment(OrderDto orderDto) {
        StockAdjustmentRequest deductRequest = createStockAdjustmentRequest(
                orderDto.getProductId(),
                BigDecimal.valueOf(1), // 화면 진입시 1개 이상 구매할 것으로 가정
                PaymentStatus.INITIATED
        );
        inventoryServiceClient.deductStock(deductRequest);

        Payment payment = createPayment(orderDto);
        paymentRepository.save(payment);
        return payment;
    }

    // 결제 화면 이탈 시 재고 복구 (Redis) - 결제 시작 후 취소
    public void cancelPayment(Long paymentId) {
        Payment payment = findPaymentById(paymentId);

        // 재고 복구 로직에 따라 다르게 처리
        revertStockBasedOnStatus(payment);

        // 결제 상태를 취소로 업데이트
        updatePaymentStatusAndTime(payment, INITIATED_CANCELLED);

        paymentRepository.save(payment);
    }

    // 결제 진행
    public Payment proceedPayment(Long paymentId, BigDecimal finalQuantity) {
        Payment payment = findPaymentById(paymentId);
        // 결제 진행 시 실제 수량 반영하여 Redis에서 재고 차감
        StockAdjustmentRequest deductRequest = createStockAdjustmentRequest(
                payment.getProductId(),
                finalQuantity,
                IN_PROGRESS
        );
        inventoryServiceClient.deductStock(deductRequest);

        // 결제 객체에 최종 수량 반영
        payment.updatePaymentAmount(finalQuantity);
        updatePaymentStatusAndTime(payment, IN_PROGRESS);

        paymentRepository.save(payment);
        return payment;
    }

    // 결제 진행 중 취소 - 추가적인 처리가 필요한 경우 여기에 로직을 구현
    public void proceedCancel(Long paymentId) {
        Payment payment = findPaymentById(paymentId);

        // 결제 진행 중 취소 시, 실제 수량 반영하여 Redis에서 재고 복구
        revertStockBasedOnStatus(payment);

        // 결제 상태를 진행 중 취소로 업데이트
        updatePaymentStatusAndTime(payment, IN_PROGRESS_CANCELLED);

        paymentRepository.save(payment);
    }

    // 결제 완료 시 재고 차감 (MySQL)
    public void completePayment(Long paymentId) {
        Payment payment = findPaymentById(paymentId);
        StockAdjustmentRequest deductRequest = createStockAdjustmentRequest(
                payment.getProductId(),
                payment.getPaymentAmount(), // 실제 결제 시 결정된 수량
                PaymentStatus.COMPLETED
        );
        inventoryServiceClient.deductStock(deductRequest);

        updatePaymentStatusAndTime(payment, COMPLETED);
        paymentRepository.save(payment);
    }

    // 결제 완료 후 취소
    public void cancelCompletedPayment(Long paymentId) {
        Payment payment = findPaymentById(paymentId);
        StockAdjustmentRequest request = createStockAdjustmentRequest(
                payment.getProductId(),
                payment.getPaymentAmount(), // 취소 시 복구해야 하는 수량
                PaymentStatus.CANCELLED
        );
        inventoryServiceClient.plusStock(request);

        updatePaymentStatusAndTime(payment, CANCELLED);
        paymentRepository.save(payment);
    }

    //주문 내역 정의
    private Payment createPayment(OrderDto orderDto) {
        return Payment.builder()
                .productId(orderDto.getProductId())
                .payerId(orderDto.getPayerId())
                .paymentAmount(BigDecimal.valueOf(1)) // 화면 진입시 1로 설정
                .productType(orderDto.getProductType())
                .paymentStatus(INITIATED)
                .paymentTime(LocalDateTime.now())
                .build();
    }

    //결제 시간 상태 업데이트
    private void updatePaymentStatusAndTime(Payment payment, PaymentStatus status) {
        payment.updatePaymentStatus(status);
        paymentRepository.save(payment);
    }

    //재고 조정 요청
    private StockAdjustmentRequest createStockAdjustmentRequest(String productId, BigDecimal amount, PaymentStatus status) {
        return StockAdjustmentRequest.builder()
                .productId(productId)
                .paymentAmount(amount)
                .paymentStatus(status)
                .build();
    }

    // 재고 복구 로직
    public void revertStockBasedOnStatus(Payment payment) {
        StockAdjustmentRequest request = createStockAdjustmentRequest(
                payment.getProductId(),
                payment.getPaymentStatus() == PaymentStatus.INITIATED ? BigDecimal.ONE : payment.getPaymentAmount(),
                getRevertStatus(payment.getPaymentStatus())
        );
        inventoryServiceClient.plusStock(request);
    }

    // 취소시 상태 변경
    private PaymentStatus getRevertStatus(PaymentStatus currentStatus) {
        switch (currentStatus) {
            case IN_PROGRESS:
                return IN_PROGRESS_CANCELLED;
            case INITIATED:
            default:
                return INITIATED_CANCELLED;
        }
    }

    //주문 정보조회
    public List<Payment> getPaymentsByPayerId(String payerId) {
        return paymentRepository.findByPayerId(payerId);
    }

    //주문 내역 예외처리
    private Payment findPaymentById(Long paymentId) {
        return paymentRepository.findById(paymentId).orElseThrow(() -> {
            String errorMessage = String.format("주문 내역이 없습니다.: %s", paymentId);
            logger.error(errorMessage);
            throw new EntityNotFoundException(errorMessage);
        });
    }
}
