package com.harsh.payflow.merchant.service.impl;

import com.harsh.payflow.common.response.ApiResponse;
import com.harsh.payflow.common.util.ApiKeyGenerator;
import com.harsh.payflow.common.util.MerchantIdGenerator;
import com.harsh.payflow.merchant.dto.request.CreateMerchantRequest;
import com.harsh.payflow.merchant.dto.response.CreateMerchantResponse;
import com.harsh.payflow.merchant.dto.response.MerchantDetailsResponse;
import com.harsh.payflow.merchant.entity.Merchant;
import com.harsh.payflow.merchant.exception.MerchantAlreadyExistsException;
import com.harsh.payflow.merchant.exception.MerchantNotFoundException;
import com.harsh.payflow.merchant.mapper.MerchantMapper;
import com.harsh.payflow.merchant.repository.MerchantRepository;
import com.harsh.payflow.merchant.service.MerchantService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MerchantServiceImpl implements MerchantService {

    private final MerchantRepository merchantRepository;
    private final MerchantMapper merchantMapper;
    private final MerchantIdGenerator merchantIdGenerator;
    private final ApiKeyGenerator apiKeyGenerator;
    private final PasswordEncoder passwordEncoder;

    @Override
    public ApiResponse<CreateMerchantResponse> createMerchant(CreateMerchantRequest request) {

        if (merchantRepository.existsByEmail(request.email())) {
            throw new MerchantAlreadyExistsException(request.email());
        }

        String merchantId = merchantIdGenerator.generate();

        String apiKey = apiKeyGenerator.generate();

        String apiKeyHash = passwordEncoder.encode(apiKey);

        Merchant merchant = merchantMapper.toEntity(
                request,
                merchantId,
                apiKeyHash
        );

        Merchant savedMerchant = merchantRepository.save(merchant);

        CreateMerchantResponse response = merchantMapper.toResponse(
                savedMerchant,
                apiKey
        );

        return ApiResponse.success(
                "Merchant created successfully",
                response
        );
    }
    @Override
    public ApiResponse<MerchantDetailsResponse> getMerchant(String merchantId) {
        Merchant merchant = merchantRepository
                .findByMerchantId(merchantId)
                .orElseThrow(() -> new MerchantNotFoundException(merchantId));

        MerchantDetailsResponse response =
                merchantMapper.toDetailsResponse(merchant);

        return ApiResponse.success(
                "Merchant fetched successfully",
                response
        );
    }
}