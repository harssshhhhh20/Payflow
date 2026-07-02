package com.harsh.payflow.payment.metrics;

public enum PaymentRequestOutcome {
    SUCCESS,
    FAILED,
    RATE_LIMITED,
    BULKHEAD_REJECTED
}