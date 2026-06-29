package com.harsh.payflow.payment.listener;

import com.harsh.payflow.common.config.RabbitMQConfig;
import com.harsh.payflow.payment.event.PaymentCreatedEvent;
import com.harsh.payflow.payment.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class PaymentEventListener {

    private final NotificationService notificationService;

    @RabbitListener(
            queues = RabbitMQConfig.PAYMENT_QUEUE
    )
    public void consume(
            PaymentCreatedEvent event
    ) {
        notificationService.sendPaymentCreatedNotification(
                event
        );

    }
}