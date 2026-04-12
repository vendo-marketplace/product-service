package com.vendo.product_service.adapter.security.out.jwt.parser;

import com.vendo.product_service.adapter.security.out.jwt.JwtService;
import com.vendo.security_lib.type.UserTokenClaim;
import com.vendo.user_lib.type.UserStatus;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtClaimsParserUser implements UserTokenClaimsParser {

    private final JwtService jwtService;

    @Override
    public UserTokenClaims extract(String token) {
        Claims claims = jwtService.extractAllClaims(token);

        String id = extractId(claims);
        List<String> roles = extractRoles(claims);

        Boolean verification = extractEmailVerification(claims);
        UserStatus status = extractStatus(claims);

        return new UserTokenClaims(id, status, roles, verification);
    }

    private String extractId(Claims claims) {
        String id = claims.get(UserTokenClaim.ID.getClaim(), String.class);

        if (id == null || id.isBlank()) {
            log.error("Id claim is not present.");
            throw new BadCredentialsException("Invalid token.");
        }

        return id;
    }

    private List<String> extractRoles(Claims claims) {
        Object rawRoles = claims.get(UserTokenClaim.ROLES.getClaim());
        AuthenticationException e = new BadCredentialsException("Invalid token.");

        if (rawRoles instanceof List<?> list) {
            if (list.stream().allMatch(String.class::isInstance)) {
                List<String> roles = list.stream()
                        .map(String.class::cast)
                        .toList();

                if (roles.isEmpty()) {
                    log.error("Invalid roles claim.");
                    throw e;
                }
                return roles;
            }
        }

        log.error("Invalid roles claim.");
        throw e;
    }

    private Boolean extractEmailVerification(Claims claims) {
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
