package com.vendo.product_service.adapter.security.in;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vendo.core_lib.type.ServiceName;
import com.vendo.product_service.adapter.security.out.props.JwtProperties;
import com.vendo.product_service.test_utils.builder.TokenClaimsDataBuilder;
import com.vendo.product_service.test_utils.security.SecurityContextService;
import com.vendo.security_lib.exception.ExceptionResponse;
import com.vendo.security_starter.jwt.parser.TokenClaims;
import com.vendo.security_starter.jwt.parser.TokenClaimsParser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static com.vendo.security_lib.http.HttpUtils.AUTHORIZATION_HEADER;
import static com.vendo.security_lib.http.HttpUtils.BEARER_PREFIX;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class InternalFilterIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtProperties properties;

    @MockitoBean
    private TokenClaimsParser tokenClaimsParser;

    @Test
    void doFilterInternal_shouldSuccessfullyFilter() throws Exception {
        String token = "valid_token";
        TokenClaims claims = TokenClaimsDataBuilder.buildWithAllFields().build();

        when(tokenClaimsParser.extract(token, properties.getInternal().key())).thenReturn(claims);

        String content = mockMvc.perform(get("/internal/ping")
                        .header(AUTHORIZATION_HEADER, BEARER_PREFIX + token))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertThat(content).isNotBlank();
        assertThat(content).isEqualTo("pong");
    }

    @Test
    void doFilterInternal_shouldSuccessfullyFilter_whenAlreadyAuthorized() throws Exception {
        TokenClaims claims = TokenClaimsDataBuilder.buildWithAllFields().build();
        Authentication auth = SecurityContextService.initializeAuth(claims);

        String content = mockMvc.perform(get("/internal/ping").with(authentication(auth)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertThat(content).isNotBlank();
        assertThat(content).isEqualTo("pong");
    }

    @Test
    void doFilterInternal_shouldReturnUnauthorized_whenNoToken() throws Exception {
        String requestPath = "/internal/ping";

        String content = mockMvc.perform(get(requestPath))
                .andExpect(status().isUnauthorized())
                .andReturn().getResponse().getContentAsString();

        assertThat(content).isNotBlank();

        ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
        assertThat(exceptionResponse).isNotNull();
        assertThat(exceptionResponse.getMessage()).isEqualTo("Unauthorized.");
        assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
        assertThat(exceptionResponse.getPath()).isEqualTo(requestPath);
    }

    @Test
    void doFilterInternal_shouldReturnUnauthorized_whenTokenWithoutBearerPrefix() throws Exception {
        String requestPath = "/ping/pong";
        String token = "valid_token";

        String content = mockMvc.perform(get(requestPath).header(AUTHORIZATION_HEADER, token))
                .andExpect(status().isUnauthorized())
                .andReturn().getResponse().getContentAsString();

        assertThat(content).isNotBlank();

        ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
        assertThat(exceptionResponse).isNotNull();
        assertThat(exceptionResponse.getMessage()).isEqualTo("Unauthorized.");
        assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
        assertThat(exceptionResponse.getPath()).isEqualTo(requestPath);
    }

    @Test
    void doFilterInternal_shouldReturnUnauthorized_whenInvalidToken() throws Exception {
        String requestPath = "/internal/ping";
        String invalidToken = "invalid_token";

        when(tokenClaimsParser.extract(invalidToken, properties.getInternal().key())).thenThrow(new BadCredentialsException("Invalid token."));

        String content = mockMvc.perform(get(requestPath).header(AUTHORIZATION_HEADER, BEARER_PREFIX + invalidToken))
                .andExpect(status().isUnauthorized())
                .andReturn().getResponse().getContentAsString();

        assertThat(content).isNotBlank();

        ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
        assertThat(exceptionResponse).isNotNull();
        assertThat(exceptionResponse.getMessage()).isEqualTo("Unauthorized.");
        assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
        assertThat(exceptionResponse.getPath()).isEqualTo(requestPath);
    }

    @Test
    void doFilterInternal_shouldReturnUnauthorized_whenAudienceClaimIsNotProductService() throws Exception {
        TokenClaims payload = TokenClaimsDataBuilder.buildWithAllFields()
                .audience(Set.of(ServiceName.AUTH_SERVICE.toString()))
                .build();
        String requestPath = "/internal/ping";
        String token = "valid_token";

        when(tokenClaimsParser.extract(token, properties.getInternal().key())).thenReturn(payload);

        String content = mockMvc.perform(get(requestPath).header(AUTHORIZATION_HEADER, BEARER_PREFIX + token))
                .andExpect(status().isUnauthorized())
                .andReturn().getResponse().getContentAsString();

        assertThat(content).isNotBlank();

        ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
        assertThat(exceptionResponse).isNotNull();
        assertThat(exceptionResponse.getMessage()).isEqualTo("Unauthorized.");
        assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
        assertThat(exceptionResponse.getPath()).isEqualTo(requestPath);
    }

    @Test
    void doFilterInternal_shouldReturnUnauthorized_whenRoleIsNotInternal() throws Exception {
        TokenClaims payload = TokenClaimsDataBuilder.buildWithAllFields()
                .roles(Set.of("NOT_INTERNAL"))
                .build();
        String requestPath = "/internal/ping";
        String token = "valid_token";

        when(tokenClaimsParser.extract(token, properties.getInternal().key())).thenReturn(payload);

        String content = mockMvc.perform(get(requestPath).header(AUTHORIZATION_HEADER, BEARER_PREFIX + token))
                .andExpect(status().isUnauthorized())
                .andReturn().getResponse().getContentAsString();

        assertThat(content).isNotBlank();

        ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
        assertThat(exceptionResponse).isNotNull();
        assertThat(exceptionResponse.getMessage()).isEqualTo("Unauthorized.");
        assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
        assertThat(exceptionResponse.getPath()).isEqualTo(requestPath);
    }

    @Test
    void doFilterInternal_shouldReturnUnauthorized_whenSubjectIsNotAllowed() throws Exception {
        TokenClaims payload = TokenClaimsDataBuilder.buildWithAllFields()
                .subject("not_allowed_subject")
                .build();
        String requestPath = "/internal/ping";
        String token = "valid_token";

        when(tokenClaimsParser.extract(token, properties.getInternal().key())).thenReturn(payload);

        String content = mockMvc.perform(get(requestPath).header(AUTHORIZATION_HEADER, BEARER_PREFIX + token))
                .andExpect(status().isUnauthorized())
                .andReturn().getResponse().getContentAsString();

        assertThat(content).isNotBlank();

        ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
        assertThat(exceptionResponse).isNotNull();
        assertThat(exceptionResponse.getMessage()).isEqualTo("Unauthorized.");
        assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
        assertThat(exceptionResponse.getPath()).isEqualTo(requestPath);
    }
}
