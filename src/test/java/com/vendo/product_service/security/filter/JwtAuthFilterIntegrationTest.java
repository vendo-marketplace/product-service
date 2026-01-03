package com.vendo.product_service.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vendo.common.exception.ExceptionResponse;
import com.vendo.domain.user.common.type.UserRole;
import com.vendo.domain.user.common.type.UserStatus;
import com.vendo.product_service.common.builder.JwtPayloadDataBuilder;
import com.vendo.product_service.common.dto.JwtPayload;
import com.vendo.product_service.service.JwtService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.google.common.net.HttpHeaders.AUTHORIZATION;
import static com.vendo.product_service.service.JwtService.*;
import static com.vendo.security.common.constants.AuthConstants.BEARER_PREFIX;
import static com.vendo.security.common.type.TokenClaim.*;
import static org.apache.kafka.common.security.oauthbearer.internals.secured.HttpAccessTokenRetriever.AUTHORIZATION_HEADER;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
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

    @Autowired
    private JwtService jwtService;

    @Autowired
    private JwtPayloadDataBuilder jwtPayloadDataBuilder;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_shouldPassAuthorization_whenUserAlreadyAuthorized() throws Exception {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                "existingUser",
                null,
                null);

        MockHttpServletResponse response = mockMvc.perform(
                        get("/test/ping").with(authentication(authToken)))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        String responseContent = response.getContentAsString();
        assertThat(responseContent).isNotBlank();
        assertThat(responseContent).isEqualTo("pong");
    }

    @Test
    void doFilterInternal_shouldReturnUnauthorized_whenNoTokenInRequest() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(get("/test/ping"))
                .andExpect(status().isUnauthorized())
                .andReturn()
                .getResponse();

        String responseContent = response.getContentAsString();
        assertThat(responseContent).isNotBlank();
        ExceptionResponse exceptionResponse = objectMapper.readValue(responseContent, ExceptionResponse.class);

        assertThat(exceptionResponse.getMessage()).isEqualTo("Invalid token.");
        assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
        assertThat(exceptionResponse.getPath()).isEqualTo("/test/ping");
    }

    @Test
    void doFilterInternal_shouldReturnUnauthorized_whenTokenWithoutBearerPrefix() throws Exception {
        JwtPayload jwtPayload = jwtPayloadDataBuilder.buildValidJwtPayload().build();
        String token = jwtService.generateAccessToken(jwtPayload);

        MockHttpServletResponse response = mockMvc.perform(get("/test/ping")
                        .header(AUTHORIZATION, token))
                .andExpect(status().isUnauthorized())
                .andReturn()
                .getResponse();

        String responseContent = response.getContentAsString();
        assertThat(responseContent).isNotBlank();
        ExceptionResponse exceptionResponse = objectMapper.readValue(responseContent, ExceptionResponse.class);

        assertThat(exceptionResponse.getMessage()).isEqualTo("Invalid token.");
        assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
        assertThat(exceptionResponse.getPath()).isEqualTo("/test/ping");
    }

    @Test
    void doFilterInternal_shouldReturnForbidden_whenUserBlocked() throws Exception {
        Map<String, Object> claims = Map.of(
                USER_ID_CLAIM.getClaim(), String.valueOf(UUID.randomUUID()),
                EMAIL_VERIFIED_CLAIM.getClaim(), true,
                STATUS_CLAIM.getClaim(), UserStatus.BLOCKED,
                ROLES_CLAIM.getClaim(), List.of(UserRole.USER)
        );
        JwtPayload jwtPayload = jwtPayloadDataBuilder.buildValidJwtPayload().claims(claims).build();
        String token = jwtService.generateAccessToken(jwtPayload);

        MockHttpServletResponse response = mockMvc.perform(get("/test/ping").header(AUTHORIZATION_HEADER, BEARER_PREFIX + token))
                .andExpect(status().isForbidden())
                .andReturn().getResponse();

        String responseContent = response.getContentAsString();
        assertThat(responseContent).isNotBlank();
        ExceptionResponse exceptionResponse = objectMapper.readValue(responseContent, ExceptionResponse.class);

        assertThat(exceptionResponse.getMessage()).isEqualTo("User is blocked.");
        assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
        assertThat(exceptionResponse.getPath()).isEqualTo("/test/ping");
    }


    @Test
    void doFilterInternal_shouldReturnUnauthorized_whenTokenExpired() throws Exception {
        JwtPayload jwtPayload = jwtPayloadDataBuilder.buildValidJwtPayload().expiration(0).build();
        String expiredToken = jwtService.generateAccessToken(jwtPayload);

        MockHttpServletResponse response = mockMvc.perform(get("/test/ping").header(AUTHORIZATION_HEADER, BEARER_PREFIX + expiredToken))
                .andExpect(status().isUnauthorized())
                .andReturn().getResponse();

        String responseContent = response.getContentAsString();
        assertThat(responseContent).isNotBlank();
        ExceptionResponse exceptionResponse = objectMapper.readValue(responseContent, ExceptionResponse.class);

        assertThat(exceptionResponse.getMessage()).isEqualTo("Token has expired.");
        assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
        assertThat(exceptionResponse.getPath()).isEqualTo("/test/ping");
    }

    @Test
    void doFilterInternal_shouldReturnForbidden_whenUserIsIncomplete() throws Exception {
        Map<String, Object> claims = Map.of(
                USER_ID_CLAIM.getClaim(), String.valueOf(UUID.randomUUID()),
                EMAIL_VERIFIED_CLAIM.getClaim(), true,
                STATUS_CLAIM.getClaim(), UserStatus.INCOMPLETE,
                ROLES_CLAIM.getClaim(), List.of(UserRole.USER)
        );
        JwtPayload jwtPayload = jwtPayloadDataBuilder.buildValidJwtPayload().claims(claims).build();
        String token = jwtService.generateAccessToken(jwtPayload);


        MockHttpServletResponse response = mockMvc.perform(get("/test/ping")
                        .header(AUTHORIZATION, BEARER_PREFIX + token))
                .andExpect(status().isForbidden())
                .andReturn()
                .getResponse();

        String responseContent = response.getContentAsString();
        assertThat(responseContent).isNotBlank();
        ExceptionResponse exceptionResponse = objectMapper.readValue(responseContent, ExceptionResponse.class);

        assertThat(exceptionResponse.getMessage()).isEqualTo("User is unactive.");
        assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
        assertThat(exceptionResponse.getPath()).isEqualTo("/test/ping");
    }
    @Test
    void doFilterInternal_shouldReturnUnauthorized_whenTokenWithoutRoles() throws Exception {
        Map<String, Object> claims = Map.of(
                USER_ID_CLAIM.getClaim(), String.valueOf(UUID.randomUUID()),
                EMAIL_VERIFIED_CLAIM.getClaim(), true,
                STATUS_CLAIM.getClaim(), UserStatus.ACTIVE
        );
        JwtPayload jwtPayload = jwtPayloadDataBuilder.buildValidJwtPayload().claims(claims).build();
        String tokenWithoutRoles = jwtService.generateAccessToken(jwtPayload);

        MockHttpServletResponse response = mockMvc.perform(get("/test/ping").header(AUTHORIZATION_HEADER, BEARER_PREFIX + tokenWithoutRoles))
                .andExpect(status().isUnauthorized())
                .andReturn().getResponse();

        String responseContent = response.getContentAsString();
        assertThat(responseContent).isNotBlank();
        ExceptionResponse exceptionResponse = objectMapper.readValue(responseContent, ExceptionResponse.class);

        assertThat(exceptionResponse.getMessage()).isEqualTo("Invalid token.");
        assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
        assertThat(exceptionResponse.getPath()).isEqualTo("/test/ping");
    }

    @Test
    void doFilterInternal_shouldReturnUnauthorized_whenUserStatusMissing() throws Exception {
        Map<String, Object> claims = Map.of(
                USER_ID_CLAIM.getClaim(), String.valueOf(UUID.randomUUID()),
                EMAIL_VERIFIED_CLAIM.getClaim(), true,
                ROLES_CLAIM.getClaim(), List.of(UserRole.USER)
        );
        JwtPayload jwtPayload = jwtPayloadDataBuilder.buildValidJwtPayload().claims(claims).build();
        String tokenWithoutStatus = jwtService.generateAccessToken(jwtPayload);

        MockHttpServletResponse response = mockMvc.perform(get("/test/ping").header(AUTHORIZATION_HEADER, BEARER_PREFIX + tokenWithoutStatus))
                .andExpect(status().isUnauthorized())
                .andReturn().getResponse();

        String responseContent = response.getContentAsString();
        assertThat(responseContent).isNotBlank();
        ExceptionResponse exceptionResponse = objectMapper.readValue(responseContent, ExceptionResponse.class);

        assertThat(exceptionResponse.getMessage()).isEqualTo("Invalid token.");
        assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
        assertThat(exceptionResponse.getPath()).isEqualTo("/test/ping");
    }

    @Test
    void doFilterInternal_shouldReturnUnauthorized_whenTokenHasInvalidStatus() throws Exception {
        Map<String, Object> claims = Map.of(
                USER_ID_CLAIM.getClaim(), String.valueOf(UUID.randomUUID()),
                EMAIL_VERIFIED_CLAIM.getClaim(), true,
                ROLES_CLAIM.getClaim(), List.of(UserRole.USER),
                STATUS_CLAIM.getClaim(), INVALID_STATUS
        );
        JwtPayload jwtPayload = jwtPayloadDataBuilder.buildValidJwtPayload().claims(claims).build();
        String tokenWithInvalidStatus = jwtService.generateAccessToken(jwtPayload);

        MockHttpServletResponse response = mockMvc.perform(get("/test/ping").header(AUTHORIZATION_HEADER, BEARER_PREFIX + tokenWithInvalidStatus))
                .andExpect(status().isUnauthorized())
                .andReturn().getResponse();

        String responseContent = response.getContentAsString();
        assertThat(responseContent).isNotBlank();
        ExceptionResponse exceptionResponse = objectMapper.readValue(responseContent, ExceptionResponse.class);

        assertThat(exceptionResponse.getMessage()).isEqualTo("Invalid token.");
        assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
        assertThat(exceptionResponse.getPath()).isEqualTo("/test/ping");
    }

    @Test
    void doFilterInternal_shouldReturnUnauthorized_whenTokenHasInvalidFormat() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(get("/test/ping").header(AUTHORIZATION_HEADER, BEARER_PREFIX + INVALID_TOKEN_FORMAT))
                .andExpect(status().isUnauthorized())
                .andReturn().getResponse();

        String responseContent = response.getContentAsString();
        assertThat(responseContent).isNotBlank();
        ExceptionResponse exceptionResponse = objectMapper.readValue(responseContent, ExceptionResponse.class);

        assertThat(exceptionResponse.getMessage()).isEqualTo("Invalid token.");
        assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
        assertThat(exceptionResponse.getPath()).isEqualTo("/test/ping");
    }

    @Test
    void doFilterInternal_shouldReturnUnauthorized_whenTokenHasInvalidSignature() throws Exception {
        JwtPayload jwtPayload = jwtPayloadDataBuilder.buildValidJwtPayload().key(jwtService.getBadSecretKey()).build();
        String invalidSignedToken = jwtService.generateAccessToken(jwtPayload);

        MockHttpServletResponse response = mockMvc.perform(get("/test/ping").header(AUTHORIZATION_HEADER, BEARER_PREFIX + invalidSignedToken))
                .andExpect(status().isUnauthorized())
                .andReturn().getResponse();

        String responseContent = response.getContentAsString();
        assertThat(responseContent).isNotBlank();
        ExceptionResponse exceptionResponse = objectMapper.readValue(responseContent, ExceptionResponse.class);

        assertThat(exceptionResponse.getMessage()).isEqualTo("Invalid token.");
        assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
        assertThat(exceptionResponse.getPath()).isEqualTo("/test/ping");
    }

    @Test
    void doFilterInternal_shouldReturnUnauthorized_whenTokenWithoutUserId() throws Exception {
        Map<String, Object> claims = Map.of(
                EMAIL_VERIFIED_CLAIM.getClaim(), true,
                ROLES_CLAIM.getClaim(), List.of(UserRole.USER),
                STATUS_CLAIM.getClaim(), INVALID_STATUS
        );
        JwtPayload jwtPayload = jwtPayloadDataBuilder.buildValidJwtPayload().claims(claims).build();
        String tokenWithoutSubject = jwtService.generateAccessToken(jwtPayload);

        MockHttpServletResponse response = mockMvc.perform(get("/test/ping").header(AUTHORIZATION_HEADER, BEARER_PREFIX + tokenWithoutSubject))
                .andExpect(status().isUnauthorized())
                .andReturn().getResponse();

        String responseContent = response.getContentAsString();
        assertThat(responseContent).isNotBlank();
        ExceptionResponse exceptionResponse = objectMapper.readValue(responseContent, ExceptionResponse.class);

        assertThat(exceptionResponse.getMessage()).isEqualTo("Invalid token.");
        assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
        assertThat(exceptionResponse.getPath()).isEqualTo("/test/ping");
    }

}
