package com.harsh.payflow.payment.statemachine;

import com.harsh.payflow.payment.entity.PaymentStatus;
import org.springframework.stereotype.Component;

@Component
public class PaymentStateMachine {

    public boolean canTransition(
            PaymentStatus current,
            PaymentStatus next
    ) {

        return switch (current) {

            case PENDING ->
                    next == PaymentStatus.PROCESSING
                            || next == PaymentStatus.SUCCESS
                            || next == PaymentStatus.FAILED
                            || next == PaymentStatus.CANCELLED;

            case PROCESSING ->
                    next == PaymentStatus.SUCCESS
                            || next == PaymentStatus.FAILED
                            || next == PaymentStatus.CANCELLED;

            case FAILED ->
                    next == PaymentStatus.PENDING;

            case SUCCESS ->
                    next == PaymentStatus.REFUNDED;

            case CANCELLED,
                 REFUNDED ->
                    false;
        };
    }
}