package com.harsh.payflow.payment.audit;

import com.harsh.payflow.payment.entity.Payment;

public interface PaymentAuditService {

    void recordCreated(Payment payment);

    void recordCaptured(Payment payment);

    void recordFailed(Payment payment);

    void recordRetryInitiated(Payment payment);

}