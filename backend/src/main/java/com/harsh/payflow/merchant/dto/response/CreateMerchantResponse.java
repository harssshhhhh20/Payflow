package com.harsh.payflow.merchant.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CreateMerchantResponse(

        String merchantId,

        String businessName,

        String email,

        String apiKey,

        boolean active,

        LocalDateTime createdAt

) {
}