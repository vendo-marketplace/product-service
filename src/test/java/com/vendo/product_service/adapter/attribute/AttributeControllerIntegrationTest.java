package com.vendo.product_service.adapter.attribute;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vendo.product_service.adapter.attribute.in.dto.CreateAttributeRequest;
import com.vendo.product_service.adapter.attribute.out.mapper.DtoAttributeMapper;
import com.vendo.product_service.adapter.security.out.jwt.parser.TokenClaims;
import com.vendo.product_service.domain.attribute.model.Attribute;
import com.vendo.product_service.port.out.attribute.AttributeCommandPort;
import com.vendo.product_service.test_utils.builder.AttributeDataBuilder;
import com.vendo.product_service.test_utils.builder.CreateAttributeRequestDataBuilder;
import com.vendo.product_service.test_utils.security.SecurityContextService;
import com.vendo.security_lib.exception.response.ExceptionResponse;
import com.vendo.user_lib.type.UserRole;
import com.vendo.user_lib.type.UserStatus;
import com.vendo.utils_lib.AssertionUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AttributeControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private DtoAttributeMapper mapper;

    @MockitoBean
    private AttributeCommandPort attributeCommandPort;

    @BeforeEach
    public void setUp() {
        SecurityContextHolder.clearContext();
    }

    private TokenClaims buildTokenClaims(UserRole role) {
        return new TokenClaims("id", UserStatus.ACTIVE, List.of(role.name()), true);
    }

    @Nested
    class SaveAttribute {

        @Test
        void save_shouldSuccessfullySave() throws Exception {
            TokenClaims claims = buildTokenClaims(UserRole.ADMIN);
            Attribute attribute = AttributeDataBuilder.withAllFields();
            CreateAttributeRequest request = CreateAttributeRequestDataBuilder.withAllFields();
            ArgumentCaptor<Attribute> captor = ArgumentCaptor.forClass(Attribute.class);

            when(mapper.toAttribute(request)).thenReturn(attribute);
            doNothing().when(attributeCommandPort).save(captor.capture());

            mockMvc.perform(post("/attributes")
                            .with(authentication(SecurityContextService.initializeAuth(claims)))
                            .content(objectMapper.writeValueAsString(request))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());

            verify(mapper).toAttribute(request);
            verify(attributeCommandPort).save(attribute);

            Attribute captorValue = captor.getValue();
            AssertionUtils.assertFrom(attribute, captorValue);
        }

        @Test
        void save_shouldReturnBadRequest_whenAttributeTitleIsNotValid() throws Exception {
            TokenClaims claims = buildTokenClaims(UserRole.ADMIN);
            CreateAttributeRequest request = CreateAttributeRequestDataBuilder.withAllFields("invalid_title");

            String content = mockMvc.perform(post("/attributes")
                            .with(authentication(SecurityContextService.initializeAuth(claims)))
                            .content(objectMapper.writeValueAsString(request))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andReturn().getResponse().getContentAsString();

            ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
            assertThat(exceptionResponse).isNotNull();
            assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            assertThat(exceptionResponse.getMessage()).isEqualTo("Validation failed.");
            assertThat(exceptionResponse.getErrors()).isNotNull();
            assertThat(exceptionResponse.getErrors().size()).isEqualTo(1);
            assertThat(exceptionResponse.getErrors().get("title")).isEqualTo("Attribute name validation failed.");
            assertThat(exceptionResponse.getPath()).isEqualTo("/attributes");

            verifyNoInteractions(attributeCommandPort, mapper);
        }

        @Test
        void save_shouldReturnUnauthorized_whenNotAdmin() throws Exception {
            TokenClaims claims = buildTokenClaims(UserRole.USER);
            CreateAttributeRequest request = CreateAttributeRequestDataBuilder.withAllFields();

            String content = mockMvc.perform(post("/attributes")
                            .with(authentication(SecurityContextService.initializeAuth(claims)))
                            .content(objectMapper.writeValueAsString(request))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isForbidden())
                    .andReturn().getResponse().getContentAsString();

            ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
            assertThat(exceptionResponse).isNotNull();
            assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
            assertThat(exceptionResponse.getMessage()).isEqualTo("Resource is unreachable.");
            assertThat(exceptionResponse.getPath()).isEqualTo("/attributes");

            verifyNoInteractions(attributeCommandPort, mapper);
        }
    }
}
