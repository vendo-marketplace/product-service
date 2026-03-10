package com.vendo.product_service.adapter.security.out.jwt.parser;

import com.vendo.product_service.adapter.security.out.jwt.JwtService;
import com.vendo.security_lib.exception.InvalidTokenException;
import com.vendo.security_lib.type.UserTokenClaim;
import com.vendo.user_lib.type.UserStatus;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.RequiredTypeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtClaimsParser implements TokenClaimsParser {

    private final JwtService jwtService;

    @Override
    public TokenClaims extract(String token) {
        Claims claims = jwtService.extractAllClaims(token);

        try {
            String id = extractId(claims);
            List<String> roles = extractRoles(claims);
            Boolean verification = extractEmailVerification(claims);
            UserStatus status = extractStatus(claims);
            return new TokenClaims(id, status, roles, verification);
        } catch (RequiredTypeException e) {
            throw new InvalidTokenException("Invalid token.");
        }
    }

    private String extractId(Claims claims) {
        String id = claims.get(UserTokenClaim.ID.getClaim(), String.class);

        if (id == null || id.isBlank()) {
            throw new InvalidTokenException("Invalid token.");
        }

        return id;
    }

    private List<String> extractRoles(Claims claims) {
        Object rawRoles = claims.get(UserTokenClaim.ROLES.getClaim());
        RuntimeException e = new InvalidTokenException("Invalid token.");

        if (rawRoles instanceof List<?> list) {
            if (list.stream().allMatch(String.class::isInstance)) {
                List<String> roles = list.stream()
                        .map(String.class::cast)
                        .toList();

                if (roles.isEmpty()) throw e;
                return roles;
            }
        }

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
            throw new InvalidTokenException("Invalid token.");
        }
    }
}
