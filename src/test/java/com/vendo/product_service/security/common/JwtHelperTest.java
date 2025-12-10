package com.vendo.product_service.security.common;

import com.vendo.domain.user.common.type.UserStatus;
import com.vendo.product_service.common.builder.JwtPayloadBuilder;
import com.vendo.product_service.security.common.helper.JwtHelper;
import com.vendo.product_service.service.JwtService;
import com.vendo.security.common.exception.InvalidTokenException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Map;

import static com.vendo.product_service.common.builder.JwtPayloadBuilder.JWT_USER_SUBJECT;
import static com.vendo.product_service.service.JwtService.INVALID_STATUS;
import static com.vendo.product_service.service.JwtService.INVALID_TOKEN_FORMAT;
import static com.vendo.security.common.type.TokenClaim.ROLES_CLAIM;
import static com.vendo.security.common.type.TokenClaim.STATUS_CLAIM;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
class JwtHelperTest {

    @Autowired
    private JwtHelper jwtHelper;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private JwtPayloadBuilder jwtPayloadBuilder;

    @Nested
    class TokenParsingTests {

        @Test
        void shouldParseValidToken() {
            String token = jwtService.generateAccessToken(
                    jwtPayloadBuilder.buildValidUserJwtPayload().build()
            );

            Claims claims = jwtHelper.extractAllClaims(token);

            assertThat(claims).isNotNull();
            assertThat(claims.getSubject()).isEqualTo(JWT_USER_SUBJECT);
        }

        @Test
        void shouldThrowExpiredJwtException_whenTokenExpired() {
            String expiredToken = jwtService.generateAccessToken(
                    jwtPayloadBuilder.buildValidUserJwtPayload()
                            .expiration(-1)
                            .build()
            );

            assertThatThrownBy(() -> jwtHelper.extractAllClaims(expiredToken))
                    .isInstanceOf(ExpiredJwtException.class);
        }

        @Test
        void shouldThrowSignatureException_whenInvalidSignature() {
            String tokenWithWrongSignature = jwtService.generateAccessToken(
                    jwtPayloadBuilder.buildValidUserJwtPayload()
                            .key(jwtService.getBadSecretKey())
                            .build()
            );

            assertThatThrownBy(() -> jwtHelper.extractAllClaims(tokenWithWrongSignature))
                    .isInstanceOf(SignatureException.class);
        }

        @Test
        void shouldThrowMalformedJwtException_whenTokenIsMalformed() {
            assertThatThrownBy(() -> jwtHelper.extractAllClaims(INVALID_TOKEN_FORMAT))
                    .isInstanceOf(MalformedJwtException.class);
        }

