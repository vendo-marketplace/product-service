package com.vendo.product_service.security.common;

import com.vendo.domain.user.common.type.UserStatus;
import com.vendo.product_service.common.builder.JwtPayloadBuilder;
import com.vendo.product_service.common.dto.JwtPayload;
import com.vendo.product_service.security.common.config.JwtProperties;
import com.vendo.product_service.security.common.helper.JwtHelper;
import com.vendo.product_service.service.JwtService;
import com.vendo.security.common.exception.InvalidTokenException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

import static com.vendo.security.common.type.TokenClaim.ROLES_CLAIM;
import static com.vendo.security.common.type.TokenClaim.STATUS_CLAIM;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;

@SpringBootTest
@ActiveProfiles("test")
class JwtHelperTest {

    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private JwtHelper jwtHelper;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private JwtPayloadBuilder jwtPayloadBuilder;

    private static final String VALID_SUBJECT = "JWT_USER_SUBJECT";
    private static final String WRONG_SECRET_KEY = "wrong-secret-key-123456789012345678901234567890";

    @BeforeEach
    void setUp() {
    }

    private String createValidToken() {
        JwtPayload payload = jwtPayloadBuilder.buildValidUserJwtPayload().build();
        return jwtService.generateAccessToken(payload);
    }

    private String createExpiredToken() {
        SecretKey key = jwtService.getSecretKey();
        return Jwts.builder()
                .subject("subject")
                .expiration(new Date(System.currentTimeMillis() - 10000))
                .issuedAt(new Date(System.currentTimeMillis() - 20000))
                .signWith(key)
                .compact();
    }

    private String createTokenWithWrongSignature() {
        SecretKey wrongKey = Keys.hmacShaKeyFor(WRONG_SECRET_KEY.getBytes());
        return Jwts.builder()
                .subject("user-123")
                .expiration(new Date(System.currentTimeMillis() + 60000))
                .issuedAt(new Date())
                .signWith(wrongKey)
                .compact();
    }

    @Nested
    @DisplayName("Token Parsing Tests")
    class TokenParsingTests {

        @Test
        @DisplayName("Should successfully parse valid token")
        void shouldParseValidToken() {
            String token = createValidToken();

            Claims claims = jwtHelper.extractAllClaims(token);

            assertThat(claims).isNotNull();
            assertThat(claims.getSubject()).isEqualTo(VALID_SUBJECT);
        }

        @Test
        @DisplayName("Should throw ExpiredJwtException when token is expired")
        void shouldThrowExpiredJwtException_whenTokenExpired() {
            String expiredToken = createExpiredToken();

            assertThatThrownBy(() -> jwtHelper.extractAllClaims(expiredToken))
                    .isInstanceOf(ExpiredJwtException.class);
        }

        @Test
        @DisplayName("Should throw SignatureException when token has invalid signature")
        void shouldThrowSignatureException_whenInvalidSignature() {
            String tokenWithWrongSignature = createTokenWithWrongSignature();

            assertThatThrownBy(() -> jwtHelper.extractAllClaims(tokenWithWrongSignature))
                    .isInstanceOf(SignatureException.class);
        }

