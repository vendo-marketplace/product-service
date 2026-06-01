package com.vendo.product_service.test_utils.security;

import com.vendo.product_service.adapter.security.out.jwt.parser.TokenClaims;
import com.vendo.product_service.domain.user.User;
import com.vendo.user_lib.type.UserRole;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.List;

public class SecurityContextService {

    public static Authentication initializeAuth(User authUser) {
        List<SimpleGrantedAuthority> authorities = authUser.roles().stream()
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .toList();

        return new UsernamePasswordAuthenticationToken(
                authUser,
                null,
                authorities);
    }

    public static Authentication initializeAuth(TokenClaims claims) {
        String role = claims.roles().get(0);
        if (role == null || role.isBlank()) role = UserRole.USER.name();

        return new UsernamePasswordAuthenticationToken(
                claims,
                null,
                Collections.singletonList(new SimpleGrantedAuthority(role))
        );
    }

}
