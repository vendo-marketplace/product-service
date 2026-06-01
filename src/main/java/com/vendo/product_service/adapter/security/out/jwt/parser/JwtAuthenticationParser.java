package com.vendo.product_service.adapter.security.out.jwt.parser;

import com.vendo.product_service.adapter.security.out.jwt.JwtService;
import com.vendo.product_service.adapter.security.out.props.JwtProperties;
import com.vendo.product_service.domain.user.User;
import com.vendo.security_lib.type.UserTokenClaim;
import com.vendo.user_lib.type.UserStatus;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtAuthenticationParser implements AuthenticationParser {

    private final JwtService jwtService;

    private final JwtProperties jwtProperties;

    @Override
    public User extract(String token) {
        Claims claims = jwtService.extractAllClaims(token, jwtProperties.getSecret().key());

        String id = extractId(claims);
        List<String> roles = JwtUtils.extractRoles(claims, UserTokenClaim.ROLES.getClaim());
        Boolean verified = extractEmailVerified(claims);
        UserStatus status = extractStatus(claims);

        return new User(id, status, User.toRoles(roles), verified);
    }

    private String extractId(Claims claims) {
        String id = claims.get(UserTokenClaim.ID.getClaim(), String.class);

        if (id == null || id.isBlank()) {
            log.error("Id claim is not present.");
            throw new BadCredentialsException("Invalid token.");
        }

        return id;
    }

    private Boolean extractEmailVerified(Claims claims) {
        return claims.get(UserTokenClaim.VERIFIED.getClaim(), Boolean.class);
    }

    private UserStatus extractStatus(Claims claims) {
        String status = claims.get(UserTokenClaim.STATUS.getClaim(), String.class);

        try {
            return UserStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            log.error("Invalid status claim, " + status + " is not type of UserStatus.");
            throw new BadCredentialsException("Invalid token.");
        }
    }
}