        @Test
        @DisplayName("Should throw MalformedJwtException when token is malformed")
        void shouldThrowMalformedJwtException_whenTokenIsMalformed() {
            String malformedToken = "not.a.valid.token";

            assertThatThrownBy(() -> jwtHelper.extractAllClaims(malformedToken))
                    .isInstanceOf(MalformedJwtException.class);
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when token is null")
        void shouldThrowIllegalArgumentException_whenTokenIsNull() {
            assertThatThrownBy(() -> jwtHelper.extractAllClaims(null))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when token is empty")
        void shouldThrowIllegalArgumentException_whenTokenIsEmpty() {
            assertThatThrownBy(() -> jwtHelper.extractAllClaims(""))
                    .isInstanceOf(IllegalArgumentException.class);
        }


        @Test
        @DisplayName("Should handle token with future issuedAt date")
        void shouldHandleTokenWithFutureIssuedAt() {
            SecretKey key = jwtService.getSecretKey();
            String futureToken = Jwts.builder()
                    .subject("test-subject")
                    .issuedAt(new Date(System.currentTimeMillis() + 60000))
                    .expiration(new Date(System.currentTimeMillis() + 120000))
                    .signWith(key)
                    .compact();

            assertThatCode(() -> jwtHelper.extractAllClaims(futureToken))
                    .doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("Subject Extraction Tests")
    class SubjectExtractionTests {

        @Test
        @DisplayName("Should extract subject when subject is present")
        void shouldExtractSubject_whenSubjectPresent() {
            String token = createValidToken();
            Claims claims = jwtHelper.extractAllClaims(token);

            String subject = jwtHelper.extractSubject(claims);

            assertThat(subject).isEqualTo(VALID_SUBJECT);
        }

        @Test
        @DisplayName("Should throw InvalidTokenException when subject is missing")
        void shouldThrowInvalidTokenException_whenSubjectMissing() {
            SecretKey key = jwtService.getSecretKey();
            String tokenWithoutSubject = Jwts.builder()
                    .expiration(new Date(System.currentTimeMillis() + 60000))
                    .issuedAt(new Date())
                    .signWith(key)
                    .compact();
            Claims claims = jwtHelper.extractAllClaims(tokenWithoutSubject);

            assertThatThrownBy(() -> jwtHelper.extractSubject(claims))
                    .isInstanceOf(InvalidTokenException.class);
        }

        @Test
        @DisplayName("Should throw InvalidTokenException when subject is empty")
        void shouldThrowInvalidTokenException_whenSubjectEmpty() {
            SecretKey key = jwtService.getSecretKey();
            String tokenWithEmptySubject = Jwts.builder()
                    .subject("")
                    .expiration(new Date(System.currentTimeMillis() + 60000))
                    .issuedAt(new Date())
                    .signWith(key)
                    .compact();
            Claims claims = jwtHelper.extractAllClaims(tokenWithEmptySubject);

            assertThatThrownBy(() -> jwtHelper.extractSubject(claims))
                    .isInstanceOf(InvalidTokenException.class);
        }
    }

    @Nested
    @DisplayName("Authorities Extraction Tests")
    class AuthoritiesExtractionTests {

        @Test
        @DisplayName("Should extract authorities when roles are valid")
        void shouldExtractAuthorities_whenRolesValid() {
            String token = createValidToken();
            Claims claims = jwtHelper.extractAllClaims(token);

            var authorities = jwtHelper.extractAuthorities(claims);

            assertThat(authorities)
                    .hasSize(1)
                    .extracting(SimpleGrantedAuthority::getAuthority)
                    .containsExactly("ROLE_USER");
        }

        @Test
        @DisplayName("Should throw InvalidTokenException when roles claim has invalid type")
        void shouldThrowInvalidTokenException_whenRolesInvalidType() {
            SecretKey key = jwtService.getSecretKey();
            String invalidToken = Jwts.builder()
                    .claim(ROLES_CLAIM.getClaim(), "invalid_string_type")
                    .expiration(new Date(System.currentTimeMillis() + 60000))
                    .issuedAt(new Date())
                    .signWith(key)
                    .compact();
            Claims claims = jwtHelper.extractAllClaims(invalidToken);

            assertThatThrownBy(() -> jwtHelper.extractAuthorities(claims))
                    .isInstanceOf(InvalidTokenException.class);
        }

        @Test
        @DisplayName("Should throw InvalidTokenException when roles claim is missing")
        void shouldThrowInvalidTokenException_whenRolesMissing() {
            SecretKey key = jwtService.getSecretKey();
            String tokenWithoutRoles = Jwts.builder()
                    .subject("test-subject")
                    .expiration(new Date(System.currentTimeMillis() + 60000))
                    .issuedAt(new Date())
                    .signWith(key)
                    .compact();
            Claims claims = jwtHelper.extractAllClaims(tokenWithoutRoles);

            assertThatThrownBy(() -> jwtHelper.extractAuthorities(claims))
                    .isInstanceOf(InvalidTokenException.class);
        }

        @Test
        @DisplayName("Should extract empty authorities when roles list is empty")
        void shouldExtractEmptyAuthorities_whenRolesListEmpty() {
            SecretKey key = jwtService.getSecretKey();
            String tokenWithEmptyRoles = Jwts.builder()
                    .claim(ROLES_CLAIM.getClaim(), List.of())
                    .expiration(new Date(System.currentTimeMillis() + 60000))
                    .issuedAt(new Date())
                    .signWith(key)
                    .compact();
            Claims claims = jwtHelper.extractAllClaims(tokenWithEmptyRoles);

            var authorities = jwtHelper.extractAuthorities(claims);

            assertThat(authorities).isEmpty();
        }
    }

    @Nested
    @DisplayName("User Status Extraction Tests")
    class UserStatusExtractionTests {

        @Test
        @DisplayName("Should extract user status when status is valid")
        void shouldExtractUserStatus_whenStatusValid() {
            String token = createValidToken();
            Claims claims = jwtHelper.extractAllClaims(token);

            UserStatus status = jwtHelper.extractUserStatus(claims);

            assertThat(status).isEqualTo(UserStatus.ACTIVE);
        }

        @Test
        @DisplayName("Should throw InvalidTokenException when status is invalid")
        void shouldThrowInvalidTokenException_whenStatusInvalid() {
            SecretKey key = jwtService.getSecretKey();
            String tokenWithInvalidStatus = Jwts.builder()
                    .claim(STATUS_CLAIM.getClaim(), "INVALID_STATUS")
                    .expiration(new Date(System.currentTimeMillis() + 60000))
                    .issuedAt(new Date())
                    .signWith(key)
                    .compact();
            Claims claims = jwtHelper.extractAllClaims(tokenWithInvalidStatus);

            assertThatThrownBy(() -> jwtHelper.extractUserStatus(claims))
                    .isInstanceOf(InvalidTokenException.class);
        }

        @Test
        @DisplayName("Should throw InvalidTokenException when status is missing")
        void shouldThrowInvalidTokenException_whenStatusMissing() {
            SecretKey key = jwtService.getSecretKey();
            String tokenWithoutStatus = Jwts.builder()
                    .subject("test-subject")
                    .expiration(new Date(System.currentTimeMillis() + 60000))
                    .issuedAt(new Date())
                    .signWith(key)
                    .compact();
            Claims claims = jwtHelper.extractAllClaims(tokenWithoutStatus);

            assertThatThrownBy(() -> jwtHelper.extractUserStatus(claims))
                    .isInstanceOf(InvalidTokenException.class);
        }

        @Test
        @DisplayName("Should throw InvalidTokenException when status is null")
        void shouldThrowInvalidTokenException_whenStatusNull() {
            SecretKey key = jwtService.getSecretKey();
            String tokenWithNullStatus = Jwts.builder()
                    .claim(STATUS_CLAIM.getClaim(), null)
                    .expiration(new Date(System.currentTimeMillis() + 60000))
                    .issuedAt(new Date())
                    .signWith(key)
                    .compact();
            Claims claims = jwtHelper.extractAllClaims(tokenWithNullStatus);

            assertThatThrownBy(() -> jwtHelper.extractUserStatus(claims))
                    .isInstanceOf(InvalidTokenException.class);
        }
    }

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should handle token with multiple roles")
        void shouldHandleTokenWithMultipleRoles() {
            SecretKey key = jwtService.getSecretKey();
            String tokenWithMultipleRoles = Jwts.builder()
                    .claim(ROLES_CLAIM.getClaim(), List.of("ROLE_USER", "ROLE_ADMIN", "ROLE_MODERATOR"))
                    .expiration(new Date(System.currentTimeMillis() + 60000))
                    .issuedAt(new Date())
                    .signWith(key)
                    .compact();
            Claims claims = jwtHelper.extractAllClaims(tokenWithMultipleRoles);

            var authorities = jwtHelper.extractAuthorities(claims);

            assertThat(authorities)
                    .hasSize(3)
                    .extracting(SimpleGrantedAuthority::getAuthority)
                    .containsExactlyInAnyOrder("ROLE_USER", "ROLE_ADMIN", "ROLE_MODERATOR");
        }

        @Test
        @DisplayName("Should handle various valid user statuses")
        void shouldHandleVariousValidUserStatuses() {
            SecretKey key = jwtService.getSecretKey();

            for (UserStatus userStatus : UserStatus.values()) {
                String token = Jwts.builder()
                        .claim(STATUS_CLAIM.getClaim(), userStatus.name())
                        .expiration(new Date(System.currentTimeMillis() + 60000))
                        .issuedAt(new Date())
                        .signWith(key)
                        .compact();
                Claims claims = jwtHelper.extractAllClaims(token);

                assertThat(jwtHelper.extractUserStatus(claims)).isEqualTo(userStatus);
            }
        }
    }
}