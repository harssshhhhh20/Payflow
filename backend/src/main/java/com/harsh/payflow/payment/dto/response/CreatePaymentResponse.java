package com.harsh.payflow.payment.dto.response;

import com.harsh.payflow.payment.entity.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CreatePaymentResponse(

        String paymentId,

        String merchantId,

        BigDecimal amount,

        String currency,

        PaymentStatus status,

        LocalDateTime createdAt

) {
}