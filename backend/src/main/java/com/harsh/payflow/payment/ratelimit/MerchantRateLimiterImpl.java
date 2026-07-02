package com.harsh.payflow.payment.ratelimit;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class MerchantRateLimiterImpl implements MerchantRateLimiter {

    private static final String KEY_PREFIX = "rate_limit:";

    private final StringRedisTemplate redisTemplate;
    private final RateLimitProperties rateLimitProperties;

    @Override
    public void checkLimit(String merchantId) {

        String key = KEY_PREFIX + merchantId;

        Long currentCount = redisTemplate.opsForValue().increment(key);

        if (currentCount == null) {
            throw new IllegalStateException(
                    "Unable to access Redis for rate limiting."
            );
        }

        if (currentCount == 1) {
            redisTemplate.expire(
                    key,
                    Duration.ofSeconds(
                            rateLimitProperties.getWindowSeconds()
                    )
            );
        }

        if (currentCount > rateLimitProperties.getLimit()) {
            throw new RateLimitException(
                    "Rate limit exceeded. Please try again later."
            );
        }
    }
}