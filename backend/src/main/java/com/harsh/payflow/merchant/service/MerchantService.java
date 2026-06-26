package com.harsh.payflow.merchant.service;

import com.harsh.payflow.common.response.ApiResponse;
import com.harsh.payflow.merchant.dto.request.CreateMerchantRequest;
import com.harsh.payflow.merchant.dto.response.CreateMerchantResponse;
import com.harsh.payflow.merchant.dto.response.MerchantDetailsResponse;

public interface MerchantService {

    ApiResponse<CreateMerchantResponse> createMerchant(CreateMerchantRequest request);
    ApiResponse<MerchantDetailsResponse> getMerchant(String merchantId);

}