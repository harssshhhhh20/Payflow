package com.harsh.payflow.payment.mapper;

import com.harsh.payflow.merchant.entity.Merchant;
import com.harsh.payflow.payment.dto.request.CreatePaymentRequest;
import com.harsh.payflow.payment.dto.response.CreatePaymentResponse;
import com.harsh.payflow.payment.entity.GatewayType;
import com.harsh.payflow.payment.entity.Payment;
import com.harsh.payflow.payment.entity.PaymentStatus;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

    public Payment toEntity(
            CreatePaymentRequest request,
            Merchant merchant,
            String paymentId,
            GatewayType gateway
    ) {

        return Payment.builder()
                .paymentId(paymentId)
                .merchant(merchant)
                .amount(request.amount())
                .currency(request.currency().trim().toUpperCase())
                .status(PaymentStatus.PENDING)
                .gateway(gateway)
                .idempotencyKey(request.idempotencyKey())
                .build();
    }

    public CreatePaymentResponse toResponse(Payment payment) {

        return new CreatePaymentResponse(
                payment.getPaymentId(),
                payment.getMerchant().getMerchantId(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getStatus(),
                payment.getCreatedAt()
        );
    }
}