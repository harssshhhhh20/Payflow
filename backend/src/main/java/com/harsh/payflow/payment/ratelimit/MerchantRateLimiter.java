package com.harsh.payflow.payment.ratelimit;

public interface MerchantRateLimiter {

    void checkLimit(String merchantId);

}