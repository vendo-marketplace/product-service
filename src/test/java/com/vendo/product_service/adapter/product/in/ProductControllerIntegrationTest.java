package com.vendo.product_service.adapter.product.in;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vendo.product_service.adapter.product.in.dto.CreateProductRequest;
import com.vendo.product_service.adapter.product.in.dto.ProductResponse;
import com.vendo.product_service.adapter.product.in.dto.UpdateProductRequest;
import com.vendo.product_service.adapter.product.out.mapper.DtoProductMapper;
import com.vendo.product_service.adapter.security.out.jwt.parser.UserTokenClaims;
import com.vendo.product_service.application.category.validation.dto.AttributePayload;
import com.vendo.product_service.domain.category.model.AttributeDefinition;
import com.vendo.product_service.domain.category.model.AttributeType;
import com.vendo.product_service.domain.category.model.Category;
import com.vendo.product_service.domain.product.exception.ProductNotFoundException;
import com.vendo.product_service.domain.product.model.Product;
import com.vendo.product_service.port.category.CategoryQueryPort;
import com.vendo.product_service.port.product.ProductCommandPort;
import com.vendo.product_service.port.product.ProductQueryPort;
import com.vendo.product_service.port.user.CurrentUserPort;
import com.vendo.product_service.test_utils.builder.*;
import com.vendo.product_service.test_utils.security.SecurityContextService;
import com.vendo.security_lib.exception.response.ExceptionResponse;
import com.vendo.test_utils_lib.AssertionUtils;
import com.vendo.user_lib.type.UserRole;
import com.vendo.user_lib.type.UserStatus;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ProductControllerIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DtoProductMapper dtoProductMapper;
    @MockitoBean
    private ProductCommandPort productCommandPort;
    @MockitoBean
    private ProductQueryPort productQueryPort;
    @MockitoBean
    private CurrentUserPort currentUserPort;
    @MockitoBean
    private CategoryQueryPort categoryQueryPort;

    private ResultActions performProductPersist(CreateProductRequest createProductRequest) throws Exception {
        return performProductPersist(objectMapper.writeValueAsString(createProductRequest));
    }

    private ResultActions performProductPersist(String createProductRequest) throws Exception {
        UserTokenClaims claims = new UserTokenClaims("id", UserStatus.ACTIVE, List.of(UserRole.USER.name()), true);
        return mockMvc.perform(post("/products")
                .with(authentication(SecurityContextService.initializeAuth(claims)))
                .content(createProductRequest)
                .contentType(MediaType.APPLICATION_JSON));
    }

    private ResultActions performProductUpdate(String productId, UpdateProductRequest updateProductRequest) throws Exception {
        UserTokenClaims claims = new UserTokenClaims("id", UserStatus.ACTIVE, List.of(UserRole.USER.name()), true);
        return mockMvc.perform(put("/products/{id}", productId)
                .with(authentication(SecurityContextService.initializeAuth(claims)))
                .content(objectMapper.writeValueAsString(updateProductRequest))
                .contentType(MediaType.APPLICATION_JSON));
    }

    private ResultActions performProductGet(String productId) throws Exception {
        UserTokenClaims claims = new UserTokenClaims("id", UserStatus.ACTIVE, List.of(UserRole.USER.name()), true);
        return mockMvc.perform(get("/products/{id}", productId)
                .with(authentication(SecurityContextService.initializeAuth(claims)))
                .contentType(MediaType.APPLICATION_JSON));
    }

    private void shouldSaveProductWithAttribute(AttributePayload payload, List<String> requestAttributes) throws Exception {
        String userId = "user_id";

        Category category = CategoryDataBuilder.withAllFields()
                .attributes(Map.of(payload.name(), AttributeDefinition.builder().type(payload.definition().type()).allowedValues(payload.definition().allowedValues()).build()))
                .build();
        Product product = ProductDataBuilder.withAllFields()
                .categoryId(category.getId())
                .attributes(Map.of(payload.name(), requestAttributes))
                .build();
        CreateProductRequest request = CreateProductRequestDataBuilder.withAllFields()
                .categoryId(category.getId())
                .attributes(Map.of(payload.name(), requestAttributes))
                .build();
        String exceptionMessage = "Parent category not found.";
        ArgumentCaptor<Product> argumentCaptor = ArgumentCaptor.forClass(Product.class);

        when(dtoProductMapper.toEntity(request)).thenReturn(product);
        when(categoryQueryPort.findById(category.getId(), exceptionMessage)).thenReturn(category);
        when(currentUserPort.getCurrentUserId()).thenReturn(userId);

        performProductPersist(request).andExpect(status().isOk());

        verify(dtoProductMapper).toEntity(request);
        verify(categoryQueryPort).findById(category.getId(), exceptionMessage);
        verify(currentUserPort).getCurrentUserId();
        verify(productCommandPort).save(argumentCaptor.capture());

        Product captorValue = argumentCaptor.getValue();
        assertThat(captorValue.getOwnerId()).isEqualTo(userId);
        assertThat(captorValue.getActive()).isEqualTo(true);

        product.setActive(captorValue.getActive());
        product.setOwnerId(captorValue.getOwnerId());

        AssertionUtils.assertFromDto(product, captorValue);
    }

    private void shouldFailValidationOnSaveProductWithAttribute(AttributePayload payload, List<String> requestAttributes, String validationMessage) throws Exception {
        String userId = "user_id";

        Category category = CategoryDataBuilder.withAllFields()
                .attributes(Map.of(payload.name(), AttributeDefinition.builder().type(payload.definition().type()).allowedValues(payload.definition().allowedValues()).build()))
                .build();
        Product product = ProductDataBuilder.withAllFields()
                .categoryId(category.getId())
                .attributes(Map.of(payload.name(), requestAttributes))
                .build();
        CreateProductRequest request = CreateProductRequestDataBuilder.withAllFields()
                .categoryId(category.getId())
                .attributes(Map.of(payload.name(), requestAttributes))
                .build();
        String exceptionMessage = "Parent category not found.";

        when(dtoProductMapper.toEntity(request)).thenReturn(product);
        when(categoryQueryPort.findById(category.getId(), exceptionMessage)).thenReturn(category);
        when(currentUserPort.getCurrentUserId()).thenReturn(userId);

        String content = performProductPersist(request)
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
        assertThat(exceptionResponse).isNotNull();
        assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(exceptionResponse.getMessage()).isEqualTo("Validation failed.");
        assertThat(exceptionResponse.getErrors()).isNotNull();
        assertThat(exceptionResponse.getErrors().size()).isEqualTo(1);
        assertThat(exceptionResponse.getErrors().get(payload.name())).isEqualTo(validationMessage);
        assertThat(exceptionResponse.getPath()).isEqualTo("/products");

        verify(dtoProductMapper).toEntity(request);
        verify(categoryQueryPort).findById(category.getId(), exceptionMessage);
        verifyNoInteractions(currentUserPort);
        verifyNoInteractions(productCommandPort);
    }

    @Nested
    class UpdateProductTests {

        @Test
        void update_shouldUpdateProduct() throws Exception {
            Product product = ProductDataBuilder.withAllFields().build();
            UpdateProductRequest request = UpdateProductRequestDataBuilder.withAllFields().build();

            when(dtoProductMapper.toEntity(request)).thenReturn(product);
            when(productQueryPort.findById(product.getId())).thenReturn(product);
            when(currentUserPort.getCurrentUserId()).thenReturn(product.getOwnerId());
            when(categoryQueryPort.existsById(product.getCategoryId())).thenReturn(true);

            performProductUpdate(product.getId(), request).andExpect(status().isOk());

            verify(dtoProductMapper).toEntity(request);
            verify(productQueryPort).findById(product.getId());
            verify(currentUserPort).getCurrentUserId();
            verify(categoryQueryPort).existsById(product.getCategoryId());
            verify(productCommandPort).update(product.getId(), product);
        }

        @Test
        void update_returnNotFound_whenProductNotFound() throws Exception {
            UpdateProductRequest request = UpdateProductRequestDataBuilder.withAllFields().build();
            Product product = ProductDataBuilder.withAllFields().build();

            when(dtoProductMapper.toEntity(request)).thenReturn(product);
            when(productQueryPort.findById(product.getId())).thenThrow(new ProductNotFoundException("Product not found."));

            String content = performProductUpdate(product.getId(), request)
                    .andExpect(status().isNotFound())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
            assertThat(exceptionResponse).isNotNull();
            assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
            assertThat(exceptionResponse.getMessage()).isEqualTo("Product not found.");
            assertThat(exceptionResponse.getPath()).isEqualTo("/products/" + product.getId());

            verify(dtoProductMapper).toEntity(request);
            verify(productQueryPort).findById(product.getId());
            verifyNoInteractions(currentUserPort);
            verifyNoInteractions(categoryQueryPort);
            verifyNoInteractions(productCommandPort);
        }

        @Test
        void update_shouldReturnForbidden_whenAuthenticatedUserIsNotOwner() throws Exception {
            String otherUserId = "other_user_id";
            UpdateProductRequest request = UpdateProductRequestDataBuilder.withAllFields().build();
            Product product = ProductDataBuilder.withAllFields().build();

            when(dtoProductMapper.toEntity(request)).thenReturn(product);
            when(productQueryPort.findById(product.getId())).thenReturn(product);
            when(currentUserPort.getCurrentUserId()).thenReturn(otherUserId);

            String content = performProductUpdate(product.getId(), request)
                    .andExpect(status().isForbidden())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
            assertThat(exceptionResponse).isNotNull();
            assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
            assertThat(exceptionResponse.getMessage()).isEqualTo("You're not product's owner.");
            assertThat(exceptionResponse.getPath()).isEqualTo("/products/" + product.getId());

            verify(dtoProductMapper).toEntity(request);
            verify(productQueryPort).findById(product.getId());
            verify(currentUserPort).getCurrentUserId();
            verifyNoInteractions(categoryQueryPort);
            verifyNoInteractions(productCommandPort);
        }
    }

    @Nested
    class FindProductTests {

        @Test
        void findById_shouldReturnProduct() throws Exception {
            Product product = ProductDataBuilder.withAllFields().build();
            ProductResponse response = ProductResponseDataBuilder.withAllFields().build();

            when(productQueryPort.findById(product.getId())).thenReturn(product);
            when(dtoProductMapper.toResponse(product)).thenReturn(response);

            String content = performProductGet(product.getId())
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            ProductResponse productResponse = objectMapper.readValue(content, ProductResponse.class);
            AssertionUtils.assertFromDto(product, productResponse);

            verify(productQueryPort).findById(product.getId());
            verify(dtoProductMapper).toResponse(product);
        }

        @Test
        void findById_shouldReturnNotFound_whenProductNotFound() throws Exception {
            Product product = ProductDataBuilder.withAllFields().build();

            when(productQueryPort.findById(product.getId())).thenThrow(new ProductNotFoundException("Product not found."));

            String content = performProductGet(product.getId())
                    .andExpect(status().isNotFound())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
            assertThat(exceptionResponse).isNotNull();
            assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
            assertThat(exceptionResponse.getMessage()).isEqualTo("Product not found.");
            assertThat(exceptionResponse.getPath()).isEqualTo("/products/" + product.getId());

            verify(productQueryPort).findById(product.getId());
            verifyNoInteractions(dtoProductMapper);
        }
    }

    @Nested
    class SaveProductTests {

        @Test
        void save_shouldSaveProduct() throws Exception {
            String userId = "user_id";
            Category category = CategoryDataBuilder.withAllFields().build();
            CreateProductRequest request = CreateProductRequestDataBuilder.withAllFields().categoryId(category.getId()).build();
            Product product = ProductDataBuilder.withAllFields().categoryId(category.getId()).build();
            ArgumentCaptor<Product> argumentCaptor = ArgumentCaptor.forClass(Product.class);

            when(dtoProductMapper.toEntity(request)).thenReturn(product);
            when(categoryQueryPort.findById(category.getId(), "Parent category not found.")).thenReturn(category);
            when(currentUserPort.getCurrentUserId()).thenReturn(userId);

            performProductPersist(request).andExpect(status().isOk());

            verify(dtoProductMapper).toEntity(request);
            verify(categoryQueryPort).findById(category.getId(), "Parent category not found.");
            verify(currentUserPort).getCurrentUserId();
            verify(productCommandPort).save(argumentCaptor.capture());

            Product captorValue = argumentCaptor.getValue();
            assertThat(captorValue.getOwnerId()).isEqualTo(userId);
            assertThat(captorValue.getActive()).isEqualTo(true);

            product.setActive(captorValue.getActive());
            product.setOwnerId(captorValue.getOwnerId());

            AssertionUtils.assertFromDto(product, captorValue);
        }

        @Test
        void save_shouldReturnBadRequest_whenValidationFailed() throws Exception {
            CreateProductRequest request = CreateProductRequestDataBuilder.withAllFields()
                    .title(null)
                    .description(null)
                    .quantity(-1)
                    .price(null)
                    .categoryId(null)
                    .attributes(null)
                    .build();

            String content = performProductPersist(request)
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
            assertThat(exceptionResponse).isNotNull();
            assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            assertThat(exceptionResponse.getMessage()).isEqualTo("Validation failed.");
            assertThat(exceptionResponse.getErrors()).isNotNull();
            assertThat(exceptionResponse.getErrors().size()).isEqualTo(6);
            assertThat(exceptionResponse.getPath()).isEqualTo("/products");

            verifyNoInteractions(dtoProductMapper);
            verifyNoInteractions(categoryQueryPort);
            verifyNoInteractions(currentUserPort);
            verifyNoInteractions(productCommandPort);
        }

        @Test
        void save_shouldReturnBadRequest_whenInvalidBodyStructure() throws Exception {
            String invalidRequestBody = """
                       "title": "Title",
                       "description": "Description",
                       "quantity": 1,
                       "price": 0,
                       "categoryId": "id",
                       "attributes": "invalid_attribute_value_structure"
                    """;

            String content = performProductPersist(invalidRequestBody).andReturn()
                    .getResponse()
                    .getContentAsString();

            ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
            assertThat(exceptionResponse).isNotNull();
            assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            assertThat(exceptionResponse.getMessage()).isEqualTo("Invalid body structure.");
            assertThat(exceptionResponse.getPath()).isEqualTo("/products");

            verifyNoInteractions(dtoProductMapper);
            verifyNoInteractions(categoryQueryPort);
            verifyNoInteractions(currentUserPort);
            verifyNoInteractions(productCommandPort);
        }

        @Test
        void save_shouldReturnNotFound_whenCategoryNotFound() throws Exception {
            String categoryId = "category_id";
            CreateProductRequest request = CreateProductRequestDataBuilder.withAllFields().categoryId(categoryId).build();
            Product product = ProductDataBuilder.withAllFields().categoryId(categoryId).build();
            String exceptionMessage = "Parent category not found.";

            when(dtoProductMapper.toEntity(request)).thenReturn(product);
            when(categoryQueryPort.findById(categoryId, exceptionMessage)).thenThrow(new ProductNotFoundException(exceptionMessage));

            String content = performProductPersist(request).andExpect(status().isNotFound())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
            assertThat(exceptionResponse).isNotNull();
            assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
            assertThat(exceptionResponse.getMessage()).isEqualTo("Parent category not found.");
            assertThat(exceptionResponse.getPath()).isEqualTo("/products");

            verify(dtoProductMapper).toEntity(request);
            verify(categoryQueryPort).findById(categoryId, exceptionMessage);
            verifyNoInteractions(currentUserPort);
            verifyNoInteractions(productCommandPort);
        }

        @Test
        void save_shouldReturnBadRequest_whenCategoryIsNotChild() throws Exception {
            Category category = CategoryDataBuilder.withAllFields().attributes(null).parentId(null).build();
            Product product = ProductDataBuilder.withAllFields().categoryId(category.getId()).build();
            CreateProductRequest request = CreateProductRequestDataBuilder.withAllFields().build();
            String exceptionMessage = "Parent category not found.";

            when(dtoProductMapper.toEntity(request)).thenReturn(product);
            when(categoryQueryPort.findById(category.getId(), exceptionMessage)).thenReturn(category);

            String content = performProductPersist(request)
                    .andExpect(status().isBadRequest())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
            assertThat(exceptionResponse).isNotNull();
            assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            assertThat(exceptionResponse.getMessage()).isEqualTo("Category type should be child.");
            assertThat(exceptionResponse.getPath()).isEqualTo("/products");

            verify(dtoProductMapper).toEntity(request);
            verify(categoryQueryPort).findById(category.getId(), exceptionMessage);
            verifyNoInteractions(currentUserPort);
            verifyNoInteractions(productCommandPort);
        }

        @Nested
        class SaveProductWithAttributes {

            @Test
            void save_shouldReturnBadRequest_whenAttributeNameIsNotValid() throws Exception {
                String validAttributeName = "Number", invalidAttributeName = "number";
                Category category = CategoryDataBuilder.withAllFields()
                        .attributes(Map.of(validAttributeName, AttributeDefinition.builder().type(AttributeType.NUMBER).build()))
                        .build();
                CreateProductRequest request = CreateProductRequestDataBuilder.withAllFields()
                        .categoryId(category.getId())
                        .attributes(Map.of(invalidAttributeName, List.of("1")))
                        .build();

                String content = performProductPersist(request)
                        .andExpect(status().isBadRequest())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

                ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
                assertThat(exceptionResponse).isNotNull();
                assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                assertThat(exceptionResponse.getMessage()).isEqualTo("Validation failed.");
                assertThat(exceptionResponse.getErrors()).isNotNull();
                assertThat(exceptionResponse.getErrors().size()).isEqualTo(1);
                assertThat(exceptionResponse.getErrors().get(invalidAttributeName)).isEqualTo("Attribute name validation failed.");
                assertThat(exceptionResponse.getPath()).isEqualTo("/products");

                verifyNoInteractions(dtoProductMapper);
            }

            @Nested
            class SaveProductWithNumberAttribute {

                @Test
                void save_shouldSaveProductWithNumberAttribute() throws Exception {
                    AttributeDefinition attributeDefinition = new AttributeDefinition(AttributeType.NUMBER, false, null);
                    AttributePayload payload = new AttributePayload("Number", attributeDefinition);
                    shouldSaveProductWithAttribute(payload, List.of("1"));
                }

                @Test
                void save_shouldReturnBadRequest_whenNumberAttributeIsMoreThanOne() throws Exception {
                    AttributeDefinition attributeDefinition = new AttributeDefinition(AttributeType.NUMBER, false, null);
                    AttributePayload payload = new AttributePayload("Number", attributeDefinition);
                    shouldFailValidationOnSaveProductWithAttribute(payload, List.of("1", "2"), "Must contain exactly one value.");
                }

                @Test
                void save_shouldReturnBadRequest_whenNumberAttributeIsNegative() throws Exception {
                    AttributeDefinition attributeDefinition = new AttributeDefinition(AttributeType.NUMBER, false, null);
                    AttributePayload payload = new AttributePayload("Number", attributeDefinition);
                    shouldFailValidationOnSaveProductWithAttribute(payload, List.of("-1"), "Must be equal or greater than zero.");
                }

                @Test
                void save_shouldReturnBadRequest_whenNumberAttributeIsNotNumeric() throws Exception {
                    AttributeDefinition attributeDefinition = new AttributeDefinition(AttributeType.NUMBER, false, null);
                    AttributePayload payload = new AttributePayload("Number", attributeDefinition);
                    shouldFailValidationOnSaveProductWithAttribute(payload, List.of("not_numeric"), "Invalid number value.");
                }

                @Test
                void save_shouldReturnBadRequest_whenNumberAttributeIsHigherThanIntegerMaxValue() throws Exception {
                    AttributeDefinition attributeDefinition = new AttributeDefinition(AttributeType.NUMBER, false, null);
                    AttributePayload payload = new AttributePayload("Number", attributeDefinition);
                    shouldFailValidationOnSaveProductWithAttribute(payload, List.of("1_000_000_000_000"), "Invalid number value.");
                }
            }

            @Nested
            class SaveProductWithBooleanAttribute {

                @Test
                void save_shouldSaveProductWithBooleanAttribute() throws Exception {
                    AttributeDefinition attributeDefinition = new AttributeDefinition(AttributeType.BOOLEAN, false, null);
                    AttributePayload payload = new AttributePayload("Boolean", attributeDefinition);
                    shouldSaveProductWithAttribute(payload, List.of("true"));
                }

                @Test
                void save_shouldReturnBadRequest_whenBooleanAttributeIsMoreThanOne() throws Exception {
                    AttributeDefinition attributeDefinition = new AttributeDefinition(AttributeType.BOOLEAN, false, null);
                    AttributePayload payload = new AttributePayload("Boolean", attributeDefinition);
                    shouldFailValidationOnSaveProductWithAttribute(payload, List.of("true", "false"), "Must contain exactly one value.");
                }

                @Test
                void save_shouldReturnBadRequest_whenInvalidBooleanAttributeValue() throws Exception {
                    AttributeDefinition attributeDefinition = new AttributeDefinition(AttributeType.BOOLEAN, false, null);
                    AttributePayload payload = new AttributePayload("Boolean", attributeDefinition);
                    shouldFailValidationOnSaveProductWithAttribute(payload, List.of("not_boolean_value"), "Invalid boolean value. Allowed values: true, false.");
                }
            }

            @Nested
            class SaveProductWithEnumAttribute {

                @Test
                void save_shouldSaveProductWithEnumAttribute() throws Exception {
                    AttributeDefinition attributeDefinition = new AttributeDefinition(AttributeType.ENUM, false, List.of("TYPE1", "TYPE2"));
                    AttributePayload payload = new AttributePayload("Enum", attributeDefinition);
                    shouldSaveProductWithAttribute(payload, List.of("TYPE1"));
                }

                @Test
                void save_shouldReturnBadRequest_whenAttributeIsMoreThanOne() throws Exception {
                    AttributeDefinition attributeDefinition = new AttributeDefinition(AttributeType.ENUM, false, List.of("TYPE1", "TYPE2"));
                    AttributePayload payload = new AttributePayload("Enum", attributeDefinition);
                    shouldFailValidationOnSaveProductWithAttribute(payload, List.of("TYPE1", "TYPE2"), "Must contain exactly one value.");
                }

                @Test
                void save_shouldReturnBadRequest_whenAttributeValueIsNotAllowed() throws Exception {
                    AttributeDefinition attributeDefinition = new AttributeDefinition(AttributeType.ENUM, false, List.of("TYPE1", "TYPE2"));
                    AttributePayload payload = new AttributePayload("Enum", attributeDefinition);
                    shouldFailValidationOnSaveProductWithAttribute(payload, List.of("TYPE3"), "Invalid value. Allowed values: " + String.join(", ", attributeDefinition.allowedValues()));
                }
            }

            @Nested
            class SaveProductWithRangeAttribute {

                @Test
                void save_shouldSaveProductWithRangeAttribute() throws Exception {
                    AttributeDefinition attributeDefinition = new AttributeDefinition(AttributeType.RANGE, false, List.of("0", "100"));
                    AttributePayload payload = new AttributePayload("Range", attributeDefinition);
                    shouldSaveProductWithAttribute(payload, List.of("50", "70"));
                }

                @Test
                void save_shouldReturnBadRequest_whenAttributesAreMoreThanTwo() throws Exception {
                    AttributeDefinition attributeDefinition = new AttributeDefinition(AttributeType.RANGE, false, List.of("0", "100"));
                    AttributePayload payload = new AttributePayload("Range", attributeDefinition);
                    shouldFailValidationOnSaveProductWithAttribute(payload, List.of("50", "70", "90"), "Must contain exactly two values.");
                }

                @Test
                void save_shouldReturnBadRequest_whenFirstAttributeValueIsLowerThanZero() throws Exception {
                    AttributeDefinition attributeDefinition = new AttributeDefinition(AttributeType.RANGE, false, List.of("0", "100"));
                    AttributePayload payload = new AttributePayload("Range", attributeDefinition);
                    shouldFailValidationOnSaveProductWithAttribute(payload, List.of("-1", "100"), "The first value must be equal or greater than to zero.");
                }

                @Test
                void save_shouldReturnBadRequest_whenFirstAttributeValueIsGreaterThanSecond() throws Exception {
                    AttributeDefinition attributeDefinition = new AttributeDefinition(AttributeType.RANGE, false, List.of("0", "100"));
                    AttributePayload payload = new AttributePayload("Range", attributeDefinition);
                    shouldFailValidationOnSaveProductWithAttribute(payload, List.of("50", "10"), "The first value must be less than the second value.");
                }

                @Test
                void save_shouldReturnBadRequest_whenAttributesAreNotNumericType() throws Exception {
                    AttributeDefinition attributeDefinition = new AttributeDefinition(AttributeType.RANGE, false, List.of("0", "100"));
                    AttributePayload payload = new AttributePayload("Range", attributeDefinition);
                    shouldFailValidationOnSaveProductWithAttribute(payload, List.of("not_numeric", "not_numeric"), "Invalid range value format.");
                }
            }

            @Nested
            class SaveProductWithStringAttribute {

                @Test
                void save_shouldSaveProductWithStringAttribute() throws Exception {
                    AttributeDefinition attributeDefinition = new AttributeDefinition(AttributeType.STRING, false, List.of("string"));
                    AttributePayload payload = new AttributePayload("String", attributeDefinition);
                    shouldSaveProductWithAttribute(payload, List.of("string"));
                }

                @Test
                void save_shouldReturnBadRequest_whenAttributeIsBlank() throws Exception {
                    AttributeDefinition attributeDefinition = new AttributeDefinition(AttributeType.STRING, false, null);
                    AttributePayload payload = new AttributePayload("String", attributeDefinition);
                    shouldFailValidationOnSaveProductWithAttribute(payload, List.of(""), "Must not be blank.");
                }
            }
        }
    }
}
