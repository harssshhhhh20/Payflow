package com.harsh.payflow.common.security.principal;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MerchantPrincipal {

    private final String merchantId;

    private final String businessName;

    private final String email;

}