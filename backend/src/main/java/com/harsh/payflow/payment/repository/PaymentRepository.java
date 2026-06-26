package com.harsh.payflow.payment.repository;

import com.harsh.payflow.payment.entity.Payment;
import com.harsh.payflow.payment.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    Optional<Payment> findByPaymentId(String paymentId);

    Optional<Payment> findByGatewayPaymentId(String gatewayPaymentId);

    Optional<Payment> findByIdempotencyKey(String idempotencyKey);

    List<Payment> findByMerchant_MerchantId(String merchantId);

    List<Payment> findByStatus(PaymentStatus status);

    boolean existsByPaymentId(String paymentId);
}