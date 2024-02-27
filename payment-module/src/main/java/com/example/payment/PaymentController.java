package com.example.payment;

import com.example.payment.dto.OrderDto;
import com.example.payment.dto.PaymentQuantityDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    // 결제 시작
    @PostMapping("/init")
    public ResponseEntity<Payment> initiatePayment(@RequestBody OrderDto orderDto) {
        Payment initiatedPayment = paymentService.initiatePayment(orderDto);
        return ResponseEntity.ok(initiatedPayment);
    }

    // 결제 진행
    @PatchMapping("/progress/{paymentId}")
    public ResponseEntity<Payment> proceedPayment(@PathVariable Long paymentId, @RequestBody PaymentQuantityDto quantityDto) {
        Payment updatedPayment = paymentService.proceedPayment(paymentId, quantityDto.getQuantity());
        return ResponseEntity.ok(updatedPayment);
    }

    // 결제 완료
    @PatchMapping("/complete/{paymentId}")
    public ResponseEntity<Void> completePayment(@PathVariable Long paymentId) {
        paymentService.completePayment(paymentId);
        return ResponseEntity.ok().build();
    }
    // 결제 시작 단계 취소
    @DeleteMapping("/cancel/init/{paymentId}")
    public ResponseEntity<?> initiateCancel(@PathVariable("paymentId") Long paymentId) {
        paymentService.initiateCancel(paymentId);
        return ResponseEntity.ok().build();
    }
    // 결제 진행 중 취소
    @DeleteMapping("/cancel/progress/{paymentId}")
    public ResponseEntity<?> proceedCancel(@PathVariable("paymentId") Long paymentId) {
        paymentService.proceedCancel(paymentId);
        return ResponseEntity.ok().build();
    }


    // 결제 완료 후 취소
    @DeleteMapping("/cancel/complete/{paymentId}")
    public ResponseEntity<Void> cancelCompletedPayment(@PathVariable Long paymentId) {
        paymentService.cancelCompletedPayment(paymentId);
        return ResponseEntity.ok().build();
    }

    // 주문 정보 조회
    @GetMapping("/payments")
    public ResponseEntity<List<Payment>> getPaymentsByPayerId(@RequestParam String payerId) {
        List<Payment> payments = paymentService.getPaymentsByPayerId(payerId);
        return ResponseEntity.ok(payments);
    }
}
