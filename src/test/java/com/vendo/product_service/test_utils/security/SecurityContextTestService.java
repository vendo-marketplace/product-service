package com.vendo.product_service.test_utils.security;

import com.vendo.product_service.domain.user.User;
import com.vendo.security_lib.type.AuthHeader;
import com.vendo.security_starter.jwt.parser.TokenClaims;
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

        httpHeaders.add(AuthHeader.ID.getHeader(), user.id());
        httpHeaders.add(AuthHeader.EMAIL.getHeader(), user.email());
        httpHeaders.add(AuthHeader.ROLES.getHeader(), String.join(COMMA_DELIMITER, user.toRoleNames()));
        httpHeaders.add(AuthHeader.EMAIL_VERIFIED.getHeader(), String.valueOf(user.emailVerified()));
        httpHeaders.add(AuthHeader.STATUS.getHeader(), String.valueOf(user.status()));

        return httpHeaders;
    }
}
