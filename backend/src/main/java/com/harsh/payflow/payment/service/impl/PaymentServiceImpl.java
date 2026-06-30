package com.harsh.payflow.payment.service.impl;

import com.harsh.payflow.common.response.ApiResponse;
import com.harsh.payflow.common.security.AuthenticatedMerchantProvider;
import com.harsh.payflow.common.util.PaymentIdGenerator;
import com.harsh.payflow.merchant.entity.Merchant;
import com.harsh.payflow.merchant.finder.MerchantFinder;
import com.harsh.payflow.payment.audit.PaymentAuditService;
import com.harsh.payflow.payment.dto.request.CreatePaymentRequest;
import com.harsh.payflow.payment.dto.response.CreatePaymentResponse;
import com.harsh.payflow.payment.entity.GatewayType;
import com.harsh.payflow.payment.entity.Payment;
import com.harsh.payflow.payment.entity.PaymentStatus;
import com.harsh.payflow.payment.event.PaymentCreatedEvent;
import com.harsh.payflow.payment.exception.PaymentGatewayException;
import com.harsh.payflow.payment.exception.PaymentRetryException;
import com.harsh.payflow.payment.finder.PaymentFinder;
import com.harsh.payflow.payment.gateway.GatewayResponse;
import com.harsh.payflow.payment.gateway.PaymentGateway;
import com.harsh.payflow.payment.mapper.PaymentMapper;
import com.harsh.payflow.payment.metrics.PaymentMetricEvent;
import com.harsh.payflow.payment.metrics.PaymentMetricsService;
import com.harsh.payflow.payment.publisher.PaymentEventPublisher;
import com.harsh.payflow.payment.repository.PaymentRepository;
import com.harsh.payflow.payment.service.PaymentService;
import com.harsh.payflow.payment.statemachine.PaymentStateMachine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final MerchantFinder merchantFinder;
    private final PaymentIdGenerator paymentIdGenerator;
    private final PaymentFinder paymentFinder;
    private final PaymentGateway paymentGateway;
    private final PaymentStateMachine paymentStateMachine;
    private final PaymentAuditService paymentAuditService;
    private final PaymentEventPublisher paymentEventPublisher;
    private final AuthenticatedMerchantProvider authenticatedMerchantProvider;
    private final PaymentMetricsService paymentMetricsService;

    @Transactional
    @Override
    public ApiResponse<CreatePaymentResponse> createPayment(
            CreatePaymentRequest request
    ) {

        Merchant merchant = merchantFinder.getByMerchantId(
                authenticatedMerchantProvider
                        .getAuthenticatedMerchant()
                        .getMerchantId()
        );

        Optional<Payment> existingPayment =
                paymentRepository
                        .findByMerchant_MerchantIdAndIdempotencyKey(
                                merchant.getMerchantId(),
                                request.idempotencyKey()
                        );

        if (existingPayment.isPresent()) {
            return ApiResponse.success(
                    "Payment already exists",
                    paymentMapper.toResponse(
                            existingPayment.get(),
                            null
                    )
            );
        }

        String paymentId;

        do {
            paymentId = paymentIdGenerator.generate();
        } while (paymentRepository.existsByPaymentId(paymentId));

        Payment payment = paymentMapper.toEntity(
                request,
                merchant,
                paymentId,
                GatewayType.RAZORPAY
        );

        Payment savedPayment = paymentRepository.save(payment);

        GatewayResponse gatewayResponse =
                paymentGateway.createPayment(savedPayment);

        if (!gatewayResponse.success()) {
            throw new PaymentGatewayException(
                    gatewayResponse.errorMessage()
            );
        }

        savedPayment.setGatewayPaymentId(
                gatewayResponse.gatewayPaymentId()
        );

        savedPayment = paymentRepository.save(savedPayment);
        paymentMetricsService.recordPayment(
                PaymentMetricEvent.CREATED
        );
        paymentAuditService.recordCreated(savedPayment);
        paymentEventPublisher.publishPaymentCreated(
                new PaymentCreatedEvent(
                        savedPayment.getPaymentId(),
                        savedPayment.getMerchant().getMerchantId(),
                        savedPayment.getAmount(),
                        savedPayment.getCurrency(),
                        savedPayment.getGatewayPaymentId()
                )
        );

        log.info(
                "Payment {} created with gateway order {}",
                savedPayment.getPaymentId(),
                savedPayment.getGatewayPaymentId()
        );

        return ApiResponse.success(
                "Payment created successfully",
                paymentMapper.toResponse(
                        savedPayment,
                        gatewayResponse.gatewayPublicKey()
                )
        );
    }

    @Transactional(readOnly = true)
    @Override
    public ApiResponse<CreatePaymentResponse> getPayment(
            String paymentId
    ) {

        Payment payment =
                paymentFinder.getByPaymentId(paymentId);
        String authenticatedMerchantId =
                authenticatedMerchantProvider
                        .getAuthenticatedMerchant()
                        .getMerchantId();

        if (!payment.getMerchant()
                .getMerchantId()
                .equals(authenticatedMerchantId)) {

            throw new AccessDeniedException(
                    "You are not authorized to access this payment."
            );
        }

        return ApiResponse.success(
                "Payment fetched successfully",
                paymentMapper.toResponse(
                        payment,
                        null
                )
        );
    }

    @Transactional(readOnly = true)
    @Override
    public ApiResponse<List<CreatePaymentResponse>> getMyPayments(){

        String merchantId =
                authenticatedMerchantProvider
                        .getAuthenticatedMerchant()
                        .getMerchantId();

        List<CreatePaymentResponse> payments =
                paymentRepository
                        .findByMerchant_MerchantId(merchantId)
                        .stream()
                        .map(payment ->
                                paymentMapper.toResponse(
                                        payment,
                                        null
                                )
                        )
                        .toList();

        return ApiResponse.success(
                "Payments fetched successfully",
                payments
        );
    }

    @Transactional
    @Override
    public ApiResponse<CreatePaymentResponse> retryPayment(
            String paymentId
    ) {

        Payment payment = paymentFinder.getByPaymentId(paymentId);
        String authenticatedMerchantId =
                authenticatedMerchantProvider
                        .getAuthenticatedMerchant()
                        .getMerchantId();

        if (!payment.getMerchant()
                .getMerchantId()
                .equals(authenticatedMerchantId)) {

            throw new AccessDeniedException(
                    "You are not authorized to retry this payment."
            );
        }

        if (!paymentStateMachine.canTransition(
                payment.getStatus(),
                PaymentStatus.PENDING
        )) {
            throw new PaymentRetryException(
                    "Payment cannot be retried from status "
                            + payment.getStatus()
            );
        }

        if (payment.getRetryCount() >= 3) {
            throw new PaymentRetryException(
                    "Maximum retry limit reached."
            );
        }

        GatewayResponse gatewayResponse =
                paymentGateway.createPayment(payment);

        if (!gatewayResponse.success()) {
            throw new PaymentGatewayException(
                    gatewayResponse.errorMessage()
            );
        }

        payment.setGatewayPaymentId(
                gatewayResponse.gatewayPaymentId()
        );

        payment.setRetryCount(
                payment.getRetryCount() + 1
        );

        payment.setStatus(PaymentStatus.PENDING);

        payment = paymentRepository.save(payment);

        paymentMetricsService.recordRetry();

        paymentAuditService.recordRetryInitiated(payment);

        log.info(
                "Payment {} retried successfully. Retry Count: {}, New Gateway Order: {}",
                payment.getPaymentId(),
                payment.getRetryCount(),
                payment.getGatewayPaymentId()
        );

        return ApiResponse.success(
                "Payment retry initiated successfully",
                paymentMapper.toResponse(
                        payment,
                        gatewayResponse.gatewayPublicKey()
                )
        );
    }
}