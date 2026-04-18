package com.vendo.product_service.adapter.security.in;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vendo.product_service.adapter.security.out.jwt.parser.TokenClaims;
import com.vendo.product_service.adapter.security.out.jwt.parser.TokenClaimsParser;
import com.vendo.security_lib.exception.response.ExceptionResponse;
import com.vendo.user_lib.type.UserRole;
import com.vendo.user_lib.type.UserStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.vendo.security_lib.constants.AuthConstants.AUTHORIZATION_HEADER;
import static com.vendo.security_lib.constants.AuthConstants.BEARER_PREFIX;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class JwtAuthFilterIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TokenClaimsParser tokenClaimsParser;

    @Test
    void doFilterInternal_shouldPassAuthorization_whenUserAlreadyAuthorized() throws Exception {
        TokenClaims claims = new TokenClaims("id", UserStatus.ACTIVE, List.of(UserRole.USER.name()), true);
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                claims,
                null,
                null);

        MockHttpServletResponse response = mockMvc.perform(get("/test/user/ping").with(authentication(authToken)))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        String responseContent = response.getContentAsString();
        assertThat(responseContent).isNotBlank();
        assertThat(responseContent).isEqualTo("pong");

        verifyNoInteractions(tokenClaimsParser);
    }

    @Test
    void doFilterInternal_shouldReturnUnauthorized_whenNoTokenInRequest() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(get("/test/user/ping"))
                .andExpect(status().isUnauthorized())
                .andReturn()
                .getResponse();

        String responseContent = response.getContentAsString();
        assertThat(responseContent).isNotBlank();
        ExceptionResponse exceptionResponse = objectMapper.readValue(responseContent, ExceptionResponse.class);

        assertThat(exceptionResponse.getMessage()).isEqualTo("Invalid token.");
        assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
        assertThat(exceptionResponse.getPath()).isEqualTo("/test/user/ping");

        verifyNoInteractions(tokenClaimsParser);
    }

    @Test
    void doFilterInternal_shouldReturnUnauthorized_whenTokenWithoutBearerPrefix() throws Exception {
        String token = "token";

        MockHttpServletResponse response = mockMvc.perform(get("/test/user/ping")
                        .header(AUTHORIZATION_HEADER, token))
                .andExpect(status().isUnauthorized())
                .andReturn()
                .getResponse();

        String responseContent = response.getContentAsString();
        assertThat(responseContent).isNotBlank();
        ExceptionResponse exceptionResponse = objectMapper.readValue(responseContent, ExceptionResponse.class);

        assertThat(exceptionResponse.getMessage()).isEqualTo("Invalid token.");
        assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
        assertThat(exceptionResponse.getPath()).isEqualTo("/test/user/ping");

        verifyNoInteractions(tokenClaimsParser);
    }

    @Test
    void doFilterInternal_shouldReturnForbidden_whenUserBlocked() throws Exception {
        String token = "token";
        TokenClaims claims = new TokenClaims("user_id", UserStatus.BLOCKED, List.of(UserRole.USER.toString()), true);

        when(tokenClaimsParser.extract(token)).thenReturn(claims);

        MockHttpServletResponse response = mockMvc.perform(get("/test/user/ping").header(AUTHORIZATION_HEADER, BEARER_PREFIX + token))
                .andExpect(status().isForbidden())
                .andReturn().getResponse();

        String responseContent = response.getContentAsString();
        assertThat(responseContent).isNotBlank();
        ExceptionResponse exceptionResponse = objectMapper.readValue(responseContent, ExceptionResponse.class);

        assertThat(exceptionResponse.getMessage()).isEqualTo("User is blocked.");
        assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
        assertThat(exceptionResponse.getPath()).isEqualTo("/test/user/ping");

        verify(tokenClaimsParser).extract(token);
    }

    @Test
    void doFilterInternal_shouldReturnUnauthorized_whenTokenExpired() throws Exception {
        String expiredToken = "expired_token";

        when(tokenClaimsParser.extract(expiredToken)).thenThrow(new BadCredentialsException("Token expired."));

        MockHttpServletResponse response = mockMvc.perform(get("/test/user/ping").header(AUTHORIZATION_HEADER, BEARER_PREFIX + expiredToken))
                .andExpect(status().isUnauthorized())
                .andReturn().getResponse();

        String responseContent = response.getContentAsString();
        assertThat(responseContent).isNotBlank();
        ExceptionResponse exceptionResponse = objectMapper.readValue(responseContent, ExceptionResponse.class);

        assertThat(exceptionResponse.getMessage()).isEqualTo("Token expired.");
        assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
        assertThat(exceptionResponse.getPath()).isEqualTo("/test/user/ping");

        verify(tokenClaimsParser).extract(expiredToken);
    }

    @Test
    void doFilterInternal_shouldReturnForbidden_whenUserIsIncomplete() throws Exception {
        String token = "token";
        TokenClaims claims = new TokenClaims("user_id", UserStatus.INCOMPLETE, List.of(UserRole.USER.toString()), true);

        when(tokenClaimsParser.extract(token)).thenReturn(claims);

        MockHttpServletResponse response = mockMvc.perform(get("/test/user/ping")
                        .header(AUTHORIZATION_HEADER, BEARER_PREFIX + token))
                .andExpect(status().isForbidden())
                .andReturn()
                .getResponse();

        String responseContent = response.getContentAsString();
        assertThat(responseContent).isNotBlank();
        ExceptionResponse exceptionResponse = objectMapper.readValue(responseContent, ExceptionResponse.class);

        assertThat(exceptionResponse.getMessage()).isEqualTo("User is unactive.");
        assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
        assertThat(exceptionResponse.getPath()).isEqualTo("/test/user/ping");

        verify(tokenClaimsParser).extract(token);
    }
}
