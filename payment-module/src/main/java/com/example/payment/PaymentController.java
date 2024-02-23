package com.example.payment;

import com.example.payment.dto.OrderDto;
import com.example.payment.dto.PaymentQuantityDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    //결제 시작
    @PostMapping("/init")
    public ResponseEntity<?> paymentInit(@RequestBody OrderDto orderDto) {
        paymentService.initiatePayment(orderDto);
        return ResponseEntity.ok().build();
    }

    //결제 요청
    @PatchMapping("/progress/{paymentId}")
    public ResponseEntity<?> paymentProgress(@PathVariable("paymentId") Long paymentId,
                                             @RequestBody PaymentQuantityDto quantityDto) {
        paymentService.proceedPayment(paymentId, quantityDto.getQuantity());
        return ResponseEntity.ok().build();
    }


    //결제 완료
    @PatchMapping("/complete/{paymentId}")
    public ResponseEntity<?> paymentComplete(@PathVariable("paymentId") Long paymentId) {
        paymentService.completePayment(paymentId);
        return ResponseEntity.ok().build();
    }

    // 결제 시작 단계 취소
    @DeleteMapping("/cancel/init/{paymentId}")
    public ResponseEntity<?> cancelInitiatedPayment(@PathVariable("paymentId") Long paymentId) {
        paymentService.initiateCancel(paymentId);
        return ResponseEntity.ok().build();
    }

    // 결제 진행 중 취소
    @DeleteMapping("/cancel/progress/{paymentId}")
    public ResponseEntity<?> cancelInProgressPayment(@PathVariable("paymentId") Long paymentId) {
        paymentService.proceedCancel(paymentId);
        return ResponseEntity.ok().build();
    }

    // 결제 완료 후 취소
    @DeleteMapping("/cancel/complete/{paymentId}")
    public ResponseEntity<?> cancelCompletedPayment(@PathVariable("paymentId") Long paymentId) {
        paymentService.cancelCompletedPayment(paymentId);
        return ResponseEntity.ok().build();
    }

}
