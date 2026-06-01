package com.vendo.product_service.adapter.security.out.jwt;

import com.vendo.core_lib.type.ServiceName;
import com.vendo.core_lib.type.ServiceRole;
import com.vendo.product_service.adapter.security.out.jwt.parser.JwtInternalAuthenticationParser;
import com.vendo.product_service.adapter.security.out.jwt.parser.TokenClaims;
import com.vendo.product_service.adapter.security.out.props.InternalJwtProperties;
import com.vendo.security_lib.type.InternalTokenClaim;
import com.vendo.utils_lib.AssertionUtils;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JwtInternalAuthenticationParserTest {

    private final String token = "token", secret = "secret";

    @InjectMocks
    private JwtInternalAuthenticationParser claimsParser;

    @Mock
    private JwtService jwtService;
    @Mock
    private InternalJwtProperties jwtProperties;

    @Test
    void extract_shouldReturnInternalTokenClaims() {
        Claims claims = mock(Claims.class);
        TokenClaims tokenClaims = new TokenClaims("subject", List.of(ServiceRole.INTERNAL.name()), Set.of(ServiceName.INDEXER_SERVICE.getServiceName()));

        when(jwtProperties.getKey()).thenReturn(secret);
        when(jwtService.extractAllClaims(token, secret)).thenReturn(claims);

        when(claims.get(InternalTokenClaim.ROLES.getClaim())).thenReturn(tokenClaims.roles());
        when(claims.getAudience()).thenReturn(tokenClaims.audience());
        when(claims.getSubject()).thenReturn(tokenClaims.subject());

        TokenClaims extract = claimsParser.extract(token);
        AssertionUtils.assertFrom(extract, tokenClaims);

        verify(jwtService).extractAllClaims(token, secret);
        verify(claims).get(InternalTokenClaim.ROLES.getClaim());
        verify(claims).getAudience();
        verify(claims).getSubject();
    }

    @Test
    void extract_shouldThrowException_whenRolesAreEmpty() {
        Claims claims = mock(Claims.class);

        when(jwtProperties.getKey()).thenReturn(secret);
        when(jwtService.extractAllClaims(token, secret)).thenReturn(claims);
        when(claims.get(InternalTokenClaim.ROLES.getClaim())).thenReturn(List.of());

        assertThatThrownBy(() -> claimsParser.extract(token)).hasMessage("Invalid token.").isInstanceOf(BadCredentialsException.class);

        verify(jwtService).extractAllClaims(token, secret);
        verify(claims).get(InternalTokenClaim.ROLES.getClaim());
        verify(claims, never()).getAudience();
        verify(claims, never()).getSubject();
    }

    @Test
    void extract_shouldThrowException_whenRolesNotInstanceOfList() {
        Claims claims = mock(Claims.class);

        when(jwtProperties.getKey()).thenReturn(secret);
        when(jwtService.extractAllClaims(token, secret)).thenReturn(claims);
        when(claims.get(InternalTokenClaim.ROLES.getClaim())).thenReturn("");

        assertThatThrownBy(() -> claimsParser.extract(token)).hasMessage("Invalid token.").isInstanceOf(BadCredentialsException.class);

        verify(jwtService).extractAllClaims(token, secret);
        verify(claims).get(InternalTokenClaim.ROLES.getClaim());
        verify(claims, never()).getAudience();
        verify(claims, never()).getSubject();
    }

    @Test
    void extract_shouldThrowException_whenAudienceIsEmpty() {
        Claims claims = mock(Claims.class);
        TokenClaims tokenClaims = new TokenClaims("subject", List.of(ServiceRole.INTERNAL.name()), Set.of(ServiceName.INDEXER_SERVICE.getServiceName()));

        when(jwtProperties.getKey()).thenReturn(secret);
        when(jwtService.extractAllClaims(token, secret)).thenReturn(claims);

        when(claims.get(InternalTokenClaim.ROLES.getClaim())).thenReturn(tokenClaims.roles());
        when(claims.getAudience()).thenReturn(Set.of());

        assertThatThrownBy(() -> claimsParser.extract(token)).hasMessage("Invalid token.").isInstanceOf(BadCredentialsException.class);

        verify(jwtService).extractAllClaims(token, secret);
        verify(claims).get(InternalTokenClaim.ROLES.getClaim());
        verify(claims).getAudience();
        verify(claims, never()).getSubject();
    }

}
