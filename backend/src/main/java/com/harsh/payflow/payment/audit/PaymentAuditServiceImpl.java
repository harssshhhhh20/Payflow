package com.harsh.payflow.payment.audit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.harsh.payflow.audit.entity.AuditEntityType;
import com.harsh.payflow.audit.entity.AuditEventType;
import com.harsh.payflow.audit.service.AuditService;
import com.harsh.payflow.payment.entity.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentAuditServiceImpl implements PaymentAuditService {

    private final AuditService auditService;
    private final ObjectMapper objectMapper;

    @Override
    public void recordCreated(Payment payment) {
        record(
                payment,
                AuditEventType.PAYMENT_CREATED
        );
    }

    @Override
    public void recordCaptured(Payment payment) {
        record(
                payment,
                AuditEventType.PAYMENT_CAPTURED
        );
    }

    @Override
    public void recordFailed(Payment payment) {
        record(
                payment,
                AuditEventType.PAYMENT_FAILED
        );
    }

    @Override
    public void recordRetryInitiated(Payment payment) {
        record(
                payment,
                AuditEventType.PAYMENT_RETRY_INITIATED
        );
    }

    private void record(
            Payment payment,
            AuditEventType eventType
    ) {

        Map<String, Object> data = new HashMap<>();

        data.put(
                "merchantId",
                payment.getMerchant().getMerchantId()
        );

        data.put(
                "gatewayOrderId",
                payment.getGatewayPaymentId()
        );

        data.put(
                "amount",
                payment.getAmount()
        );

        data.put(
                "currency",
                payment.getCurrency()
        );

        data.put(
                "status",
                payment.getStatus()
        );

        data.put(
                "retryCount",
                payment.getRetryCount()
        );

        try {

            auditService.record(
                    payment.getPaymentId(),
                    AuditEntityType.PAYMENT,
                    eventType,
                    objectMapper.writeValueAsString(data)
            );

        } catch (JsonProcessingException e) {

            throw new RuntimeException(
                    "Failed to serialize audit event.",
                    e
            );

        }
    }
}