package com.harsh.payflow.payment.gateway.razorpay;

import com.harsh.payflow.payment.entity.Payment;
import com.harsh.payflow.payment.gateway.GatewayResponse;
import com.harsh.payflow.payment.gateway.PaymentGateway;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class RazorpayGateway implements PaymentGateway {

    private final RazorpayClient razorpayClient;
    private final RazorpayProperties razorpayProperties;

    @Override
    public GatewayResponse createPayment(Payment payment) {

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

            return new GatewayResponse(
                    order.get("id").toString(),
                    razorpayProperties.getKeyId(),
                    null,
                    true,
                    null
            );

        } catch (Exception e) {

            return new GatewayResponse(
                    null,
                    null,
                    null,
                    false,
                    e.getMessage()
            );
        }
    }
}