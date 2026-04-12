package com.vendo.product_service.adapter.security.out.jwt;

import com.vendo.product_service.adapter.security.out.config.JwtProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtService {

    private final JwtProperties jwtProperties;

    public Claims extractAllClaims(String token) {
        try {
            return parseSignedClaims(token).getPayload();
        } catch (ExpiredJwtException e) {
            log.error(e.getMessage());
            throw new BadCredentialsException("Token expired.");
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BadCredentialsException("Invalid token.");
        }
    }

    private Key getSignInKey() {
        try {
            return Keys.hmacShaKeyFor(jwtProperties.getSecret().key().getBytes(StandardCharsets.UTF_8));
        } catch (NullPointerException e) {
            log.error(e.getMessage());
            throw new BadCredentialsException("Invalid token.");
        }
    }

    private Jws<Claims> parseSignedClaims(String token) throws JwtException {
        return Jwts.parser()
                .verifyWith((SecretKey) getSignInKey())
                .build()
                .parseSignedClaims(token);
    }
}
