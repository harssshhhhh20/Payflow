package com.harsh.payflow.payment.notification;

import com.harsh.payflow.payment.event.PaymentCreatedEvent;

public interface NotificationService {

    void sendPaymentCreatedNotification(
            PaymentCreatedEvent event
    );

}