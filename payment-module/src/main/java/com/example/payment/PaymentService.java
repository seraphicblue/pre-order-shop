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
        handleStockAdjustment(orderDto.getProductId(), BigDecimal.valueOf(1), INITIATED);

        Payment payment = Payment.builder()
                .productId(orderDto.getProductId())
                .payerId(orderDto.getPayerId())
                .paymentAmount(BigDecimal.valueOf(1)) // 작성자가 수량을 정하기 전이므로 1로 설정
                .productType(orderDto.getProductType())
                .paymentStatus(INITIATED)
                .paymentTime(LocalDateTime.now())
                .build();

        paymentRepository.save(payment);
        return payment;
    }

    // 결제 화면 진입 중 취소
    public Payment initiateCancel(Long paymentId) {
        Payment payment = getPayment(paymentId, "결제화면에 접속한 적이 없습니다:");

        handleStockAdjustment(payment.getProductId(),payment.getPaymentAmount(), INITIATED_CANCELLED);

        updatePaymentStatusAndTime(payment, INITIATED_CANCELLED);

        paymentRepository.save(payment);
        return payment;
    }

    // 결제 진행
    public Payment proceedPayment(Long paymentId,BigDecimal finalQuantity) {
        Payment payment = getPayment(paymentId, "결제내역이 없습니다.: ");

        handleStockAdjustment(payment.getProductId(),finalQuantity, IN_PROGRESS);// 수량 문제 처리 이슈

        // Payment 객체에 최종 수량 반영
        payment.updatePaymentAmount(finalQuantity);

        updatePaymentStatusAndTime(payment, IN_PROGRESS);

        paymentRepository.save(payment);
        return payment;
    }

    // 결제 진행 중 취소
    public Payment proceedCancel(Long paymentId) {
        Payment payment = getPayment(paymentId, "결제 내역이 없습니다.: ");

        handleStockAdjustment(payment.getProductId(), payment.getPaymentAmount(), IN_PROGRESS_CANCELLED);

        updatePaymentStatusAndTime(payment, IN_PROGRESS_CANCELLED);

        paymentRepository.save(payment);

        return payment;
    }

    // 결제 완료
    public void completePayment(Long paymentId) {
        Payment payment = getPayment(paymentId, "결제내역이 없습니다.: ");

        updatePaymentStatusAndTime(payment, PaymentStatus.COMPLETED);

        //  MySQL에서 재고 차감
        DeductRequest deductRequest = DeductRequest.builder()
                .productId(payment.getProductId())
                .paymentAmount(payment.getPaymentAmount())
                .build();


        productServiceClient.deductProductFromMysql(deductRequest);

        paymentRepository.save(payment);
    }

    // 결제 완료 후 취소
    public Payment cancelCompletedPayment(Long paymentId) {
        Payment payment = getPayment(paymentId, "결제내역이 없습니다.: ");

        updatePaymentStatusAndTime(payment, PaymentStatus.CANCELLED);

        //  MySQL에서 재고 추가
        DeductRequest request = DeductRequest.builder()
                .productId(payment.getProductId())
                .paymentAmount(payment.getPaymentAmount())
                .build();

        productServiceClient.plusProductFromMysql(request);

        handleStockAdjustment(payment.getProductId(), payment.getPaymentAmount(), PaymentStatus.CANCELLED);

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
    public void handleStockAdjustment(String productId, BigDecimal paymentAmount, PaymentStatus paymentStatus) {
        // DeductRequest 객체 생성
        DeductRequest request = DeductRequest.builder()
                .productId(productId)
                .paymentAmount(paymentAmount)
                .paymentStatus(paymentStatus)
                .build();

        switch (paymentStatus) {
            case INITIATED:
            case IN_PROGRESS:
                // 진행 중 또는 시작 시 재고 차감 요청
                productServiceClient.deductStockFromRedis(request);
                break;
            case INITIATED_CANCELLED:
            case IN_PROGRESS_CANCELLED:
            case CANCELLED:
                // 취소 시 재고 추가 요청
                productServiceClient.plusStockFromRedis(request);
                break;
            default:
                throw new IllegalArgumentException("결제 상태가 파악되지 않습니다 : " + paymentStatus);
        }
    }

}
