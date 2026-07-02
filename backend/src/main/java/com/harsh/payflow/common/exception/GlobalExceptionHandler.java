package com.harsh.payflow.common.exception;

import com.harsh.payflow.common.response.ApiResponse;
import com.harsh.payflow.merchant.exception.MerchantAlreadyExistsException;
import com.harsh.payflow.merchant.exception.MerchantNotFoundException;
import com.harsh.payflow.payment.exception.PaymentGatewayException;
import com.harsh.payflow.payment.exception.PaymentNotFoundException;
import com.harsh.payflow.payment.exception.PaymentRetryException;
import com.harsh.payflow.payment.metrics.PaymentMetricsService;
import com.harsh.payflow.payment.metrics.PaymentRequestOutcome;
import com.harsh.payflow.payment.ratelimit.RateLimitException;
import io.github.resilience4j.bulkhead.BulkheadFullException;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final PaymentMetricsService paymentMetricsService;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(
            MethodArgumentNotValidException ex
    ) {

        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .toList();

        return ResponseEntity
                .badRequest()
                .body(
                        ApiResponse.failure(
                                "Validation Failed",
                                errors
                        )
                );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConstraintViolation(
            ConstraintViolationException ex
    ) {

        List<String> errors = ex.getConstraintViolations()
                .stream()
                .map(constraint -> constraint.getMessage())
                .toList();

        return ResponseEntity
                .badRequest()
                .body(
                        ApiResponse.failure(
                                "Validation Failed",
                                errors
                        )
                );
    }

    @ExceptionHandler(MerchantAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<Void>> handleMerchantAlreadyExists(
            MerchantAlreadyExistsException ex
    ) {

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(
                        ApiResponse.failure(ex.getMessage())
                );
    }

    @ExceptionHandler(MerchantNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleMerchantNotFound(
            MerchantNotFoundException ex
    ) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(
                        ApiResponse.failure(ex.getMessage())
                );
    }

    @ExceptionHandler(PaymentNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handlePaymentNotFound(
            PaymentNotFoundException ex
    ) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(
                        ApiResponse.failure(ex.getMessage())
                );
    }

    @ExceptionHandler(PaymentGatewayException.class)
    public ResponseEntity<ApiResponse<Void>> handlePaymentGatewayException(
            PaymentGatewayException ex
    ) {

        paymentMetricsService.recordRequestOutcome(
                PaymentRequestOutcome.FAILED
        );

        return ResponseEntity
                .status(HttpStatus.BAD_GATEWAY)
                .body(
                        ApiResponse.failure(ex.getMessage())
                );
    }

    @ExceptionHandler(PaymentRetryException.class)
    public ResponseEntity<ApiResponse<Void>> handlePaymentRetryException(
            PaymentRetryException ex
    ) {

        return ResponseEntity
                .badRequest()
                .body(
                        ApiResponse.failure(ex.getMessage())
                );
    }

    @ExceptionHandler(RateLimitException.class)
    public ResponseEntity<ApiResponse<Void>> handleRateLimitException(
            RateLimitException ex
    ) {

        paymentMetricsService.recordRequestOutcome(
                PaymentRequestOutcome.RATE_LIMITED
        );

        return ResponseEntity
                .status(HttpStatus.TOO_MANY_REQUESTS)
                .body(
                        ApiResponse.failure(ex.getMessage())
                );
    }

    @ExceptionHandler(BulkheadFullException.class)
    public ResponseEntity<ApiResponse<Void>> handleBulkheadFullException(
            BulkheadFullException ex
    ) {

        paymentMetricsService.recordRequestOutcome(
                PaymentRequestOutcome.BULKHEAD_REJECTED
        );

        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(
                        ApiResponse.failure(
                                "Payment service is currently under heavy load. Please try again shortly."
                        )
                );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(
            Exception ex
    ) {

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                        ApiResponse.failure(
                                "An unexpected error occurred."
                        )
                );
    }
}