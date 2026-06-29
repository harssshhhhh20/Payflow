package com.harsh.payflow.payment.publisher;

import com.harsh.payflow.common.config.RabbitMQConfig;
import com.harsh.payflow.payment.event.PaymentCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishPaymentCreated(
            PaymentCreatedEvent event
    ) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.PAYMENT_EXCHANGE,
                RabbitMQConfig.PAYMENT_ROUTING_KEY,
                event
        );
    }
}