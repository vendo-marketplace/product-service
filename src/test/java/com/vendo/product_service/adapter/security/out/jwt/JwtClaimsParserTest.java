package com.vendo.product_service.adapter.security.out.jwt;

import com.vendo.product_service.adapter.security.out.jwt.parser.JwtClaimsParserUser;
import com.vendo.product_service.adapter.security.out.jwt.parser.UserTokenClaims;
import com.vendo.security_lib.type.UserTokenClaim;
import com.vendo.test_utils_lib.AssertionUtils;
import com.vendo.user_lib.type.UserStatus;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.RequiredTypeException;
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
public class JwtClaimsParserTest {

    @InjectMocks
    private JwtClaimsParserUser claimsParser;

    @Mock
    private JwtService jwtService;

    @Test
    void extract_shouldReturnTokenClaims() {
        String token = "token";
        Claims claims = mock(Claims.class);
        UserTokenClaims userTokenClaims = new UserTokenClaims("id", UserStatus.ACTIVE, List.of("USER"), true);

        when(claims.get(UserTokenClaim.ID.getClaim(), String.class)).thenReturn(userTokenClaims.userId());
        when(claims.get(UserTokenClaim.ROLES.getClaim())).thenReturn(userTokenClaims.roles());
        when(claims.get(UserTokenClaim.VERIFIED.getClaim(), Boolean.class)).thenReturn(userTokenClaims.emailVerification());
        when(claims.get(UserTokenClaim.STATUS.getClaim(), String.class)).thenReturn(userTokenClaims.status().toString());

        when(jwtService.extractAllClaims(token)).thenReturn(claims);

        UserTokenClaims extract = claimsParser.extract(token);
        AssertionUtils.assertFromDto(extract, userTokenClaims);

        verify(jwtService).extractAllClaims(token);
        verify(claims).get(UserTokenClaim.ID.getClaim(), String.class);
        verify(claims).get(UserTokenClaim.ROLES.getClaim());
        verify(claims).get(UserTokenClaim.VERIFIED.getClaim(), Boolean.class);
        verify(claims).get(UserTokenClaim.STATUS.getClaim(), String.class);
    }

    @Test
    void extract_shouldThrowException_whenUserIdIsNotPresent() {
        String token = "token";
        Claims claims = mock(Claims.class);
        UserTokenClaims userTokenClaims = new UserTokenClaims(null, UserStatus.ACTIVE, List.of("USER"), true);

        when(claims.get(UserTokenClaim.ID.getClaim(), String.class)).thenReturn(userTokenClaims.userId());

        when(jwtService.extractAllClaims(token)).thenReturn(claims);

        assertThatThrownBy(() -> claimsParser.extract(token)).isInstanceOf(BadCredentialsException.class)
                .hasMessage("Invalid token.");

        verify(jwtService).extractAllClaims(token);
        verify(claims).get(UserTokenClaim.ID.getClaim(), String.class);
        verify(claims, never()).get(UserTokenClaim.ROLES.getClaim());
        verify(claims, never()).get(UserTokenClaim.VERIFIED.getClaim(), Boolean.class);
        verify(claims, never()).get(UserTokenClaim.STATUS.getClaim(), String.class);
    }

    @Test
    void extract_shouldThrowException_whenUserIdIsNotInstanceOfString() {
        String token = "token";
        Claims claims = mock(Claims.class);

        when(claims.get(UserTokenClaim.ID.getClaim(), String.class)).thenThrow(new RequiredTypeException(anyString()));
        when(jwtService.extractAllClaims(token)).thenReturn(claims);

        assertThatThrownBy(() -> claimsParser.extract(token)).isInstanceOf(RequiredTypeException.class);

        verify(jwtService).extractAllClaims(token);
        verify(claims).get(UserTokenClaim.ID.getClaim(), String.class);
        verify(claims, never()).get(UserTokenClaim.ROLES.getClaim());
        verify(claims, never()).get(UserTokenClaim.VERIFIED.getClaim(), Boolean.class);
        verify(claims, never()).get(UserTokenClaim.STATUS.getClaim(), String.class);
    }

    @Test
    void extract_shouldThrowException_whenRolesNotInstanceOfList() {
        String token = "token";
        Claims claims = mock(Claims.class);

        when(claims.get(UserTokenClaim.ID.getClaim(), String.class)).thenReturn("id");
        when(claims.get(UserTokenClaim.ROLES.getClaim())).thenReturn(1);

        when(jwtService.extractAllClaims(token)).thenReturn(claims);

        assertThatThrownBy(() -> claimsParser.extract(token)).isInstanceOf(BadCredentialsException.class)
                .hasMessage("Invalid token.");

        verify(jwtService).extractAllClaims(token);

        verify(claims).get(UserTokenClaim.ID.getClaim(), String.class);
        verify(claims).get(UserTokenClaim.ROLES.getClaim());
        verify(claims, never()).get(UserTokenClaim.VERIFIED.getClaim(), Boolean.class);
        verify(claims, never()).get(UserTokenClaim.STATUS.getClaim(), String.class);

    }

    @Test
    void extract_shouldThrowException_whenEachRoleIsNotInstanceOfString() {
        String token = "token";
        Claims claims = mock(Claims.class);

        when(claims.get(UserTokenClaim.ID.getClaim(), String.class)).thenReturn("id");
        when(claims.get(UserTokenClaim.ROLES.getClaim())).thenReturn(List.of(1, 2));

        when(jwtService.extractAllClaims(token)).thenReturn(claims);

        assertThatThrownBy(() -> claimsParser.extract(token)).isInstanceOf(BadCredentialsException.class)
                .hasMessage("Invalid token.");

        verify(jwtService).extractAllClaims(token);
        verify(claims).get(UserTokenClaim.ID.getClaim(), String.class);
        verify(claims).get(UserTokenClaim.ROLES.getClaim());
        verify(claims, never()).get(UserTokenClaim.VERIFIED.getClaim(), Boolean.class);
        verify(claims, never()).get(UserTokenClaim.STATUS.getClaim(), String.class);
    }

