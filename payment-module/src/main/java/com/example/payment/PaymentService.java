package com.example.payment;

import com.example.payment.dto.OrderDto;
import com.example.payment.dto.PaymentStatus;
import com.example.payment.request.DeductRequest;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.example.payment.dto.PaymentStatus.*;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ProductServiceClient productServiceClient;

    // 결제 시작
    public Payment initiatePayment(OrderDto orderDto) {

        DeductRequest initRequest = DeductRequest.builder()
                .productId(orderDto.getProductId())
                .paymentStatus(INITIATED)
                .build();

        // Redis에서 재고 차감 요청
        productServiceClient.deductInitStockFromRedis(initRequest);

        Payment payment = Payment.builder()
                .productId(orderDto.getProductId())
                .payerId(orderDto.getPayerId())
                .paymentAmount(BigDecimal.valueOf(1))// 작성자가 수량을 정하기 전이므로 1로 설정
                .paymentStatus(INITIATED)
                .paymentTime(LocalDateTime.now())
                .build();

        paymentRepository.save(payment);
        return payment;
    }

    // 결제 화면 진입 중 취소
    public Payment initiateCancel(Long paymentId) {

        Payment payment = getPayment(paymentId, "결제화면에 접속한 적이 없습니다:");

        // Redis에서 재고 추가 요청 1개
        DeductRequest initRequest = DeductRequest.builder()
                .productId(payment.getProductId())
                .paymentStatus(INITIATED_CANCELLED)
                .build();

        productServiceClient.plusInitStockFromRedis(initRequest);

        updatePaymentStatusAndTime(payment, INITIATED_CANCELLED);

        paymentRepository.save(payment);

        return payment;
    }
    // 결제 진행
    public Payment proceedPayment(Long paymentId) {
        Payment payment = getPayment(paymentId, "결제내역이 없습니다.: ");

        DeductRequest proceedRequest = DeductRequest.builder()
                .productId(payment.getProductId())
                .paymentAmount(payment.getPaymentAmount())
                .paymentStatus(IN_PROGRESS)
                .build();

        productServiceClient.deductProceedStockFromRedis(proceedRequest);

        updatePaymentStatusAndTime(payment,IN_PROGRESS);

        paymentRepository.save(payment);
        return payment;
    }

    // 결제 진행 중 취소
    public Payment proceedCancel(Long paymentId) {
        Payment payment = getPayment(paymentId, "결제 내역이 없습니다.: ");

        // Redis에서 재고 추가 요청 (결제 진행 단계에서의 재고 추가)
        DeductRequest proceedRequest =DeductRequest.builder()
                .productId(payment.getProductId())
                .paymentAmount(payment.getPaymentAmount())
                .build();

        productServiceClient.plusProceedStockFromRedis(proceedRequest);

        updatePaymentStatusAndTime(payment, IN_PROGRESS_CANCELLED);

        paymentRepository.save(payment);

        return payment;
    }

    // 결제 완료
    public void completePayment(Long paymentId) {
        Payment payment = getPayment(paymentId, "결제내역이 없습니다.: ");

        DeductRequest completeRequest = DeductRequest.builder()
                .productId(payment.getProductId())
                .paymentAmount(payment.getPaymentAmount())
                .paymentStatus(COMPLETED)
                .build();

        productServiceClient.deductCompleteStockFromRedis(completeRequest);

        updatePaymentStatusAndTime(payment,COMPLETED);

        paymentRepository.save(payment);
    }

    // 결제 완료 후 취소
    public Payment cancelCompletedPayment(Long paymentId) {
        Payment payment = getPayment(paymentId, "결제내역이 없습니다.: ");

        // 재고 추가 요청 로직
        DeductRequest cancelRequest = DeductRequest.builder()
                .productId(payment.getProductId())
                .paymentAmount(payment.getPaymentAmount())
                .paymentStatus(CANCELLED)
                .build();

        productServiceClient.plusCompleteStockFromRedis(cancelRequest);

        updatePaymentStatusAndTime(payment, CANCELLED);

        paymentRepository.save(payment);

        return payment;
    }

    //결제 상태와 시간 변경
    private void updatePaymentStatusAndTime(Payment payment, PaymentStatus status) {
        payment.updatePaymentStatus(status);
        payment.updatePaymentTime(LocalDateTime.now());
    }

    // 결제 객체 조회 및 예외 처리
    private Payment getPayment(Long paymentId, String errorMessagePrefix) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new EntityNotFoundException(errorMessagePrefix + paymentId));
    }

}
