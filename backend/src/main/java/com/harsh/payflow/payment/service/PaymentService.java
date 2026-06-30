package com.harsh.payflow.payment.service;

import com.harsh.payflow.common.response.ApiResponse;
import com.harsh.payflow.payment.dto.request.CreatePaymentRequest;
import com.harsh.payflow.payment.dto.response.CreatePaymentResponse;

import java.util.List;

public interface PaymentService {

    ApiResponse<CreatePaymentResponse> createPayment(CreatePaymentRequest request);

    ApiResponse<CreatePaymentResponse> getPayment(String paymentId);

    ApiResponse<List<CreatePaymentResponse>> getMyPayments();

    ApiResponse<CreatePaymentResponse> retryPayment(String paymentId);
}