package com.vendo.product_service.test_utils.security;

import com.vendo.product_service.adapter.security.out.jwt.parser.UserTokenClaims;
import com.vendo.user_lib.type.UserRole;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;

public class SecurityContextService {

    public static Authentication initializeAuth(UserTokenClaims claims) {
        String role = claims.roles().get(0);
        if (role == null || role.isBlank()) role = UserRole.USER.name();

        return new UsernamePasswordAuthenticationToken(
                claims,
                null,
                Collections.singletonList(new SimpleGrantedAuthority(role))
        );
    }
}
