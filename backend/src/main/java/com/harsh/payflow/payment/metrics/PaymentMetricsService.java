package com.harsh.payflow.payment.metrics;

import io.micrometer.core.instrument.Timer;

import java.util.function.Supplier;

public interface PaymentMetricsService {

    void recordPayment(PaymentMetricEvent event);

    void recordRetry();

    Timer.Sample startPaymentProcessing();

    void stopPaymentProcessing(Timer.Sample sample);

}