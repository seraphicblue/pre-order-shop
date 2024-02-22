package com.example.payment;

import com.example.payment.dto.OrderDto;
import com.example.payment.request.DeductCompleteRequest;
import com.example.payment.request.DeductInitRequest;
import com.example.payment.request.DeductProceedRequest;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ProductServiceClient productServiceClient;

    // 결제 시작
    public Payment initiatePayment(OrderDto orderDto) {
        DeductInitRequest initRequest = DeductInitRequest.builder()
                .productId(orderDto.getProductId())
                .initiated("INITIATED")
                .build();

        // Redis에서 재고 차감
        productServiceClient.deductInitStockFromRedis(initRequest);

        Payment payment = Payment.builder()
                .productId(orderDto.getProductId())
                .payerId(orderDto.getPayerId())
                .paymentAmount(orderDto.getAmount())
                .productType(orderDto.getProductType())
                .paymentStatus("INITIATED")
                .paymentTime(LocalDateTime.now())
                .build();

        paymentRepository.save(payment);
        return payment;
    }

    // 결제 진행
    public Payment proceedPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new EntityNotFoundException("결제내역이 없습니다: " + paymentId));

        DeductProceedRequest proceedRequest = DeductProceedRequest.builder()
                .productId(payment.getProductId())
                .paymentAmount(payment.getPaymentAmount())
                .progress("IN_PROGRESS")
                .build();

        productServiceClient.deductProceedStockFromRedis(proceedRequest);

        payment.updatePaymentStatus("IN_PROGRESS");
        payment.updatePaymentTime(LocalDateTime.now());


        paymentRepository.save(payment);
        return payment;
    }

    // 결제 완료
    public void completePayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new EntityNotFoundException("결제내역이 없습니다: " + paymentId));

        DeductCompleteRequest completeRequest = DeductCompleteRequest.builder()
                .productId(payment.getProductId())
                .paymentAmount(payment.getPaymentAmount())
                .completed("COMPLETED")
                .build();

        productServiceClient.deductCompleteStockFromRedis(completeRequest);

        payment.updatePaymentStatus("COMPLETED");
        payment.updatePaymentTime(LocalDateTime.now());


        paymentRepository.save(payment);
    }
}
