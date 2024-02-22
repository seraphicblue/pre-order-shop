package com.example.payment.request;

import lombok.Builder;
import lombok.Getter;
@Builder
@Getter
public class DeductInitRequest {
    private String productId;
    private String initiated;



}
