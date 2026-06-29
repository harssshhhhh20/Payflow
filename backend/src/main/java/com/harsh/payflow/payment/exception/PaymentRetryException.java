package com.harsh.payflow.payment.exception;

public class PaymentRetryException extends RuntimeException {

    public PaymentRetryException(String message) {
        super(message);
    }
}