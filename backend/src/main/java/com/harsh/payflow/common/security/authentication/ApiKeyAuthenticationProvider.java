package com.harsh.payflow.common.security.authentication;

import com.harsh.payflow.common.security.principal.MerchantPrincipal;
import com.harsh.payflow.merchant.entity.Merchant;
import com.harsh.payflow.merchant.repository.MerchantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ApiKeyAuthenticationProvider implements AuthenticationProvider {

    private final MerchantRepository merchantRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(
            Authentication authentication
    ) throws AuthenticationException {

        ApiKeyAuthenticationToken token =
                (ApiKeyAuthenticationToken) authentication;

        String apiKey =
                (String) token.getCredentials();

        if (apiKey == null || apiKey.isBlank()) {
            throw new BadCredentialsException("API key is missing");
        }

        if (!apiKey.startsWith("pf_live_")) {
            throw new BadCredentialsException("Invalid API key");
        }
        String apiKeyPrefix = apiKey.substring(
                "pf_live_".length(),
                "pf_live_".length() + 8
        );

        Merchant merchant = merchantRepository
                .findByApiKeyPrefix(apiKeyPrefix)
                .orElseThrow(() ->
                        new BadCredentialsException("Invalid API key")
                );
        if (!passwordEncoder.matches(
                apiKey,
                merchant.getApiKeyHash()
        )) {
            throw new BadCredentialsException("Invalid API key");
        }

        MerchantPrincipal principal = new MerchantPrincipal(
                merchant.getMerchantId(),
                merchant.getBusinessName(),
                merchant.getEmail()
        );

        return new ApiKeyAuthenticationToken(principal);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return ApiKeyAuthenticationToken.class.isAssignableFrom(authentication);
    }

}