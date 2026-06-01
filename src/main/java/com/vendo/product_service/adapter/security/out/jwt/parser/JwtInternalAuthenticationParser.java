package com.vendo.product_service.adapter.security.out.jwt.parser;

import com.vendo.product_service.adapter.security.out.jwt.JwtService;
import com.vendo.product_service.adapter.security.out.props.InternalJwtProperties;
import com.vendo.security_lib.type.InternalTokenClaim;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtInternalAuthenticationParser implements InternalAuthenticationParser{

    private final JwtService jwtService;
    private final InternalJwtProperties internalJwtProperties;

    @Override
    public TokenClaims extract(String token) {
        try {
            Claims claims = jwtService.extractAllClaims(token, internalJwtProperties.getKey());

            List<String> roles = JwtUtils.extractRoles(claims, InternalTokenClaim.ROLES.getClaim());
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

    private Set<String> extractAudience(Claims claims) {
        Set<String> audience = claims.getAudience();

        if (CollectionUtils.isEmpty(audience)) {
            log.error("Empty audience.");
            throw new BadCredentialsException("Invalid token.");
        }

        return audience;
    }

}
