package com.harsh.payflow.merchant.service.impl;

import com.harsh.payflow.common.response.ApiResponse;
import com.harsh.payflow.common.util.ApiKeyGenerator;
import com.harsh.payflow.common.util.MerchantIdGenerator;
import com.harsh.payflow.merchant.dto.request.CreateMerchantRequest;
import com.harsh.payflow.merchant.dto.response.CreateMerchantResponse;
import com.harsh.payflow.merchant.dto.response.MerchantDetailsResponse;
import com.harsh.payflow.merchant.entity.Merchant;
import com.harsh.payflow.merchant.exception.MerchantAlreadyExistsException;
import com.harsh.payflow.merchant.finder.MerchantFinder;
import com.harsh.payflow.merchant.mapper.MerchantMapper;
import com.harsh.payflow.merchant.repository.MerchantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MerchantServiceImplTest {

    @Mock
    private MerchantRepository merchantRepository;

    @Mock
    private MerchantMapper merchantMapper;

    @Mock
    private MerchantIdGenerator merchantIdGenerator;

    @Mock
    private ApiKeyGenerator apiKeyGenerator;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private MerchantFinder merchantFinder;

    @InjectMocks
    private MerchantServiceImpl merchantService;

    private Merchant merchant;

    @BeforeEach
    void setUp() {

        merchant = Merchant.builder()
                .merchantId("MER_123")
                .businessName("Acme Pvt Ltd")
                .email("merchant@test.com")
                .apiKeyHash("hashed")
                .apiKeyPrefix("ABCDEFGH")
                .active(true)
                .createdAt(LocalDateTime.now())
                .build();

        lenient().when(merchantRepository.save(any(Merchant.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    private CreateMerchantRequest createRequest() {

        return CreateMerchantRequest.builder()
                .businessName("Acme Pvt Ltd")
                .email("merchant@test.com")
                .build();
    }

    private CreateMerchantResponse createResponse() {

        return CreateMerchantResponse.builder()
                .merchantId("MER_123")
                .businessName("Acme Pvt Ltd")
                .email("merchant@test.com")
                .apiKey("pf_live_test")
                .active(true)
                .createdAt(LocalDateTime.now())
                .build();
    }

    private MerchantDetailsResponse detailsResponse() {

        return MerchantDetailsResponse.builder()
                .merchantId("MER_123")
                .businessName("Acme Pvt Ltd")
                .email("merchant@test.com")
                .active(true)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void shouldCreateMerchantSuccessfully() {

        CreateMerchantRequest request = createRequest();

        when(merchantRepository.existsByEmail(request.email()))
                .thenReturn(false);

        when(merchantIdGenerator.generate())
                .thenReturn("MER_123");

        when(merchantRepository.existsByMerchantId("MER_123"))
                .thenReturn(false);

        when(apiKeyGenerator.generate())
                .thenReturn("pf_live_12345678901234567890123456789012");

        when(passwordEncoder.encode(anyString()))
                .thenReturn("hashed");

        when(merchantMapper.toEntity(
                any(),
                anyString(),
                anyString(),
                anyString()))
                .thenReturn(merchant);

        when(merchantMapper.toResponse(
                any(),
                anyString()))
                .thenReturn(createResponse());

        ApiResponse<CreateMerchantResponse> response =
                merchantService.createMerchant(request);

        assertTrue(response.success());

        assertEquals(
                "Merchant created successfully",
                response.message()
        );

        verify(merchantRepository)
                .save(any(Merchant.class));
    }

    @Test
    void shouldThrowWhenMerchantAlreadyExists() {

        CreateMerchantRequest request = createRequest();

        when(merchantRepository.existsByEmail(request.email()))
                .thenReturn(true);

        assertThrows(
                MerchantAlreadyExistsException.class,
                () -> merchantService.createMerchant(request)
        );

        verify(merchantRepository, never())
                .save(any());
    }

    @Test
    void shouldReturnMerchantSuccessfully() {

        when(merchantFinder.getByMerchantId("MER_123"))
                .thenReturn(merchant);

        when(merchantMapper.toDetailsResponse(merchant))
                .thenReturn(detailsResponse());

        ApiResponse<MerchantDetailsResponse> response =
                merchantService.getMerchant("MER_123");

        assertTrue(response.success());

        assertEquals(
                "Merchant fetched successfully",
                response.message()
        );
    }

    @Test
    void shouldReturnAllMerchants() {

        when(merchantRepository.findAll())
                .thenReturn(List.of(merchant));

        when(merchantMapper.toDetailsResponse(merchant))
                .thenReturn(detailsResponse());

        ApiResponse<List<MerchantDetailsResponse>> response =
                merchantService.getAllMerchants();

        assertEquals(
                1,
                response.data().size()
        );
    }

    @Test
    void shouldDeactivateMerchant() {

        merchant.setActive(true);

        when(merchantFinder.getByMerchantId("MER_123"))
                .thenReturn(merchant);

        ApiResponse<Void> response =
                merchantService.deactivateMerchant("MER_123");

        assertFalse(merchant.isActive());

        assertEquals(
                "Merchant deactivated successfully",
                response.message()
        );
    }

    @Test
    void shouldReturnAlreadyDeactivated() {

        merchant.setActive(false);

        when(merchantFinder.getByMerchantId("MER_123"))
                .thenReturn(merchant);

        ApiResponse<Void> response =
                merchantService.deactivateMerchant("MER_123");

        assertEquals(
                "Merchant is already deactivated",
                response.message()
        );
    }

}