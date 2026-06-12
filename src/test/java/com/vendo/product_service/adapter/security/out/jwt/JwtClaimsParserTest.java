//package com.vendo.product_service.adapter.security.out.jwt;
//
//import com.vendo.core_lib.type.ServiceName;
//import com.vendo.core_lib.type.ServiceRole;
//import com.vendo.product_service.adapter.security.out.jwt.parser.JwtClaimsParser;
//import com.vendo.product_service.adapter.security.out.jwt.parser.TokenClaims;
//import com.vendo.product_service.adapter.security.out.props.JwtProperties;
//import com.vendo.security_lib.type.UserClaims;
//import com.vendo.user_lib.type.UserStatus;
//import com.vendo.utils_lib.AssertionUtils;
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.RequiredTypeException;
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.security.authentication.BadCredentialsException;
//
//import java.util.List;
//import java.util.Set;
//
//import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//public class JwtClaimsParserTest {
//
//    private final String token = "token", secret = "secret";
//    private final JwtProperties.Internal jwtSecret = new JwtProperties.Internal(secret);
//
//    @InjectMocks
//    private JwtClaimsParser claimsParser;
//    @Mock
//    private JwtService jwtService;
//
//    @Mock
//    private JwtProperties jwtProperties;
//    @Mock
//    private JwtProperties jwtProperties;
//
//    @Nested
//    class ExtractClaimsTests {
//        @Test
//        void extract_shouldReturnTokenClaims() {
//            Claims claims = mock(Claims.class);
//            TokenClaims tokenClaims = new TokenClaims("id" ,UserStatus.ACTIVE, List.of("USER"), true);
//
//            when(claims.get(UserClaims.ID.getClaim(), String.class)).thenReturn(tokenClaims.userId());
//            when(claims.get(UserClaims.ROLES.getClaim())).thenReturn(tokenClaims.roles());
//            when(claims.get(UserClaims.VERIFIED.getClaim(), Boolean.class)).thenReturn(tokenClaims.emailVerified());
//            when(claims.get(UserClaims.STATUS.getClaim(), String.class)).thenReturn(tokenClaims.status().toString());
//
//            when(jwtProperties.getSecret()).thenReturn(jwtSecret);
//            when(jwtService.extractAllClaims(token, secret)).thenReturn(claims);
//
//            TokenClaims extract = claimsParser.extract(token);
//            AssertionUtils.assertFrom(extract, tokenClaims);
//
//            verify(jwtService).extractAllClaims(token, secret);
//            verify(claims).get(UserTokenClaim.ID.getClaim(), String.class);
//            verify(claims).get(UserTokenClaim.ROLES.getClaim());
//            verify(claims).get(UserTokenClaim.VERIFIED.getClaim(), Boolean.class);
//            verify(claims).get(UserTokenClaim.STATUS.getClaim(), String.class);
//        }
//
//        @Test
//        void extract_shouldThrowException_whenUserIdIsNotPresent() {
//            Claims claims = mock(Claims.class);
//            TokenClaims tokenClaims = new TokenClaims(null, UserStatus.ACTIVE, List.of("USER"), true);
//
//            when(claims.get(UserTokenClaim.ID.getClaim(), String.class)).thenReturn(tokenClaims.userId());
//
//            when(jwtProperties.getSecret()).thenReturn(jwtSecret);
//            when(jwtService.extractAllClaims(token, secret)).thenReturn(claims);
//
//            assertThatThrownBy(() -> claimsParser.extract(token)).isInstanceOf(BadCredentialsException.class)
//                    .hasMessage("Invalid token.");
//
//            verify(jwtService).extractAllClaims(token, secret);
//            verify(claims).get(UserTokenClaim.ID.getClaim(), String.class);
//            verify(claims, never()).get(UserTokenClaim.ROLES.getClaim());
//            verify(claims, never()).get(UserTokenClaim.VERIFIED.getClaim(), Boolean.class);
//            verify(claims, never()).get(UserTokenClaim.STATUS.getClaim(), String.class);
//        }
//
//        @Test
//        void extract_shouldThrowException_whenUserIdIsNotInstanceOfString() {
//            Claims claims = mock(Claims.class);
//
//            when(claims.get(UserTokenClaim.ID.getClaim(), String.class)).thenThrow(RequiredTypeException.class);
//            when(jwtProperties.getSecret()).thenReturn(jwtSecret);
//            when(jwtService.extractAllClaims(token, secret)).thenReturn(claims);
//
//            assertThatThrownBy(() -> claimsParser.extract(token)).isInstanceOf(RequiredTypeException.class);
//
//            verify(jwtService).extractAllClaims(token, secret);
//            verify(claims).get(UserTokenClaim.ID.getClaim(), String.class);
//            verify(claims, never()).get(UserTokenClaim.ROLES.getClaim());
//            verify(claims, never()).get(UserTokenClaim.VERIFIED.getClaim(), Boolean.class);
//            verify(claims, never()).get(UserTokenClaim.STATUS.getClaim(), String.class);
//        }
//
//        @Test
//        void extract_shouldThrowException_whenRolesNotInstanceOfList() {
//            Claims claims = mock(Claims.class);
//
//            when(claims.get(UserTokenClaim.ID.getClaim(), String.class)).thenReturn("id");
//            when(claims.get(UserTokenClaim.ROLES.getClaim())).thenReturn(1);
//
//            when(jwtProperties.getSecret()).thenReturn(jwtSecret);
//            when(jwtService.extractAllClaims(token, secret)).thenReturn(claims);
//
//            assertThatThrownBy(() -> claimsParser.extract(token)).isInstanceOf(BadCredentialsException.class)
//                    .hasMessage("Invalid token.");
//
//            verify(jwtService).extractAllClaims(token, secret);
//            verify(claims).get(UserTokenClaim.ID.getClaim(), String.class);
//            verify(claims).get(UserTokenClaim.ROLES.getClaim());
//            verify(claims, never()).get(UserTokenClaim.VERIFIED.getClaim(), Boolean.class);
//            verify(claims, never()).get(UserTokenClaim.STATUS.getClaim(), String.class);
//
//        }
//
//        @Test
//        void extract_shouldThrowException_whenEachRoleIsNotInstanceOfString() {
//            Claims claims = mock(Claims.class);
//
//            when(claims.get(UserTokenClaim.ID.getClaim(), String.class)).thenReturn("id");
//            when(claims.get(UserTokenClaim.ROLES.getClaim())).thenReturn(List.of(1, 2));
//
//            when(jwtProperties.getSecret()).thenReturn(jwtSecret);
//            when(jwtService.extractAllClaims(token, secret)).thenReturn(claims);
//
//            assertThatThrownBy(() -> claimsParser.extract(token)).isInstanceOf(BadCredentialsException.class)
//                    .hasMessage("Invalid token.");
//
//            verify(jwtService).extractAllClaims(token, secret);
//            verify(claims).get(UserTokenClaim.ID.getClaim(), String.class);
//            verify(claims).get(UserTokenClaim.ROLES.getClaim());
//            verify(claims, never()).get(UserTokenClaim.VERIFIED.getClaim(), Boolean.class);
//            verify(claims, never()).get(UserTokenClaim.STATUS.getClaim(), String.class);
//        }
//
//        @Test
//        void extract_shouldThrowException_whenRolesAreEmpty() {
//            Claims claims = mock(Claims.class);
//
//            when(claims.get(UserTokenClaim.ID.getClaim(), String.class)).thenReturn("id");
//            when(claims.get(UserTokenClaim.ROLES.getClaim())).thenReturn(List.of());
//
//            when(jwtProperties.getSecret()).thenReturn(jwtSecret);
//            when(jwtService.extractAllClaims(token, secret)).thenReturn(claims);
//
//            assertThatThrownBy(() -> claimsParser.extract(token)).isInstanceOf(BadCredentialsException.class)
//                    .hasMessage("Invalid token.");
//
//            verify(jwtService).extractAllClaims(token, secret);
//            verify(claims).get(UserTokenClaim.ID.getClaim(), String.class);
//            verify(claims).get(UserTokenClaim.ROLES.getClaim());
//            verify(claims, never()).get(UserTokenClaim.VERIFIED.getClaim(), Boolean.class);
//            verify(claims, never()).get(UserTokenClaim.STATUS.getClaim(), String.class);
//        }
//
//        @Test
//        void extract_shouldThrowException_whenEmailVerificationIsNotInstanceOfBoolean() {
//            Claims claims = mock(Claims.class);
//            TokenClaims tokenClaims = new TokenClaims("id", UserStatus.ACTIVE, List.of("USER"), true);
//
//            when(claims.get(UserTokenClaim.ID.getClaim(), String.class)).thenReturn(tokenClaims.userId());
//            when(claims.get(UserTokenClaim.ROLES.getClaim())).thenReturn(tokenClaims.roles());
//            when(claims.get(UserTokenClaim.VERIFIED.getClaim(), Boolean.class)).thenThrow(RequiredTypeException.class);
//
//            when(jwtProperties.getSecret()).thenReturn(jwtSecret);
//            when(jwtService.extractAllClaims(token, secret)).thenReturn(claims);
//
//            assertThatThrownBy(() -> claimsParser.extract(token)).isInstanceOf(RequiredTypeException.class);
//
//            verify(jwtService).extractAllClaims(token, secret);
//            verify(claims).get(UserTokenClaim.ID.getClaim(), String.class);
//            verify(claims).get(UserTokenClaim.ROLES.getClaim());
//            verify(claims).get(UserTokenClaim.VERIFIED.getClaim(), Boolean.class);
//            verify(claims, never()).get(UserTokenClaim.STATUS.getClaim(), String.class);
//        }
//
//        @Test
//        void extract_shouldThrowException_whenUserStatusIsNotInstanceOfString() {
//            Claims claims = mock(Claims.class);
//            TokenClaims tokenClaims = new TokenClaims("id", UserStatus.ACTIVE, List.of("USER"), true);
//
//            when(claims.get(UserTokenClaim.ID.getClaim(), String.class)).thenReturn(tokenClaims.userId());
//            when(claims.get(UserTokenClaim.ROLES.getClaim())).thenReturn(tokenClaims.roles());
//            when(claims.get(UserTokenClaim.VERIFIED.getClaim(), Boolean.class)).thenReturn(tokenClaims.emailVerified());
//            when(claims.get(UserTokenClaim.STATUS.getClaim(), String.class)).thenThrow(RequiredTypeException.class);
//
//            when(jwtProperties.getSecret()).thenReturn(jwtSecret);
//            when(jwtService.extractAllClaims(token, secret)).thenReturn(claims);
//
//            assertThatThrownBy(() -> claimsParser.extract(token)).isInstanceOf(RequiredTypeException.class);
//
//            verify(jwtService).extractAllClaims(token, secret);
//            verify(claims).get(UserTokenClaim.ID.getClaim(), String.class);
//            verify(claims).get(UserTokenClaim.ROLES.getClaim());
//            verify(claims).get(UserTokenClaim.VERIFIED.getClaim(), Boolean.class);
//            verify(claims).get(UserTokenClaim.STATUS.getClaim(), String.class);
//        }
//
//        @Test
//        void extract_shouldThrowException_whenUserStatusNotValueOfEnum() {
//            Claims claims = mock(Claims.class);
//            TokenClaims tokenClaims = new TokenClaims("id", UserStatus.ACTIVE, List.of("USER"), true);
//
//            when(claims.get(UserTokenClaim.ID.getClaim(), String.class)).thenReturn(tokenClaims.userId());
//            when(claims.get(UserTokenClaim.ROLES.getClaim())).thenReturn(tokenClaims.roles());
//            when(claims.get(UserTokenClaim.VERIFIED.getClaim(), Boolean.class)).thenReturn(tokenClaims.emailVerified());
//            when(claims.get(UserTokenClaim.STATUS.getClaim(), String.class)).thenReturn("not_enum");
//
//            when(jwtProperties.getSecret()).thenReturn(jwtSecret);
//            when(jwtService.extractAllClaims(token, secret)).thenReturn(claims);
//
//            assertThatThrownBy(() -> claimsParser.extract(token)).isInstanceOf(BadCredentialsException.class)
//                    .hasMessage("Invalid token.");
//
//            verify(jwtService).extractAllClaims(token, secret);
//            verify(claims).get(UserTokenClaim.ID.getClaim(), String.class);
//            verify(claims).get(UserTokenClaim.ROLES.getClaim());
//            verify(claims).get(UserTokenClaim.VERIFIED.getClaim(), Boolean.class);
//            verify(claims).get(UserTokenClaim.STATUS.getClaim(), String.class);
//        }
//    }
//
//    @Nested
//    class ExtractInternalClaimsTests {
//
//        @Test
//        void extractInternal_shouldReturnInternalTokenClaims() {
//            Claims claims = mock(Claims.class);
//            InternalTokenClaims tokenClaims = new InternalTokenClaims("subject", List.of(ServiceRole.INTERNAL.name()), Set.of(ServiceName.INDEXER_SERVICE.getServiceName()));
//
//            when(jwtProperties.getKey()).thenReturn(secret);
//            when(jwtService.extractAllClaims(token, secret)).thenReturn(claims);
//
//            when(claims.get(InternalTokenClaim.ROLES.getClaim())).thenReturn(tokenClaims.roles());
//            when(claims.getAudience()).thenReturn(tokenClaims.audience());
//            when(claims.getSubject()).thenReturn(tokenClaims.subject());
//
//            InternalTokenClaims extract = claimsParser.extractInternal(token);
//            AssertionUtils.assertFrom(extract, tokenClaims);
//
//            verify(jwtService).extractAllClaims(token, secret);
//            verify(claims).get(InternalTokenClaim.ROLES.getClaim());
//            verify(claims).getAudience();
//            verify(claims).getSubject();
//        }
//
//        @Test
//        void extractInternal_shouldThrowException_whenRolesAreEmpty() {
//            Claims claims = mock(Claims.class);
//
//            when(jwtProperties.getKey()).thenReturn(secret);
//            when(jwtService.extractAllClaims(token, secret)).thenReturn(claims);
//            when(claims.get(InternalTokenClaim.ROLES.getClaim())).thenReturn(List.of());
//
//            assertThatThrownBy(() -> claimsParser.extractInternal(token)).hasMessage("Invalid token.").isInstanceOf(BadCredentialsException.class);
//
//            verify(jwtService).extractAllClaims(token, secret);
//            verify(claims).get(InternalTokenClaim.ROLES.getClaim());
//            verify(claims, never()).getAudience();
//            verify(claims, never()).getSubject();
//        }
//
//        @Test
//        void extractInternal_shouldThrowException_whenRolesNotInstanceOfList() {
//            Claims claims = mock(Claims.class);
//
//            when(jwtProperties.getKey()).thenReturn(secret);
//            when(jwtService.extractAllClaims(token, secret)).thenReturn(claims);
//            when(claims.get(InternalTokenClaim.ROLES.getClaim())).thenReturn("");
//
//            assertThatThrownBy(() -> claimsParser.extractInternal(token)).hasMessage("Invalid token.").isInstanceOf(BadCredentialsException.class);
//
//            verify(jwtService).extractAllClaims(token, secret);
//            verify(claims).get(InternalTokenClaim.ROLES.getClaim());
//            verify(claims, never()).getAudience();
//            verify(claims, never()).getSubject();
//        }
//
//        @Test
//        void extractInternal_shouldThrowException_whenAudienceIsEmpty() {
//            Claims claims = mock(Claims.class);
//            InternalTokenClaims tokenClaims = new InternalTokenClaims("subject", List.of(ServiceRole.INTERNAL.name()), Set.of(ServiceName.INDEXER_SERVICE.getServiceName()));
//
//            when(jwtProperties.getKey()).thenReturn(secret);
//            when(jwtService.extractAllClaims(token, secret)).thenReturn(claims);
//
//            when(claims.get(InternalTokenClaim.ROLES.getClaim())).thenReturn(tokenClaims.roles());
//            when(claims.getAudience()).thenReturn(Set.of());
//
//            assertThatThrownBy(() -> claimsParser.extractInternal(token)).hasMessage("Invalid token.").isInstanceOf(BadCredentialsException.class);
//
//            verify(jwtService).extractAllClaims(token, secret);
//            verify(claims).get(InternalTokenClaim.ROLES.getClaim());
//            verify(claims).getAudience();
//            verify(claims, never()).getSubject();
//        }
//    }
//}
