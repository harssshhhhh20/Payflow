package com.harsh.payflow.payment.event;

import java.math.BigDecimal;

public record PaymentCreatedEvent(

        String paymentId,

        String merchantId,

        BigDecimal amount,

        String currency,

        String gatewayOrderId

) {
}