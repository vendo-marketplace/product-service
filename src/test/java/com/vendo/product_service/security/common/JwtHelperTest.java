package com.vendo.product_service.security.common;

import com.vendo.domain.user.common.type.UserStatus;
import com.vendo.product_service.security.common.config.JwtProperties;
import com.vendo.product_service.security.common.helper.JwtHelper;
import com.vendo.security.common.exception.InvalidTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.List;

import static com.vendo.security.common.type.TokenClaim.ROLES_CLAIM;
import static com.vendo.security.common.type.TokenClaim.STATUS_CLAIM;
import static org.assertj.core.api.Assertions.assertThat;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;


class JwtHelperTest {

    private JwtProperties jwtProperties;
    private JwtHelper jwtHelper;

    @BeforeEach
    void setUp() {
        jwtProperties = mock(JwtProperties.class);
        when(jwtProperties.getSecretKey())
                .thenReturn("super-secret-key-12345678901234567890");
        jwtHelper = new JwtHelper(jwtProperties);
    }

    @Test
    void shouldExtractSubject_whenSubjectPresent() {
        SecretKey key = (SecretKey) jwtHelper.getSignInKey();

        String token = Jwts.builder()
                .setSubject("user-123")
                .signWith(key)
                .compact();

        Claims claims = jwtHelper.extractAllClaims(token);
        String subject = jwtHelper.extractSubject(claims);

        assertThat(subject).isEqualTo("user-123");
    }

    @Test
    void shouldThrowInvalidTokenException_whenSubjectMissing() {
        SecretKey key = (SecretKey) jwtHelper.getSignInKey();
        String token = Jwts.builder()
                .claim("meow", "value")
                .signWith(key)
                .compact();

        Claims claims = jwtHelper.extractAllClaims(token);

        assertThatThrownBy(() -> jwtHelper.extractSubject(claims))
                .isInstanceOf(InvalidTokenException.class);
    }

    @Test
    void shouldExtractAuthorities_whenRolesValid() {
        Key key = jwtHelper.getSignInKey();

        String token = Jwts.builder()
                .claim(ROLES_CLAIM.getClaim(), List.of("ROLE_USER", "ROLE_ADMIN"))
                .signWith(key)
                .compact();

        Claims claims = jwtHelper.extractAllClaims(token);
        List<SimpleGrantedAuthority> result = jwtHelper.extractAuthorities(claims);

        assertThat(result)
                .extracting(SimpleGrantedAuthority::getAuthority)
                .containsExactly("ROLE_USER", "ROLE_ADMIN");
    }

    @Test
    void shouldThrowInvalidException_whenRolesInvalidType() {
        Key key = jwtHelper.getSignInKey();

        String token = Jwts.builder()
                .claim(ROLES_CLAIM.getClaim(), "invalid")
                .signWith(key)
                .compact();

        Claims claims = jwtHelper.extractAllClaims(token);

        assertThatThrownBy(() -> jwtHelper.extractAuthorities(claims))
                .isInstanceOf(InvalidTokenException.class);
    }

    @Test
    void shouldExtractUserStatus_whenStatusValid() {
        Key key = jwtHelper.getSignInKey();

        String token = Jwts.builder()
                .claim(STATUS_CLAIM.getClaim(), "ACTIVE")
                .signWith(key)
                .compact();

        Claims claims = jwtHelper.extractAllClaims(token);
        var status = jwtHelper.extractUserStatus(claims);

        assertThat(status).isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    void shouldThrowInvalidTokenException_whenStatusInvalid() {
        Key key = jwtHelper.getSignInKey();

        String token = Jwts.builder()
                .claim(STATUS_CLAIM.getClaim(), "null")
                .signWith(key)
                .compact();

        Claims claims = jwtHelper.extractAllClaims(token);

        assertThatThrownBy(() -> jwtHelper.extractUserStatus(claims))
                .isInstanceOf(InvalidTokenException.class);
    }

    @Test
    void shouldCreateSignInKey() {
        Key key = jwtHelper.getSignInKey();
        assertThat(key).isInstanceOf(SecretKey.class);
    }
}
