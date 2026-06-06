package com.vendo.product_service.adapter.security.out.jwt.parser;

import com.vendo.product_service.adapter.security.out.jwt.JwtService;
import com.vendo.product_service.adapter.security.out.props.JwtProperties;
import com.vendo.security_lib.type.InternalClaims;
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
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtClaimsParser implements TokenClaimsParser {

    private final JwtService jwtService;

    private final JwtProperties jwtProperties;

    @Override
    public TokenClaims extract(String token) {
        try {
            Claims claims = jwtService.extractAllClaims(token, jwtProperties.internal().key());

            Set<String> roles = extractRoles(claims, InternalClaims.ROLES.getClaim());
            Set<String> audience = extractAudience(claims);

            return new TokenClaims(claims.getSubject(), roles, audience);
        } catch (ExpiredJwtException e) {
            log.error(e.getMessage());
            throw new BadCredentialsException("Expired token.");
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BadCredentialsException("Invalid token.");
        }
    }

    private Set<String> extractRoles(Claims claims, String rolesClaim) {
        Object rawRoles = claims.get(rolesClaim);
        AuthenticationException e = new BadCredentialsException("Invalid token.");

        if (rawRoles instanceof List<?> list && !list.isEmpty()) {
            if (list.stream().allMatch(String.class::isInstance)) {

                return list.stream()
                        .map(String.class::cast)
                        .collect(Collectors.toSet());
            }
        }

        log.error("Invalid roles claim.");
        throw e;
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
