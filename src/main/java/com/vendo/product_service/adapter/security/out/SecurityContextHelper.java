package com.vendo.product_service.adapter.security.out;

import com.vendo.product_service.adapter.security.out.jwt.parser.TokenClaims;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityContextHelper {

    public static TokenClaims getAuthClaims() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof TokenClaims claims)) {
            throw new AuthenticationCredentialsNotFoundException("Unauthorized.");
        }

        return claims;
    }
}