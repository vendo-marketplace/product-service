package com.vendo.product_service.adapter.security.out.jwt;

import com.vendo.product_service.adapter.security.in.config.JwtProperties;
import com.vendo.security_lib.exception.InvalidTokenException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
        } catch (JwtException e) {
            throw new InvalidTokenException("Couldn't parse claims from token: " + e.getMessage());
        }
    }

    private Key getSignInKey() {
        return Keys.hmacShaKeyFor(jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8));
    }

    private Jws<Claims> parseSignedClaims(String token) throws JwtException {
        return Jwts.parser()
                .verifyWith((SecretKey) getSignInKey())
                .build()
                .parseSignedClaims(token);
    }
}
