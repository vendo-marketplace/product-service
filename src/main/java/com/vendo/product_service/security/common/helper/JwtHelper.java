package com.vendo.product_service.security.common.helper;

import com.vendo.domain.user.common.type.UserStatus;
import com.vendo.product_service.security.common.config.JwtProperties;
import com.vendo.security.common.exception.InvalidTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.List;
import java.util.function.Supplier;

import static com.vendo.security.common.type.TokenClaim.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtHelper {

    private final JwtProperties jwtProperties;

    public Claims extractAllClaims(String token) {
        return parseSignedClaims(token).getPayload();
    }

    public Key getSignInKey() {
        return Keys.hmacShaKeyFor(jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8));
    }

    private Jws<Claims> parseSignedClaims(String token) throws JwtException {
        return Jwts.parser()
                .verifyWith((SecretKey) getSignInKey())
                .build()
                .parseSignedClaims(token);
    }

    public String extractUserId(Claims claims) {
        return extractClaim(() -> String.valueOf(claims.get(USER_ID_CLAIM.getClaim())));
    }

    public UserStatus extractUserStatus(Claims claims) {
        Supplier<UserStatus> userStatusExtractor = () -> {
            try {
                Object status = claims.get(STATUS_CLAIM.getClaim());
                return UserStatus.valueOf(String.valueOf(status));
            } catch (IllegalArgumentException e) {
                log.error("Invalid status type: ", e);
                throw new InvalidTokenException("Invalid token.");
            }
        };

        return extractClaim(userStatusExtractor);
    }

    public List<SimpleGrantedAuthority> extractAuthorities(Claims claims) {
        Supplier<List<SimpleGrantedAuthority>> authoritiesExtractor = () -> {
            Object rolesClaim = claims.get(ROLES_CLAIM.getClaim());

            if (rolesClaim instanceof List<?> roles) {
                return roles.stream()
                        .map(Object::toString)
                        .map(SimpleGrantedAuthority::new)
                        .toList();
            }

            log.error("Invalid roles type.");
            throw new InvalidTokenException("Invalid token.");
        };

        return extractClaim(authoritiesExtractor);
    }

    private <T> T extractClaim(Supplier<? extends T> extractor) {
        try {
            return extractor.get();
        } catch (ClassCastException | NullPointerException e) {
            log.error("Invalid claim type: ", e);
            throw new InvalidTokenException("Invalid token.");
        }
    }
}
