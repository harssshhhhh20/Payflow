package com.harsh.payflow.merchant.controller;

import com.harsh.payflow.common.response.ApiResponse;
import com.harsh.payflow.merchant.dto.request.CreateMerchantRequest;
import com.harsh.payflow.merchant.dto.response.CreateMerchantResponse;
import com.harsh.payflow.merchant.dto.response.MerchantDetailsResponse;
import com.harsh.payflow.merchant.service.MerchantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/merchants")
@RequiredArgsConstructor
public class MerchantController {

    private final MerchantService merchantService;

    @PostMapping
    public ResponseEntity<ApiResponse<CreateMerchantResponse>> createMerchant(
            @Valid @RequestBody CreateMerchantRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        merchantService.createMerchant(request)
                );
    }

    @GetMapping("/{merchantId}")
    public ResponseEntity<ApiResponse<MerchantDetailsResponse>> getMerchant(
            @PathVariable String merchantId
    ) {
        return ResponseEntity.ok(
                merchantService.getMerchant(merchantId)
        );
    }

}