package com.example.payment;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "payment")
@EntityListeners(AuditingEntityListener.class)
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id", updatable = false, nullable = false)
    private Long paymentId;

    @CreationTimestamp
    @Column(name = "payment_time", nullable = false)
    private LocalDateTime paymentTime;

    @Column(name = "payment_amount", nullable = false)
    private BigDecimal paymentAmount;

    @Column(name = "payment_status", nullable = false)
    private String paymentStatus;

    @Column(name = "product_id", nullable = false)
    private String productId;

    @Column(name = "product_type", nullable = false)
    private String productType;

    @Column(name = "payer_id", nullable = false)
    private String payerId;

    @Builder
    public Payment(Long paymentId, LocalDateTime paymentTime, BigDecimal paymentAmount,
                   String paymentStatus, String productId, String productType, String payerId) {
        this.paymentAmount = paymentAmount;
        this.paymentStatus = paymentStatus;
        this.productId = productId;
        this.productType = productType;
        this.payerId = payerId;
    }


    // 상태 업데이트 메소드
    public void updatePaymentStatus(String newStatus) {
        this.paymentStatus = newStatus;
    }

    // 결제 시간 업데이트 메소드
    public void updatePaymentTime(LocalDateTime newPaymentTime) {
        this.paymentTime = newPaymentTime;
    }
}
