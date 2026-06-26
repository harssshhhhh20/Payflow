package com.harsh.payflow.merchant.exception;

public class MerchantNotFoundException extends RuntimeException {
    public MerchantNotFoundException(String merchantId) {
        super("Merchant not found: " + merchantId);
    }
}