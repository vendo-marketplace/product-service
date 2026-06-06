package com.vendo.product_service.adapter.security.in.filter;

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static com.vendo.security_lib.constants.AuthConstants.BEARER_PREFIX;

final class FilterHelper {

    static void addAuthToContext(Object principal, List<String> roles) {
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(principal, null, toAuthorities(roles));
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }

    static String getTokenFromRequest(String authorization) {
        if (authorization == null) {
            throw new AuthenticationCredentialsNotFoundException("Unauthorized.");
        } else if (!authorization.startsWith(BEARER_PREFIX)) {
            throw new BadCredentialsException("Invalid token.");
        }

        return authorization.substring(BEARER_PREFIX.length());
    }

    private static List<SimpleGrantedAuthority> toAuthorities(List<String> roles) {
        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
    }
}
