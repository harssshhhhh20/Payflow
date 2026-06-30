package com.harsh.payflow.payment.metrics.impl;

import com.harsh.payflow.payment.metrics.PaymentMetricEvent;
import com.harsh.payflow.payment.metrics.PaymentMetricsService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.Map;

@Service
public class PaymentMetricsServiceImpl implements PaymentMetricsService {

    private final MeterRegistry meterRegistry;

    private final Map<PaymentMetricEvent, Counter> paymentCounters =
            new EnumMap<>(PaymentMetricEvent.class);

    private final Counter retryCounter;

    private final Timer paymentProcessingTimer;

    public PaymentMetricsServiceImpl(MeterRegistry meterRegistry) {

        this.meterRegistry = meterRegistry;

        for (PaymentMetricEvent event : PaymentMetricEvent.values()) {
            paymentCounters.put(
                    event,
                    Counter.builder("payments.total")
                            .description("Total payment events")
                            .tag("event", event.name().toLowerCase())
                            .register(meterRegistry)
            );
        }

        this.retryCounter = Counter.builder("payment.retries.total")
                .description("Total payment retries")
                .register(meterRegistry);

        this.paymentProcessingTimer = Timer.builder("payment.processing.time")
                .description("Payment processing duration")
                .register(meterRegistry);
    }

    @Override
    public void recordPayment(PaymentMetricEvent event) {
        paymentCounters.get(event).increment();
    }

    @Override
    public void recordRetry() {
        retryCounter.increment();
    }

    @Override
    public Timer.Sample startPaymentProcessing() {
        return Timer.start(meterRegistry);
    }

    @Override
    public void stopPaymentProcessing(Timer.Sample sample) {
        sample.stop(paymentProcessingTimer);
    }
}