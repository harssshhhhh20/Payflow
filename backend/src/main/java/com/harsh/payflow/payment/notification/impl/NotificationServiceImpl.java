package com.harsh.payflow.payment.notification.impl;

import com.harsh.payflow.payment.event.PaymentCapturedEvent;
import com.harsh.payflow.payment.event.PaymentCreatedEvent;
import com.harsh.payflow.payment.notification.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationServiceImpl
        implements NotificationService {

    @Override
    public void sendPaymentCreatedNotification(
            PaymentCreatedEvent event
    ) {

        log.info(
                """
                ===========================================
                EMAIL NOTIFICATION
                Payment Created
                Payment Id : {}
                Merchant   : {}
                Amount     : {}
                Currency   : {}
                ===========================================
                """,
                event.paymentId(),
                event.merchantId(),
                event.amount(),
                event.currency()
        );
    }

    public void sendPaymentCapturedNotification(
            PaymentCapturedEvent event
    ){
        log.info(
                """
                ===========================================
                PAYMENT CAPTURED
                Payment Id : {}
                Merchant   : {}
                Amount     : {}
                Currency   : {}
                Gateway Id : {}
                ===========================================
                """,
                event.paymentId(),
                event.merchantId(),
                event.amount(),
                event.currency(),
                event.gatewayPaymentId()
        );
    }
}