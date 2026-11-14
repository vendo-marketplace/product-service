package com.vendo.product_service.adapter.attribute.in;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vendo.core_lib.constants.Delimiters;
import com.vendo.core_lib.utils.AssertionUtils;
import com.vendo.core_lib.utils.ClassFields;
import com.vendo.product_service.adapter.attribute.in.dto.CreateAttributeRequest;
import com.vendo.product_service.domain.attribute.exception.AttributeAlreadyExistsException;
import com.vendo.product_service.domain.attribute.model.Attribute;
import com.vendo.product_service.domain.attribute.model.AttributeType;
import com.vendo.product_service.domain.product.pattern.ProductPatterns;
import com.vendo.product_service.domain.user.User;
import com.vendo.product_service.port.attribute.AttributeCommandPort;
import com.vendo.product_service.test_utils.builder.CreateAttributeRequestDataBuilder;
import com.vendo.product_service.test_utils.security.SecurityContextService;
import com.vendo.security_lib.exception.ExceptionResponse;
import com.vendo.user_lib.type.UserRole;
import com.vendo.user_lib.type.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
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
import org.springframework.test.web.servlet.ResultActions;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

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
            CreateAttributeRequest request = CreateAttributeRequestDataBuilder.withAllFields().build();
            ArgumentCaptor<Attribute> captor = ArgumentCaptor.forClass(Attribute.class);

            doNothing().when(attributeCommandPort).save(captor.capture());

            mockMvc.perform(post("/attributes")
                            .with(authentication(SecurityContextService.initializeAuth(user)))
                            .content(objectMapper.writeValueAsString(request))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());

            Attribute captorValue = captor.getValue();
            AssertionUtils.assertFrom(request, captorValue, "id");

            verify(attributeCommandPort).save(captorValue);
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

            verifyNoInteractions(attributeCommandPort);
        }

        @Test
        void save_shouldReturnBadRequest_whenInvalidEnumType() throws Exception {
            User user = buildUser(UserRole.ADMIN);
            String enumValues = String.join(Delimiters.COMMA_DELIMITER, ClassFields.getEnumValues(AttributeType.class));
            String invalidBody = """
                    {"title": "Title", "slug": "slug", "type": "invalid_type"}
                    """;

            String content = mockMvc.perform(post("/attributes")
                            .with(authentication(SecurityContextService.initializeAuth(user)))
                            .content(invalidBody)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            assertThat(content).isNotBlank();

            ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
            assertThat(exceptionResponse).isNotNull();
            assertThat(exceptionResponse.getTimestamp()).isNotNull();
            assertThat(exceptionResponse.getCode()).isEqualTo(400);
            assertThat(exceptionResponse.getMessage()).isEqualTo("Validation failed.");
            assertThat(exceptionResponse.getErrors()).isNotNull();
            assertThat(exceptionResponse.getErrors().get("type")).isEqualTo("Allowed types are: " + enumValues);
            assertThat(exceptionResponse.getPath()).isEqualTo("/attributes");

            verifyNoInteractions(attributeCommandPort);
        }

        public static void main(String[] args) {

        }

        @ParameterizedTest
        @EnumSource(value = AttributeType.class, names = {"ENUM", "SELECT"})
        void save_shouldSave_whenAllowedValuesTypeHasValidAllowedValues(AttributeType type) throws Exception {
            CreateAttributeRequest request = CreateAttributeRequestDataBuilder.withAllFields()
                    .type(type)
                    .allowedValues(List.of("RED", "GREEN"))
                    .build();

            performAttributeSave(request).andExpect(status().isOk());

            ArgumentCaptor<Attribute> captor = ArgumentCaptor.forClass(Attribute.class);
            verify(attributeCommandPort).save(captor.capture());
            assertThat(captor.getValue().type()).isEqualTo(type);
            assertThat(captor.getValue().allowedValues()).isEqualTo(List.of("RED", "GREEN"));
        }

        @ParameterizedTest
        @EnumSource(value = AttributeType.class, names = {"STRING", "NUMBER", "BOOLEAN", "RANGE"})
        void save_shouldSave_whenTypeHasNoDefinitionValidator_andAllowedValuesAreAbsent(AttributeType type) throws Exception {
            CreateAttributeRequest request = CreateAttributeRequestDataBuilder.withAllFields()
                    .type(type)
                    .allowedValues(null)
                    .build();

            performAttributeSave(request).andExpect(status().isOk());

            verify(attributeCommandPort).save(any());
        }

        static Stream<Arguments> invalidAllowedValues() {
            return Stream.of(AttributeType.ENUM, AttributeType.SELECT).flatMap(type -> Stream.of(
                    Arguments.of(type, null, "Allowed values must contain at least one value."),
                    Arguments.of(type, List.of(), "Allowed values must contain at least one value."),
                    Arguments.of(type, List.of("RED", " "), "Allowed values must not be blank."),
                    Arguments.of(type, Arrays.asList("RED", null), "Allowed values must not be blank."),
                    Arguments.of(type, List.of("RED", "RED"), "Allowed values must be unique.")
            ));
        }

        @ParameterizedTest
        @MethodSource("invalidAllowedValues")
        void save_shouldReturnBadRequest_whenAllowedValuesAreInvalid(AttributeType type, List<String> allowedValues, String message) throws Exception {
            CreateAttributeRequest request = CreateAttributeRequestDataBuilder.withAllFields()
                    .type(type)
                    .allowedValues(allowedValues)
                    .build();

            String content = performAttributeSave(request)
                    .andExpect(status().isBadRequest())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
            assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            assertThat(exceptionResponse.getMessage()).isEqualTo("Attribute validation failed.");
            assertThat(exceptionResponse.getErrors()).isEqualTo(Map.of("allowedValues", message));
            assertThat(exceptionResponse.getPath()).isEqualTo("/attributes");

            verifyNoInteractions(attributeCommandPort);
        }

        private ResultActions performAttributeSave(CreateAttributeRequest request) throws Exception {
            return mockMvc.perform(post("/attributes")
                    .with(authentication(SecurityContextService.initializeAuth(buildUser(UserRole.ADMIN))))
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON));
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
            assertThat(exceptionResponse.getErrors().get("title")).isEqualTo(ProductPatterns.ATTRIBUTE_TITLE_VALIDATION_MESSAGE);
            assertThat(exceptionResponse.getPath()).isEqualTo("/attributes");

            verifyNoInteractions(attributeCommandPort);
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

            verifyNoInteractions(attributeCommandPort);
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

            verifyNoInteractions(attributeCommandPort);
        }

        @Test
        void save_shouldReturnConflict_whenAttributeAlreadyExistsBySlug() throws Exception {
            User user = buildUser(UserRole.ADMIN);
            CreateAttributeRequest request = CreateAttributeRequestDataBuilder.withAllFields().build();
            ArgumentCaptor<Attribute> captor = ArgumentCaptor.forClass(Attribute.class);

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

            Attribute captorValue = captor.getValue();
            verify(attributeCommandPort).save(captorValue);

            AssertionUtils.assertFrom(request, captorValue, "id");
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

            verifyNoInteractions(attributeCommandPort);
        }
    }
}
