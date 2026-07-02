package com.harsh.payflow.payment.metrics.impl;

import com.harsh.payflow.payment.metrics.PaymentMetricEvent;
import com.harsh.payflow.payment.metrics.PaymentMetricsService;
import com.harsh.payflow.payment.metrics.PaymentRequestOutcome;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.Map;

@Service
public class PaymentMetricsServiceImpl implements PaymentMetricsService {

    private final MeterRegistry meterRegistry;
    private final Timer gatewayProcessingTimer;
    private final Map<PaymentMetricEvent, Counter> paymentCounters =
            new EnumMap<>(PaymentMetricEvent.class);
    private final Counter retryCounter;
    private final Timer paymentProcessingTimer;
    private final Map<PaymentRequestOutcome, Counter> requestOutcomeCounters =
            new EnumMap<>(PaymentRequestOutcome.class);

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

        for (PaymentRequestOutcome outcome : PaymentRequestOutcome.values()) {

            requestOutcomeCounters.put(
                    outcome,
                    Counter.builder("payment.requests.total")
                            .description("Payment request outcomes")
                            .tag("outcome", outcome.name().toLowerCase())
                            .register(meterRegistry)
            );
        }

        this.retryCounter = Counter.builder("payment.retries.total")
                .description("Total payment retries")
                .register(meterRegistry);

        this.paymentProcessingTimer = Timer.builder("payment.processing.time")
                .description("Payment processing duration")
                .register(meterRegistry);
        this.gatewayProcessingTimer =
                Timer.builder("payment.gateway.time")
                        .description("Gateway API processing duration")
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

    @Override
    public Timer.Sample startGatewayProcessing() {
        return Timer.start(meterRegistry);
    }

    @Override
    public void stopGatewayProcessing(
            Timer.Sample sample
    ) {
        sample.stop(gatewayProcessingTimer);
    }

    @Override
    public void recordRequestOutcome(
            PaymentRequestOutcome outcome
    ) {
        requestOutcomeCounters.get(outcome).increment();
    }
}