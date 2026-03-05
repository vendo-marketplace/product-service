package com.vendo.product_service.test_utils.security;

import com.vendo.domain.user.common.type.UserRole;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;

public class SecurityContextService {

    public static Authentication initializeAuth(UserRole role) {
        return new UsernamePasswordAuthenticationToken(
                new Object(),
                null,
                Collections.singletonList(new SimpleGrantedAuthority(role.name()))
        );
    }
}
