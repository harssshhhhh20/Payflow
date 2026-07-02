package com.harsh.payflow.payment.gateway.razorpay;

import com.harsh.payflow.payment.entity.Payment;
import com.harsh.payflow.payment.exception.PaymentGatewayException;
import com.harsh.payflow.payment.gateway.GatewayResponse;
import com.harsh.payflow.payment.gateway.PaymentGateway;
import com.harsh.payflow.payment.metrics.PaymentMetricsService;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.retry.annotation.Retry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

@Slf4j
@Component
@RequiredArgsConstructor
public class RazorpayGateway implements PaymentGateway {

    private final RazorpayClient razorpayClient;
    private final RazorpayProperties razorpayProperties;
    private final PaymentMetricsService paymentMetricsService;

    @Override
    @Bulkhead(name = "razorpayGateway")
    @Retry(name = "razorpayGateway")
    @CircuitBreaker(
            name = "razorpayGateway"
    )
    public GatewayResponse createPayment(Payment payment) {

        Timer.Sample sample =
                paymentMetricsService.startGatewayProcessing();
        try {

            JSONObject options = new JSONObject();

            options.put(
                    "amount",
                    payment.getAmount()
                            .multiply(BigDecimal.valueOf(100))
                            .longValue()
            );

            options.put("currency", payment.getCurrency());

            options.put("receipt", payment.getPaymentId());

            Order order = razorpayClient.orders.create(options);

            paymentMetricsService.stopGatewayProcessing(sample);

            return new GatewayResponse(
                    order.get("id").toString(),
                    razorpayProperties.getKeyId(),
                    null
            );

        } catch (Exception e) {

            log.error("Razorpay order creation failed", e);

            paymentMetricsService.stopGatewayProcessing(sample);

            throw new PaymentGatewayException(
                    "Failed to create Razorpay order",
                    e
            );
        }
    }
}