        @Test
        void shouldThrowIllegalArgumentException_whenTokenIsNull() {
            assertThatThrownBy(() -> jwtHelper.extractAllClaims(null))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void shouldThrowIllegalArgumentException_whenTokenIsEmpty() {
            assertThatThrownBy(() -> jwtHelper.extractAllClaims(""))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    class SubjectExtractionTests {

        @Test
        void shouldExtractSubject_whenSubjectIsPresent() {
            String token = jwtService.generateAccessToken(
                    jwtPayloadBuilder.buildValidUserJwtPayload().build()
            );

            Claims claims = jwtHelper.extractAllClaims(token);
            String subject = jwtHelper.extractSubject(claims);

            assertThat(subject).isEqualTo(JWT_USER_SUBJECT);
        }

        @Test
        void shouldThrowInvalidTokenException_whenSubjectIsMissing() {
            String token = jwtService.generateAccessToken(
                    jwtPayloadBuilder.buildValidUserJwtPayload().subject(null).build()
            );

            Claims claims = jwtHelper.extractAllClaims(token);

            assertThatThrownBy(() -> jwtHelper.extractSubject(claims))
                    .isInstanceOf(InvalidTokenException.class);
        }

        @Test
        void shouldThrowInvalidTokenException_whenSubjectEmpty() {
            String token = jwtService.generateAccessToken(
                    jwtPayloadBuilder.buildValidUserJwtPayload().subject("").build()
            );

            Claims claims = jwtHelper.extractAllClaims(token);

            assertThatThrownBy(() -> jwtHelper.extractSubject(claims))
                    .isInstanceOf(InvalidTokenException.class);
        }
    }

    @Nested
    class AuthoritiesExtractionTests {

        @Test
        void shouldExtractAuthorities_whenRolesValid() {
            String token = jwtService.generateAccessToken(
                    jwtPayloadBuilder.buildValidUserJwtPayload().build()
            );

            Claims claims = jwtHelper.extractAllClaims(token);
            var authorities = jwtHelper.extractAuthorities(claims);

            assertThat(authorities)
                    .hasSize(1)
                    .extracting(SimpleGrantedAuthority::getAuthority)
                    .containsExactly("ROLE_USER");
        }

        @Test
        void shouldThrowInvalidTokenException_whenRolesInvalidType() {

            String invalidToken = jwtService.generateAccessToken(
                    jwtPayloadBuilder.buildValidUserJwtPayload()
                            .claims(Map.of(
                            STATUS_CLAIM.getClaim(), UserStatus.ACTIVE,
                            ROLES_CLAIM.getClaim(), "invalid_string_type"
                            )).build()
            );
            Claims claims = jwtHelper.extractAllClaims(invalidToken);

            assertThatThrownBy(() -> jwtHelper.extractAuthorities(claims))
                    .isInstanceOf(InvalidTokenException.class);
        }

        @Test
        void shouldThrowInvalidTokenException_whenRolesMissing() {
            String tokenWithoutRoles = jwtService.generateAccessToken(
                    jwtPayloadBuilder.buildValidUserJwtPayload()
                            .claims(Map.of(
                                    STATUS_CLAIM.getClaim(), UserStatus.ACTIVE
                            )).build()
            );

            Claims claims = jwtHelper.extractAllClaims(tokenWithoutRoles);

            assertThatThrownBy(() -> jwtHelper.extractAuthorities(claims))
                    .isInstanceOf(InvalidTokenException.class);
        }

        @Test
        void shouldExtractEmptyAuthorities_whenRolesListEmpty() {
            String tokenWithEmptyRoles = jwtService.generateAccessToken(
                    jwtPayloadBuilder.buildValidUserJwtPayload()
                            .claims(Map.of(
                                    STATUS_CLAIM.getClaim(), UserStatus.ACTIVE,
                                    ROLES_CLAIM.getClaim(), List.of()
                            )).build()
            );

            Claims claims = jwtHelper.extractAllClaims(tokenWithEmptyRoles);
            var authorities = jwtHelper.extractAuthorities(claims);

            assertThat(authorities).isEmpty();
        }
    }

    @Nested
    class UserStatusExtractionTests {

        @Test
        void shouldExtractUserStatus_whenStatusValid() {
            String token = jwtService.generateAccessToken(
                    jwtPayloadBuilder.buildValidUserJwtPayload().build()
            );

            Claims claims = jwtHelper.extractAllClaims(token);
            UserStatus status = jwtHelper.extractUserStatus(claims);

            assertThat(status).isEqualTo(UserStatus.ACTIVE);
        }

        @Test
        void shouldThrowInvalidTokenException_whenStatusInvalid() {
            String tokenWithInvalidStatus = jwtService.generateAccessToken(
                    jwtPayloadBuilder.buildValidUserJwtPayload()
                            .claims(Map.of(
                                    STATUS_CLAIM.getClaim(), INVALID_STATUS,
                                    ROLES_CLAIM.getClaim(), List.of()
                            )).build()
            );

            Claims claims = jwtHelper.extractAllClaims(tokenWithInvalidStatus);

            assertThatThrownBy(() -> jwtHelper.extractUserStatus(claims))
                    .isInstanceOf(InvalidTokenException.class);
        }

        @Test
        void shouldThrowInvalidTokenException_whenStatusMissing() {
            String tokenWithoutStatus = jwtService.generateAccessToken(
                    jwtPayloadBuilder.buildValidUserJwtPayload()
                            .claims(Map.of(
                                    ROLES_CLAIM.getClaim(), List.of()
                            )).build()
            );

            Claims claims = jwtHelper.extractAllClaims(tokenWithoutStatus);

            assertThatThrownBy(() -> jwtHelper.extractUserStatus(claims))
                    .isInstanceOf(InvalidTokenException.class);
        }
    }

    @Nested
    class EdgeCasesTests {

        @Test
        void shouldHandleTokenWithMultipleRoles() {
            String tokenWithMultipleRoles = jwtService.generateAccessToken(
                    jwtPayloadBuilder.buildValidUserJwtPayload()
                            .claims(Map.of(
                                    STATUS_CLAIM.getClaim(), UserStatus.ACTIVE,
                                    ROLES_CLAIM.getClaim(), List.of("ROLE_USER", "ROLE_ADMIN", "ROLE_MODERATOR")
                            )).build()
            );

            Claims claims = jwtHelper.extractAllClaims(tokenWithMultipleRoles);
            var authorities = jwtHelper.extractAuthorities(claims);

            assertThat(authorities)
                    .hasSize(3)
                    .extracting(SimpleGrantedAuthority::getAuthority)
                    .containsExactlyInAnyOrder("ROLE_USER", "ROLE_ADMIN", "ROLE_MODERATOR");
        }
    }
}