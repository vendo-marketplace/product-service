package com.vendo.product_service.security.common;

import com.vendo.domain.user.common.type.UserRole;
import com.vendo.domain.user.common.type.UserStatus;
import com.vendo.product_service.common.builder.JwtPayloadBuilder;
import com.vendo.product_service.common.dto.JwtPayload;
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

import java.util.HashMap;
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
    class ExtractAllClaimsTests {

        @Test
        void extractAllClaims_whenTokenValid_returnsClaims() {
            JwtPayload payload = jwtPayloadBuilder.buildValidUserJwtPayload().build();
            String token = jwtService.generateAccessToken(payload);

            Claims claims = jwtHelper.extractAllClaims(token);

            assertThat(claims).isNotNull();
            assertThat(claims.getSubject()).isEqualTo(JWT_USER_SUBJECT);
        }

        @Test
        void extractAllClaims_whenTokenExpired_throwsExpiredJwtException() {
            JwtPayload payload = jwtPayloadBuilder.buildValidUserJwtPayload()
                    .expiration(-1)
                    .build();
            String expiredToken = jwtService.generateAccessToken(payload);

            assertThatThrownBy(() -> jwtHelper.extractAllClaims(expiredToken))
                    .isInstanceOf(ExpiredJwtException.class);
        }

        @Test
        void extractAllClaims_whenSignatureInvalid_throwsSignatureException() {
            JwtPayload payload = jwtPayloadBuilder.buildValidUserJwtPayload()
                    .key(jwtService.getBadSecretKey())
                    .build();
            String invalidSignatureToken = jwtService.generateAccessToken(payload);

            assertThatThrownBy(() -> jwtHelper.extractAllClaims(invalidSignatureToken))
                    .isInstanceOf(SignatureException.class);
        }

        @Test
        void extractAllClaims_whenTokenMalformed_throwsMalformedJwtException() {
            assertThatThrownBy(() -> jwtHelper.extractAllClaims(INVALID_TOKEN_FORMAT))
                    .isInstanceOf(MalformedJwtException.class);
        }

        @Test
        void extractAllClaims_whenTokenNull_throwsIllegalArgumentException() {
            assertThatThrownBy(() -> jwtHelper.extractAllClaims(null))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void extractAllClaims_whenTokenEmpty_throwsIllegalArgumentException() {
            assertThatThrownBy(() -> jwtHelper.extractAllClaims(""))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    class ExtractSubjectTests {

        @Test
        void extractSubject_whenSubjectPresent_returnsSubject() {
            JwtPayload payload = jwtPayloadBuilder.buildValidUserJwtPayload().build();
            String token = jwtService.generateAccessToken(payload);
            Claims claims = jwtHelper.extractAllClaims(token);

            String subject = jwtHelper.extractSubject(claims);

            assertThat(subject).isEqualTo(JWT_USER_SUBJECT);
        }

        @Test
        void extractSubject_whenSubjectNull_throwsInvalidTokenException() {
            JwtPayload payload = jwtPayloadBuilder.buildValidUserJwtPayload()
                    .subject(null)
                    .build();
            String token = jwtService.generateAccessToken(payload);
            Claims claims = jwtHelper.extractAllClaims(token);

            assertThatThrownBy(() -> jwtHelper.extractSubject(claims))
                    .isInstanceOf(InvalidTokenException.class);
        }

        @Test
        void extractSubject_whenSubjectEmpty_throwsInvalidTokenException() {
            JwtPayload payload = jwtPayloadBuilder.buildValidUserJwtPayload()
                    .subject("")
                    .build();
            String token = jwtService.generateAccessToken(payload);
            Claims claims = jwtHelper.extractAllClaims(token);

            assertThatThrownBy(() -> jwtHelper.extractSubject(claims))
                    .isInstanceOf(InvalidTokenException.class);
        }
    }

    @Nested
    class ExtractAuthoritiesTests {

        @Test
        void extractAuthorities_whenRolesValid_returnsAuthorities() {
            JwtPayload payload = jwtPayloadBuilder.buildValidUserJwtPayload().build();
            String token = jwtService.generateAccessToken(payload);
            Claims claims = jwtHelper.extractAllClaims(token);

            List<SimpleGrantedAuthority> authorities = jwtHelper.extractAuthorities(claims);

            assertThat(authorities)
                    .hasSize(1)
                    .extracting(SimpleGrantedAuthority::getAuthority)
                    .containsExactly(UserRole.USER.name());
        }

        @Test
        void extractAuthorities_whenRolesInvalidType_throwsInvalidTokenException() {
            JwtPayload payload = jwtPayloadBuilder.buildValidUserJwtPayload()
                    .claims(Map.of(
                            STATUS_CLAIM.getClaim(), UserStatus.ACTIVE,
                            ROLES_CLAIM.getClaim(), "invalid_string"
                    ))
                    .build();

            String token = jwtService.generateAccessToken(payload);
            Claims claims = jwtHelper.extractAllClaims(token);

            assertThatThrownBy(() -> jwtHelper.extractAuthorities(claims))
                    .isInstanceOf(InvalidTokenException.class);
        }

        @Test
        void extractAuthorities_whenRolesMissing_throwsInvalidTokenException() {
            JwtPayload payload = jwtPayloadBuilder.buildValidUserJwtPayload()
                    .claims(Map.of(
                            STATUS_CLAIM.getClaim(), UserStatus.ACTIVE
                    ))
                    .build();

            String token = jwtService.generateAccessToken(payload);
            Claims claims = jwtHelper.extractAllClaims(token);

            assertThatThrownBy(() -> jwtHelper.extractAuthorities(claims))
                    .isInstanceOf(InvalidTokenException.class);
        }

        @Test
        void extractAuthorities_whenRolesNull_throwsInvalidTokenException() {
            HashMap<String,Object> claimsMap = new HashMap<>();
            claimsMap.put(STATUS_CLAIM.getClaim(), UserStatus.ACTIVE);
            claimsMap.put(ROLES_CLAIM.getClaim(), null);

            JwtPayload payload = jwtPayloadBuilder.buildValidUserJwtPayload()
                    .claims(claimsMap)
                    .build();

            String token = jwtService.generateAccessToken(payload);
            Claims claims = jwtHelper.extractAllClaims(token);

            assertThatThrownBy(() -> jwtHelper.extractAuthorities(claims))
                    .isInstanceOf(InvalidTokenException.class);
        }

        @Test
        void extractAuthorities_whenRolesEmpty_throwsInvalidTokenException() {
            HashMap<String,Object> claimsMap = new HashMap<>();
            claimsMap.put(STATUS_CLAIM.getClaim(), UserStatus.ACTIVE);
            claimsMap.put(ROLES_CLAIM.getClaim(), List.of());

            JwtPayload payload = jwtPayloadBuilder.buildValidUserJwtPayload()
                    .claims(claimsMap)
                    .build();

            String token = jwtService.generateAccessToken(payload);
            Claims claims = jwtHelper.extractAllClaims(token);

            assertThatThrownBy(() -> jwtHelper.extractAuthorities(claims))
                    .isInstanceOf(InvalidTokenException.class);
        }

        @Test
        void extractAuthorities_whenMultipleRoles_returnsAll() {
            List<String> roles = List.of("ROLE_USER", "ROLE_ADMIN", "ROLE_MODERATOR");

            JwtPayload payload = jwtPayloadBuilder.buildValidUserJwtPayload()
                    .claims(Map.of(
                            STATUS_CLAIM.getClaim(), UserStatus.ACTIVE,
                            ROLES_CLAIM.getClaim(), roles
                    ))
                    .build();

            String token = jwtService.generateAccessToken(payload);
            Claims claims = jwtHelper.extractAllClaims(token);

            List<SimpleGrantedAuthority> authorities = jwtHelper.extractAuthorities(claims);

            assertThat(authorities)
                    .hasSize(3)
                    .extracting(SimpleGrantedAuthority::getAuthority)
                    .containsExactlyInAnyOrderElementsOf(roles);
        }
    }

    @Nested
    class ExtractUserStatusTests {

        @Test
        void extractUserStatus_whenStatusValid_returnsStatus() {
            JwtPayload payload = jwtPayloadBuilder.buildValidUserJwtPayload().build();
            String token = jwtService.generateAccessToken(payload);
            Claims claims = jwtHelper.extractAllClaims(token);

            UserStatus status = jwtHelper.extractUserStatus(claims);

            assertThat(status).isEqualTo(UserStatus.ACTIVE);
        }

        @Test
        void extractUserStatus_whenStatusInvalid_throwsInvalidTokenException() {
            JwtPayload payload = jwtPayloadBuilder.buildValidUserJwtPayload()
                    .claims(Map.of(
                            STATUS_CLAIM.getClaim(), INVALID_STATUS,
                            ROLES_CLAIM.getClaim(), List.of("ROLE_USER")
                    ))
                    .build();

            String token = jwtService.generateAccessToken(payload);
            Claims claims = jwtHelper.extractAllClaims(token);

            assertThatThrownBy(() -> jwtHelper.extractUserStatus(claims))
                    .isInstanceOf(InvalidTokenException.class);
        }

        @Test
        void extractUserStatus_whenStatusNull_throwsInvalidTokenException() {
            HashMap<String, Object> claimsMap = new HashMap<>();
            claimsMap.put(STATUS_CLAIM.getClaim(), null);
            claimsMap.put(ROLES_CLAIM.getClaim(), List.of("ROLE_USER"));

            JwtPayload payload = jwtPayloadBuilder.buildValidUserJwtPayload()
                    .claims(claimsMap)
                    .build();

            String token = jwtService.generateAccessToken(payload);
            Claims claims = jwtHelper.extractAllClaims(token);

            assertThatThrownBy(() -> jwtHelper.extractUserStatus(claims))
                    .isInstanceOf(InvalidTokenException.class);
        }

        @Test
        void extractUserStatus_whenStatusMissing_throwsInvalidTokenException() {
            HashMap<String, Object> claimsMap = new HashMap<>();
            claimsMap.put(ROLES_CLAIM.getClaim(), List.of("ROLE_USER"));

            JwtPayload payload = jwtPayloadBuilder.buildValidUserJwtPayload()
                    .claims(claimsMap)
                    .build();

            String token = jwtService.generateAccessToken(payload);
            Claims claims = jwtHelper.extractAllClaims(token);

            assertThatThrownBy(() -> jwtHelper.extractUserStatus(claims))
                    .isInstanceOf(InvalidTokenException.class);
        }
    }
}
