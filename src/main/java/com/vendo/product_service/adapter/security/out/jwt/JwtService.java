package com.vendo.product_service.adapter.security.out.jwt;

import com.vendo.product_service.adapter.security.in.config.JwtProperties;
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
        return parseSignedClaims(token).getPayload();
    }

    private Key getSignInKey() {
        try {
            return Keys.hmacShaKeyFor(jwtProperties.getSecret().key().getBytes(StandardCharsets.UTF_8));
        } catch (NullPointerException e) {
            log.error(e.getMessage());
            throw new BadCredentialsException("Error while signing secret key.");
        }
    }

    private Jws<Claims> parseSignedClaims(String token) throws JwtException {
        return Jwts.parser()
                .verifyWith((SecretKey) getSignInKey())
                .build()
                .parseSignedClaims(token);
    }
}
