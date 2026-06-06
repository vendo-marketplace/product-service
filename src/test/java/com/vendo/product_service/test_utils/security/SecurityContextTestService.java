package com.vendo.product_service.test_utils.security;

import com.vendo.product_service.adapter.security.out.jwt.parser.TokenClaims;
import com.vendo.product_service.domain.user.User;
import com.vendo.security_lib.type.UserHeaders;
import com.vendo.user_lib.type.UserRole;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;

import static com.vendo.core_lib.constants.Delimiters.COMMA_DELIMITER;

public class SecurityContextTestService {

    public static Authentication initializeAuth(TokenClaims claims) {
        Set<String> roles = claims.roles();
        if (roles == null || roles.isEmpty()) roles = Set.of(UserRole.USER.name());

        return new UsernamePasswordAuthenticationToken(
                claims,
                null,
                roles.stream().map(SimpleGrantedAuthority::new).toList()
        );
    }

    public static Authentication initializeAuth(User user) {
        Set<UserRole> roles = user.roles();
        if (roles == null || roles.isEmpty()) roles = Set.of(UserRole.USER);

        return new UsernamePasswordAuthenticationToken(
                user,
                null,
                roles.stream().map(role -> new SimpleGrantedAuthority(role.name())).toList()
        );
    }

    public static HttpHeaders extractHeaders(User user) {
        HttpHeaders httpHeaders = new HttpHeaders();

        httpHeaders.add(UserHeaders.ID.getHeader(), user.id());
        httpHeaders.add(UserHeaders.EMAIL.getHeader(), user.email());
        httpHeaders.add(UserHeaders.ROLES.getHeader(), String.join(COMMA_DELIMITER, user.toRoleNames()));
        httpHeaders.add(UserHeaders.EMAIL_VERIFIED.getHeader(), String.valueOf(user.emailVerified()));
        httpHeaders.add(UserHeaders.STATUS.getHeader(), String.valueOf(user.status()));

        return httpHeaders;
    }
}
