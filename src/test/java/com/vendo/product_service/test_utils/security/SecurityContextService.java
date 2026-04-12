package com.vendo.product_service.test_utils.security;

import com.vendo.product_service.adapter.security.out.jwt.parser.UserTokenClaims;
import com.vendo.user_lib.type.UserRole;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;

public class SecurityContextService {

    public static Authentication initializeAuth(UserRole role) {
        return new UsernamePasswordAuthenticationToken(
                new UserTokenClaims(null, null, null, false),
                null,
                Collections.singletonList(new SimpleGrantedAuthority(role.name()))
        );
    }
}
