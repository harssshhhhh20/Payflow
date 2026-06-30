package com.harsh.payflow.payment.publisher;

import com.harsh.payflow.common.config.RabbitMQConfig;
import com.harsh.payflow.payment.event.PaymentCapturedEvent;
import com.harsh.payflow.payment.event.PaymentCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import com.harsh.payflow.common.logging.CorrelationIdConstants;
import com.harsh.payflow.common.messaging.RabbitHeaders;
import org.slf4j.MDC;
import org.springframework.amqp.core.Message;

@Component
@RequiredArgsConstructor
public class PaymentEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishPaymentCreated(
            PaymentCreatedEvent event
    ) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.PAYMENT_EXCHANGE,
                RabbitMQConfig.PAYMENT_CREATED_ROUTING_KEY,
                event,
                this::addCorrelationId
        );
    }

    public void publishPaymentCaptured(
            PaymentCapturedEvent event
    ) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.PAYMENT_EXCHANGE,
                RabbitMQConfig.PAYMENT_CAPTURED_ROUTING_KEY,
                event,
                this::addCorrelationId
        );
    }

    private Message addCorrelationId(Message message) {
        String correlationId =
                MDC.get(CorrelationIdConstants.MDC_KEY);
        if (correlationId != null) {
            message.getMessageProperties()
                    .setHeader(
                            RabbitHeaders.CORRELATION_ID,
                            correlationId
                    );
        }
        return message;
    }

}