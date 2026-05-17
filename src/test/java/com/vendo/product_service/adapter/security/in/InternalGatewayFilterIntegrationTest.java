package com.vendo.product_service.adapter.security.in;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vendo.core_lib.type.ServiceName;
import com.vendo.product_service.adapter.security.out.jwt.parser.InternalTokenClaims;
import com.vendo.product_service.adapter.security.out.jwt.parser.TokenClaimsParser;
import com.vendo.product_service.test_utils.builder.InternalTokenClaimsDataBuilder;
import com.vendo.product_service.test_utils.security.SecurityContextService;
import com.vendo.security_lib.exception.response.ExceptionResponse;
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

import java.util.List;
import java.util.Set;

import static com.vendo.security_lib.constants.AuthConstants.AUTHORIZATION_HEADER;
import static com.vendo.security_lib.constants.AuthConstants.BEARER_PREFIX;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class InternalGatewayFilterIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TokenClaimsParser tokenClaimsParser;

    @Test
    void doFilterInternal_shouldSuccessfullyFilter() throws Exception {
        String token = "valid_token";
        InternalTokenClaims claims = InternalTokenClaimsDataBuilder.buildWithAllFields().build();

        when(tokenClaimsParser.extractInternal(token)).thenReturn(claims);

        String content = mockMvc.perform(get("/internal/test/ping")
                        .header(AUTHORIZATION_HEADER, BEARER_PREFIX + token))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertThat(content).isNotBlank();
        assertThat(content).isEqualTo("pong");
    }

    @Test
    void doFilterInternal_shouldSuccessfullyFilter_whenAlreadyAuthorized() throws Exception {
        InternalTokenClaims claims = InternalTokenClaimsDataBuilder.buildWithAllFields().build();
        Authentication auth = SecurityContextService.initializeAuth(claims);

        String content = mockMvc.perform(get("/internal/test/ping").with(authentication(auth)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertThat(content).isNotBlank();
        assertThat(content).isEqualTo("pong");
    }

    @Test
    void doFilterInternal_shouldReturnUnauthorized_whenNoToken() throws Exception {
        String requestPath = "/internal/test/ping";

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
        String requestPath = "/internal/test/ping";
        String token = "valid_token";

        String content = mockMvc.perform(get(requestPath).header(AUTHORIZATION_HEADER, token))
                .andExpect(status().isUnauthorized())
                .andReturn().getResponse().getContentAsString();

        assertThat(content).isNotBlank();

        ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
        assertThat(exceptionResponse).isNotNull();
        assertThat(exceptionResponse.getMessage()).isEqualTo("Invalid token.");
        assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
        assertThat(exceptionResponse.getPath()).isEqualTo(requestPath);
    }

    @Test
    void doFilterInternal_shouldReturnUnauthorized_whenInvalidToken() throws Exception {
        String requestPath = "/internal/test/ping";
        String invalidToken = "invalid_token";

        when(tokenClaimsParser.extractInternal(invalidToken)).thenThrow(new BadCredentialsException("Invalid token."));

        String content = mockMvc.perform(get(requestPath).header(AUTHORIZATION_HEADER, BEARER_PREFIX + invalidToken))
                .andExpect(status().isUnauthorized())
                .andReturn().getResponse().getContentAsString();

        assertThat(content).isNotBlank();

        ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
        assertThat(exceptionResponse).isNotNull();
        assertThat(exceptionResponse.getMessage()).isEqualTo("Invalid token.");
        assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
        assertThat(exceptionResponse.getPath()).isEqualTo(requestPath);
    }

    @Test
    void doFilterInternal_shouldReturnUnauthorized_whenAudienceClaimIsNotProductService() throws Exception {
        InternalTokenClaims payload = InternalTokenClaimsDataBuilder.buildWithAllFields()
                .audience(Set.of(ServiceName.AUTH_SERVICE.toString()))
                .build();
        String requestPath = "/internal/test/ping";
        String token = "valid_token";

        when(tokenClaimsParser.extractInternal(token)).thenReturn(payload);

        String content = mockMvc.perform(get(requestPath).header(AUTHORIZATION_HEADER, BEARER_PREFIX + token))
                .andExpect(status().isUnauthorized())
                .andReturn().getResponse().getContentAsString();

        assertThat(content).isNotBlank();

        ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
        assertThat(exceptionResponse).isNotNull();
        assertThat(exceptionResponse.getMessage()).isEqualTo("Invalid token.");
        assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
        assertThat(exceptionResponse.getPath()).isEqualTo(requestPath);
    }

    @Test
    void doFilterInternal_shouldReturnUnauthorized_whenRoleIsNotInternal() throws Exception {
        InternalTokenClaims payload = InternalTokenClaimsDataBuilder.buildWithAllFields()
                .roles(List.of("NOT_INTERNAL"))
                .build();
        String requestPath = "/internal/test/ping";
        String token = "valid_token";

        when(tokenClaimsParser.extractInternal(token)).thenReturn(payload);

        String content = mockMvc.perform(get(requestPath).header(AUTHORIZATION_HEADER, BEARER_PREFIX + token))
                .andExpect(status().isUnauthorized())
                .andReturn().getResponse().getContentAsString();

        assertThat(content).isNotBlank();

        ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
        assertThat(exceptionResponse).isNotNull();
        assertThat(exceptionResponse.getMessage()).isEqualTo("Invalid token.");
        assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
        assertThat(exceptionResponse.getPath()).isEqualTo(requestPath);
    }
}
