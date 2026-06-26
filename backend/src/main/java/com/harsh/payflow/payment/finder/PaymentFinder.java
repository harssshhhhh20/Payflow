package com.harsh.payflow.payment.finder;

import com.harsh.payflow.payment.entity.Payment;
import com.harsh.payflow.payment.exception.PaymentNotFoundException;
import com.harsh.payflow.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentFinder {

    private final PaymentRepository paymentRepository;

    public Payment getByPaymentId(String paymentId) {
        return paymentRepository.findByPaymentId(paymentId)
                .orElseThrow(() ->
                        new PaymentNotFoundException(paymentId));
    }
}