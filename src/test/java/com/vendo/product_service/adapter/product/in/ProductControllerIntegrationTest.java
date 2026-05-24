package com.vendo.product_service.adapter.product.in;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vendo.product_service.adapter.product.in.dto.CreateProductRequest;
import com.vendo.product_service.adapter.product.in.dto.ProductResponse;
import com.vendo.product_service.adapter.product.in.dto.UpdateProductRequest;
import com.vendo.product_service.adapter.product.out.mapper.DtoProductMapper;
import com.vendo.product_service.adapter.security.out.jwt.parser.TokenClaims;
import com.vendo.product_service.domain.attribute.model.Attribute;
import com.vendo.product_service.domain.attribute.model.AttributeType;
import com.vendo.product_service.domain.attribute.model.AttributeValue;
import com.vendo.product_service.domain.category.model.Category;
import com.vendo.product_service.domain.product.exception.ProductNotFoundException;
import com.vendo.product_service.domain.product.model.Product;
import com.vendo.product_service.port.out.attribute.AttributeQueryPort;
import com.vendo.product_service.port.out.category.CategoryQueryPort;
import com.vendo.product_service.port.out.product.ProductCommandPort;
import com.vendo.product_service.port.out.product.ProductEventSenderPort;
import com.vendo.product_service.port.out.product.ProductQueryPort;
import com.vendo.product_service.test_utils.builder.*;
import com.vendo.product_service.test_utils.security.SecurityContextService;
import com.vendo.security_lib.exception.response.ExceptionResponse;
import com.vendo.user_lib.type.UserRole;
import com.vendo.user_lib.type.UserStatus;
import com.vendo.utils_lib.AssertionUtils;
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
    private CategoryQueryPort categoryQueryPort;
    @MockitoBean
    private AttributeQueryPort attributeQueryPort;
    @MockitoBean
    private ProductEventSenderPort productEventSenderPort;

    private ResultActions performProductPersist(String userId, CreateProductRequest request) throws Exception {
        TokenClaims claims = new TokenClaims(userId, UserStatus.ACTIVE, List.of(UserRole.USER.name()), true);
        return mockMvc.perform(post("/products")
                .with(authentication(SecurityContextService.initializeAuth(claims)))
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON));
    }

    private ResultActions performProductPersist(String request) throws Exception {
        TokenClaims claims = new TokenClaims("id", UserStatus.ACTIVE, List.of(UserRole.USER.name()), true);
        return mockMvc.perform(post("/products")
                .with(authentication(SecurityContextService.initializeAuth(claims)))
                .content(request)
                .contentType(MediaType.APPLICATION_JSON));
    }

    private ResultActions performProductPersist(CreateProductRequest request) throws Exception {
        return performProductPersist("id", request);
    }

    private ResultActions performProductUpdate(String userId, String productId, UpdateProductRequest updateProductRequest) throws Exception {
        TokenClaims claims = new TokenClaims(userId, UserStatus.ACTIVE, List.of(UserRole.USER.name()), true);
        return mockMvc.perform(put("/products/{id}", productId)
                .with(authentication(SecurityContextService.initializeAuth(claims)))
                .content(objectMapper.writeValueAsString(updateProductRequest))
                .contentType(MediaType.APPLICATION_JSON));
    }

    private ResultActions performProductGet(String productId) throws Exception {
        TokenClaims claims = new TokenClaims("id", UserStatus.ACTIVE, List.of(UserRole.USER.name()), true);
        return mockMvc.perform(get("/products/{id}", productId)
                .with(authentication(SecurityContextService.initializeAuth(claims)))
                .contentType(MediaType.APPLICATION_JSON));
    }

    private void shouldSaveProductWithAttribute(Attribute attribute, AttributeValue attributeValue) throws Exception {
        String userId = "user_id";

        Category category = CategoryDataBuilder.withAllFields()
                .attributes(List.of(attributeValue.id()))
                .build();
        Product product = ProductDataBuilder.withAllFields()
                .categoryId(category.getId())
                .attributes(List.of(attributeValue))
                .ownerId(userId)
                .build();
        CreateProductRequest request = CreateProductRequestDataBuilder.withAllFields()
                .categoryId(category.getId())
                .attributes(List.of(attributeValue))
                .build();
        ArgumentCaptor<Product> argumentCaptor = ArgumentCaptor.forClass(Product.class);

        when(dtoProductMapper.toEntity(request)).thenReturn(product);
        when(categoryQueryPort.findById(eq(category.getId()))).thenReturn(category);
        when(attributeQueryPort.findAllByIds(List.of(attributeValue.id()))).thenReturn(List.of(attribute));
        when(productCommandPort.save(product)).thenReturn(product);

        performProductPersist(userId, request).andExpect(status().isOk());

        verify(dtoProductMapper).toEntity(request);
        verify(categoryQueryPort).findById(eq(category.getId()));
        verify(attributeQueryPort).findAllByIds(List.of(attributeValue.id()));
        verify(productEventSenderPort).sendCreated(product, List.of(attribute));
        verify(productCommandPort).save(argumentCaptor.capture());

        Product captorValue = argumentCaptor.getValue();
        assertThat(captorValue.getOwnerId()).isEqualTo(userId);
        assertThat(captorValue.getActive()).isEqualTo(true);

        AssertionUtils.assertFrom(product, captorValue, "active", "ownerId");
    }

    private void shouldFailValidationOnSaveProductWithAttribute(Attribute attribute, AttributeValue attributeValue, String validationMessage) throws Exception {
        String userId = "userId";

        Category category = CategoryDataBuilder.withAllFields()
                .attributes(List.of(attributeValue.id()))
                .build();
        Product product = ProductDataBuilder.withAllFields()
                .ownerId(userId)
                .categoryId(category.getId())
                .attributes(List.of(attributeValue))
                .build();
        CreateProductRequest request = CreateProductRequestDataBuilder.withAllFields()
                .categoryId(category.getId())
                .attributes(List.of(attributeValue))
                .build();

        when(dtoProductMapper.toEntity(request)).thenReturn(product);
        when(categoryQueryPort.findById(category.getId())).thenReturn(category);
        when(attributeQueryPort.findAllByIds(List.of(attributeValue.id()))).thenReturn(List.of(attribute));

        String content = performProductPersist(userId, request)
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
        assertThat(exceptionResponse.getErrors().get(attribute.title())).isEqualTo(validationMessage);
        assertThat(exceptionResponse.getPath()).isEqualTo("/products");

        verify(dtoProductMapper).toEntity(request);
        verify(categoryQueryPort).findById(category.getId());
        verify(attributeQueryPort).findAllByIds(List.of(attributeValue.id()));
        verifyNoInteractions(productCommandPort, productEventSenderPort);
    }

    @Nested
    class UpdateProductTests {

        @Test
        void update_shouldUpdateProduct() throws Exception {
            Product product = ProductDataBuilder.withAllFields().build();
            List<String> attributeIds = product.getAttributes().stream().map(AttributeValue::id).toList();
            List<Attribute> attributes = attributeIds.stream().map(id -> AttributeDataBuilder.withAllFields().id(id).build()).toList();
            Category category = CategoryDataBuilder.withAllFields().attributes(attributeIds).build();
            UpdateProductRequest request = UpdateProductRequestDataBuilder.withAllFields().build();

            when(dtoProductMapper.toEntity(request)).thenReturn(product);
            when(productQueryPort.findById(product.getId())).thenReturn(product);
            when(categoryQueryPort.findById(product.getCategoryId())).thenReturn(category);
            when(attributeQueryPort.findAllByIds(attributeIds)).thenReturn(attributes);

            performProductUpdate(product.getOwnerId(), product.getId(), request).andExpect(status().isOk());

            verify(dtoProductMapper).toEntity(request);
            verify(productQueryPort).findById(product.getId());
            verify(categoryQueryPort).findById(product.getCategoryId());
            verify(attributeQueryPort).findAllByIds(attributeIds);
            verify(productEventSenderPort).sendUpdated(product, attributes);
            verify(productCommandPort).update(product.getId(), product);
        }

        @Test
        void update_returnNotFound_whenProductNotFound() throws Exception {
            UpdateProductRequest request = UpdateProductRequestDataBuilder.withAllFields().build();
            Product product = ProductDataBuilder.withAllFields().build();

            when(dtoProductMapper.toEntity(request)).thenReturn(product);
            when(productQueryPort.findById(product.getId())).thenThrow(new ProductNotFoundException("Product not found."));

            String content = performProductUpdate(product.getOwnerId(), product.getId(), request)
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
            verifyNoInteractions(categoryQueryPort, productCommandPort, productEventSenderPort);
        }

        @Test
        void update_shouldReturnForbidden_whenAuthenticatedUserIsNotOwner() throws Exception {
            UpdateProductRequest request = UpdateProductRequestDataBuilder.withAllFields().build();
            Product product = ProductDataBuilder.withAllFields().build();

            when(dtoProductMapper.toEntity(request)).thenReturn(product);
            when(productQueryPort.findById(product.getId())).thenReturn(product);

            String content = performProductUpdate("not_owner_id", product.getId(), request)
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
            verifyNoInteractions(categoryQueryPort, productCommandPort, productEventSenderPort);
        }

        @Test
        void update_shouldUpdateProduct_whenNoAttributes() throws Exception {
            Product product = ProductDataBuilder.withAllFields().attributes(null).build();
            UpdateProductRequest request = UpdateProductRequestDataBuilder.withAllFields().attributes(null).build();
            Category category = CategoryDataBuilder.withAllFields().attributes(null).build();

            when(dtoProductMapper.toEntity(request)).thenReturn(product);
            when(productQueryPort.findById(product.getId())).thenReturn(product);
            when(categoryQueryPort.findById(product.getCategoryId())).thenReturn(category);
            when(attributeQueryPort.findAllByIds(category.getAttributes())).thenReturn(List.of());

            performProductUpdate(product.getOwnerId(), product.getId(), request).andExpect(status().isOk());

            verify(dtoProductMapper).toEntity(request);
            verify(productQueryPort).findById(product.getId());
            verify(categoryQueryPort).findById(product.getCategoryId());
            verify(attributeQueryPort).findAllByIds(category.getAttributes());
            verify(productEventSenderPort).sendUpdated(product, List.of());
            verify(productCommandPort).update(product.getId(), product);
        }
    }

    @Nested
    class FindProductTests {

        @Test
        void findById_shouldReturnProduct() throws Exception {
            Product product = ProductDataBuilder.withAllFields().build();
            ProductResponse response = ProductResponseDataBuilder.withAllFields().id(product.getId()).build();

            when(productQueryPort.findById(product.getId())).thenReturn(product);
            when(dtoProductMapper.toResponse(product)).thenReturn(response);

            String content = performProductGet(product.getId())
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            ProductResponse productResponse = objectMapper.readValue(content, ProductResponse.class);
            AssertionUtils.assertFrom(product, productResponse);

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
            Attribute attribute = AttributeDataBuilder.withAllFields().build();
            Category category = CategoryDataBuilder.withAllFields().attributes(List.of(attribute.id())).build();
            CreateProductRequest request = CreateProductRequestDataBuilder.withAllFields()
                    .categoryId(category.getId())
                    .attributes(List.of(new AttributeValue(attribute.id(), List.of("Text"))))
                    .build();
            Product product = ProductDataBuilder.withAllFields()
                    .categoryId(category.getId())
                    .ownerId(userId)
                    .attributes(List.of(new AttributeValue(attribute.id(), List.of("Text"))))
                    .build();
            ArgumentCaptor<Product> argumentCaptor = ArgumentCaptor.forClass(Product.class);

            when(dtoProductMapper.toEntity(request)).thenReturn(product);
            when(categoryQueryPort.findById(category.getId())).thenReturn(category);
            when(attributeQueryPort.findAllByIds(List.of(attribute.id()))).thenReturn(List.of(attribute));
            when(dtoProductMapper.toEntity(request)).thenReturn(product);
            when(productCommandPort.save(product)).thenReturn(product);

            performProductPersist(userId, request).andExpect(status().isOk());

            verify(dtoProductMapper).toEntity(request);
            verify(categoryQueryPort).findById(category.getId());
            verify(attributeQueryPort).findAllByIds(List.of(attribute.id()));
            verify(productEventSenderPort).sendCreated(product, List.of(attribute));
            verify(productCommandPort).save(argumentCaptor.capture());

            Product captorValue = argumentCaptor.getValue();
            assertThat(captorValue.getOwnerId()).isEqualTo(userId);
            assertThat(captorValue.getActive()).isEqualTo(true);

            product.setActive(captorValue.getActive());
            product.setOwnerId(captorValue.getOwnerId());

            AssertionUtils.assertFrom(product, captorValue);
        }

        @Test
        void save_shouldReturnBadRequest_whenRequiredAttributeNotPresent() throws Exception {
            String userId = "userId";
            Attribute requiredAttribute = AttributeDataBuilder.withAllFields().required(true).build();
            AttributeValue attributeValue = new AttributeValue(requiredAttribute.id(), List.of("value"));
            Category category = CategoryDataBuilder.withAllFields().attributes(List.of(attributeValue.id())).build();
            CreateProductRequest request = CreateProductRequestDataBuilder.withAllFields().build();
            Product product = ProductDataBuilder.withAllFields()
                    .categoryId(category.getId())
                    .attributes(List.of())
                    .ownerId(userId)
                    .build();

            when(dtoProductMapper.toEntity(request)).thenReturn(product);
            when(categoryQueryPort.findById(product.getCategoryId())).thenReturn(category);
            when(attributeQueryPort.findAllByIds(List.of(attributeValue.id()))).thenReturn(List.of(requiredAttribute));

            String content = performProductPersist(userId, request)
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
            assertThat(exceptionResponse.getErrors().get(requiredAttribute.title())).isEqualTo("Title is required.");
            assertThat(exceptionResponse.getPath()).isEqualTo("/products");

            verify(dtoProductMapper).toEntity(request);
            verify(categoryQueryPort).findById(product.getCategoryId());
            verify(attributeQueryPort).findAllByIds(List.of(attributeValue.id()));

            verifyNoInteractions(productCommandPort, productEventSenderPort);
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

            verifyNoInteractions(dtoProductMapper, categoryQueryPort, productCommandPort, productEventSenderPort);
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

            verifyNoInteractions(dtoProductMapper, categoryQueryPort, productCommandPort, productEventSenderPort);
        }

        @Test
        void save_shouldReturnNotFound_whenCategoryNotFound() throws Exception {
            String categoryId = "category_id", attributeId = "attribute_id";
            CreateProductRequest request = CreateProductRequestDataBuilder.withAllFields().categoryId(categoryId).build();
            Product product = ProductDataBuilder.withAllFields()
                    .categoryId(categoryId)
                    .attributes(List.of(new AttributeValue(attributeId, List.of("Text"))))
                    .build();

            when(dtoProductMapper.toEntity(request)).thenReturn(product);
            when(categoryQueryPort.findById(categoryId)).thenThrow(new ProductNotFoundException("Category not found."));

            String content = performProductPersist(request).andExpect(status().isNotFound())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
            assertThat(exceptionResponse).isNotNull();
            assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
            assertThat(exceptionResponse.getMessage()).isEqualTo("Category not found.");
            assertThat(exceptionResponse.getPath()).isEqualTo("/products");

            verify(dtoProductMapper).toEntity(request);
            verify(categoryQueryPort).findById(categoryId);
            verifyNoInteractions(productCommandPort, productEventSenderPort);
        }

        @Test
        void save_shouldReturnBadRequest_whenCategoryIsNotChild() throws Exception {
            Category category = CategoryDataBuilder.withAllFields().attributes(null).parentId(null).build();
            CreateProductRequest request = CreateProductRequestDataBuilder.withAllFields().categoryId(category.getId()).build();
            String attributeId = "attribute_id";
            Product product = ProductDataBuilder.withAllFields()
                    .categoryId(category.getId())
                    .attributes(List.of(new AttributeValue(attributeId, List.of("Text"))))
                    .build();

            when(dtoProductMapper.toEntity(request)).thenReturn(product);
            when(categoryQueryPort.findById(request.categoryId())).thenReturn(category);

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
            verify(categoryQueryPort).findById(category.getId());
            verifyNoInteractions(productCommandPort, productEventSenderPort);
        }

        @Nested
        class SaveProductWithAttributes {

            @Nested
            class SaveProductWithNumberAttribute {

                @Test
                void save_shouldSaveProductWithNumberAttribute() throws Exception {
                    Attribute attribute = new Attribute("id", "Title", AttributeType.NUMBER, false, null);
                    AttributeValue attributeValue = new AttributeValue(attribute.id(), List.of("1"));
                    shouldSaveProductWithAttribute(attribute, attributeValue);
                }

                @Test
                void save_shouldReturnBadRequest_whenNumberAttributeIsMoreThanOne() throws Exception {
                    Attribute attribute = new Attribute("id", "Title", AttributeType.NUMBER, false, null);
                    AttributeValue attributeValue = new AttributeValue(attribute.id(), List.of("1", "2"));
                    shouldFailValidationOnSaveProductWithAttribute(attribute, attributeValue, "Must contain exactly one value.");
                }

                @Test
                void save_shouldReturnBadRequest_whenNumberAttributeIsNegative() throws Exception {
                    Attribute attribute = new Attribute("id", "Number", AttributeType.NUMBER, false, null);
                    AttributeValue attributeValue = new AttributeValue(attribute.id(), List.of("-1"));
                    shouldFailValidationOnSaveProductWithAttribute(attribute, attributeValue, "Must be equal or greater than zero.");
                }

                @Test
                void save_shouldReturnBadRequest_whenNumberAttributeIsNotNumeric() throws Exception {
                    Attribute attribute = new Attribute("id", "Number", AttributeType.NUMBER, false, null);
                    AttributeValue attributeValue = new AttributeValue(attribute.id(), List.of("not_numeric"));
                    shouldFailValidationOnSaveProductWithAttribute(attribute, attributeValue, "Invalid number value.");
                }

                @Test
                void save_shouldReturnBadRequest_whenNumberAttributeIsHigherThanIntegerMaxValue() throws Exception {
                    Attribute attribute = new Attribute("id", "Number", AttributeType.NUMBER, false, null);
                    AttributeValue attributeValue = new AttributeValue(attribute.id(), List.of("1_000_000_000_000"));
                    shouldFailValidationOnSaveProductWithAttribute(attribute, attributeValue, "Invalid number value.");
                }
            }

            @Nested
            class SaveProductWithBooleanAttribute {

                @Test
                void save_shouldSaveProductWithBooleanAttribute() throws Exception {
                    Attribute attribute = new Attribute("id", "Boolean", AttributeType.BOOLEAN, false, null);
                    AttributeValue attributeValue = new AttributeValue(attribute.id(), List.of("true"));
                    shouldSaveProductWithAttribute(attribute, attributeValue);
                }

                @Test
                void save_shouldReturnBadRequest_whenBooleanAttributeIsMoreThanOne() throws Exception {
                    Attribute attribute = new Attribute("id", "Boolean", AttributeType.BOOLEAN, false, null);
                    AttributeValue attributeValue = new AttributeValue(attribute.id(), List.of("true", "false"));
                    shouldFailValidationOnSaveProductWithAttribute(attribute, attributeValue, "Must contain exactly one value.");
                }

                @Test
                void save_shouldReturnBadRequest_whenInvalidBooleanAttributeValue() throws Exception {
                    Attribute attribute = new Attribute("id", "Boolean", AttributeType.BOOLEAN, false, null);
                    AttributeValue attributeValue = new AttributeValue(attribute.id(), List.of("not_boolean_value"));
                    shouldFailValidationOnSaveProductWithAttribute(attribute, attributeValue, "Invalid boolean value. Allowed values: true, false.");
                }
            }

            @Nested
            class SaveProductWithEnumAttribute {

                @Test
                void save_shouldSaveProductWithEnumAttribute() throws Exception {
                    Attribute attribute = new Attribute("id", "Enum", AttributeType.ENUM, false, List.of("TYPE1", "TYPE2"));
                    AttributeValue attributeValue = new AttributeValue(attribute.id(), List.of("TYPE1"));
                    shouldSaveProductWithAttribute(attribute, attributeValue);
                }

                @Test
                void save_shouldReturnBadRequest_whenAttributeIsMoreThanOne() throws Exception {
                    Attribute attribute = new Attribute("id", "Enum", AttributeType.ENUM, false, List.of("TYPE1", "TYPE2"));
                    AttributeValue attributeValue = new AttributeValue(attribute.id(), List.of("TYPE1", "TYPE2"));
                    shouldFailValidationOnSaveProductWithAttribute(attribute, attributeValue, "Must contain exactly one value.");
                }

                @Test
                void save_shouldReturnBadRequest_whenAttributeValueIsNotAllowed() throws Exception {
                    Attribute attribute = new Attribute("id", "Enum", AttributeType.ENUM, false, List.of("TYPE1", "TYPE2"));
                    AttributeValue attributeValue = new AttributeValue(attribute.id(), List.of("TYPE3"));
                    shouldFailValidationOnSaveProductWithAttribute(attribute, attributeValue, "Invalid value. Allowed values: " + String.join(", ", attribute.allowedValues()));
                }
            }

            @Nested
            class SaveProductWithRangeAttribute {

                @Test
                void save_shouldSaveProductWithRangeAttribute() throws Exception {
                    Attribute attribute = new Attribute("id", "Range", AttributeType.RANGE, false, List.of("0", "100"));
                    AttributeValue attributeValue = new AttributeValue(attribute.id(), List.of("50", "70"));
                    shouldSaveProductWithAttribute(attribute, attributeValue);
                }

                @Test
                void save_shouldReturnBadRequest_whenAttributesAreMoreThanTwo() throws Exception {
                    Attribute attribute = new Attribute("id", "Range", AttributeType.RANGE, false, List.of("0", "100"));
                    AttributeValue attributeValue = new AttributeValue(attribute.id(), List.of("50", "70", "90"));
                    shouldFailValidationOnSaveProductWithAttribute(attribute, attributeValue, "Must contain exactly two values.");
                }

                @Test
                void save_shouldReturnBadRequest_whenFirstAttributeValueIsLowerThanZero() throws Exception {
                    Attribute attribute = new Attribute("id", "Range", AttributeType.RANGE, false, List.of("0", "100"));
                    AttributeValue attributeValue = new AttributeValue(attribute.id(), List.of("-1", "100"));
                    shouldFailValidationOnSaveProductWithAttribute(attribute, attributeValue, "The first value must be equal or greater than to zero.");
                }

                @Test
                void save_shouldReturnBadRequest_whenFirstAttributeValueIsGreaterThanSecond() throws Exception {
                    Attribute attribute = new Attribute("id", "Range", AttributeType.RANGE, false, List.of("0", "100"));
                    AttributeValue attributeValue = new AttributeValue(attribute.id(), List.of("50", "10"));
                    shouldFailValidationOnSaveProductWithAttribute(attribute, attributeValue, "The first value must be less than the second value.");
                }

                @Test
                void save_shouldReturnBadRequest_whenAttributesAreNotNumericType() throws Exception {
                    Attribute attribute = new Attribute("id", "Range", AttributeType.RANGE, false, List.of("0", "100"));
                    AttributeValue attributeValue = new AttributeValue(attribute.id(), List.of("not_numeric", "not_numeric"));
                    shouldFailValidationOnSaveProductWithAttribute(attribute, attributeValue, "Invalid range value format.");
                }
            }

            @Nested
            class SaveProductWithStringAttribute {

                @Test
                void save_shouldSaveProductWithStringAttribute() throws Exception {
                    Attribute attribute = new Attribute("id", "String", AttributeType.STRING, false, null);
                    AttributeValue attributeValue = new AttributeValue(attribute.id(), List.of("string"));
                    shouldSaveProductWithAttribute(attribute, attributeValue);
                }

                @Test
                void save_shouldReturnBadRequest_whenAttributeIsBlank() throws Exception {
                    Attribute attribute = new Attribute("id", "String", AttributeType.STRING, false, null);
                    AttributeValue attributeValue = new AttributeValue(attribute.id(), List.of(""));
                    shouldFailValidationOnSaveProductWithAttribute(attribute, attributeValue, "Must not be blank.");
                }
            }
        }
    }
}
