package com.harsh.payflow.merchant.mapper;

import com.harsh.payflow.merchant.dto.request.CreateMerchantRequest;
import com.harsh.payflow.merchant.dto.response.MerchantDetailsResponse;
import com.harsh.payflow.merchant.dto.response.CreateMerchantResponse;
import com.harsh.payflow.merchant.entity.Merchant;
import org.springframework.stereotype.Component;

@Component
public class MerchantMapper {

    public Merchant toEntity(
            CreateMerchantRequest request,
            String merchantId,
            String apiKeyHash
    ) {

        return Merchant.builder()
                .merchantId(merchantId)
                .businessName(request.businessName())
                .email(request.email())
                .apiKeyHash(apiKeyHash)
                .active(true)
                .build();
    }

    public CreateMerchantResponse toResponse(
            Merchant merchant,
            String apiKey
    ) {

        return CreateMerchantResponse.builder()
                .merchantId(merchant.getMerchantId())
                .businessName(merchant.getBusinessName())
                .email(merchant.getEmail())
                .apiKey(apiKey)
                .active(merchant.isActive())
                .createdAt(merchant.getCreatedAt())
                .build();
    }
    public MerchantDetailsResponse toDetailsResponse(Merchant merchant){
        return MerchantDetailsResponse.builder()
                .merchantId(merchant.getMerchantId())
                .businessName(merchant.getBusinessName())
                .email(merchant.getEmail())
                .active(merchant.isActive())
                .createdAt(merchant.getCreatedAt())
                .build();
    }
}