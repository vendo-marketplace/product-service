package com.vendo.product_service.adapter.security.out.jwt;

import com.vendo.product_service.adapter.security.out.jwt.parser.JwtAuthenticationParser;
import com.vendo.product_service.adapter.security.out.props.JwtProperties;
import com.vendo.product_service.domain.user.User;
import com.vendo.security_lib.type.UserTokenClaim;
import com.vendo.user_lib.type.UserRole;
import com.vendo.user_lib.type.UserStatus;
import com.vendo.utils_lib.AssertionUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.RequiredTypeException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JwtAuthenticationParserTest {

    private final String token = "token", secret = "secret";
    private final JwtProperties.Secret jwtSecret = new JwtProperties.Secret(secret);

    @InjectMocks
    private JwtAuthenticationParser claimsParser;
    @Mock
    private JwtService jwtService;

    @Mock
    private JwtProperties jwtProperties;

    @Nested
    class ExtractClaimsTests {
        @Test
        void extract_shouldReturnTokenClaims() {
            Claims claims = mock(Claims.class);
            User authUser = new User("id", UserStatus.ACTIVE, List.of(UserRole.USER), true);

            when(claims.get(UserTokenClaim.ID.getClaim(), String.class)).thenReturn(authUser.id());
            when(claims.get(UserTokenClaim.ROLES.getClaim())).thenReturn(User.toNames(authUser.roles()));
            when(claims.get(UserTokenClaim.VERIFIED.getClaim(), Boolean.class)).thenReturn(authUser.emailVerified());
            when(claims.get(UserTokenClaim.STATUS.getClaim(), String.class)).thenReturn(authUser.status().toString());

            when(jwtProperties.getSecret()).thenReturn(jwtSecret);
            when(jwtService.extractAllClaims(token, secret)).thenReturn(claims);

            User extract = claimsParser.extract(token);
            AssertionUtils.assertFrom(extract, authUser);

            verify(jwtService).extractAllClaims(token, secret);
            verify(claims).get(UserTokenClaim.ID.getClaim(), String.class);
            verify(claims).get(UserTokenClaim.ROLES.getClaim());
            verify(claims).get(UserTokenClaim.VERIFIED.getClaim(), Boolean.class);
            verify(claims).get(UserTokenClaim.STATUS.getClaim(), String.class);
        }

        @Test
        void extract_shouldThrowException_whenUserIdIsNotPresent() {
            Claims claims = mock(Claims.class);
            User authUser = new User(null, UserStatus.ACTIVE, List.of(UserRole.USER), true);

            when(claims.get(UserTokenClaim.ID.getClaim(), String.class)).thenReturn(authUser.id());

            when(jwtProperties.getSecret()).thenReturn(jwtSecret);
            when(jwtService.extractAllClaims(token, secret)).thenReturn(claims);

            assertThatThrownBy(() -> claimsParser.extract(token)).isInstanceOf(BadCredentialsException.class)
                    .hasMessage("Invalid token.");

            verify(jwtService).extractAllClaims(token, secret);
            verify(claims).get(UserTokenClaim.ID.getClaim(), String.class);
            verify(claims, never()).get(UserTokenClaim.ROLES.getClaim());
            verify(claims, never()).get(UserTokenClaim.VERIFIED.getClaim(), Boolean.class);
            verify(claims, never()).get(UserTokenClaim.STATUS.getClaim(), String.class);
        }

        @Test
        void extract_shouldThrowException_whenUserIdIsNotInstanceOfString() {
            Claims claims = mock(Claims.class);

            when(claims.get(UserTokenClaim.ID.getClaim(), String.class)).thenThrow(RequiredTypeException.class);
            when(jwtProperties.getSecret()).thenReturn(jwtSecret);
            when(jwtService.extractAllClaims(token, secret)).thenReturn(claims);

            assertThatThrownBy(() -> claimsParser.extract(token)).isInstanceOf(RequiredTypeException.class);

            verify(jwtService).extractAllClaims(token, secret);
            verify(claims).get(UserTokenClaim.ID.getClaim(), String.class);
            verify(claims, never()).get(UserTokenClaim.ROLES.getClaim());
            verify(claims, never()).get(UserTokenClaim.VERIFIED.getClaim(), Boolean.class);
            verify(claims, never()).get(UserTokenClaim.STATUS.getClaim(), String.class);
        }

        @Test
        void extract_shouldThrowException_whenRolesNotInstanceOfList() {
            Claims claims = mock(Claims.class);

            when(claims.get(UserTokenClaim.ID.getClaim(), String.class)).thenReturn("id");
            when(claims.get(UserTokenClaim.ROLES.getClaim())).thenReturn(1);

            when(jwtProperties.getSecret()).thenReturn(jwtSecret);
            when(jwtService.extractAllClaims(token, secret)).thenReturn(claims);

            assertThatThrownBy(() -> claimsParser.extract(token)).isInstanceOf(BadCredentialsException.class)
                    .hasMessage("Invalid token.");

            verify(jwtService).extractAllClaims(token, secret);
            verify(claims).get(UserTokenClaim.ID.getClaim(), String.class);
            verify(claims).get(UserTokenClaim.ROLES.getClaim());
            verify(claims, never()).get(UserTokenClaim.VERIFIED.getClaim(), Boolean.class);
            verify(claims, never()).get(UserTokenClaim.STATUS.getClaim(), String.class);

        }

        @Test
        void extract_shouldThrowException_whenEachRoleIsNotInstanceOfString() {
            Claims claims = mock(Claims.class);

            when(claims.get(UserTokenClaim.ID.getClaim(), String.class)).thenReturn("id");
            when(claims.get(UserTokenClaim.ROLES.getClaim())).thenReturn(List.of(1, 2));

            when(jwtProperties.getSecret()).thenReturn(jwtSecret);
            when(jwtService.extractAllClaims(token, secret)).thenReturn(claims);

            assertThatThrownBy(() -> claimsParser.extract(token)).isInstanceOf(BadCredentialsException.class)
                    .hasMessage("Invalid token.");

            verify(jwtService).extractAllClaims(token, secret);
            verify(claims).get(UserTokenClaim.ID.getClaim(), String.class);
            verify(claims).get(UserTokenClaim.ROLES.getClaim());
            verify(claims, never()).get(UserTokenClaim.VERIFIED.getClaim(), Boolean.class);
            verify(claims, never()).get(UserTokenClaim.STATUS.getClaim(), String.class);
        }

        @Test
        void extract_shouldThrowException_whenRolesAreEmpty() {
            Claims claims = mock(Claims.class);

            when(claims.get(UserTokenClaim.ID.getClaim(), String.class)).thenReturn("id");
            when(claims.get(UserTokenClaim.ROLES.getClaim())).thenReturn(List.of());

            when(jwtProperties.getSecret()).thenReturn(jwtSecret);
            when(jwtService.extractAllClaims(token, secret)).thenReturn(claims);

            assertThatThrownBy(() -> claimsParser.extract(token)).isInstanceOf(BadCredentialsException.class)
                    .hasMessage("Invalid token.");

            verify(jwtService).extractAllClaims(token, secret);
            verify(claims).get(UserTokenClaim.ID.getClaim(), String.class);
            verify(claims).get(UserTokenClaim.ROLES.getClaim());
            verify(claims, never()).get(UserTokenClaim.VERIFIED.getClaim(), Boolean.class);
            verify(claims, never()).get(UserTokenClaim.STATUS.getClaim(), String.class);
        }

        @Test
        void extract_shouldThrowException_whenEmailVerificationIsNotInstanceOfBoolean() {
            Claims claims = mock(Claims.class);
            User authUser = new User("id", UserStatus.ACTIVE, List.of(UserRole.USER), true);

            when(claims.get(UserTokenClaim.ID.getClaim(), String.class)).thenReturn(authUser.id());
            when(claims.get(UserTokenClaim.ROLES.getClaim())).thenReturn(User.toNames(authUser.roles()));
            when(claims.get(UserTokenClaim.VERIFIED.getClaim(), Boolean.class)).thenThrow(RequiredTypeException.class);

            when(jwtProperties.getSecret()).thenReturn(jwtSecret);
            when(jwtService.extractAllClaims(token, secret)).thenReturn(claims);

            assertThatThrownBy(() -> claimsParser.extract(token)).isInstanceOf(RequiredTypeException.class);

            verify(jwtService).extractAllClaims(token, secret);
            verify(claims).get(UserTokenClaim.ID.getClaim(), String.class);
            verify(claims).get(UserTokenClaim.ROLES.getClaim());
            verify(claims).get(UserTokenClaim.VERIFIED.getClaim(), Boolean.class);
            verify(claims, never()).get(UserTokenClaim.STATUS.getClaim(), String.class);
        }

        @Test
        void extract_shouldThrowException_whenUserStatusIsNotInstanceOfString() {
            Claims claims = mock(Claims.class);
            User authUser = new User("id", UserStatus.ACTIVE, List.of(UserRole.USER), true);

            when(claims.get(UserTokenClaim.ID.getClaim(), String.class)).thenReturn(authUser.id());
            when(claims.get(UserTokenClaim.ROLES.getClaim())).thenReturn(User.toNames(authUser.roles()));
            when(claims.get(UserTokenClaim.VERIFIED.getClaim(), Boolean.class)).thenReturn(authUser.emailVerified());
            when(claims.get(UserTokenClaim.STATUS.getClaim(), String.class)).thenThrow(RequiredTypeException.class);

            when(jwtProperties.getSecret()).thenReturn(jwtSecret);
            when(jwtService.extractAllClaims(token, secret)).thenReturn(claims);

            assertThatThrownBy(() -> claimsParser.extract(token)).isInstanceOf(RequiredTypeException.class);

            verify(jwtService).extractAllClaims(token, secret);
            verify(claims).get(UserTokenClaim.ID.getClaim(), String.class);
            verify(claims).get(UserTokenClaim.ROLES.getClaim());
            verify(claims).get(UserTokenClaim.VERIFIED.getClaim(), Boolean.class);
            verify(claims).get(UserTokenClaim.STATUS.getClaim(), String.class);
        }

        @Test
        void extract_shouldThrowException_whenUserStatusNotValueOfEnum() {
            Claims claims = mock(Claims.class);
            User authUser = new User("id", UserStatus.ACTIVE, List.of(UserRole.USER), true);

            when(claims.get(UserTokenClaim.ID.getClaim(), String.class)).thenReturn(authUser.id());
            when(claims.get(UserTokenClaim.ROLES.getClaim())).thenReturn(User.toNames(authUser.roles()));
            when(claims.get(UserTokenClaim.VERIFIED.getClaim(), Boolean.class)).thenReturn(authUser.emailVerified());
            when(claims.get(UserTokenClaim.STATUS.getClaim(), String.class)).thenReturn("not_enum");

            when(jwtProperties.getSecret()).thenReturn(jwtSecret);
            when(jwtService.extractAllClaims(token, secret)).thenReturn(claims);

            assertThatThrownBy(() -> claimsParser.extract(token)).isInstanceOf(BadCredentialsException.class)
                    .hasMessage("Invalid token.");

            verify(jwtService).extractAllClaims(token, secret);
            verify(claims).get(UserTokenClaim.ID.getClaim(), String.class);
            verify(claims).get(UserTokenClaim.ROLES.getClaim());
            verify(claims).get(UserTokenClaim.VERIFIED.getClaim(), Boolean.class);
            verify(claims).get(UserTokenClaim.STATUS.getClaim(), String.class);
        }
    }

}
