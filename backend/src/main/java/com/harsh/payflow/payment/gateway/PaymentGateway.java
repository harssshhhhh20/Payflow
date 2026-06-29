package com.harsh.payflow.payment.gateway;

import com.harsh.payflow.payment.entity.Payment;

public interface PaymentGateway {

    GatewayResponse createPayment(Payment payment);

}