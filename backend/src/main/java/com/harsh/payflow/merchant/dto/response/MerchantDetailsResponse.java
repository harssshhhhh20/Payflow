package com.harsh.payflow.merchant.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record MerchantDetailsResponse(

        String merchantId,
        String businessName,
        String email,
        boolean active,
        LocalDateTime createdAt

) {}
