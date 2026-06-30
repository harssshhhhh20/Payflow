package com.harsh.payflow.payment.listener;

import com.harsh.payflow.common.config.RabbitMQConfig;
import com.harsh.payflow.common.logging.CorrelationIdMdcHelper;
import com.harsh.payflow.payment.event.PaymentCreatedEvent;
import com.harsh.payflow.payment.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentCreatedListener {

    private final NotificationService notificationService;
    private final CorrelationIdMdcHelper correlationIdMdcHelper;

    @RabbitListener(
            queues = RabbitMQConfig.PAYMENT_CREATED_QUEUE,
            containerFactory = "rabbitListenerContainerFactory"
    )
    public void consume(
            PaymentCreatedEvent event,
            Message message
    ) {
        correlationIdMdcHelper.populate(message);
        try {
            notificationService.sendPaymentCreatedNotification(event);
        } finally {
            correlationIdMdcHelper.clear();
        }
    }
}