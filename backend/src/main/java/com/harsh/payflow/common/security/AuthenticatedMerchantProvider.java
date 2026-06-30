package com.harsh.payflow.common.security;

import com.harsh.payflow.common.security.principal.MerchantPrincipal;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthenticatedMerchantProvider {

    public MerchantPrincipal getAuthenticatedMerchant() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null ||
                !(authentication.getPrincipal() instanceof MerchantPrincipal principal)) {
            throw new AccessDeniedException("Merchant is not authenticated.");
        }

        return principal;
    }

}