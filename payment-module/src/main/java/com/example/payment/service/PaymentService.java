package com.example.payment.service;

import com.example.payment.controller.request.InventoryAdjustmentRequest;
import com.example.payment.entity.Payment;
import com.example.payment.repository.PaymentRepository;
import com.example.payment.controller.dto.OrderDto;
import com.example.payment.controller.dto.PaymentStatus;
import com.example.payment.service.exception.ErrorCode;
import com.example.payment.service.exception.InvalidFinalQuantityException;
import com.example.payment.service.exception.PaymentNotFoundException;
import com.example.payment.controller.request.UpdateStockRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static com.example.payment.controller.request.InventoryAdjustmentRequest.createInventoryAdjustmentRequest;
import static com.example.payment.service.exception.ErrorCode.PAYMENT_NOT_FOUND;
import static com.example.payment.controller.dto.PaymentStatus.*;


@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final InventoryServiceClient inventoryServiceClient;
    private final ProductServiceClient productServiceClient;

    // 결제 화면 진입 시 재고 차감 (Redis)
    @Transactional
    public Payment initiatePayment(OrderDto orderDto) {
        InventoryAdjustmentRequest deductRequest = createInventoryAdjustmentRequest(
                orderDto.getProductId(),
                BigDecimal.valueOf(1), // 화면 진입시 1개 이상 구매할 것으로 가정
                PaymentStatus.INITIATED
        );
        inventoryServiceClient.deductInventory(deductRequest);

        Payment payment = createPayment(orderDto);

        return paymentRepository.save(payment);
    }

    // 결제 화면 이탈 시 재고 복구 (Redis) - 결제 시작 후 취소
    @Transactional
    public void initiateCancel(Long paymentId) {
        Payment payment = findPaymentById(paymentId);

        // 재고 복구 로직에 따라 다르게 처리
        revertStockBasedOnStatus(payment);

        paymentRepository.save(payment);
    }

    // 결제 진행
    @Transactional
    public Payment proceedPayment(Long paymentId, BigDecimal finalQuantity) {
        Payment payment = findPaymentById(paymentId);
        if (finalQuantity == null || finalQuantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidFinalQuantityException(ErrorCode.INVALID_FINAL_QUANTITY, finalQuantity);
        }
        // 결제 진행 시 실제 수량 반영하여 Redis에서 재고 차감
        InventoryAdjustmentRequest deductRequest = createInventoryAdjustmentRequest(
                payment.getProductId(),
                finalQuantity.subtract(BigDecimal.ONE),
                IN_PROGRESS
        );
        inventoryServiceClient.deductInventory(deductRequest);

        // 결제 객체에 최종 수량 반영
        payment.updatePaymentAmount(finalQuantity);

        Payment updatedPayment = payment.updatePaymentStatus(PaymentStatus.IN_PROGRESS, LocalDateTime.now());

        return paymentRepository.save(updatedPayment);
    }

    // 결제 진행 중 취소 - 추가적인 처리가 필요한 경우 여기에 로직을 구현
    @Transactional
    public void proceedCancel(Long paymentId) {
        Payment payment = findPaymentById(paymentId);

        // 결제 진행 중 취소 시, 실제 수량 반영하여 Redis에서 재고 복구
        revertStockBasedOnStatus(payment);

        paymentRepository.save(payment);

    }

    // 결제 완료 시 재고 차감 (MySQL)
    @Transactional
    public void completePayment(Long paymentId) {
        Payment payment = findPaymentById(paymentId);

        InventoryAdjustmentRequest request = createInventoryAdjustmentRequest(
                payment.getProductId(),
                payment.getPaymentAmount(), // 실제 결제 시 결정된 수량
                COMPLETED
        );

        Payment updatedPayment = payment.updatePaymentStatus(PaymentStatus.COMPLETED, LocalDateTime.now());


        UpdateStockRequest deductrequest = UpdateStockRequest.builder()
                .productId(payment.getProductId())
                .paymentAmount(payment.getPaymentAmount())
                .build();

        productServiceClient.deductProduct(deductrequest);

        paymentRepository.save(updatedPayment);
    }

    // 결제 완료 후 취소
    @Transactional
    public void cancelCompletedPayment(Long paymentId) {
        Payment payment = findPaymentById(paymentId);

        InventoryAdjustmentRequest request = createInventoryAdjustmentRequest(
                payment.getProductId(),
                payment.getPaymentAmount(), // 취소 시 복구해야 하는 수량
                PaymentStatus.CANCELLED
        );

        inventoryServiceClient.plusInventory(request);

        UpdateStockRequest plusrequest = UpdateStockRequest.builder()
                .productId(payment.getProductId())
                .paymentAmount(payment.getPaymentAmount())
                .build();

        productServiceClient.plusProduct(plusrequest);

        Payment updatedPayment = payment.updatePaymentStatus(PaymentStatus.CANCELLED, LocalDateTime.now());

        paymentRepository.save(updatedPayment);

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


    // 재고 복구 로직
    @Transactional
    public void revertStockBasedOnStatus(Payment payment) {
        InventoryAdjustmentRequest plusrequest = createInventoryAdjustmentRequest(
                payment.getProductId(),
                payment.getPaymentStatus() == PaymentStatus.INITIATED ? BigDecimal.ONE : payment.getPaymentAmount(),
                getRevertStatus(payment.getPaymentStatus())
        );
        inventoryServiceClient.plusInventory(plusrequest);
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
    @Transactional(readOnly = true)
    public List<Payment> getPaymentsByPayerId(String payerId) {

        return paymentRepository.findByPayerId(payerId);
    }

    //주문 내역 예외처리
    private Payment findPaymentById(Long paymentId) {
        return paymentRepository.findById(paymentId).orElseThrow(() -> {
            return new PaymentNotFoundException(PAYMENT_NOT_FOUND, paymentId);
        });
    }

}
