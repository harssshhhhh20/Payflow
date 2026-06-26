package com.harsh.payflow.payment.service.impl;

import com.harsh.payflow.common.response.ApiResponse;
import com.harsh.payflow.common.util.PaymentIdGenerator;
import com.harsh.payflow.merchant.entity.Merchant;
import com.harsh.payflow.merchant.finder.MerchantFinder;
import com.harsh.payflow.payment.dto.request.CreatePaymentRequest;
import com.harsh.payflow.payment.dto.response.CreatePaymentResponse;
import com.harsh.payflow.payment.entity.GatewayType;
import com.harsh.payflow.payment.entity.Payment;
import com.harsh.payflow.payment.finder.PaymentFinder;
import com.harsh.payflow.payment.mapper.PaymentMapper;
import com.harsh.payflow.payment.repository.PaymentRepository;
import com.harsh.payflow.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final MerchantFinder merchantFinder;
    private final PaymentIdGenerator paymentIdGenerator;
    private final PaymentFinder paymentFinder;

    @Transactional
    @Override
    public ApiResponse<CreatePaymentResponse> createPayment(
            CreatePaymentRequest request
    ) {
        Optional<Payment> existingPayment =
                paymentRepository.findByIdempotencyKey(
                        request.idempotencyKey()
                );
        if (existingPayment.isPresent()) {
            return ApiResponse.success(
                    "Payment already exists",
                    paymentMapper.toResponse(existingPayment.get())
            );
        }
        Merchant merchant =
                merchantFinder.getByMerchantId(request.merchantId());
        String paymentId;
        do {
            paymentId = paymentIdGenerator.generate();
        } while (paymentRepository.existsByPaymentId(paymentId));
        Payment payment = paymentMapper.toEntity(
                request,
                merchant,
                paymentId,
                GatewayType.STRIPE
        );

        Payment savedPayment =
                paymentRepository.save(payment);


        return ApiResponse.success(
                "Payment created successfully",
                paymentMapper.toResponse(savedPayment)
        );
    }

    @Transactional(readOnly = true)
    @Override
    public ApiResponse<CreatePaymentResponse> getPayment(String paymentId) {

        Payment payment = paymentFinder.getByPaymentId(paymentId);

        CreatePaymentResponse response =
                paymentMapper.toResponse(payment);

        return ApiResponse.success(
                "Payment fetched successfully",
                response
        );
    }

}
