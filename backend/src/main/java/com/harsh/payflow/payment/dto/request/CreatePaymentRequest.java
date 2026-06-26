package com.harsh.payflow.payment.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreatePaymentRequest(

        @NotBlank(message = "Merchant ID is required")
        String merchantId,

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "1.00", message = "Amount must be greater than 0")
        BigDecimal amount,

        @NotBlank(message = "Currency is required")
        String currency,

        @NotBlank(message = "Idempotency key is required")
        String idempotencyKey

) {
}