package com.harsh.payflow.payment.notification;

import com.harsh.payflow.payment.event.PaymentCapturedEvent;
import com.harsh.payflow.payment.event.PaymentCreatedEvent;

public interface NotificationService {

    void sendPaymentCreatedNotification(
            PaymentCreatedEvent event
    );

    void sendPaymentCapturedNotification(
            PaymentCapturedEvent event
    );

}