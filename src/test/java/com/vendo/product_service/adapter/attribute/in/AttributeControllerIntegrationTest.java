package com.vendo.product_service.adapter.attribute.in;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vendo.core_lib.utils.AssertionUtils;
import com.vendo.product_service.adapter.attribute.in.dto.CreateAttributeRequest;
import com.vendo.product_service.adapter.attribute.out.mapper.DtoAttributeMapper;
import com.vendo.product_service.domain.attribute.exception.AttributeAlreadyExistsException;
import com.vendo.product_service.domain.attribute.model.Attribute;
import com.vendo.product_service.domain.product.pattern.ProductPatterns;
import com.vendo.product_service.domain.user.User;
import com.vendo.product_service.port.out.attribute.AttributeCommandPort;
import com.vendo.product_service.test_utils.builder.AttributeDataBuilder;
import com.vendo.product_service.test_utils.builder.CreateAttributeRequestDataBuilder;
import com.vendo.product_service.test_utils.security.SecurityContextService;
import com.vendo.security_lib.exception.response.ExceptionResponse;
import com.vendo.user_lib.type.UserRole;
import com.vendo.user_lib.type.UserStatus;
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

import java.util.Set;

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

    private User buildUser(UserRole role) {
        return new User("id", "email", UserStatus.ACTIVE, Set.of(role), true);
    }

    @Nested
    class SaveAttribute {

        @Test
        void save_shouldSuccessfullySave() throws Exception {
            User user = buildUser(UserRole.ADMIN);
            Attribute attribute = AttributeDataBuilder.withAllFields().build();
            CreateAttributeRequest request = CreateAttributeRequestDataBuilder.withAllFields().build();
            ArgumentCaptor<Attribute> captor = ArgumentCaptor.forClass(Attribute.class);

            when(mapper.toAttribute(request)).thenReturn(attribute);
            doNothing().when(attributeCommandPort).save(captor.capture());

            mockMvc.perform(post("/attributes")
                            .with(authentication(SecurityContextService.initializeAuth(user)))
                            .content(objectMapper.writeValueAsString(request))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());

            verify(mapper).toAttribute(request);
            verify(attributeCommandPort).save(attribute);

            Attribute captorValue = captor.getValue();
            AssertionUtils.assertFrom(attribute, captorValue);
        }

        @Test
        void save_shouldReturnBadRequest_whenTitleIsNotPresent() throws Exception {
            User user = buildUser(UserRole.ADMIN);
            CreateAttributeRequest request = CreateAttributeRequestDataBuilder.withAllFields().title(null).build();

            String content = mockMvc.perform(post("/attributes")
                            .with(authentication(SecurityContextService.initializeAuth(user)))
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
            assertThat(exceptionResponse.getErrors().get("title")).isEqualTo("Title is required.");
            assertThat(exceptionResponse.getPath()).isEqualTo("/attributes");

            verifyNoInteractions(attributeCommandPort, mapper);
        }

        @Test
        void save_shouldReturnBadRequest_whenTitleIsNotValid() throws Exception {
            User user = buildUser(UserRole.ADMIN);
            CreateAttributeRequest request = CreateAttributeRequestDataBuilder.withAllFields().title("invalid_title").build();

            String content = mockMvc.perform(post("/attributes")
                            .with(authentication(SecurityContextService.initializeAuth(user)))
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
            assertThat(exceptionResponse.getErrors().get("title")).isEqualTo(ProductPatterns.TITLE_VALIDATION_MESSAGE);
            assertThat(exceptionResponse.getPath()).isEqualTo("/attributes");

            verifyNoInteractions(attributeCommandPort, mapper);
        }

        @Test
        void save_shouldReturnBadRequest_whenTypeIsNotPresent() throws Exception {
            User user = buildUser(UserRole.ADMIN);
            CreateAttributeRequest request = CreateAttributeRequestDataBuilder.withAllFields().type(null).build();

            String content = mockMvc.perform(post("/attributes")
                            .with(authentication(SecurityContextService.initializeAuth(user)))
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
            assertThat(exceptionResponse.getErrors().get("type")).isEqualTo("Type is required.");
            assertThat(exceptionResponse.getPath()).isEqualTo("/attributes");

            verifyNoInteractions(attributeCommandPort, mapper);
        }

        @Test
        void save_shouldReturnBadRequest_whenSlugIsNotPresent() throws Exception {
            User user = buildUser(UserRole.ADMIN);
            CreateAttributeRequest request = CreateAttributeRequestDataBuilder.withAllFields().slug(null).build();

            String content = mockMvc.perform(post("/attributes")
                            .with(authentication(SecurityContextService.initializeAuth(user)))
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
            assertThat(exceptionResponse.getErrors().get("slug")).isEqualTo("Slug is required.");
            assertThat(exceptionResponse.getPath()).isEqualTo("/attributes");

            verifyNoInteractions(attributeCommandPort, mapper);
        }

        @Test
        void save_shouldReturnConflict_whenAttributeAlreadyExistsBySlug() throws Exception {
            User user = buildUser(UserRole.ADMIN);
            Attribute attribute = AttributeDataBuilder.withAllFields().build();
            CreateAttributeRequest request = CreateAttributeRequestDataBuilder.withAllFields().build();
            ArgumentCaptor<Attribute> captor = ArgumentCaptor.forClass(Attribute.class);

            when(mapper.toAttribute(request)).thenReturn(attribute);
            doThrow(new AttributeAlreadyExistsException("Attribute already exists by slug.")).when(attributeCommandPort).save(captor.capture());

            String content = mockMvc.perform(post("/attributes")
                            .with(authentication(SecurityContextService.initializeAuth(user)))
                            .content(objectMapper.writeValueAsString(request))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isConflict()).andReturn().getResponse().getContentAsString();

            ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
            assertThat(exceptionResponse).isNotNull();
            assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.CONFLICT.value());
            assertThat(exceptionResponse.getMessage()).isEqualTo("Attribute already exists by slug.");
            assertThat(exceptionResponse.getPath()).isEqualTo("/attributes");

            verify(mapper).toAttribute(request);
            verify(attributeCommandPort).save(attribute);

            Attribute captorValue = captor.getValue();
            AssertionUtils.assertFrom(attribute, captorValue);
        }

        @Test
        void save_shouldReturnUnauthorized_whenNotAdmin() throws Exception {
            User user = buildUser(UserRole.USER);
            CreateAttributeRequest request = CreateAttributeRequestDataBuilder.withAllFields().build();

            String content = mockMvc.perform(post("/attributes")
                            .with(authentication(SecurityContextService.initializeAuth(user)))
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
