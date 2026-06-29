package com.harsh.payflow.payment.gateway;

public record GatewayResponse(

        String gatewayPaymentId,

        String gatewayPublicKey,

        String checkoutUrl,

        boolean success,

        String errorMessage

) {
}