package com.harsh.payflow.payment.webhook;

public interface WebhookService {

    void processWebhook(String payload);

}