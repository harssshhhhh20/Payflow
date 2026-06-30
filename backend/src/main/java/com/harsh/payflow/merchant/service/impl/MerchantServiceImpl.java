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
import com.harsh.payflow.merchant.service.MerchantService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MerchantServiceImpl implements MerchantService {

    private final MerchantRepository merchantRepository;
    private final MerchantMapper merchantMapper;
    private final MerchantIdGenerator merchantIdGenerator;
    private final ApiKeyGenerator apiKeyGenerator;
    private final PasswordEncoder passwordEncoder;
    private final MerchantFinder merchantFinder;

    @Transactional
    @Override
    public ApiResponse<CreateMerchantResponse> createMerchant(CreateMerchantRequest request) {

        if (merchantRepository.existsByEmail(request.email())) {
            throw new MerchantAlreadyExistsException(request.email());
        }

        String merchantId;
        do {
            merchantId = merchantIdGenerator.generate();
        } while (merchantRepository.existsByMerchantId(merchantId));

        String apiKey = apiKeyGenerator.generate();
        String apiKeyPrefix = apiKey.substring("pf_live_".length(),
                "pf_live_".length() + 8);

        String apiKeyHash = passwordEncoder.encode(apiKey);

        Merchant merchant = merchantMapper.toEntity(
                request,
                merchantId,
                apiKeyPrefix,
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

    @Transactional(readOnly = true)
    @Override
    public ApiResponse<MerchantDetailsResponse> getMerchant(String merchantId) {
        Merchant merchant = merchantFinder.getByMerchantId(merchantId);

        MerchantDetailsResponse response =
                merchantMapper.toDetailsResponse(merchant);

        return ApiResponse.success(
                "Merchant fetched successfully",
                response
        );
    }

    @Transactional(readOnly = true)
    @Override
    public ApiResponse<List<MerchantDetailsResponse>> getAllMerchants() {
        List<MerchantDetailsResponse> merchants = merchantRepository
                .findAll()
                .stream()
                .map(merchantMapper::toDetailsResponse)
                .toList();

        return ApiResponse.success(
                "Merchants fetched successfully",
                merchants
        );
    }

    @Transactional
    @Override
    public ApiResponse<Void> deactivateMerchant(String merchantId) {
        Merchant merchant = merchantFinder.getByMerchantId(merchantId);
        if (!merchant.isActive()) {
            return ApiResponse.success("Merchant is already deactivated");
        }
        merchant.setActive(false);
        return ApiResponse.success("Merchant deactivated successfully");
    }
}