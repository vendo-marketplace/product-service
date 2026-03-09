package com.vendo.product_service.adapter.category.in;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vendo.core_lib.exception.ExceptionResponse;
import com.vendo.product_service.adapter.category.in.dto.CategoryResponse;
import com.vendo.product_service.adapter.category.in.dto.CreateCategoryRequest;
import com.vendo.product_service.domain.category.exception.CategoryAlreadyExistsException;
import com.vendo.product_service.domain.category.exception.CategoryNotFoundException;
import com.vendo.product_service.domain.category.model.AttributeDefinition;
import com.vendo.product_service.domain.category.model.AttributeType;
import com.vendo.product_service.domain.category.model.Category;
import com.vendo.product_service.port.category.CategoryCommandPort;
import com.vendo.product_service.port.category.CategoryQueryPort;
import com.vendo.product_service.test_utils.builder.CategoryDataBuilder;
import com.vendo.product_service.test_utils.builder.CreateCategoryRequestDataBuilder;
import com.vendo.product_service.test_utils.security.SecurityContextService;
import com.vendo.user_lib.type.UserRole;
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

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CategoryControllerIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CategoryCommandPort commandPort;

    @MockitoBean
    private CategoryQueryPort queryPort;

    private ResultActions performCategoryGet(String categoryId) throws Exception {
        return mockMvc.perform(get("/categories/{id}", categoryId)
                .with(authentication(SecurityContextService.initializeAuth(UserRole.ADMIN))));
    }

    private ResultActions performCategoryPersist(CreateCategoryRequest categoryRequest) throws Exception {
        return performCategoryPersist(categoryRequest, UserRole.ADMIN);
    }

    private ResultActions performCategoryPersist(CreateCategoryRequest categoryRequest, UserRole role) throws Exception {
        return mockMvc.perform(post("/categories")
                .with(authentication(SecurityContextService.initializeAuth(role)))
                .content(objectMapper.writeValueAsString(categoryRequest))
                .contentType(MediaType.APPLICATION_JSON));
    }

    @Nested
    class SaveCategoryEntityTests {

        @Test
        void save_shouldReturnBadRequest_whenTitleIsNotPresent() throws Exception {
            CreateCategoryRequest categoryRequest = CreateCategoryRequestDataBuilder.withAllFields()
                    .title(null)
                    .build();

            String content = performCategoryPersist(categoryRequest)
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
            assertThat(exceptionResponse.getErrors().get("title")).isEqualTo("Title is required.");
            assertThat(exceptionResponse.getPath()).isEqualTo("/categories");

            verifyNoInteractions(queryPort);
        }

        @Test
        void save_shouldReturnBadRequest_whenTitleIsBlank() throws Exception {
            CreateCategoryRequest categoryRequest = CreateCategoryRequestDataBuilder.withAllFields()
                    .title("")
                    .build();

            String content = performCategoryPersist(categoryRequest)
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
            assertThat(exceptionResponse.getErrors().get("title")).isEqualTo("Title is required.");
            assertThat(exceptionResponse.getPath()).isEqualTo("/categories");

            verifyNoInteractions(queryPort);
        }

        @Test
        void save_shouldReturnBadRequest_whenCodeIsNotPresent() throws Exception {
            CreateCategoryRequest categoryRequest = CreateCategoryRequestDataBuilder.withAllFields()
                    .code(null)
                    .build();

            String content = performCategoryPersist(categoryRequest)
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
            assertThat(exceptionResponse.getErrors().get("code")).isEqualTo("Code is required.");
            assertThat(exceptionResponse.getPath()).isEqualTo("/categories");

            verifyNoInteractions(queryPort);
        }

        @Test
        void save_shouldReturnBadRequest_whenCodeIsBlank() throws Exception {
            CreateCategoryRequest categoryRequest = CreateCategoryRequestDataBuilder.withAllFields()
                    .code("")
                    .build();

            String content = performCategoryPersist(categoryRequest)
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
            assertThat(exceptionResponse.getErrors().get("code")).isEqualTo("Code is required.");
            assertThat(exceptionResponse.getPath()).isEqualTo("/categories");

            verifyNoInteractions(queryPort);
        }

        @Test
        void save_shouldReturnConflict_whenCategoryIsAlreadyExistsByCode() throws Exception {
            CreateCategoryRequest categoryRequest = CreateCategoryRequestDataBuilder.withAllFields().parentId(null).attributes(null).build();
            Category category = Category.builder()
                    .code(categoryRequest.code())
                    .build();

            when(queryPort.existsByCode(category.getCode())).thenThrow(new CategoryAlreadyExistsException("Category already exists by code."));

            String content = performCategoryPersist(categoryRequest)
                    .andExpect(status().isConflict())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
            assertThat(exceptionResponse).isNotNull();
            assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.CONFLICT.value());
            assertThat(exceptionResponse.getMessage()).isEqualTo("Category already exists by code.");
            assertThat(exceptionResponse.getPath()).isEqualTo("/categories");

            verify(queryPort).existsByCode(category.getCode());
            verifyNoInteractions(commandPort);
        }

        @Test
        void save_shouldReturnForbidden_whenAuthenticatedUserIsNotAdmin() throws Exception {
            CreateCategoryRequest categoryRequest = CreateCategoryRequestDataBuilder.withAllFields()
                    .parentId(null)
                    .attributes(null)
                    .build();

            String content = performCategoryPersist(categoryRequest, UserRole.USER)
                    .andExpect(status().isForbidden())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
            assertThat(exceptionResponse).isNotNull();
            assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
            assertThat(exceptionResponse.getMessage()).isEqualTo("You do not have permission to access this resource.");
            assertThat(exceptionResponse.getPath()).isEqualTo("/categories");

            verifyNoInteractions(queryPort);
        }

        @Test
        void save_shouldReturnBadRequest_whenAttributesPresentAndNoParent() throws Exception {
            CreateCategoryRequest categoryRequest = CreateCategoryRequestDataBuilder.withAllFields()
                    .parentId(null)
                    .build();

            when(queryPort.existsByCode(categoryRequest.code())).thenReturn(false);

            String content = performCategoryPersist(categoryRequest)
                    .andExpect(status().isBadRequest())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
            assertThat(exceptionResponse).isNotNull();
            assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            assertThat(exceptionResponse.getMessage()).isEqualTo("Invalid category structure.");
            assertThat(exceptionResponse.getPath()).isEqualTo("/categories");

            verify(queryPort).existsByCode(categoryRequest.code());
            verifyNoInteractions(commandPort);
        }
    }

    @Nested
    class FindCategoriesTests {

        @Test
        void findById_shouldReturnCategory() throws Exception {
            Category category = CategoryDataBuilder.withAllFields().build();
            String attributeName = "Attribute";

            when(queryPort.findById(category.getId(), "Category not found.")).thenReturn(category);

            String content = performCategoryGet(category.getId())
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            CategoryResponse categoryResponse = objectMapper.readValue(content, CategoryResponse.class);

            assertThat(categoryResponse).isNotNull();
            assertThat(categoryResponse.title()).isEqualTo(category.getTitle());
            assertThat(categoryResponse.parentId()).isEqualTo(category.getParentId());
            assertThat(categoryResponse.attributes()).isNotNull();
            assertThat(categoryResponse.attributes().size()).isEqualTo(category.getAttributes().size());
            assertThat(categoryResponse.attributes().get(attributeName)).isNotNull();

            AttributeDefinition responseAttributeName = categoryResponse.attributes().get(attributeName);
            AttributeDefinition categoryAttributeName = category.getAttributes().get(attributeName);
            assertThat(responseAttributeName.type()).isEqualTo(categoryAttributeName.type());
            assertThat(responseAttributeName.required()).isEqualTo(categoryAttributeName.required());
            assertThat(responseAttributeName.allowedValues()).isNotNull();
            assertThat(responseAttributeName.allowedValues().size()).isEqualTo(categoryAttributeName.allowedValues().size());

            verify(queryPort).findById(category.getId(), "Category not found.");
        }

        @Test
        void findById_shouldReturnNotFound_whenCategoryNotFound() throws Exception {
            String categoryId = String.valueOf(UUID.randomUUID());

            when(queryPort.findById(categoryId, "Category not found.")).thenThrow(new CategoryNotFoundException("Category not found."));

            String content = performCategoryGet(categoryId).andExpect(status().isNotFound())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
            assertThat(exceptionResponse).isNotNull();
            assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
            assertThat(exceptionResponse.getMessage()).isEqualTo("Category not found.");
            assertThat(exceptionResponse.getPath()).isEqualTo("/categories/%s".formatted(categoryId));

            verify(queryPort).findById(categoryId, "Category not found.");
        }

        @Nested
        class SaveParentCategoryEntityTests {

            @Test
            void save_shouldSaveParentCategory_whenNoParentIdAndNoAttributes() throws Exception {
                CreateCategoryRequest categoryRequest = CreateCategoryRequestDataBuilder.withAllFields()
                        .parentId(null)
                        .attributes(null)
                        .build();

                ArgumentCaptor<Category> argumentCaptor = ArgumentCaptor.forClass(Category.class);

                when(queryPort.existsByCode(categoryRequest.code())).thenReturn(false);
                doNothing().when(commandPort).save(argumentCaptor.capture());

                performCategoryPersist(categoryRequest).andExpect(status().isOk());

                Category category = argumentCaptor.getValue();
                assertThat(category).isNotNull();
                assertThat(category.getCode()).isEqualTo(categoryRequest.code());
                assertThat(category.getParentId()).isNull();
            }
        }

        @Nested
        class SaveSubCategoryEntityTests {

            @Test
            void save_shouldSaveCategory_whenParentIdAndNoAttributes() throws Exception {
                CreateCategoryRequest categoryRequest = CreateCategoryRequestDataBuilder.withAllFields()
                        .attributes(null)
                        .build();
                Category category = CategoryDataBuilder.withAllFields()
                        .attributes(null)
                        .build();

                ArgumentCaptor<Category> argumentCaptor = ArgumentCaptor.forClass(Category.class);

                when(queryPort.existsByCode(categoryRequest.code())).thenReturn(false);
                when(queryPort.findById(categoryRequest.parentId(), "Parent category not found.")).thenReturn(category);
                doNothing().when(commandPort).save(argumentCaptor.capture());

                performCategoryPersist(categoryRequest).andExpect(status().isOk());

                Category capturedCategory = argumentCaptor.getValue();
                assertThat(capturedCategory).isNotNull();
                assertThat(capturedCategory.getCode()).isEqualTo(categoryRequest.code());
                assertThat(capturedCategory.getParentId()).isEqualTo(categoryRequest.parentId());

                verify(queryPort).existsByCode(categoryRequest.code());
                verify(queryPort).findById(categoryRequest.parentId(), "Parent category not found.");
                verify(commandPort).save(capturedCategory);
            }

            @Test
            void save_shouldSaveCategory_whenParentIsSub() throws Exception {
                Category parentSub = CategoryDataBuilder.withAllFields().attributes(null).build();
                CreateCategoryRequest categoryRequest = CreateCategoryRequestDataBuilder.withAllFields()
                        .parentId(parentSub.getId())
                        .attributes(null)
                        .build();

                ArgumentCaptor<Category> argumentCaptor = ArgumentCaptor.forClass(Category.class);

                when(queryPort.existsByCode(categoryRequest.code())).thenReturn(false);
                when(queryPort.findById(categoryRequest.parentId(), "Parent category not found.")).thenReturn(parentSub);
                doNothing().when(commandPort).save(argumentCaptor.capture());

                performCategoryPersist(categoryRequest).andExpect(status().isOk());

                Category capturedCategory = argumentCaptor.getValue();
                assertThat(capturedCategory).isNotNull();
                assertThat(capturedCategory.getCode()).isEqualTo(categoryRequest.code());
                assertThat(capturedCategory.getParentId()).isEqualTo(categoryRequest.parentId());

                verify(queryPort).existsByCode(categoryRequest.code());
                verify(queryPort).findById(categoryRequest.parentId(), "Parent category not found.");
                verify(commandPort).save(capturedCategory);
            }

            @Test
            void save_shouldReturnNotFound_whenParentNotFoundInSub() throws Exception {
                CreateCategoryRequest categoryRequest = CreateCategoryRequestDataBuilder.withAllFields()
                        .attributes(null)
                        .build();

                when(queryPort.existsByCode(categoryRequest.code())).thenReturn(false);
                when(queryPort.findById(categoryRequest.parentId(), "Parent category not found.")).thenThrow(new CategoryNotFoundException("Parent category not found."));

                String content = performCategoryPersist(categoryRequest)
                        .andExpect(status().isNotFound())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

                ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
                assertThat(exceptionResponse).isNotNull();
                assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
                assertThat(exceptionResponse.getMessage()).isEqualTo("Parent category not found.");
                assertThat(exceptionResponse.getPath()).isEqualTo("/categories");

                verify(queryPort).existsByCode(categoryRequest.code());
                verify(queryPort).findById(categoryRequest.parentId(), "Parent category not found.");
                verifyNoInteractions(commandPort);
            }

            @Test
            void save_shouldReturnBadRequest_whenSubCategoryHasChildParent() throws Exception {
                Category childCategory = CategoryDataBuilder.withAllFields().build();

                CreateCategoryRequest categoryRequest = CreateCategoryRequestDataBuilder.withAllFields()
                        .parentId(childCategory.getId())
                        .attributes(null)
                        .build();

                when(queryPort.existsByCode(categoryRequest.code())).thenReturn(false);
                when(queryPort.findById(categoryRequest.parentId(), "Parent category not found.")).thenReturn(childCategory);

                String content = performCategoryPersist(categoryRequest)
                        .andExpect(status().isBadRequest())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

                ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
                assertThat(exceptionResponse).isNotNull();
                assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                assertThat(exceptionResponse.getMessage()).isEqualTo("A subcategory cannot have a child category as its parent.");
                assertThat(exceptionResponse.getPath()).isEqualTo("/categories");

                verify(queryPort).existsByCode(categoryRequest.code());
                verify(queryPort).findById(categoryRequest.parentId(), "Parent category not found.");
                verifyNoInteractions(commandPort);
            }
        }

        @Nested
        class SaveChildCategoryEntityTests {

            @Test
            void save_shouldSaveCategory_whenHasParentCategoryAndAttributes() throws Exception {
                Category parentCategory = CategoryDataBuilder.withAllFields().attributes(null).build();
                CreateCategoryRequest categoryRequest = CreateCategoryRequestDataBuilder.withAllFields()
                        .parentId(parentCategory.getId())
                        .build();

                ArgumentCaptor<Category> argumentCaptor = ArgumentCaptor.forClass(Category.class);

                when(queryPort.existsByCode(categoryRequest.code())).thenReturn(false);
                when(queryPort.findById(categoryRequest.parentId(), "Parent category not found.")).thenReturn(parentCategory);
                doNothing().when(commandPort).save(argumentCaptor.capture());

                performCategoryPersist(categoryRequest).andExpect(status().isOk());

                Category capturedCategory = argumentCaptor.getValue();
                assertThat(capturedCategory).isNotNull();
                assertThat(capturedCategory.getCode()).isEqualTo(categoryRequest.code());
                assertThat(capturedCategory.getParentId()).isEqualTo(categoryRequest.parentId());

                verify(queryPort).existsByCode(categoryRequest.code());
                verify(queryPort).findById(categoryRequest.parentId(), "Parent category not found.");
                verify(commandPort).save(capturedCategory);
            }

            @Test
            void save_shouldReturnBadRequest_whenAttributeNameIsInvalid() throws Exception {
                String invalidAttributeName = "invalid_attribute_name";
                AttributeDefinition attributeDefinition = AttributeDefinition.builder().type(AttributeType.STRING).build();

                Category parentCategory = CategoryDataBuilder.withAllFields().attributes(null).build();
                CreateCategoryRequest categoryRequest = CreateCategoryRequestDataBuilder.withAllFields()
                        .parentId(parentCategory.getId())
                        .attributes(Map.of(invalidAttributeName, attributeDefinition))
                        .build();

                String content = performCategoryPersist(categoryRequest)
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
                assertThat(exceptionResponse.getErrors().containsKey(invalidAttributeName)).isTrue();
                assertThat(exceptionResponse.getErrors().get(invalidAttributeName)).isEqualTo("Attribute name validation failed.");
                assertThat(exceptionResponse.getPath()).isEqualTo("/categories");

                verifyNoInteractions(queryPort);
                verifyNoInteractions(commandPort);
            }

            @Test
            void save_shouldReturnBadRequest_whenParentCategoryIsChild() throws Exception {
                Category subCategory = CategoryDataBuilder.withAllFields().build();
                CreateCategoryRequest categoryRequest = CreateCategoryRequestDataBuilder.withAllFields()
                        .parentId(subCategory.getId())
                        .build();

                when(queryPort.existsByCode(categoryRequest.code())).thenReturn(false);
                when(queryPort.findById(categoryRequest.parentId(), "Parent category not found.")).thenReturn(subCategory);

                String content = performCategoryPersist(categoryRequest)
                        .andExpect(status().isBadRequest())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

                ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
                assertThat(exceptionResponse).isNotNull();
                assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                assertThat(exceptionResponse.getMessage()).isEqualTo("A child category cannot have another child category as its parent.");
                assertThat(exceptionResponse.getPath()).isEqualTo("/categories");

                verify(queryPort).existsByCode(categoryRequest.code());
                verify(queryPort).findById(categoryRequest.parentId(), "Parent category not found.");
                verifyNoInteractions(commandPort);
            }

            @Test
            void save_shouldReturnNotFound_whenParentCategoryNotFound() throws Exception {
                CreateCategoryRequest categoryRequest = CreateCategoryRequestDataBuilder.withAllFields().build();

                when(queryPort.existsByCode(categoryRequest.code())).thenReturn(false);
                when(queryPort.findById(categoryRequest.parentId(), "Parent category not found.")).thenThrow(new CategoryNotFoundException("Parent category not found."));

                String content = performCategoryPersist(categoryRequest)
                        .andExpect(status().isNotFound())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

                ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
                assertThat(exceptionResponse).isNotNull();
                assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
                assertThat(exceptionResponse.getMessage()).isEqualTo("Parent category not found.");
                assertThat(exceptionResponse.getPath()).isEqualTo("/categories");

                verify(queryPort).existsByCode(categoryRequest.code());
                verify(queryPort).findById(categoryRequest.parentId(), "Parent category not found.");
                verifyNoInteractions(commandPort);
            }
        }
    }
}
