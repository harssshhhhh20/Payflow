package com.harsh.payflow.payment.listener;

import com.harsh.payflow.common.config.RabbitMQConfig;
import com.harsh.payflow.common.logging.CorrelationIdMdcHelper;
import com.harsh.payflow.payment.event.PaymentCapturedEvent;
import com.harsh.payflow.payment.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentCapturedListener {

    private final NotificationService notificationService;
    private final CorrelationIdMdcHelper correlationIdMdcHelper;

    @RabbitListener(
            queues = RabbitMQConfig.PAYMENT_CAPTURED_QUEUE,
            containerFactory = "rabbitListenerContainerFactory"
    )
    public void consume(
            PaymentCapturedEvent event,
            Message message
    ) {
        correlationIdMdcHelper.populate(message);
        try {
            notificationService.sendPaymentCapturedNotification(event);
        } finally {
            correlationIdMdcHelper.clear();

        }
    }
}