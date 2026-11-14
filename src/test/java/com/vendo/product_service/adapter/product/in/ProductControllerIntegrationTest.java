package com.vendo.product_service.adapter.product.in;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vendo.core_lib.utils.AssertionUtils;
import com.vendo.product_service.adapter.product.in.dto.AttributeValueRequest;
import com.vendo.product_service.adapter.product.in.dto.CreateProductRequest;
import com.vendo.product_service.adapter.product.in.dto.ProductResponse;
import com.vendo.product_service.adapter.product.in.dto.UpdateProductRequest;
import com.vendo.product_service.domain.attribute.model.Attribute;
import com.vendo.product_service.domain.attribute.model.AttributeType;
import com.vendo.product_service.domain.attribute.model.AttributeValue;
import com.vendo.product_service.domain.category.exception.CategoryNotFoundException;
import com.vendo.product_service.domain.category.model.Category;
import com.vendo.product_service.domain.product.exception.ProductNotFoundException;
import com.vendo.product_service.domain.product.model.Product;
import com.vendo.product_service.domain.user.User;
import com.vendo.product_service.port.attribute.AttributeQueryPort;
import com.vendo.product_service.port.category.CategoryQueryPort;
import com.vendo.product_service.port.image.usecase.ImageUseCase;
import com.vendo.product_service.port.product.ProductCommandPort;
import com.vendo.product_service.port.product.ProductEventSenderPort;
import com.vendo.product_service.port.product.ProductQueryPort;
import com.vendo.product_service.test_utils.builder.*;
import com.vendo.product_service.test_utils.security.SecurityContextService;
import com.vendo.security_lib.exception.ExceptionResponse;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    private ProductCommandPort productCommandPort;
    @MockitoBean
    private ProductQueryPort productQueryPort;
    @MockitoBean
    private CategoryQueryPort categoryQueryPort;
    @MockitoBean
    private AttributeQueryPort attributeQueryPort;
    @MockitoBean
    private ProductEventSenderPort productEventSenderPort;
    @MockitoBean
    private ImageUseCase imageUseCase;

    private ResultActions performProductPersist(String userId, CreateProductRequest request) throws Exception {
        User user = new User(
                userId,
                "email",
                UserStatus.ACTIVE,
                Set.of(UserRole.USER),
                true
        );

        MockMultipartFile requestPart = new MockMultipartFile(
                "request",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request)
        );

        MockMultipartFile image = new MockMultipartFile(
                "images",
                "image.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "image content".getBytes()
        );

        return mockMvc.perform(
                multipart("/products")
                        .file(requestPart)
                        .file(image)
                        .with(authentication(SecurityContextService.initializeAuth(user)))
        );
    }

    private ResultActions performProductPersist(String request) throws Exception {
        User user = new User("id", "user", UserStatus.ACTIVE, Set.of(UserRole.USER), true);
        return mockMvc.perform(post("/products")
                .with(authentication(SecurityContextService.initializeAuth(user)))
                .content(request)
                .contentType(MediaType.APPLICATION_JSON));
    }

    private ResultActions performProductPersist(CreateProductRequest request) throws Exception {
        return performProductPersist("id", request);
    }

    private ResultActions performProductUpdate(String userId, String productId, UpdateProductRequest updateProductRequest) throws Exception {
        User user = new User(userId, "email", UserStatus.ACTIVE, Set.of(UserRole.USER), true);
        return mockMvc.perform(put("/products/{id}", productId)
                .with(authentication(SecurityContextService.initializeAuth(user)))
                .content(objectMapper.writeValueAsString(updateProductRequest))
                .contentType(MediaType.APPLICATION_JSON));
    }

    private ResultActions performProductGet(String productId) throws Exception {
        User user = new User("id", "email", UserStatus.ACTIVE, Set.of(UserRole.USER), true);
        return mockMvc.perform(get("/products/{id}", productId)
                .with(authentication(SecurityContextService.initializeAuth(user)))
                .contentType(MediaType.APPLICATION_JSON));
    }

    private void shouldSaveProductWithAttribute(Attribute attribute, AttributeValue attributeValue) throws Exception {
        String userId = "user_id";

        Category category = CategoryDataBuilder.withChild()
                .attributes(List.of(attributeValue.id()))
                .build();
        Product product = ProductDataBuilder.withAllFields()
                .categoryId(category.getId())
                .attributes(List.of(attributeValue))
                .ownerId(userId)
                .build();
        CreateProductRequest request = CreateProductRequestDataBuilder.withAllFields()
                .categoryId(category.getId())
                .attributes(List.of(AttributeValueRequestDataBuilder.from(attributeValue)))
                .build();
        ArgumentCaptor<Product> argumentCaptor = ArgumentCaptor.forClass(Product.class);

        when(categoryQueryPort.findById(eq(category.getId()))).thenReturn(category);
        when(attributeQueryPort.findAllByIds(List.of(attributeValue.id()))).thenReturn(List.of(attribute));
        when(productCommandPort.save(any())).thenReturn(product);

        performProductPersist(userId, request).andExpect(status().isOk());

        verify(categoryQueryPort).findById(eq(category.getId()));
        verify(attributeQueryPort).findAllByIds(List.of(attributeValue.id()));
        verify(productEventSenderPort).sendCreated(product, List.of(attribute));
        verify(productCommandPort).save(argumentCaptor.capture());

        Product captorValue = argumentCaptor.getValue();
        assertThat(captorValue.getOwnerId()).isEqualTo(userId);
        assertThat(captorValue.getActive()).isEqualTo(true);
    }

    private void shouldFailValidationOnSaveProductWithAttribute(Attribute attribute, AttributeValue attributeValue, String validationMessage) throws Exception {
        String userId = "userId";

        Category category = CategoryDataBuilder.withChild()
                .attributes(List.of(attributeValue.id()))
                .build();
        CreateProductRequest request = CreateProductRequestDataBuilder.withAllFields()
                .categoryId(category.getId())
                .attributes(List.of(AttributeValueRequestDataBuilder.from(attributeValue)))
                .build();

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
        assertThat(exceptionResponse.getMessage()).isEqualTo("Attributes validation failed.");
        assertThat(exceptionResponse.getErrors()).isNotNull();
        assertThat(exceptionResponse.getErrors().size()).isEqualTo(1);
        assertThat(exceptionResponse.getErrors().get(attribute.slug())).isEqualTo(validationMessage);
        assertThat(exceptionResponse.getPath()).isEqualTo("/products");

        verify(categoryQueryPort).findById(category.getId());
        verify(attributeQueryPort).findAllByIds(List.of(attributeValue.id()));
        verifyNoInteractions(productCommandPort, productEventSenderPort);
    }

    private void shouldFailAttributesValidationOnSave(List<Attribute> originAttributes, List<AttributeValue> requestAttributes, Map<String, String> expectedErrors) throws Exception {
        List<String> originAttributeIds = originAttributes.stream().map(Attribute::id).toList();
        Category category = CategoryDataBuilder.withChild()
                .attributes(originAttributeIds)
                .build();
        CreateProductRequest request = CreateProductRequestDataBuilder.withAllFields()
                .categoryId(category.getId())
                .attributes(AttributeValueRequestDataBuilder.fromAll(requestAttributes))
                .build();

        when(categoryQueryPort.findById(category.getId())).thenReturn(category);
        when(attributeQueryPort.findAllByIds(originAttributeIds)).thenReturn(originAttributes);

        String content = performProductPersist("userId", request)
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
        assertThat(exceptionResponse.getMessage()).isEqualTo("Attributes validation failed.");
        assertThat(exceptionResponse.getErrors()).isEqualTo(expectedErrors);

        verifyNoInteractions(productCommandPort, productEventSenderPort);
    }

    @Nested
    class UpdateProductTests {

        @Test
        void update_shouldReturnBadRequest_andNotUpdateProduct_whenAttributesAreInvalid() throws Exception {
            String productId = "product_id";

            Attribute attribute = AttributeDataBuilder.withAllFields().type(AttributeType.NUMBER).build();
            AttributeValue attributeValue = new AttributeValue(attribute.id(), List.of("not_a_number"));
            UpdateProductRequest request = UpdateProductRequestDataBuilder.withAllFields().isNew(false).attributes(List.of(AttributeValueRequestDataBuilder.from(attributeValue))).build();

            Product existing = ProductDataBuilder.withAllFields().build();
            Category category = CategoryDataBuilder.withChild().attributes(List.of(attribute.id())).build();

            when(productQueryPort.findById(productId)).thenReturn(existing);
            when(categoryQueryPort.findById(existing.getCategoryId())).thenReturn(category);
            when(attributeQueryPort.findAllByIds(List.of(attribute.id()))).thenReturn(List.of(attribute));

            performProductUpdate(existing.getOwnerId(), productId, request).andExpect(status().isBadRequest());

            verifyNoInteractions(productCommandPort, productEventSenderPort);
        }

        @Test
        void update_shouldReturnBadRequest_whenAttributeIdIsBlank() throws Exception {
            UpdateProductRequest request = UpdateProductRequestDataBuilder.withAllFields()
                    .attributes(List.of(new AttributeValueRequest(" ", List.of("value"))))
                    .build();

            String content = performProductUpdate("user_id", "product_id", request)
                    .andExpect(status().isBadRequest())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
            assertThat(exceptionResponse.getMessage()).isEqualTo("Validation failed.");
            assertThat(exceptionResponse.getErrors().size()).isEqualTo(1);
            assertThat(exceptionResponse.getErrors().values().iterator().next()).isEqualTo("Attribute id is required.");

            verifyNoInteractions(productQueryPort, categoryQueryPort, attributeQueryPort, productCommandPort, productEventSenderPort);
        }

        @Test
        void update_shouldUpdateProduct() throws Exception {
            String productId = "product_id";

            Attribute attribute = AttributeDataBuilder.withAllFields().build();
            AttributeValue attributeValue = new AttributeValue(attribute.id(), List.of("string"));
            UpdateProductRequest request = UpdateProductRequestDataBuilder.withAllFields().isNew(false).attributes(List.of(AttributeValueRequestDataBuilder.from(attributeValue))).build();

            Product existing = ProductDataBuilder.withAllFields().attributes(List.of(attributeValue)).build();
            Category category = CategoryDataBuilder.withChild().attributes(List.of(attribute.id())).build();

            when(productQueryPort.findById(productId)).thenReturn(existing);
            when(categoryQueryPort.findById(existing.getCategoryId())).thenReturn(category);
            when(attributeQueryPort.findAllByIds(List.of(attribute.id()))).thenReturn(List.of(attribute));

            performProductUpdate(existing.getOwnerId(), productId, request).andExpect(status().isOk());

            verify(productQueryPort).findById(productId);
            verify(categoryQueryPort).findById(existing.getCategoryId());
            verify(attributeQueryPort).findAllByIds(List.of(attribute.id()));

            ArgumentCaptor<Product> updateProductArgumentCaptor = ArgumentCaptor.forClass(Product.class);
            verify(productCommandPort).update(eq(productId), updateProductArgumentCaptor.capture());
            AssertionUtils.assertFrom(updateProductArgumentCaptor.getValue(), request, "attributes");
            assertThat(updateProductArgumentCaptor.getValue().getAttributes()).isEqualTo(List.of(attributeValue));

            ArgumentCaptor<Product> updateProductEventArgumentCaptor = ArgumentCaptor.forClass(Product.class);
            verify(productEventSenderPort).sendUpdated(updateProductEventArgumentCaptor.capture(), eq(List.of(attribute)));
            AssertionUtils.assertFrom(updateProductEventArgumentCaptor.getValue(), request, "attributes");
            assertThat(updateProductEventArgumentCaptor.getValue().getAttributes()).isEqualTo(List.of(attributeValue));
        }

        @Test
        void update_shouldUpdateProduct_whenCategoryHasRequiredAttributes_andRequestHasNoAttributesForUpdate() throws Exception {
            String productId = "product_id";

            Attribute attribute = AttributeDataBuilder.withAllFields().required(true).build();
            AttributeValue attributeValue = new AttributeValue(attribute.id(), List.of("string"));
            UpdateProductRequest request = UpdateProductRequestDataBuilder.withAllFields().isNew(false).attributes(null).build();

            Product existing = ProductDataBuilder.withAllFields().attributes(List.of(attributeValue)).build();
            Category category = CategoryDataBuilder.withChild().attributes(List.of(attribute.id())).build();

            when(productQueryPort.findById(productId)).thenReturn(existing);
            when(categoryQueryPort.findById(existing.getCategoryId())).thenReturn(category);

            performProductUpdate(existing.getOwnerId(), productId, request).andExpect(status().isOk());

            verify(productQueryPort).findById(productId);
            verify(categoryQueryPort).findById(existing.getCategoryId());
            verifyNoInteractions(attributeQueryPort);

            ArgumentCaptor<Product> updateProductArgumentCaptor = ArgumentCaptor.forClass(Product.class);
            verify(productCommandPort).update(eq(productId), updateProductArgumentCaptor.capture());
            AssertionUtils.assertFrom(updateProductArgumentCaptor.getValue(), request);

            ArgumentCaptor<Product> updateProductEventArgumentCaptor = ArgumentCaptor.forClass(Product.class);
            verify(productEventSenderPort).sendUpdated(updateProductEventArgumentCaptor.capture(), eq(List.of()));
            AssertionUtils.assertFrom(updateProductEventArgumentCaptor.getValue(), request);
        }

        @Test
        void update_shouldReturnBadRequest_andNotUpdateProduct_whenAttributesAreEmpty_andCategoryHasRequiredAttribute() throws Exception {
            String productId = "product_id";

            Attribute attribute = AttributeDataBuilder.withAllFields().required(true).build();
            UpdateProductRequest request = UpdateProductRequestDataBuilder.withAllFields().isNew(false).attributes(List.of()).build();

            Product existing = ProductDataBuilder.withAllFields().build();
            Category category = CategoryDataBuilder.withChild().attributes(List.of(attribute.id())).build();

            when(productQueryPort.findById(productId)).thenReturn(existing);
            when(categoryQueryPort.findById(existing.getCategoryId())).thenReturn(category);
            when(attributeQueryPort.findAllByIds(List.of(attribute.id()))).thenReturn(List.of(attribute));

            String content = performProductUpdate(existing.getOwnerId(), productId, request)
                    .andExpect(status().isBadRequest())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
            assertThat(exceptionResponse.getMessage()).isEqualTo("Attributes validation failed.");
            assertThat(exceptionResponse.getErrors()).isEqualTo(Map.of(attribute.slug(), attribute.title() + " is required."));

            verifyNoInteractions(productCommandPort, productEventSenderPort);
        }

        @Test
        void update_shouldUpdateProduct_whenAttributesAreEmpty_andCategoryHasNoRequiredAttributes() throws Exception {
            String productId = "product_id";

            Attribute attribute = AttributeDataBuilder.withAllFields().required(false).build();
            UpdateProductRequest request = UpdateProductRequestDataBuilder.withAllFields().isNew(false).attributes(List.of()).build();

            Product existing = ProductDataBuilder.withAllFields().build();
            Category category = CategoryDataBuilder.withChild().attributes(List.of(attribute.id())).build();

            when(productQueryPort.findById(productId)).thenReturn(existing);
            when(categoryQueryPort.findById(existing.getCategoryId())).thenReturn(category);
            when(attributeQueryPort.findAllByIds(List.of(attribute.id()))).thenReturn(List.of(attribute));

            performProductUpdate(existing.getOwnerId(), productId, request).andExpect(status().isOk());

            verify(attributeQueryPort).findAllByIds(List.of(attribute.id()));
            verify(productCommandPort).update(eq(productId), any());
            verify(productEventSenderPort).sendUpdated(any(), eq(List.of(attribute)));
        }

        @Test
        void update_shouldReturnBadRequest_andNotUpdateProduct_whenAttributeIsNotInCategory() throws Exception {
            String productId = "product_id";

            Attribute attribute = AttributeDataBuilder.withAllFields().build();
            UpdateProductRequest request = UpdateProductRequestDataBuilder.withAllFields()
                    .attributes(List.of(new AttributeValueRequest("unknown_id", List.of("value"))))
                    .build();

            Product existing = ProductDataBuilder.withAllFields().build();
            Category category = CategoryDataBuilder.withChild().attributes(List.of(attribute.id())).build();

            when(productQueryPort.findById(productId)).thenReturn(existing);
            when(categoryQueryPort.findById(existing.getCategoryId())).thenReturn(category);
            when(attributeQueryPort.findAllByIds(List.of(attribute.id()))).thenReturn(List.of(attribute));

            String content = performProductUpdate(existing.getOwnerId(), productId, request)
                    .andExpect(status().isBadRequest())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
            assertThat(exceptionResponse.getErrors()).isEqualTo(Map.of("attributes", "Attributes are not allowed for this category: unknown_id."));

            verifyNoInteractions(productCommandPort, productEventSenderPort);
        }

        @Nested
        class UpdateProductCategoryTests {

            private final String productId = "product_id";

            private final Attribute oldAttribute = new Attribute("old_attribute_id", "Old", "old-slug", AttributeType.STRING, false, null);
            private final Attribute newAttribute = new Attribute("new_attribute_id", "New", "new-slug", AttributeType.STRING, false, null);

            private final Product existing = ProductDataBuilder.withAllFields()
                    .attributes(List.of(new AttributeValue(oldAttribute.id(), List.of("old_value"))))
                    .build();
            private final Category newCategory = CategoryDataBuilder.withChild().attributes(List.of(newAttribute.id())).build();

            @Test
            void update_shouldValidateAttributesAgainstNewCategory_whenCategoryChanged() throws Exception {
                AttributeValueRequest newAttributeValue = new AttributeValueRequest(newAttribute.id(), List.of("new_value"));
                UpdateProductRequest request = UpdateProductRequestDataBuilder.withAllFields()
                        .categoryId(newCategory.getId())
                        .attributes(List.of(newAttributeValue))
                        .build();

                when(productQueryPort.findById(productId)).thenReturn(existing);
                when(categoryQueryPort.findById(newCategory.getId())).thenReturn(newCategory);
                when(attributeQueryPort.findAllByIds(List.of(newAttribute.id()))).thenReturn(List.of(newAttribute));

                performProductUpdate(existing.getOwnerId(), productId, request).andExpect(status().isOk());

                verify(categoryQueryPort, never()).findById(existing.getCategoryId());
                verify(attributeQueryPort).findAllByIds(List.of(newAttribute.id()));

                ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
                verify(productCommandPort).update(eq(productId), captor.capture());
                assertThat(captor.getValue().getCategoryId()).isEqualTo(newCategory.getId());
                assertThat(captor.getValue().getAttributes()).isEqualTo(List.of(new AttributeValue(newAttribute.id(), List.of("new_value"))));

                verify(productEventSenderPort).sendUpdated(any(), eq(List.of(newAttribute)));
            }

            @Test
            void update_shouldReturnBadRequest_whenCategoryChanged_andAttributesBelongToOldCategory() throws Exception {
                UpdateProductRequest request = UpdateProductRequestDataBuilder.withAllFields()
                        .categoryId(newCategory.getId())
                        .attributes(List.of(new AttributeValueRequest(oldAttribute.id(), List.of("old_value"))))
                        .build();

                when(productQueryPort.findById(productId)).thenReturn(existing);
                when(categoryQueryPort.findById(newCategory.getId())).thenReturn(newCategory);
                when(attributeQueryPort.findAllByIds(List.of(newAttribute.id()))).thenReturn(List.of(newAttribute));

                ExceptionResponse exceptionResponse = performFailedUpdate(request, status().isBadRequest());
                assertThat(exceptionResponse.getErrors()).isEqualTo(Map.of("attributes", "Attributes are not allowed for this category: old_attribute_id."));

                verifyNoInteractions(productCommandPort, productEventSenderPort);
            }

            @Test
            void update_shouldClearOldAttributes_whenCategoryChanged_andAttributesNotPassed() throws Exception {
                UpdateProductRequest request = UpdateProductRequestDataBuilder.withAllFields()
                        .categoryId(newCategory.getId())
                        .attributes(null)
                        .build();

                when(productQueryPort.findById(productId)).thenReturn(existing);
                when(categoryQueryPort.findById(newCategory.getId())).thenReturn(newCategory);
                when(attributeQueryPort.findAllByIds(List.of(newAttribute.id()))).thenReturn(List.of(newAttribute));

                performProductUpdate(existing.getOwnerId(), productId, request).andExpect(status().isOk());

                ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
                verify(productCommandPort).update(eq(productId), captor.capture());
                assertThat(captor.getValue().getCategoryId()).isEqualTo(newCategory.getId());
                assertThat(captor.getValue().getAttributes()).isEqualTo(List.of());
            }

            @Test
            void update_shouldReturnBadRequest_whenCategoryChanged_attributesNotPassed_andNewCategoryHasRequiredAttribute() throws Exception {
                Attribute requiredAttribute = new Attribute("required_id", "Required", "required-slug", AttributeType.STRING, true, null);
                Category categoryWithRequired = CategoryDataBuilder.withChild().attributes(List.of(requiredAttribute.id())).build();
                UpdateProductRequest request = UpdateProductRequestDataBuilder.withAllFields()
                        .categoryId(categoryWithRequired.getId())
                        .attributes(null)
                        .build();

                when(productQueryPort.findById(productId)).thenReturn(existing);
                when(categoryQueryPort.findById(categoryWithRequired.getId())).thenReturn(categoryWithRequired);
                when(attributeQueryPort.findAllByIds(List.of(requiredAttribute.id()))).thenReturn(List.of(requiredAttribute));

                ExceptionResponse exceptionResponse = performFailedUpdate(request, status().isBadRequest());
                assertThat(exceptionResponse.getErrors()).isEqualTo(Map.of(requiredAttribute.slug(), "Required is required."));

                verifyNoInteractions(productCommandPort, productEventSenderPort);
            }

            @Test
            void update_shouldReturnNotFound_whenNewCategoryNotFound() throws Exception {
                UpdateProductRequest request = UpdateProductRequestDataBuilder.withAllFields()
                        .categoryId("missing_category_id")
                        .build();

                when(productQueryPort.findById(productId)).thenReturn(existing);
                when(categoryQueryPort.findById("missing_category_id")).thenThrow(new CategoryNotFoundException("Category not found."));

                ExceptionResponse exceptionResponse = performFailedUpdate(request, status().isNotFound());
                assertThat(exceptionResponse.getMessage()).isEqualTo("Category not found.");

                verifyNoInteractions(attributeQueryPort, productCommandPort, productEventSenderPort);
            }

            @Test
            void update_shouldReturnBadRequest_whenNewCategoryIsNotChild() throws Exception {
                Category subCategory = CategoryDataBuilder.withSub().build();
                UpdateProductRequest request = UpdateProductRequestDataBuilder.withAllFields()
                        .categoryId(subCategory.getId())
                        .build();

                when(productQueryPort.findById(productId)).thenReturn(existing);
                when(categoryQueryPort.findById(subCategory.getId())).thenReturn(subCategory);

                ExceptionResponse exceptionResponse = performFailedUpdate(request, status().isBadRequest());
                assertThat(exceptionResponse.getMessage()).isEqualTo("Category type should be child.");

                verifyNoInteractions(attributeQueryPort, productCommandPort, productEventSenderPort);
            }

            @Test
            void update_shouldValidateAgainstCurrentCategory_whenCategoryIdIsSameAsExisting() throws Exception {
                Category currentCategory = CategoryDataBuilder.withChild().id(existing.getCategoryId()).attributes(List.of(oldAttribute.id())).build();
                UpdateProductRequest request = UpdateProductRequestDataBuilder.withAllFields()
                        .categoryId(existing.getCategoryId())
                        .attributes(null)
                        .build();

                when(productQueryPort.findById(productId)).thenReturn(existing);
                when(categoryQueryPort.findById(existing.getCategoryId())).thenReturn(currentCategory);

                performProductUpdate(existing.getOwnerId(), productId, request).andExpect(status().isOk());

                ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
                verify(productCommandPort).update(eq(productId), captor.capture());
                assertThat(captor.getValue().getAttributes()).isNull();
                verifyNoInteractions(attributeQueryPort);
            }

            private ExceptionResponse performFailedUpdate(UpdateProductRequest request, ResultMatcher expectedStatus) throws Exception {
                String content = performProductUpdate(existing.getOwnerId(), productId, request)
                        .andExpect(expectedStatus)
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

                return objectMapper.readValue(content, ExceptionResponse.class);
            }
        }

        @Test
        void update_returnNotFound_whenProductNotFound() throws Exception {
            UpdateProductRequest request = UpdateProductRequestDataBuilder.withAllFields().build();
            Product product = ProductDataBuilder.withAllFields().build();

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

            verify(productQueryPort).findById(product.getId());
            verifyNoInteractions(categoryQueryPort, productCommandPort, productEventSenderPort);
        }

        @Test
        void update_shouldReturnForbidden_whenAuthenticatedUserIsNotOwner() throws Exception {
            UpdateProductRequest request = UpdateProductRequestDataBuilder.withAllFields().build();
            Product product = ProductDataBuilder.withAllFields().build();

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

            verify(productQueryPort).findById(product.getId());
            verifyNoInteractions(categoryQueryPort, productCommandPort, productEventSenderPort);
        }

        @Test
        void update_shouldUpdateProduct_whenNoAttributes() throws Exception {
            Product product = ProductDataBuilder.withAllFields().attributes(null).build();
            UpdateProductRequest request = UpdateProductRequestDataBuilder.withAllFields().attributes(null).build();
            Category category = CategoryDataBuilder.withChild().attributes(null).build();

            when(productQueryPort.findById(product.getId())).thenReturn(product);
            when(categoryQueryPort.findById(product.getCategoryId())).thenReturn(category);
            when(attributeQueryPort.findAllByIds(category.getAttributes())).thenReturn(List.of());

            performProductUpdate(product.getOwnerId(), product.getId(), request).andExpect(status().isOk());

            verify(productQueryPort).findById(product.getId());
            verify(categoryQueryPort).findById(product.getCategoryId());
            verifyNoInteractions(attributeQueryPort);

            ArgumentCaptor<Product> updateProductArgumentCaptor = ArgumentCaptor.forClass(Product.class);
            verify(productCommandPort).update(eq(product.getId()), updateProductArgumentCaptor.capture());
            AssertionUtils.assertFrom(updateProductArgumentCaptor.getValue(), request);

            ArgumentCaptor<Product> updateProductEventArgumentCaptor = ArgumentCaptor.forClass(Product.class);
            verify(productEventSenderPort).sendUpdated(updateProductEventArgumentCaptor.capture(), eq(List.of()));
            AssertionUtils.assertFrom(updateProductEventArgumentCaptor.getValue(), request);
        }
    }

    @Nested
    class FindProductTests {

        @Test
        void findById_shouldReturnProduct() throws Exception {
            Product product = ProductDataBuilder.withAllFields().build();

            when(productQueryPort.findById(product.getId())).thenReturn(product);

            String content = performProductGet(product.getId())
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            ProductResponse productResponse = objectMapper.readValue(content, ProductResponse.class);
            AssertionUtils.assertFrom(product, productResponse);

            verify(productQueryPort).findById(product.getId());
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
        }
    }

    @Nested
    class SaveProductTests {

        @Test
        void save_shouldSaveProduct() throws Exception {
            String userId = "user_id";

            Attribute attribute = AttributeDataBuilder.withAllFields().build();
            List<AttributeValue> attributeValues = List.of(new AttributeValue(attribute.id(), List.of("Text")));
            Category category = CategoryDataBuilder.withChild().attributes(List.of(attribute.id())).build();
            CreateProductRequest request = CreateProductRequestDataBuilder.withAllFields()
                    .categoryId(category.getId())
                    .attributes(AttributeValueRequestDataBuilder.fromAll(attributeValues))
                    .build();
            Product product = ProductDataBuilder.withAllFields()
                    .categoryId(category.getId())
                    .ownerId(userId)
                    .attributes(attributeValues)
                    .build();
            ArgumentCaptor<Product> argumentCaptor = ArgumentCaptor.forClass(Product.class);

            when(categoryQueryPort.findById(request.categoryId())).thenReturn(category);
            when(attributeQueryPort.findAllByIds(category.getAttributes())).thenReturn(List.of(attribute));
            when(productCommandPort.save(any())).thenReturn(product);

            performProductPersist(userId, request).andExpect(status().isOk());

            verify(categoryQueryPort).findById(category.getId());
            verify(attributeQueryPort).findAllByIds(List.of(attribute.id()));
            verify(productEventSenderPort).sendCreated(any(), eq(List.of(attribute)));
            verify(productCommandPort).save(argumentCaptor.capture());

            Product captorValue = argumentCaptor.getValue();
            assertThat(captorValue.getOwnerId()).isEqualTo(userId);
            assertThat(captorValue.getIsNew()).isTrue();
            assertThat(captorValue.getActive()).isTrue();
        }

        @Test
        void save_shouldReturnBadRequest_whenRequiredAttributeNotPresent() throws Exception {
            String userId = "userId";

            Attribute requiredStringAttribute = AttributeDataBuilder.withAllFields().type(AttributeType.STRING).title("String").slug("string").required(true).build();
            Attribute requiredNumberAttribute = AttributeDataBuilder.withAllFields().type(AttributeType.NUMBER).title("Number").slug("number").required(true).build();
            Attribute notRequiredEnumAttribute = AttributeDataBuilder.withAllFields().type(AttributeType.ENUM).title("Enum").slug("enum").allowedValues(List.of("TYPE1")).build();
            List<Attribute> attributes = List.of(requiredStringAttribute, requiredNumberAttribute, notRequiredEnumAttribute);
            List<String> attributeIds = attributes.stream().map(Attribute::id).toList();
            List<AttributeValue> attributeValues = List.of(new AttributeValue(notRequiredEnumAttribute.id(), List.of("TYPE1")));

            Category category = CategoryDataBuilder.withChild().attributes(attributeIds).build();
            CreateProductRequest request = CreateProductRequestDataBuilder.withAllFields()
                    .categoryId(category.getId())
                    .attributes(AttributeValueRequestDataBuilder.fromAll(attributeValues))
                    .build();

            when(categoryQueryPort.findById(request.categoryId())).thenReturn(category);
            when(attributeQueryPort.findAllByIds(attributeIds)).thenReturn(attributes);

            String content = performProductPersist(userId, request)
                    .andExpect(status().isBadRequest())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
            assertThat(exceptionResponse).isNotNull();
            assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            assertThat(exceptionResponse.getMessage()).isEqualTo("Attributes validation failed.");
            assertThat(exceptionResponse.getErrors()).isNotNull();
            assertThat(exceptionResponse.getErrors().size()).isEqualTo(2);
            assertThat(exceptionResponse.getErrors().get(requiredStringAttribute.slug())).isNotNull();
            assertThat(exceptionResponse.getErrors().get(requiredStringAttribute.slug())).isEqualTo(requiredStringAttribute.title() + " is required.");
            assertThat(exceptionResponse.getErrors().get(requiredNumberAttribute.slug())).isNotNull();
            assertThat(exceptionResponse.getErrors().get(requiredNumberAttribute.slug())).isEqualTo(requiredNumberAttribute.title() + " is required.");

            assertThat(exceptionResponse.getPath()).isEqualTo("/products");

            verify(categoryQueryPort).findById(request.categoryId());
            verify(attributeQueryPort).findAllByIds(attributeIds);

            verifyNoInteractions(productCommandPort, productEventSenderPort);
        }

        @Test
        void save_shouldReturnBadRequest_whenAttributesParameterNotPresent_andOneCategoryAttributeIsRequired() throws Exception {
            String userId = "userId";

            Attribute requiredStringAttribute = AttributeDataBuilder.withAllFields().type(AttributeType.STRING).title("String").required(true).build();
            List<Attribute> attributes = List.of(requiredStringAttribute);
            List<String> attributeIds = attributes.stream().map(Attribute::id).toList();

            Category category = CategoryDataBuilder.withChild().attributes(attributeIds).build();
            CreateProductRequest request = CreateProductRequestDataBuilder.withAllFields()
                    .categoryId(category.getId())
                    .attributes(null)
                    .build();

            when(categoryQueryPort.findById(request.categoryId())).thenReturn(category);
            when(attributeQueryPort.findAllByIds(attributeIds)).thenReturn(attributes);

            String content = performProductPersist(userId, request)
                    .andExpect(status().isBadRequest())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
            assertThat(exceptionResponse).isNotNull();
            assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            assertThat(exceptionResponse.getMessage()).isEqualTo("Attributes validation failed.");
            assertThat(exceptionResponse.getErrors()).isNotNull();
            assertThat(exceptionResponse.getErrors().size()).isEqualTo(1);
            assertThat(exceptionResponse.getErrors().get(requiredStringAttribute.slug())).isNotNull();
            assertThat(exceptionResponse.getErrors().get(requiredStringAttribute.slug())).isEqualTo(requiredStringAttribute.title() + " is required.");

            assertThat(exceptionResponse.getPath()).isEqualTo("/products");

            verify(categoryQueryPort).findById(request.categoryId());
            verify(attributeQueryPort).findAllByIds(attributeIds);

            verifyNoInteractions(productCommandPort, productEventSenderPort);
        }

        @Test
        void save_shouldSaveProduct_whenAttributesParameterNotPresent_andNoRequiredAttributes() throws Exception {
            String userId = "userId";

            Attribute attribute = AttributeDataBuilder.withAllFields().type(AttributeType.STRING).title("String").build();
            List<Attribute> attributes = List.of(attribute);
            List<String> attributeIds = attributes.stream().map(Attribute::id).toList();
            ArgumentCaptor<Product> argumentCaptor = ArgumentCaptor.forClass(Product.class);

            Category category = CategoryDataBuilder.withChild().attributes(attributeIds).build();
            CreateProductRequest request = CreateProductRequestDataBuilder.withAllFields()
                    .categoryId(category.getId())
                    .attributes(null)
                    .build();

            when(categoryQueryPort.findById(request.categoryId())).thenReturn(category);
            when(attributeQueryPort.findAllByIds(attributeIds)).thenReturn(attributes);

            performProductPersist(userId, request).andExpect(status().isOk());

            verify(categoryQueryPort).findById(category.getId());
            verify(attributeQueryPort).findAllByIds(List.of(attribute.id()));
            verify(productEventSenderPort).sendCreated(any(), eq(List.of(attribute)));
            verify(productCommandPort).save(argumentCaptor.capture());

            Product captorValue = argumentCaptor.getValue();
            assertThat(captorValue.getOwnerId()).isEqualTo(userId);
        }

        @Test
        void save_shouldReturnBadRequest_whenValidationFailed() throws Exception {
            CreateProductRequest request = CreateProductRequestDataBuilder.withAllFields()
                    .title(null)
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
            assertThat(exceptionResponse.getErrors().size()).isEqualTo(1);
            assertThat(exceptionResponse.getPath()).isEqualTo("/products");

            verifyNoInteractions(categoryQueryPort, productCommandPort, productEventSenderPort);
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
            assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value());
            assertThat(exceptionResponse.getMessage()).isEqualTo("Unsupported media type.");
            assertThat(exceptionResponse.getPath()).isEqualTo("/products");

            verifyNoInteractions(categoryQueryPort, productCommandPort, productEventSenderPort);
        }

        @Test
        void save_shouldReturnNotFound_whenCategoryNotFound() throws Exception {
            String categoryId = "category_id";
            CreateProductRequest request = CreateProductRequestDataBuilder.withAllFields().categoryId(categoryId).build();

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

            verify(categoryQueryPort).findById(categoryId);
            verifyNoInteractions(productCommandPort, productEventSenderPort);
        }

        @Test
        void save_shouldReturnBadRequest_whenCategoryIsNotChild() throws Exception {
            Category category = CategoryDataBuilder.withChild().attributes(null).parentId(null).build();
            CreateProductRequest request = CreateProductRequestDataBuilder.withAllFields().categoryId(category.getId()).build();

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

            verify(categoryQueryPort).findById(category.getId());
            verifyNoInteractions(productCommandPort, productEventSenderPort);
        }

        @Test
        void save_shouldSaveProduct_andShouldNotValidateAttribute_whenNotRequired() throws Exception {
            String userId = "user_id";

            Attribute attribute = AttributeDataBuilder.withAllFields().required(false).build();
            Category category = CategoryDataBuilder.withChild().attributes(List.of(attribute.id())).build();
            CreateProductRequest request = CreateProductRequestDataBuilder.withAllFields()
                    .categoryId(category.getId())
                    .attributes(null)
                    .build();
            Product product = ProductDataBuilder.withAllFields()
                    .categoryId(category.getId())
                    .ownerId(userId)
                    .build();
            ArgumentCaptor<Product> argumentCaptor = ArgumentCaptor.forClass(Product.class);

            when(categoryQueryPort.findById(request.categoryId())).thenReturn(category);
            when(attributeQueryPort.findAllByIds(category.getAttributes())).thenReturn(List.of(attribute));
            when(productCommandPort.save(any())).thenReturn(product);

            performProductPersist(userId, request).andExpect(status().isOk());

            verify(categoryQueryPort).findById(category.getId());
            verify(attributeQueryPort).findAllByIds(List.of(attribute.id()));
            verify(productEventSenderPort).sendCreated(any(), eq(List.of(attribute)));
            verify(productCommandPort).save(argumentCaptor.capture());

            Product captorValue = argumentCaptor.getValue();
            assertThat(captorValue.getOwnerId()).isEqualTo(userId);
            assertThat(captorValue.getActive()).isEqualTo(true);
        }

        @Test
        void save_shouldSaveProduct_andShouldValidateAttribute_whenNotRequired_butNotEmpty() throws Exception {
            String userId = "user_id";

            Attribute attribute = AttributeDataBuilder.withAllFields().required(false).build();
            List<AttributeValue> attributeValues = List.of(new AttributeValue(attribute.id(), List.of("Text")));
            Category category = CategoryDataBuilder.withChild().attributes(List.of(attribute.id())).build();
            CreateProductRequest request = CreateProductRequestDataBuilder.withAllFields()
                    .categoryId(category.getId())
                    .attributes(AttributeValueRequestDataBuilder.fromAll(attributeValues))
                    .build();
            Product product = ProductDataBuilder.withAllFields()
                    .categoryId(category.getId())
                    .ownerId(userId)
                    .attributes(attributeValues)
                    .build();
            ArgumentCaptor<Product> argumentCaptor = ArgumentCaptor.forClass(Product.class);

            when(categoryQueryPort.findById(request.categoryId())).thenReturn(category);
            when(attributeQueryPort.findAllByIds(category.getAttributes())).thenReturn(List.of(attribute));
            when(productCommandPort.save(any())).thenReturn(product);

            performProductPersist(userId, request).andExpect(status().isOk());

            verify(categoryQueryPort).findById(category.getId());
            verify(attributeQueryPort).findAllByIds(List.of(attribute.id()));
            verify(productEventSenderPort).sendCreated(any(), eq(List.of(attribute)));
            verify(productCommandPort).save(argumentCaptor.capture());

            Product captorValue = argumentCaptor.getValue();
            assertThat(captorValue.getOwnerId()).isEqualTo(userId);
            assertThat(captorValue.getActive()).isEqualTo(true);
        }

        @Test
        void save_shouldReturnBadRequest_whenAttributeNotRequired_butNotEmpty_andNotValid() throws Exception {
            String userId = "user_id";

            Attribute attribute = AttributeDataBuilder.withAllFields().required(false).type(AttributeType.NUMBER).build();
            List<AttributeValue> attributeValues = List.of(new AttributeValue(attribute.id(), List.of("not_number_value")));
            Category category = CategoryDataBuilder.withChild().attributes(List.of(attribute.id())).build();
            CreateProductRequest request = CreateProductRequestDataBuilder.withAllFields()
                    .categoryId(category.getId())
                    .attributes(AttributeValueRequestDataBuilder.fromAll(attributeValues))
                    .build();
            when(categoryQueryPort.findById(request.categoryId())).thenReturn(category);
            when(attributeQueryPort.findAllByIds(category.getAttributes())).thenReturn(List.of(attribute));

            String content = performProductPersist(userId, request).andExpect(status().isBadRequest())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
            assertThat(exceptionResponse).isNotNull();
            assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            assertThat(exceptionResponse.getMessage()).isEqualTo("Attributes validation failed.");
            assertThat(exceptionResponse.getErrors()).isNotNull();
            assertThat(exceptionResponse.getErrors().size()).isEqualTo(1);
            assertThat(exceptionResponse.getErrors().get(attribute.slug())).isEqualTo("Invalid number value.");

            assertThat(exceptionResponse.getPath()).isEqualTo("/products");

            verify(categoryQueryPort).findById(category.getId());
            verify(attributeQueryPort).findAllByIds(List.of(attribute.id()));
            verifyNoInteractions(productEventSenderPort, productCommandPort);
        }

        @Nested
        class SaveProductWithAttributes {

            @Nested
            class SaveProductWithNumberAttribute {

                @Test
                void save_shouldSaveProductWithNumberAttribute() throws Exception {
                    Attribute attribute = new Attribute("id", "Title", "slug", AttributeType.NUMBER, false, null);
                    AttributeValue attributeValue = new AttributeValue(attribute.id(), List.of("1"));
                    shouldSaveProductWithAttribute(attribute, attributeValue);
                }

                @Test
                void save_shouldReturnBadRequest_whenNumberAttributeIsMoreThanOne() throws Exception {
                    Attribute attribute = new Attribute("id", "Title", "slug", AttributeType.NUMBER, false, null);
                    AttributeValue attributeValue = new AttributeValue(attribute.id(), List.of("1", "2"));
                    shouldFailValidationOnSaveProductWithAttribute(attribute, attributeValue, "Must contain exactly one value.");
                }

                @Test
                void save_shouldReturnBadRequest_whenNumberAttributeIsNegative() throws Exception {
                    Attribute attribute = new Attribute("id", "Number", "slug", AttributeType.NUMBER, false, null);
                    AttributeValue attributeValue = new AttributeValue(attribute.id(), List.of("-1"));
                    shouldFailValidationOnSaveProductWithAttribute(attribute, attributeValue, "Must be equal or greater than zero.");
                }

                @Test
                void save_shouldReturnBadRequest_whenNumberAttributeIsNotNumeric() throws Exception {
                    Attribute attribute = new Attribute("id", "Number", "slug", AttributeType.NUMBER, false, null);
                    AttributeValue attributeValue = new AttributeValue(attribute.id(), List.of("not_numeric"));
                    shouldFailValidationOnSaveProductWithAttribute(attribute, attributeValue, "Invalid number value.");
                }

                @Test
                void save_shouldReturnBadRequest_whenNumberAttributeIsHigherThanIntegerMaxValue() throws Exception {
                    Attribute attribute = new Attribute("id", "Number", "slug", AttributeType.NUMBER, false, null);
                    AttributeValue attributeValue = new AttributeValue(attribute.id(), List.of("1_000_000_000_000"));
                    shouldFailValidationOnSaveProductWithAttribute(attribute, attributeValue, "Invalid number value.");
                }
            }

            @Nested
            class SaveProductWithBooleanAttribute {

                @Test
                void save_shouldSaveProductWithBooleanAttribute() throws Exception {
                    Attribute attribute = new Attribute("id", "Boolean", "slug", AttributeType.BOOLEAN, false, null);
                    AttributeValue attributeValue = new AttributeValue(attribute.id(), List.of("true"));
                    shouldSaveProductWithAttribute(attribute, attributeValue);
                }

                @Test
                void save_shouldReturnBadRequest_whenBooleanAttributeIsMoreThanOne() throws Exception {
                    Attribute attribute = new Attribute("id", "Boolean", "slug", AttributeType.BOOLEAN, false, null);
                    AttributeValue attributeValue = new AttributeValue(attribute.id(), List.of("true", "false"));
                    shouldFailValidationOnSaveProductWithAttribute(attribute, attributeValue, "Must contain exactly one value.");
                }

                @Test
                void save_shouldReturnBadRequest_whenInvalidBooleanAttributeValue() throws Exception {
                    Attribute attribute = new Attribute("id", "Boolean", "slug", AttributeType.BOOLEAN, false, null);
                    AttributeValue attributeValue = new AttributeValue(attribute.id(), List.of("not_boolean_value"));
                    shouldFailValidationOnSaveProductWithAttribute(attribute, attributeValue, "Invalid boolean value. Allowed values: true, false.");
                }
            }

            @Nested
            class SaveProductWithEnumAttribute {

                @Test
                void save_shouldSaveProductWithEnumAttribute() throws Exception {
                    Attribute attribute = new Attribute("id", "Enum", "slug", AttributeType.ENUM, false, List.of("TYPE1", "TYPE2"));
                    AttributeValue attributeValue = new AttributeValue(attribute.id(), List.of("TYPE1"));
                    shouldSaveProductWithAttribute(attribute, attributeValue);
                }

                @Test
                void save_shouldReturnBadRequest_whenAttributeIsMoreThanOne() throws Exception {
                    Attribute attribute = new Attribute("id", "Enum", "slug", AttributeType.ENUM, false, List.of("TYPE1", "TYPE2"));
                    AttributeValue attributeValue = new AttributeValue(attribute.id(), List.of("TYPE1", "TYPE2"));
                    shouldFailValidationOnSaveProductWithAttribute(attribute, attributeValue, "Must contain exactly one value.");
                }

                @Test
                void save_shouldReturnBadRequest_whenAttributeValueIsNotAllowed() throws Exception {
                    Attribute attribute = new Attribute("id", "Enum", "slug", AttributeType.ENUM, false, List.of("TYPE1", "TYPE2"));
                    AttributeValue attributeValue = new AttributeValue(attribute.id(), List.of("TYPE3"));
                    shouldFailValidationOnSaveProductWithAttribute(attribute, attributeValue, "Invalid value. Allowed values: " + String.join(", ", attribute.allowedValues()));
                }
            }

            @Nested
            class SaveProductWithInvalidRequestStructure {

                private final Attribute stringAttribute = new Attribute("string_id", "Same title", "string-slug", AttributeType.STRING, false, null);
                private final Attribute numberAttribute = new Attribute("number_id", "Same title", "number-slug", AttributeType.NUMBER, false, null);

                @Test
                void save_shouldReturnBadRequest_whenAttributeIsNotInCategory() throws Exception {
                    List<AttributeValue> request = List.of(
                            new AttributeValue(stringAttribute.id(), List.of("value")),
                            new AttributeValue("unknown_id", List.of("value"))
                    );
                    shouldFailAttributesValidationOnSave(List.of(stringAttribute), request, Map.of("attributes", "Attributes are not allowed for this category: unknown_id."));
                }

                @Test
                void save_shouldReturnBadRequest_whenAttributeIsDuplicated() throws Exception {
                    List<AttributeValue> request = List.of(
                            new AttributeValue(stringAttribute.id(), List.of("value")),
                            new AttributeValue(stringAttribute.id(), List.of(" "))
                    );
                    shouldFailAttributesValidationOnSave(List.of(stringAttribute), request, Map.of(stringAttribute.slug(), "Attribute is duplicated."));
                }

                @Test
                void save_shouldReturnAllNotAllowedIdsUnderOneKey_whenSeveralAttributesAreNotInCategory() throws Exception {
                    List<AttributeValue> request = List.of(
                            new AttributeValue("unknown_1", List.of("value")),
                            new AttributeValue(stringAttribute.id(), List.of("value")),
                            new AttributeValue("unknown_2", List.of("value")),
                            new AttributeValue("unknown_1", List.of("value"))
                    );
                    shouldFailAttributesValidationOnSave(List.of(stringAttribute), request, Map.of("attributes", "Attributes are not allowed for this category: unknown_1, unknown_2."));
                }

                @Test
                void save_shouldReturnNotAllowedAndDuplicatedErrorsTogether() throws Exception {
                    List<AttributeValue> request = List.of(
                            new AttributeValue(stringAttribute.id(), List.of("value")),
                            new AttributeValue(stringAttribute.id(), List.of("value")),
                            new AttributeValue("unknown_id", List.of("value"))
                    );
                    shouldFailAttributesValidationOnSave(List.of(stringAttribute), request, Map.of(
                            stringAttribute.slug(), "Attribute is duplicated.",
                            "attributes", "Attributes are not allowed for this category: unknown_id."
                    ));
                }

                @Test
                void save_shouldSaveProduct_whenAttributeHasIdAndEmptyValues() throws Exception {
                    shouldSaveProductWithAttribute(stringAttribute, new AttributeValue(stringAttribute.id(), List.of()));
                }

                @Test
                void save_shouldReturnBadRequest_whenAttributeIdIsNull() throws Exception {
                    shouldFailRequestValidationOnSave(new AttributeValueRequest(null, List.of("value")), "Attribute id is required.");
                }

                @Test
                void save_shouldReturnBadRequest_whenAttributeIdIsBlank() throws Exception {
                    shouldFailRequestValidationOnSave(new AttributeValueRequest("  ", List.of("value")), "Attribute id is required.");
                }

                @Test
                void save_shouldReturnBadRequest_whenAttributeValuesAreNull() throws Exception {
                    shouldFailRequestValidationOnSave(new AttributeValueRequest(stringAttribute.id(), null), "Attribute values are required.");
                }

                @Test
                void save_shouldReturnBadRequest_whenAttributeValueIsNull() throws Exception {
                    shouldFailRequestValidationOnSave(new AttributeValueRequest(stringAttribute.id(), Arrays.asList((String) null)), "Attribute value must not be null.");
                }

                @Test
                void save_shouldReturnBadRequest_whenAttributeIsNull() throws Exception {
                    shouldFailRequestValidationOnSave(null, "Attribute must not be null.");
                }

                private void shouldFailRequestValidationOnSave(AttributeValueRequest attributeValueRequest, String validationMessage) throws Exception {
                    CreateProductRequest request = CreateProductRequestDataBuilder.withAllFields()
                            .attributes(Arrays.asList(attributeValueRequest))
                            .build();

                    String content = performProductPersist(request)
                            .andExpect(status().isBadRequest())
                            .andReturn()
                            .getResponse()
                            .getContentAsString();

                    ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
                    assertThat(exceptionResponse.getMessage()).isEqualTo("Validation failed.");
                    assertThat(exceptionResponse.getErrors().size()).isEqualTo(1);
                    assertThat(exceptionResponse.getErrors().values().iterator().next()).isEqualTo(validationMessage);

                    verifyNoInteractions(categoryQueryPort, attributeQueryPort, productCommandPort, productEventSenderPort);
                }

                @Test
                void save_shouldReturnAllErrors_whenInvalidAttributesHaveSameTitle() throws Exception {
                    List<AttributeValue> request = List.of(
                            new AttributeValue(stringAttribute.id(), List.of(" ")),
                            new AttributeValue(numberAttribute.id(), List.of("not_a_number"))
                    );
                    shouldFailAttributesValidationOnSave(List.of(stringAttribute, numberAttribute), request, Map.of(
                            stringAttribute.slug(), "Must not be blank.",
                            numberAttribute.slug(), "Invalid number value."
                    ));
                }
            }

            @Nested
            class SaveProductWithSelectAttribute {

                @Test
                void save_shouldSaveProductWithSelectAttribute() throws Exception {
                    Attribute attribute = new Attribute("id", "Select", "slug", AttributeType.SELECT, false, List.of("RED", "GREEN", "BLUE"));
                    AttributeValue attributeValue = new AttributeValue(attribute.id(), List.of("RED", "BLUE"));
                    shouldSaveProductWithAttribute(attribute, attributeValue);
                }

                @Test
                void save_shouldReturnBadRequest_whenSelectAttributeValuesAreDuplicated() throws Exception {
                    Attribute attribute = new Attribute("id", "Select", "slug", AttributeType.SELECT, false, List.of("RED", "GREEN", "BLUE"));
                    AttributeValue attributeValue = new AttributeValue(attribute.id(), List.of("RED", "RED"));
                    shouldFailValidationOnSaveProductWithAttribute(attribute, attributeValue, "Values must be unique.");
                }

                @Test
                void save_shouldReturnBadRequest_whenSelectAttributeValueIsNotAllowed() throws Exception {
                    Attribute attribute = new Attribute("id", "Select", "slug", AttributeType.SELECT, false, List.of("RED", "GREEN", "BLUE"));
                    AttributeValue attributeValue = new AttributeValue(attribute.id(), List.of("RED", "BLACK", "WHITE"));
                    shouldFailValidationOnSaveProductWithAttribute(attribute, attributeValue, "Invalid values: BLACK, WHITE. Allowed values: RED, GREEN, BLUE");
                }

                @Test
                void save_shouldReturnBadRequest_whenSelectAttributeHasNoAllowedValues() throws Exception {
                    Attribute attribute = new Attribute("id", "Select", "slug", AttributeType.SELECT, false, null);
                    AttributeValue attributeValue = new AttributeValue(attribute.id(), List.of("RED"));
                    shouldFailValidationOnSaveProductWithAttribute(attribute, attributeValue, "Attribute has no allowed values.");
                }
            }

            @Nested
            class SaveProductWithRangeAttribute {

                @Test
                void save_shouldSaveProductWithRangeAttribute() throws Exception {
                    Attribute attribute = new Attribute("id", "Range", "slug", AttributeType.RANGE, false, List.of("0", "100"));
                    AttributeValue attributeValue = new AttributeValue(attribute.id(), List.of("50", "70"));
                    shouldSaveProductWithAttribute(attribute, attributeValue);
                }

                @Test
                void save_shouldReturnBadRequest_whenAttributesAreMoreThanTwo() throws Exception {
                    Attribute attribute = new Attribute("id", "Range", "slug", AttributeType.RANGE, false, List.of("0", "100"));
                    AttributeValue attributeValue = new AttributeValue(attribute.id(), List.of("50", "70", "90"));
                    shouldFailValidationOnSaveProductWithAttribute(attribute, attributeValue, "Must contain exactly two values.");
                }

                @Test
                void save_shouldReturnBadRequest_whenFirstAttributeValueIsLowerThanZero() throws Exception {
                    Attribute attribute = new Attribute("id", "Range", "slug", AttributeType.RANGE, false, List.of("0", "100"));
                    AttributeValue attributeValue = new AttributeValue(attribute.id(), List.of("-1", "100"));
                    shouldFailValidationOnSaveProductWithAttribute(attribute, attributeValue, "The first value must be equal or greater than to zero.");
                }

                @Test
                void save_shouldReturnBadRequest_whenFirstAttributeValueIsGreaterThanSecond() throws Exception {
                    Attribute attribute = new Attribute("id", "Range", "slug", AttributeType.RANGE, false, List.of("0", "100"));
                    AttributeValue attributeValue = new AttributeValue(attribute.id(), List.of("50", "10"));
                    shouldFailValidationOnSaveProductWithAttribute(attribute, attributeValue, "The first value must be less than the second value.");
                }

                @Test
                void save_shouldReturnBadRequest_whenAttributesAreNotNumericType() throws Exception {
                    Attribute attribute = new Attribute("id", "Range", "slug", AttributeType.RANGE, false, List.of("0", "100"));
                    AttributeValue attributeValue = new AttributeValue(attribute.id(), List.of("not_numeric", "not_numeric"));
                    shouldFailValidationOnSaveProductWithAttribute(attribute, attributeValue, "Invalid range value format.");
                }
            }

            @Nested
            class SaveProductWithStringAttribute {

                @Test
                void save_shouldSaveProductWithStringAttribute() throws Exception {
                    Attribute attribute = new Attribute("id", "String", "slug", AttributeType.STRING, false, null);
                    AttributeValue attributeValue = new AttributeValue(attribute.id(), List.of("string"));
                    shouldSaveProductWithAttribute(attribute, attributeValue);
                }

                @Test
                void save_shouldReturnBadRequest_whenAttributeIsBlank() throws Exception {
                    Attribute attribute = new Attribute("id", "String", "slug", AttributeType.STRING, false, null);
                    AttributeValue attributeValue = new AttributeValue(attribute.id(), List.of(""));
                    shouldFailValidationOnSaveProductWithAttribute(attribute, attributeValue, "Must not be blank.");
                }
            }
        }
    }
}
