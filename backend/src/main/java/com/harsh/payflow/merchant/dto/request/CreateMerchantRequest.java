package com.harsh.payflow.merchant.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record CreateMerchantRequest(

        @NotBlank(message = "Business name is required")
        String businessName,

        @Email(message = "Invalid email")
        @NotBlank(message = "Email is required")
        String email

) {
}