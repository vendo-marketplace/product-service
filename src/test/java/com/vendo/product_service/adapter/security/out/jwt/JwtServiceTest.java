package com.vendo.product_service.adapter.security.out.jwt;

import com.vendo.product_service.adapter.security.in.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    @Mock
    private JwtProperties jwtProperties;

    @Nested
    class ExtractAllClaimsTests {

        @Test
        void extractAllClaims_whenTokenValid_returnsClaims() {
            String token = "token";
            Jwts jwts = Mockito.mock(Jwts.class);



            Claims claims = jwtService.extractAllClaims(token);

            assertThat(claims).isNotNull();
            assertThat(claims.getSubject()).isEqualTo("JWT_USER_SUBJECT");
        }

//        @Test
//        void extractAllClaims_whenTokenExpired_throwsExpiredJwtException() {
//            JwtPayload payload = jwtPayloadBuilder.buildValidUserJwtPayload()
//                    .expiration(-1)
//                    .build();
//            String expiredToken = testJwtService.generateAccessToken(payload);
//
//            assertThatThrownBy(() -> testJwtService.extractAllClaims(expiredToken))
//                    .isInstanceOf(ExpiredJwtException.class);
//        }
//
//        @Test
//        void extractAllClaims_whenSignatureInvalid_throwsSignatureException() {
//            JwtPayload payload = jwtPayloadBuilder.buildValidUserJwtPayload()
//                    .key(testJwtService.getBadSecretKey())
//                    .build();
//            String invalidSignatureToken = testJwtService.generateAccessToken(payload);
//
//            assertThatThrownBy(() -> testJwtService.extractAllClaims(invalidSignatureToken))
//                    .isInstanceOf(SignatureException.class);
//        }
//
//        @Test
//        void extractAllClaims_whenTokenMalformed_throwsMalformedJwtException() {
//            assertThatThrownBy(() -> testJwtService.extractAllClaims(INVALID_TOKEN_FORMAT))
//                    .isInstanceOf(MalformedJwtException.class);
//        }
//
//        @Test
//        void extractAllClaims_whenTokenNull_throwsIllegalArgumentException() {
//            assertThatThrownBy(() -> testJwtService.extractAllClaims(null))
//                    .isInstanceOf(IllegalArgumentException.class);
//        }
//
//        @Test
//        void extractAllClaims_whenTokenEmpty_throwsIllegalArgumentException() {
//            assertThatThrownBy(() -> testJwtService.extractAllClaims(""))
//                    .isInstanceOf(IllegalArgumentException.class);
//        }
//    }
//
//    @Nested
//    class ExtractSubjectTests {
//
//        @Test
//        void extractSubject_whenSubjectPresent_returnsSubject() {
//            JwtPayload payload = jwtPayloadBuilder.buildValidUserJwtPayload().build();
//            String token = testJwtService.generateAccessToken(payload);
//            Claims claims = testJwtService.extractAllClaims(token);
//
//            String subject = testJwtService.extractSubject(claims);
//
//            assertThat(subject).isEqualTo(JWT_USER_SUBJECT);
//        }
//
//        @Test
//        void extractSubject_whenSubjectNull_throwsInvalidTokenException() {
//            JwtPayload payload = jwtPayloadBuilder.buildValidUserJwtPayload()
//                    .subject(null)
//                    .build();
//            String token = testJwtService.generateAccessToken(payload);
//            Claims claims = testJwtService.extractAllClaims(token);
//
//            assertThatThrownBy(() -> testJwtService.extractSubject(claims))
//                    .isInstanceOf(InvalidTokenException.class);
//        }
//
//        @Test
//        void extractSubject_whenSubjectEmpty_throwsInvalidTokenException() {
//            JwtPayload payload = jwtPayloadBuilder.buildValidUserJwtPayload()
//                    .subject("")
//                    .build();
//            String token = testJwtService.generateAccessToken(payload);
//            Claims claims = testJwtService.extractAllClaims(token);
//
//            assertThatThrownBy(() -> testJwtService.extractSubject(claims))
//                    .isInstanceOf(InvalidTokenException.class);
//        }
//    }
//
//    @Nested
//    class ExtractAuthoritiesTests {
//
//        @Test
//        void extractAuthorities_whenRolesValid_returnsAuthorities() {
//            JwtPayload payload = jwtPayloadBuilder.buildValidUserJwtPayload().build();
//            String token = testJwtService.generateAccessToken(payload);
//            Claims claims = testJwtService.extractAllClaims(token);
//
//            List<SimpleGrantedAuthority> authorities = testJwtService.extractAuthorities(claims);
//
//            assertThat(authorities)
//                    .hasSize(1)
//                    .extracting(SimpleGrantedAuthority::getAuthority)
//                    .containsExactly(UserRole.USER.name());
//        }
//
//        @Test
//        void extractAuthorities_whenRolesInvalidType_throwsInvalidTokenException() {
//            JwtPayload payload = jwtPayloadBuilder.buildValidUserJwtPayload()
//                    .claims(Map.of(
//                            STATUS_CLAIM.getClaim(), UserStatus.ACTIVE,
//                            ROLES_CLAIM.getClaim(), "invalid_string"
//                    ))
//                    .build();
//
//            String token = testJwtService.generateAccessToken(payload);
//            Claims claims = testJwtService.extractAllClaims(token);
//
//            assertThatThrownBy(() -> testJwtService.extractAuthorities(claims))
//                    .isInstanceOf(InvalidTokenException.class);
//        }
//
//        @Test
//        void extractAuthorities_whenRolesMissing_throwsInvalidTokenException() {
//            JwtPayload payload = jwtPayloadBuilder.buildValidUserJwtPayload()
//                    .claims(Map.of(
//                            STATUS_CLAIM.getClaim(), UserStatus.ACTIVE
//                    ))
//                    .build();
//
//            String token = testJwtService.generateAccessToken(payload);
//            Claims claims = testJwtService.extractAllClaims(token);
//
//            assertThatThrownBy(() -> testJwtService.extractAuthorities(claims))
//                    .isInstanceOf(InvalidTokenException.class);
//        }
//
//        @Test
//        void extractAuthorities_whenRolesNull_throwsInvalidTokenException() {
//            HashMap<String,Object> claimsMap = new HashMap<>();
//            claimsMap.put(STATUS_CLAIM.getClaim(), UserStatus.ACTIVE);
//            claimsMap.put(ROLES_CLAIM.getClaim(), null);
//
//            JwtPayload payload = jwtPayloadBuilder.buildValidUserJwtPayload()
//                    .claims(claimsMap)
//                    .build();
//
//            String token = testJwtService.generateAccessToken(payload);
//            Claims claims = testJwtService.extractAllClaims(token);
//
//            assertThatThrownBy(() -> testJwtService.extractAuthorities(claims))
//                    .isInstanceOf(InvalidTokenException.class);
//        }
//
//        @Test
//        void extractAuthorities_whenRolesEmpty_throwsInvalidTokenException() {
//            HashMap<String,Object> claimsMap = new HashMap<>();
//            claimsMap.put(STATUS_CLAIM.getClaim(), UserStatus.ACTIVE);
//            claimsMap.put(ROLES_CLAIM.getClaim(), List.of());
//
//            JwtPayload payload = jwtPayloadBuilder.buildValidUserJwtPayload()
//                    .claims(claimsMap)
//                    .build();
//
//            String token = testJwtService.generateAccessToken(payload);
//            Claims claims = testJwtService.extractAllClaims(token);
//
//            assertThatThrownBy(() -> testJwtService.extractAuthorities(claims))
//                    .isInstanceOf(InvalidTokenException.class);
//        }
//
//        @Test
//        void extractAuthorities_whenMultipleRoles_returnsAll() {
//            List<String> roles = List.of("ROLE_USER", "ROLE_ADMIN", "ROLE_MODERATOR");
//
//            JwtPayload payload = jwtPayloadBuilder.buildValidUserJwtPayload()
//                    .claims(Map.of(
//                            STATUS_CLAIM.getClaim(), UserStatus.ACTIVE,
//                            ROLES_CLAIM.getClaim(), roles
//                    ))
//                    .build();
//
//            String token = testJwtService.generateAccessToken(payload);
//            Claims claims = testJwtService.extractAllClaims(token);
//
//            List<SimpleGrantedAuthority> authorities = testJwtService.extractAuthorities(claims);
//
//            assertThat(authorities)
//                    .hasSize(3)
//                    .extracting(SimpleGrantedAuthority::getAuthority)
//                    .containsExactlyInAnyOrderElementsOf(roles);
//        }
//    }
//
//    @Nested
//    class ExtractUserStatusTests {
//
//        @Test
//        void extractUserStatus_whenStatusValid_returnsStatus() {
//            JwtPayload payload = jwtPayloadBuilder.buildValidUserJwtPayload().build();
//            String token = testJwtService.generateAccessToken(payload);
//            Claims claims = testJwtService.extractAllClaims(token);
//
//            UserStatus status = testJwtService.extractUserStatus(claims);
//
//            assertThat(status).isEqualTo(UserStatus.ACTIVE);
//        }
//
//        @Test
//        void extractUserStatus_whenStatusInvalid_throwsInvalidTokenException() {
//            JwtPayload payload = jwtPayloadBuilder.buildValidUserJwtPayload()
//                    .claims(Map.of(
//                            STATUS_CLAIM.getClaim(), INVALID_STATUS,
//                            ROLES_CLAIM.getClaim(), List.of("ROLE_USER")
//                    ))
//                    .build();
//
//            String token = testJwtService.generateAccessToken(payload);
//            Claims claims = testJwtService.extractAllClaims(token);
//
//            assertThatThrownBy(() -> testJwtService.extractUserStatus(claims))
//                    .isInstanceOf(InvalidTokenException.class);
//        }
//
//        @Test
//        void extractUserStatus_whenStatusNull_throwsInvalidTokenException() {
//            HashMap<String, Object> claimsMap = new HashMap<>();
//            claimsMap.put(STATUS_CLAIM.getClaim(), null);
//            claimsMap.put(ROLES_CLAIM.getClaim(), List.of("ROLE_USER"));
//
//            JwtPayload payload = jwtPayloadBuilder.buildValidUserJwtPayload()
//                    .claims(claimsMap)
//                    .build();
//
//            String token = testJwtService.generateAccessToken(payload);
//            Claims claims = testJwtService.extractAllClaims(token);
//
//            assertThatThrownBy(() -> testJwtService.extractUserStatus(claims))
//                    .isInstanceOf(InvalidTokenException.class);
//        }
//
//        @Test
//        void extractUserStatus_whenStatusMissing_throwsInvalidTokenException() {
//            HashMap<String, Object> claimsMap = new HashMap<>();
//            claimsMap.put(ROLES_CLAIM.getClaim(), List.of("ROLE_USER"));
//
//            JwtPayload payload = jwtPayloadBuilder.buildValidUserJwtPayload()
//                    .claims(claimsMap)
//                    .build();
//
//            String token = testJwtService.generateAccessToken(payload);
//            Claims claims = testJwtService.extractAllClaims(token);
//
//            assertThatThrownBy(() -> testJwtService.extractUserStatus(claims))
//                    .isInstanceOf(InvalidTokenException.class);
//        }
    }
}