    @Test
    void extract_shouldThrowException_whenRolesAreEmpty() {
        String token = "token";
        Claims claims = mock(Claims.class);

        when(claims.get(UserTokenClaim.ID.getClaim(), String.class)).thenReturn("id");
        when(claims.get(UserTokenClaim.ROLES.getClaim())).thenReturn(List.of());

        when(jwtService.extractAllClaims(token)).thenReturn(claims);

        assertThatThrownBy(() -> claimsParser.extract(token)).isInstanceOf(BadCredentialsException.class)
                .hasMessage("Invalid token.");

        verify(jwtService).extractAllClaims(token);
        verify(claims).get(UserTokenClaim.ID.getClaim(), String.class);
        verify(claims).get(UserTokenClaim.ROLES.getClaim());
        verify(claims, never()).get(UserTokenClaim.VERIFIED.getClaim(), Boolean.class);
        verify(claims, never()).get(UserTokenClaim.STATUS.getClaim(), String.class);
    }

    @Test
    void extract_shouldThrowException_whenEmailVerificationIsNotInstanceOfBoolean() {
        String token = "token";
        Claims claims = mock(Claims.class);
        UserTokenClaims userTokenClaims = new UserTokenClaims("id", UserStatus.ACTIVE, List.of("USER"), true);

        when(claims.get(UserTokenClaim.ID.getClaim(), String.class)).thenReturn(userTokenClaims.userId());
        when(claims.get(UserTokenClaim.ROLES.getClaim())).thenReturn(userTokenClaims.roles());
        when(claims.get(UserTokenClaim.VERIFIED.getClaim(), Boolean.class)).thenThrow(new RequiredTypeException(anyString()));

        when(jwtService.extractAllClaims(token)).thenReturn(claims);

        assertThatThrownBy(() -> claimsParser.extract(token)).isInstanceOf(RequiredTypeException.class);

        verify(jwtService).extractAllClaims(token);

        verify(claims).get(UserTokenClaim.ID.getClaim(), String.class);
        verify(claims).get(UserTokenClaim.ROLES.getClaim());
        verify(claims).get(UserTokenClaim.VERIFIED.getClaim(), Boolean.class);
        verify(claims, never()).get(UserTokenClaim.STATUS.getClaim(), String.class);
    }

    @Test
    void extract_shouldThrowException_whenUserStatusIsNotInstanceOfString() {
        String token = "token";
        Claims claims = mock(Claims.class);
        UserTokenClaims userTokenClaims = new UserTokenClaims("id", UserStatus.ACTIVE, List.of("USER"), true);

        when(claims.get(UserTokenClaim.ID.getClaim(), String.class)).thenReturn(userTokenClaims.userId());
        when(claims.get(UserTokenClaim.ROLES.getClaim())).thenReturn(userTokenClaims.roles());
        when(claims.get(UserTokenClaim.VERIFIED.getClaim(), Boolean.class)).thenReturn(userTokenClaims.emailVerification());
        when(claims.get(UserTokenClaim.STATUS.getClaim(), String.class)).thenThrow(new RequiredTypeException(anyString()));

        when(jwtService.extractAllClaims(token)).thenReturn(claims);

        assertThatThrownBy(() -> claimsParser.extract(token)).isInstanceOf(RequiredTypeException.class);

        verify(jwtService).extractAllClaims(token);
        verify(claims).get(UserTokenClaim.ID.getClaim(), String.class);
        verify(claims).get(UserTokenClaim.ROLES.getClaim());
        verify(claims).get(UserTokenClaim.VERIFIED.getClaim(), Boolean.class);
        verify(claims).get(UserTokenClaim.STATUS.getClaim(), String.class);
    }

    @Test
    void extract_shouldThrowException_whenUserStatusNotValueOfEnum() {
        String token = "token";
        Claims claims = mock(Claims.class);
        UserTokenClaims userTokenClaims = new UserTokenClaims("id", UserStatus.ACTIVE, List.of("USER"), true);

        when(claims.get(UserTokenClaim.ID.getClaim(), String.class)).thenReturn(userTokenClaims.userId());
        when(claims.get(UserTokenClaim.ROLES.getClaim())).thenReturn(userTokenClaims.roles());
        when(claims.get(UserTokenClaim.VERIFIED.getClaim(), Boolean.class)).thenReturn(userTokenClaims.emailVerification());
        when(claims.get(UserTokenClaim.STATUS.getClaim(), String.class)).thenReturn("not_enum");

        when(jwtService.extractAllClaims(token)).thenReturn(claims);

        assertThatThrownBy(() -> claimsParser.extract(token)).isInstanceOf(BadCredentialsException.class)
                .hasMessage("Invalid token.");

        verify(jwtService).extractAllClaims(token);
        verify(claims).get(UserTokenClaim.ID.getClaim(), String.class);
        verify(claims).get(UserTokenClaim.ROLES.getClaim());
        verify(claims).get(UserTokenClaim.VERIFIED.getClaim(), Boolean.class);
        verify(claims).get(UserTokenClaim.STATUS.getClaim(), String.class);
    }
}
