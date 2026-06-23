package com.vendo.product_service.adapter.security.in;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vendo.product_service.domain.user.User;
import com.vendo.product_service.test_utils.builder.UserDataBuilder;
import com.vendo.product_service.test_utils.controller.RequestPing;
import com.vendo.product_service.test_utils.security.SecurityContextService;
import com.vendo.security_lib.exception.response.ExceptionResponse;
import com.vendo.security_lib.type.AuthHeader;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static com.vendo.core_lib.constants.Delimiters.COMMA_DELIMITER;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AuthFilterIntegrationTest {

    private final User user = UserDataBuilder.withAllFields().build();

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void doFilterInternal_shouldPassAuthorization_whenUserAlreadyAuthorized() throws Exception {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                user,
                null,
                null);

        MockHttpServletResponse response = mockMvc.perform(get("/ping").with(authentication(authToken)))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        String responseContent = response.getContentAsString();
        assertThat(responseContent).isNotBlank();
        assertThat(responseContent).isEqualTo("pong");
    }

    @Test
    void doFilterInternal_shouldReturnUnsupportedMediaType_whenTextPlainType() throws Exception {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                user,
                null,
                null);
        RequestPing request = new RequestPing("pong");

        MockHttpServletResponse response = mockMvc.perform(post("/ping").with(authentication(authToken))
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.TEXT_PLAIN))
                .andExpect(status().isUnsupportedMediaType())
                .andReturn().getResponse();

        String responseContent = response.getContentAsString();
        assertThat(responseContent).isNotBlank();
        ExceptionResponse exceptionResponse = objectMapper.readValue(responseContent, ExceptionResponse.class);

        assertThat(exceptionResponse.getMessage()).isEqualTo("Unsupported media type.");
        assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value());
        assertThat(exceptionResponse.getPath()).isEqualTo("/ping");
    }

    @Test
    void doFilterInternal_shouldReturnUnauthorized_whenNoHeadersInRequest() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(get("/ping"))
                .andExpect(status().isUnauthorized())
                .andReturn()
                .getResponse();

        String responseContent = response.getContentAsString();
        assertThat(responseContent).isNotBlank();
        ExceptionResponse exceptionResponse = objectMapper.readValue(responseContent, ExceptionResponse.class);

        assertThat(exceptionResponse.getMessage()).isEqualTo("Unauthorized.");
        assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
        assertThat(exceptionResponse.getPath()).isEqualTo("/ping");
    }

    @Test
    void doFilterInternal_shouldSuccessfullyFilter_whenCorrectHeaders() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(get("/ping")
                        .headers(SecurityContextService.extractHeaders(user)))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        String responseContent = response.getContentAsString();
        assertThat(responseContent).isNotBlank();
        assertThat(responseContent).isEqualTo("pong");
    }

    @Test
    void doFilterInternal_shouldReturnUnauthorized_whenIdHeaderIsMissing() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(get("/ping")
                        .header(AuthHeader.STATUS.getHeader(), user.status().name())
                        .header(AuthHeader.ROLES.getHeader(), String.join(COMMA_DELIMITER, user.toRoleNames())))
                .andExpect(status().isUnauthorized())
                .andReturn()
                .getResponse();

        String responseContent = response.getContentAsString();
        assertThat(responseContent).isNotBlank();
        ExceptionResponse exceptionResponse = objectMapper.readValue(responseContent, ExceptionResponse.class);

        assertThat(exceptionResponse.getMessage()).isEqualTo("Unauthorized.");
        assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
        assertThat(exceptionResponse.getPath()).isEqualTo("/ping");
    }

    @Test
    void doFilterInternal_shouldReturnUnauthorized_whenStatusIsInvalidValue() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(get("/ping")
                        .header(AuthHeader.ID.getHeader(), user.id())
                        .header(AuthHeader.STATUS.getHeader(), "invalid_value")
                        .header(AuthHeader.ROLES.getHeader(), String.join(COMMA_DELIMITER, user.toRoleNames())))
                .andExpect(status().isUnauthorized())
                .andReturn()
                .getResponse();

        String responseContent = response.getContentAsString();
        assertThat(responseContent).isNotBlank();
        ExceptionResponse exceptionResponse = objectMapper.readValue(responseContent, ExceptionResponse.class);

        assertThat(exceptionResponse.getMessage()).isEqualTo("Unauthorized.");
        assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
        assertThat(exceptionResponse.getPath()).isEqualTo("/ping");
    }

    @Test
    void doFilterInternal_shouldReturnUnauthorized_whenRoleIsInvalidValue() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(get("/ping")
                        .header(AuthHeader.ID.getHeader(), user.id())
                        .header(AuthHeader.STATUS.getHeader(), user.status().name())
                        .header(AuthHeader.ROLES.getHeader(), "invalid_role"))
                .andExpect(status().isUnauthorized())
                .andReturn()
                .getResponse();

        String responseContent = response.getContentAsString();
        assertThat(responseContent).isNotBlank();
        ExceptionResponse exceptionResponse = objectMapper.readValue(responseContent, ExceptionResponse.class);

        assertThat(exceptionResponse.getMessage()).isEqualTo("Unauthorized.");
        assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
        assertThat(exceptionResponse.getPath()).isEqualTo("/ping");
    }
}
