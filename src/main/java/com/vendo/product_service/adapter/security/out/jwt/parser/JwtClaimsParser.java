package com.vendo.product_service.adapter.security.out.jwt.parser;

import com.vendo.product_service.adapter.security.out.jwt.JwtService;
import com.vendo.security_lib.exception.InvalidTokenException;
import com.vendo.security_lib.type.UserTokenClaim;
import com.vendo.user_lib.type.UserStatus;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.RequiredTypeException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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
        return claims.get(UserTokenClaim.ID.getClaim(), String.class);
    }

    private List<String> extractRoles(Claims claims) {
        Object rawRoles = claims.get(UserTokenClaim.ROLES.getClaim());

        if (rawRoles instanceof List<?> list) {

            if (list.stream().allMatch(String.class::isInstance)) {
                return list.stream()
                        .map(String.class::cast)
                        .toList();
            }
        }

        throw new InvalidTokenException("Invalid roles.");
    }

    private Boolean extractEmailVerification(Claims claims) {
        return claims.get(UserTokenClaim.VERIFIED.getClaim(), Boolean.class);
    }

    private UserStatus extractStatus(Claims claims) {
        return claims.get(UserTokenClaim.STATUS.getClaim(), UserStatus.class);
    }
}
