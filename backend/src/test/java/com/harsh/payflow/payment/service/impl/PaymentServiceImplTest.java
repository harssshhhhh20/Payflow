package com.harsh.payflow.payment.service.impl;

import com.harsh.payflow.common.response.ApiResponse;
import com.harsh.payflow.common.security.AuthenticatedMerchantProvider;
import com.harsh.payflow.common.security.principal.MerchantPrincipal;
import com.harsh.payflow.common.util.PaymentIdGenerator;
import com.harsh.payflow.merchant.entity.Merchant;
import com.harsh.payflow.merchant.finder.MerchantFinder;
import com.harsh.payflow.payment.audit.PaymentAuditService;
import com.harsh.payflow.payment.dto.request.CreatePaymentRequest;
import com.harsh.payflow.payment.dto.response.CreatePaymentResponse;
import com.harsh.payflow.payment.entity.GatewayType;
import com.harsh.payflow.payment.entity.Payment;
import com.harsh.payflow.payment.entity.PaymentStatus;
import com.harsh.payflow.payment.exception.PaymentGatewayException;
import com.harsh.payflow.payment.exception.PaymentRetryException;
import com.harsh.payflow.payment.finder.PaymentFinder;
import com.harsh.payflow.payment.gateway.GatewayResponse;
import com.harsh.payflow.payment.gateway.PaymentGateway;
import com.harsh.payflow.payment.mapper.PaymentMapper;
import com.harsh.payflow.payment.metrics.PaymentMetricEvent;
import com.harsh.payflow.payment.metrics.PaymentMetricsService;
import com.harsh.payflow.payment.metrics.PaymentRequestOutcome;
import com.harsh.payflow.payment.publisher.PaymentEventPublisher;
import com.harsh.payflow.payment.repository.PaymentRepository;
import com.harsh.payflow.payment.statemachine.PaymentStateMachine;
import io.micrometer.core.instrument.Timer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentMapper paymentMapper;

    @Mock
    private MerchantFinder merchantFinder;

    @Mock
    private PaymentIdGenerator paymentIdGenerator;

    @Mock
    private PaymentFinder paymentFinder;

    @Mock
    private PaymentGateway paymentGateway;

    @Mock
    private PaymentStateMachine paymentStateMachine;

    @Mock
    private PaymentAuditService paymentAuditService;

    @Mock
    private PaymentEventPublisher paymentEventPublisher;

    @Mock
    private AuthenticatedMerchantProvider authenticatedMerchantProvider;

    @Mock
    private PaymentMetricsService paymentMetricsService;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private Merchant merchant;
    private MerchantPrincipal principal;
    private Timer.Sample timerSample;

    @BeforeEach
    void setUp() {

        timerSample = Mockito.mock(Timer.Sample.class);

        lenient().when(paymentMetricsService.startPaymentProcessing())
                .thenReturn(timerSample);

        principal = new MerchantPrincipal(
                "MER_123",
                "Acme Pvt Ltd",
                "merchant@test.com"
        );

        merchant = Merchant.builder()
                .merchantId("MER_123")
                .businessName("Acme Pvt Ltd")
                .email("merchant@test.com")
                .active(true)
                .build();

        lenient().when(authenticatedMerchantProvider.getAuthenticatedMerchant())
                .thenReturn(principal);

        lenient().when(merchantFinder.getByMerchantId("MER_123"))
                .thenReturn(merchant);

        lenient().when(paymentRepository.save(any(Payment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    private CreatePaymentRequest createRequest() {
        return new CreatePaymentRequest(
                BigDecimal.valueOf(500),
                "INR",
                "idem-123"
        );
    }

    private Payment createPayment() {
//        Timer.Sample timerSample = Mockito.mock(Timer.Sample.class);
//
//        when(paymentMetricsService.startPaymentProcessing())
//                .thenReturn(timerSample);
        return Payment.builder()
                .paymentId("PAY_123")
                .merchant(merchant)
                .amount(BigDecimal.valueOf(500))
                .currency("INR")
                .status(PaymentStatus.PENDING)
                .gateway(GatewayType.RAZORPAY)
                .idempotencyKey("idem-123")
                .retryCount(0)
                .createdAt(LocalDateTime.now())
                .build();
    }

    private GatewayResponse successGatewayResponse() {
        return new GatewayResponse(
                "order_123",
                "rzp_test_key",
                null
        );
    }

    private CreatePaymentResponse createPaymentResponse() {

        return new CreatePaymentResponse(
                "PAY_123",
                "MER_123",
                BigDecimal.valueOf(500),
                "INR",
                PaymentStatus.PENDING,
                "order_123",
                "rzp_test_key",
                LocalDateTime.now()
        );
    }

    @Test
    void shouldCreatePaymentSuccessfully() {

        CreatePaymentRequest request = createRequest();

        Payment payment = createPayment();

        GatewayResponse gatewayResponse = successGatewayResponse();

        CreatePaymentResponse response = createPaymentResponse();

        when(paymentRepository.findByMerchant_MerchantIdAndIdempotencyKey(
                "MER_123",
                "idem-123"
        )).thenReturn(Optional.empty());

        when(paymentIdGenerator.generate())
                .thenReturn("PAY_123");

        when(paymentRepository.existsByPaymentId("PAY_123"))
                .thenReturn(false);

        when(paymentMapper.toEntity(
                request,
                merchant,
                "PAY_123",
                GatewayType.RAZORPAY
        )).thenReturn(payment);

        when(paymentGateway.createPayment(payment))
                .thenReturn(gatewayResponse);

        when(paymentMapper.toResponse(
                any(Payment.class),
                eq("rzp_test_key")
        )).thenReturn(response);

        ApiResponse<CreatePaymentResponse> apiResponse =
                paymentService.createPayment(request);

        assertTrue(apiResponse.success());

        assertEquals(
                "Payment created successfully",
                apiResponse.message()
        );

        assertNotNull(apiResponse.data());

        verify(paymentRepository, times(2))
                .save(any(Payment.class));

        verify(paymentGateway)
                .createPayment(payment);

        verify(paymentAuditService)
                .recordCreated(any(Payment.class));

        verify(paymentEventPublisher)
                .publishPaymentCreated(any());

        verify(paymentMetricsService)
                .recordPayment(PaymentMetricEvent.CREATED);

        verify(paymentMetricsService)
                .recordRequestOutcome(
                        PaymentRequestOutcome.SUCCESS
                );

        verify(paymentMetricsService)
                .stopPaymentProcessing(timerSample);
    }

    @Test
    void shouldThrowExceptionWhenGatewayFails() {

        CreatePaymentRequest request = createRequest();

        Payment payment = createPayment();

        when(paymentRepository.findByMerchant_MerchantIdAndIdempotencyKey(
                "MER_123",
                "idem-123"
        )).thenReturn(Optional.empty());

        when(paymentIdGenerator.generate())
                .thenReturn("PAY_123");

        when(paymentRepository.existsByPaymentId("PAY_123"))
                .thenReturn(false);

        when(paymentMapper.toEntity(
                request,
                merchant,
                "PAY_123",
                GatewayType.RAZORPAY
        )).thenReturn(payment);

        when(paymentGateway.createPayment(payment))
                .thenThrow(new PaymentGatewayException("Gateway Down"));

        assertThrows(
                PaymentGatewayException.class,
                () -> paymentService.createPayment(request)
        );

        verify(paymentAuditService, never()).recordCreated(any());
        verify(paymentEventPublisher, never()).publishPaymentCreated(any());
        verify(paymentMetricsService).stopPaymentProcessing(timerSample);
    }

    @Test
    void shouldGenerateUniquePaymentId() {

        CreatePaymentRequest request =
                createRequest();

        Payment payment =
                createPayment();

        when(paymentRepository.findByMerchant_MerchantIdAndIdempotencyKey(
                "MER_123",
                "idem-123"
        )).thenReturn(Optional.empty());

        when(paymentIdGenerator.generate())
                .thenReturn("PAY_DUPLICATE")
                .thenReturn("PAY_123");

        when(paymentRepository.existsByPaymentId("PAY_DUPLICATE"))
                .thenReturn(true);

        when(paymentRepository.existsByPaymentId("PAY_123"))
                .thenReturn(false);

        when(paymentMapper.toEntity(
                request,
                merchant,
                "PAY_123",
                GatewayType.RAZORPAY
        )).thenReturn(payment);

        when(paymentGateway.createPayment(payment))
                .thenReturn(successGatewayResponse());

        when(paymentMapper.toResponse(
                any(Payment.class),
                any()
        )).thenReturn(createPaymentResponse());

        paymentService.createPayment(request);

        verify(paymentIdGenerator, times(2))
                .generate();
    }

    @Test
    void shouldRetryPaymentSuccessfully() {

        Payment payment = createPayment();

        payment.setStatus(PaymentStatus.FAILED);

        GatewayResponse gatewayResponse =
                successGatewayResponse();

        CreatePaymentResponse response =
                createPaymentResponse();

        when(paymentFinder.getByPaymentId("PAY_123"))
                .thenReturn(payment);

        when(paymentStateMachine.canTransition(
                PaymentStatus.FAILED,
                PaymentStatus.PENDING))
                .thenReturn(true);

        when(paymentGateway.createPayment(payment))
                .thenReturn(gatewayResponse);

        when(paymentMapper.toResponse(
                any(Payment.class),
                eq("rzp_test_key")))
                .thenReturn(response);

        ApiResponse<CreatePaymentResponse> apiResponse =
                paymentService.retryPayment("PAY_123");

        assertTrue(apiResponse.success());

        verify(paymentMetricsService)
                .recordRetry();

        verify(paymentAuditService)
                .recordRetryInitiated(any());

        verify(paymentRepository)
                .save(any(Payment.class));
    }

    @Test
    void shouldThrowWhenRetryLimitExceeded() {

        Payment payment = createPayment();

        payment.setStatus(PaymentStatus.FAILED);

        payment.setRetryCount(3);

        when(paymentFinder.getByPaymentId("PAY_123"))
                .thenReturn(payment);

        when(paymentStateMachine.canTransition(
                PaymentStatus.FAILED,
                PaymentStatus.PENDING))
                .thenReturn(true);

        assertThrows(
                PaymentRetryException.class,
                () -> paymentService.retryPayment("PAY_123")
        );

        verify(paymentGateway, never())
                .createPayment(any());
    }

    @Test
    void shouldThrowWhenInvalidStateTransition() {

        Payment payment = createPayment();

        payment.setStatus(PaymentStatus.SUCCESS);

        when(paymentFinder.getByPaymentId("PAY_123"))
                .thenReturn(payment);

        when(paymentStateMachine.canTransition(
                PaymentStatus.SUCCESS,
                PaymentStatus.PENDING))
                .thenReturn(false);

        assertThrows(
                PaymentRetryException.class,
                () -> paymentService.retryPayment("PAY_123")
        );
    }

    @Test
    void shouldThrowWhenRetryByAnotherMerchant() {

        Merchant anotherMerchant =
                Merchant.builder()
                        .merchantId("MER_999")
                        .build();

        Payment payment = createPayment();

        payment.setMerchant(anotherMerchant);

        when(paymentFinder.getByPaymentId("PAY_123"))
                .thenReturn(payment);

        assertThrows(
                AccessDeniedException.class,
                () -> paymentService.retryPayment("PAY_123")
        );
    }

    @Test
    void shouldReturnMerchantPayments() {

        Payment payment = createPayment();

        CreatePaymentResponse response =
                createPaymentResponse();

        when(paymentRepository.findByMerchant_MerchantId(
                "MER_123"))
                .thenReturn(List.of(payment));

        when(paymentMapper.toResponse(
                payment,
                null))
                .thenReturn(response);

        ApiResponse<List<CreatePaymentResponse>> apiResponse =
                paymentService.getMyPayments();

        assertEquals(
                1,
                apiResponse.data().size()
        );

        assertEquals(
                "Payments fetched successfully",
                apiResponse.message()
        );
    }
}
