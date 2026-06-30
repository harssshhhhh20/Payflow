package com.harsh.payflow.common.security.authentication;

import com.harsh.payflow.common.security.principal.MerchantPrincipal;
import org.springframework.security.authentication.AbstractAuthenticationToken;

import java.util.Collections;

public class ApiKeyAuthenticationToken extends AbstractAuthenticationToken {

    private final Object principal;
    private final String apiKey;

    public ApiKeyAuthenticationToken(String apiKey) {
        super(Collections.emptyList());
        this.principal = null;
        this.apiKey = apiKey;
        setAuthenticated(false);
    }

    public ApiKeyAuthenticationToken(
            MerchantPrincipal principal
    ) {
        super(Collections.emptyList());
        this.principal = principal;
        this.apiKey = null;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return apiKey;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }
}