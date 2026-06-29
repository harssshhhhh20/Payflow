package com.harsh.payflow.payment.webhook;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.harsh.payflow.payment.audit.PaymentAuditService;
import com.harsh.payflow.payment.entity.Payment;
import com.harsh.payflow.payment.entity.PaymentStatus;
import com.harsh.payflow.payment.repository.PaymentRepository;
import com.harsh.payflow.payment.statemachine.PaymentStateMachine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class WebhookServiceImpl implements WebhookService {

    private final ObjectMapper objectMapper;
    private final PaymentRepository paymentRepository;
    private final PaymentStateMachine paymentStateMachine;
    private final PaymentAuditService paymentAuditService;

    private void handlePaymentCaptured(JsonNode root) {
        String orderId = root
                .path("payload")
                .path("payment")
                .path("entity")
                .path("order_id")
                .asText();

        Payment payment = paymentRepository
                .findByGatewayPaymentId(orderId)
                .orElse(null);

        if (payment == null) {
            log.warn("No payment found for gateway order {}", orderId);
            return;
        }

        log.info(
                "Found payment {} for gateway order {}",
                payment.getPaymentId(),
                orderId
        );
        if (!paymentStateMachine.canTransition(
                payment.getStatus(),
                PaymentStatus.SUCCESS
        )) {
            log.warn(
                    "Invalid state transition: {} -> SUCCESS for payment {}",
                    payment.getStatus(),
                    payment.getPaymentId()
            );
            return;
        }
        payment.setStatus(PaymentStatus.SUCCESS);
        paymentRepository.save(payment);
        paymentAuditService.recordCaptured(payment);
        log.info(
                "Payment {} marked as SUCCESS",
                payment.getPaymentId()
        );
    }

    private void handlePaymentFailed(JsonNode root) {
        String orderId = root
                .path("payload")
                .path("payment")
                .path("entity")
                .path("order_id")
                .asText();

        Payment payment = paymentRepository
                .findByGatewayPaymentId(orderId)
                .orElse(null);
        if (payment == null) {
            log.warn("No payment found for gateway order {}", orderId);
            return;
        }
        log.info(
                "Found payment {} for gateway order {}",
                payment.getPaymentId(),
                orderId
        );
        if (!paymentStateMachine.canTransition(
                payment.getStatus(),
                PaymentStatus.FAILED
        )) {
            log.warn(
                    "Invalid state transition: {} -> FAILED for payment {}",
                    payment.getStatus(),
                    payment.getPaymentId()
            );
            return;
        }
        payment.setStatus(PaymentStatus.FAILED);
        paymentRepository.save(payment);
        paymentAuditService.recordFailed(payment);
        log.info(
                "Payment {} marked as FAILED",
                payment.getPaymentId()
        );
    }

    @Override
    public void processWebhook(String payload) {

        try {

            JsonNode root = objectMapper.readTree(payload);

            String event = root.get("event").asText();

            switch (event) {

                case "payment.captured" ->
                        handlePaymentCaptured(root);

                case "payment.failed" ->
                        handlePaymentFailed(root);

                default ->
                        log.info("Ignoring webhook event: {}", event);
            }

        } catch (Exception e) {

            log.error("Failed to process webhook", e);

        }
    }
}