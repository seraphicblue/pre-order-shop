package com.example.product.entity;

public enum PaymentStatus {
    INITIATED, // 결제 시작됨
    INITIATED_CANCELLED, // 결제 시작 후 취소됨
    IN_PROGRESS, // 결제 진행 중
    IN_PROGRESS_CANCELLED, // 결제 진행 중 취소됨
    COMPLETED, // 결제 완료됨
    CANCELLED // 결제 취소
}
