package com.harsh.payflow.payment.controller;

import com.harsh.payflow.common.response.ApiResponse;
import com.harsh.payflow.payment.dto.request.CreatePaymentRequest;
import com.harsh.payflow.payment.dto.response.CreatePaymentResponse;
import com.harsh.payflow.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<ApiResponse<CreatePaymentResponse>> createPayment(
            @Valid @RequestBody CreatePaymentRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        paymentService.createPayment(request)
                );
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<ApiResponse<CreatePaymentResponse>> getPayment(
            @PathVariable String paymentId
    ) {

        return ResponseEntity.ok(
                paymentService.getPayment(paymentId)
        );
    }

    @GetMapping("/{merchantId}/payments")
    public ResponseEntity<ApiResponse<List<CreatePaymentResponse>>> getMerchantPayments(
            @PathVariable String merchantId
    ) {
        return ResponseEntity.ok(
                paymentService.getPaymentsByMerchant(merchantId)
        );
    }

    @PostMapping("/{paymentId}/retry")
    public ResponseEntity<ApiResponse<CreatePaymentResponse>> retryPayment(
            @PathVariable String paymentId
    ) {

        return ResponseEntity.ok(
                paymentService.retryPayment(paymentId)
        );
    }
}