package com.vendo.product_service.adapter.security.out.jwt.parser;

import com.vendo.product_service.adapter.security.out.jwt.JwtService;
import com.vendo.product_service.adapter.security.out.props.InternalJwtProperties;
import com.vendo.product_service.adapter.security.out.props.JwtProperties;
import com.vendo.security_lib.type.InternalTokenClaim;
import com.vendo.security_lib.type.UserTokenClaim;
import com.vendo.user_lib.type.UserStatus;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtClaimsParser implements TokenClaimsParser {

    private final JwtService jwtService;

    private final JwtProperties jwtProperties;
    private final InternalJwtProperties internalJwtProperties;

    @Override
    public TokenClaims extract(String token) {
        Claims claims = jwtService.extractAllClaims(token, jwtProperties.getSecret().key());

        String id = extractId(claims);
        List<String> roles = extractRoles(claims, UserTokenClaim.ROLES.getClaim());

        Boolean verification = extractEmailVerification(claims);
        UserStatus status = extractStatus(claims);

        return new TokenClaims(id, status, roles, verification);
    }

    @Override
    public InternalTokenClaims extractInternal(String token) {
        try {
            Claims claims = jwtService.extractAllClaims(token, internalJwtProperties.getKey());

            List<String> roles = extractRoles(claims, InternalTokenClaim.ROLES.getClaim());
            Set<String> audience = extractAudience(claims);

            return new InternalTokenClaims(claims.getSubject(), roles, audience);
        } catch (ExpiredJwtException e) {
            log.error(e.getMessage());
            throw new BadCredentialsException("Expired token.");
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BadCredentialsException("Invalid token.");
        }
    }

    private String extractId(Claims claims) {
        String id = claims.get(UserTokenClaim.ID.getClaim(), String.class);

        if (id == null || id.isBlank()) {
            log.error("Id claim is not present.");
            throw new BadCredentialsException("Invalid token.");
        }

        return id;
    }

    private List<String> extractRoles(Claims claims, String rolesClaim) {
        Object rawRoles = claims.get(rolesClaim);
        AuthenticationException e = new BadCredentialsException("Invalid token.");

        if (rawRoles instanceof List<?> list && !list.isEmpty()) {
            if (list.stream().allMatch(String.class::isInstance)) {

                return list.stream()
                        .map(String.class::cast)
                        .toList();
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

    private Set<String> extractAudience(Claims claims) {
        Set<String> audience = claims.getAudience();

        if (CollectionUtils.isEmpty(audience)) {
            log.error("Empty audience.");
            throw new BadCredentialsException("Invalid token.");
        }

        return audience;
    }
}
