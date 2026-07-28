package com.vendo.product_service.adapter.category.in;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vendo.core_lib.utils.AssertionUtils;
import com.vendo.product_service.adapter.category.in.dto.CategoryResponse;
import com.vendo.product_service.adapter.category.in.dto.CategoryTreeResponse;
import com.vendo.product_service.adapter.category.in.dto.CreateCategoryRequest;
import com.vendo.product_service.domain.attribute.exception.AttributeNotFoundException;
import com.vendo.product_service.domain.attribute.model.Attribute;
import com.vendo.product_service.domain.attribute.model.AttributeType;
import com.vendo.product_service.domain.category.exception.CategoryAlreadyExistsException;
import com.vendo.product_service.domain.category.exception.CategoryNotFoundException;
import com.vendo.product_service.domain.category.model.Category;
import com.vendo.product_service.domain.category.type.CategoryType;
import com.vendo.product_service.domain.product.pattern.ProductPatterns;
import com.vendo.product_service.domain.user.User;
import com.vendo.product_service.port.attribute.AttributeQueryPort;
import com.vendo.product_service.port.category.CategoryCommandPort;
import com.vendo.product_service.port.category.CategoryQueryPort;
import com.vendo.product_service.test_utils.builder.AttributeDataBuilder;
import com.vendo.product_service.test_utils.builder.CategoryDataBuilder;
import com.vendo.product_service.test_utils.builder.CategoryResponseDataBuilder;
import com.vendo.product_service.test_utils.builder.CreateCategoryRequestDataBuilder;
import com.vendo.product_service.test_utils.security.SecurityContextService;
import com.vendo.security_lib.exception.ExceptionResponse;
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
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import static com.vendo.product_service.domain.category.model.Category.CATEGORY_TYPE_VALIDATION_MESSAGE;
import static org.assertj.core.api.Assertions.assertThat;
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
    private CategoryQueryPort categoryQueryPort;
    @MockitoBean
    private CategoryCommandPort categoryCommandPort;
    @MockitoBean
    private AttributeQueryPort attributeQueryPort;

    @BeforeEach
    public void setUp() {
        SecurityContextHolder.clearContext();
    }

    private ResultActions performCategoryGet(String categoryId) throws Exception {
        User user = new User("id", "email", UserStatus.ACTIVE, Set.of(UserRole.ADMIN), true);
        return mockMvc.perform(get("/categories/{id}", categoryId)
                .with(authentication(SecurityContextService.initializeAuth(user))));
    }

    private ResultActions performCategoryGetTree() throws Exception {
        return mockMvc.perform(get("/categories/tree"));
    }

    private ResultActions performCategoryPersist(CreateCategoryRequest categoryRequest) throws Exception {
        return performCategoryPersist(categoryRequest, UserRole.ADMIN);
    }

    private ResultActions performCategoryPersist(CreateCategoryRequest categoryRequest, UserRole role) throws Exception {
        User user = new User("id", "email", UserStatus.ACTIVE, Set.of(role), true);
        return mockMvc.perform(post("/categories")
                .with(authentication(SecurityContextService.initializeAuth(user)))
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

            verifyNoInteractions(categoryQueryPort);
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
            assertThat(exceptionResponse.getErrors().get("title")).isEqualTo(ProductPatterns.CATEGORY_TITLE_VALIDATION_MESSAGE);
            assertThat(exceptionResponse.getPath()).isEqualTo("/categories");

            verifyNoInteractions(categoryQueryPort);
        }

        @Test
        void save_shouldReturnBadRequest_whenSlugIsNotPresent() throws Exception {
            CreateCategoryRequest categoryRequest = CreateCategoryRequestDataBuilder.withAllFields()
                    .slug(null)
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
            assertThat(exceptionResponse.getErrors().get("slug")).isEqualTo("Slug is required.");
            assertThat(exceptionResponse.getPath()).isEqualTo("/categories");

            verifyNoInteractions(categoryQueryPort);
        }

        @Test
        void save_shouldReturnBadRequest_whenSlugIsBlank() throws Exception {
            CreateCategoryRequest categoryRequest = CreateCategoryRequestDataBuilder.withAllFields()
                    .slug("")
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
            assertThat(exceptionResponse.getErrors().get("slug")).isEqualTo(ProductPatterns.SLUG_VALIDATION_MESSAGE);
            assertThat(exceptionResponse.getPath()).isEqualTo("/categories");

            verifyNoInteractions(categoryQueryPort);
        }

        @Test
        void save_shouldReturnConflict_whenCategoryIsAlreadyExistsBySlug() throws Exception {
            CreateCategoryRequest categoryRequest = CreateCategoryRequestDataBuilder.withAllFields().parentId(null).attributes(null).build();
            ArgumentCaptor<Category> argumentCaptor = ArgumentCaptor.forClass(Category.class);

            doThrow(new CategoryAlreadyExistsException("Category already exists by slug.")).when(categoryCommandPort).save(argumentCaptor.capture());

            String content = performCategoryPersist(categoryRequest)
                    .andExpect(status().isConflict())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
            assertThat(exceptionResponse).isNotNull();
            assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.CONFLICT.value());
            assertThat(exceptionResponse.getMessage()).isEqualTo("Category already exists by slug.");
            assertThat(exceptionResponse.getPath()).isEqualTo("/categories");

            Category category = argumentCaptor.getValue();
            assertThat(category).isNotNull();
            assertThat(category.getTitle()).isEqualTo(categoryRequest.title());
            assertThat(category.getSlug()).isEqualTo(categoryRequest.slug());
            assertThat(category.getAttributes()).isNull();

            verify(categoryCommandPort).save(category);
        }

        @Test
        void save_shouldReturnForbidden_whenAuthenticatedUserIsNotAdmin() throws Exception {
            CreateCategoryRequest request = CreateCategoryRequestDataBuilder.withAllFields()
                    .parentId(null)
                    .attributes(null)
                    .build();

            String content = performCategoryPersist(request, UserRole.USER)
                    .andExpect(status().isForbidden())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
            assertThat(exceptionResponse).isNotNull();
            assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
            assertThat(exceptionResponse.getMessage()).isEqualTo("Resource is unreachable.");
            assertThat(exceptionResponse.getPath()).isEqualTo("/categories");

            verifyNoInteractions(categoryQueryPort);
        }

        @Test
        void save_shouldReturnBadRequest_whenAttributesPresentAndNoParent() throws Exception {
            CreateCategoryRequest request = CreateCategoryRequestDataBuilder.withAllFields()
                    .parentId(null)
                    .build();

            String content = performCategoryPersist(request)
                    .andExpect(status().isBadRequest())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
            assertThat(exceptionResponse).isNotNull();
            assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            assertThat(exceptionResponse.getMessage()).isEqualTo(CATEGORY_TYPE_VALIDATION_MESSAGE);
            assertThat(exceptionResponse.getPath()).isEqualTo("/categories");

            verifyNoInteractions(commandPort, categoryQueryPort);
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

                doNothing().when(commandPort).save(argumentCaptor.capture());

                performCategoryPersist(categoryRequest).andExpect(status().isOk());

                Category capturedCategory = argumentCaptor.getValue();

                assertThat(capturedCategory).isNotNull();
                assertThat(capturedCategory.getSlug()).isEqualTo(categoryRequest.slug());
                assertThat(capturedCategory.getParentId()).isNull();
                assertThat(capturedCategory.getPath()).isNotNull();
                assertThat(capturedCategory.getPath().size()).isEqualTo(1);
                assertThat(capturedCategory.getPath().get(0)).isEqualTo(capturedCategory.getId());
                assertThat(capturedCategory.getAttributes()).isNull();
            }
        }

        @Nested
        class SaveSubCategoryEntityTests {

            @Test
            void save_shouldSaveCategory_whenParentIdAndNoAttributes() throws Exception {
                String parentId = String.valueOf(UUID.randomUUID());

                CreateCategoryRequest categoryRequest = CreateCategoryRequestDataBuilder.withAllFields()
                        .parentId(parentId)
                        .attributes(null)
                        .build();
                Category parent = CategoryDataBuilder.withAllFields()
                        .id(parentId)
                        .attributes(null)
                        .parentId(null)
                        .path(List.of(parentId))
                        .build();
                Category sub = CategoryDataBuilder.withAllFields()
                        .parentId(parentId)
                        .attributes(null)
                        .slug(categoryRequest.slug())
                        .build();

                ArgumentCaptor<Category> argumentCaptor = ArgumentCaptor.forClass(Category.class);

                when(categoryQueryPort.findById(sub.getParentId(), "Parent category not found.")).thenReturn(parent);
                doNothing().when(commandPort).save(argumentCaptor.capture());

                performCategoryPersist(categoryRequest).andExpect(status().isOk());

                Category capturedCategory = argumentCaptor.getValue();
                assertThat(capturedCategory).isNotNull();
                assertThat(capturedCategory.getSlug()).isEqualTo(categoryRequest.slug());
                assertThat(capturedCategory.getParentId()).isEqualTo(categoryRequest.parentId());
                assertThat(capturedCategory.getPath()).isNotNull();
                assertThat(capturedCategory.getPath().size()).isEqualTo(2);
                assertThat(capturedCategory.getPath()).containsExactly(parentId, capturedCategory.getId());
                assertThat(capturedCategory.getAttributes()).isNull();

                verify(categoryQueryPort, times(2)).findById(categoryRequest.parentId(), "Parent category not found.");
                verify(commandPort).save(capturedCategory);
            }

            @Test
            void save_shouldSaveCategory_whenParentIsSub() throws Exception {
                String parentId = String.valueOf(UUID.randomUUID());

                Category parent = CategoryDataBuilder.withAllFields().id(parentId).attributes(null).path(List.of(parentId)).build();
                Category sub = CategoryDataBuilder.withAllFields().parentId(parent.getId()).attributes(null).build();
                CreateCategoryRequest categoryRequest = CreateCategoryRequestDataBuilder.withAllFields()
                        .parentId(parent.getId())
                        .slug(sub.getSlug())
                        .attributes(null)
                        .build();

                ArgumentCaptor<Category> argumentCaptor = ArgumentCaptor.forClass(Category.class);

                when(categoryQueryPort.findById(categoryRequest.parentId(), "Parent category not found.")).thenReturn(parent);
                doNothing().when(commandPort).save(argumentCaptor.capture());

                performCategoryPersist(categoryRequest).andExpect(status().isOk());

                Category capturedCategory = argumentCaptor.getValue();
                assertThat(capturedCategory).isNotNull();
                assertThat(capturedCategory.getSlug()).isEqualTo(categoryRequest.slug());
                assertThat(capturedCategory.getParentId()).isEqualTo(categoryRequest.parentId());
                assertThat(capturedCategory.getPath()).isNotNull();
                assertThat(capturedCategory.getPath().size()).isEqualTo(2);
                assertThat(capturedCategory.getPath()).containsExactly(parent.getId(), capturedCategory.getId());
                assertThat(capturedCategory.getAttributes()).isNull();

                verify(categoryQueryPort, times(2)).findById(categoryRequest.parentId(), "Parent category not found.");
                verify(commandPort).save(capturedCategory);
            }

            @Test
            void save_shouldReturnNotFound_whenParentNotFoundInSub() throws Exception {
                CreateCategoryRequest categoryRequest = CreateCategoryRequestDataBuilder.withAllFields()
                        .attributes(null)
                        .build();
                Category category = CategoryDataBuilder.withAllFields().attributes(null).build();

                when(categoryQueryPort.findById(categoryRequest.parentId(), "Parent category not found.")).thenThrow(new CategoryNotFoundException("Parent category not found."));

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

                verify(categoryQueryPort).findById(categoryRequest.parentId(), "Parent category not found.");
                verifyNoInteractions(commandPort);
            }

            @Test
            void save_shouldReturnBadRequest_whenSubCategoryHasChildParent() throws Exception {
                Category childCategory = CategoryDataBuilder.withAllFields().build();
                Category subCategory = CategoryDataBuilder.withAllFields().parentId(childCategory.getId()).attributes(null).build();
                CreateCategoryRequest categoryRequest = CreateCategoryRequestDataBuilder.withAllFields()
                        .parentId(childCategory.getId())
                        .attributes(null)
                        .build();

                when(categoryQueryPort.findById(categoryRequest.parentId(), "Parent category not found.")).thenReturn(childCategory);

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

                verify(categoryQueryPort).findById(categoryRequest.parentId(), "Parent category not found.");
                verifyNoInteractions(commandPort);
            }
        }

        @Nested
        class SaveChildCategoryEntityTests {

            @Test
            void save_shouldSaveCategory_whenHasParentCategoryAndAttributes() throws Exception {
                String parentId = String.valueOf(UUID.randomUUID()), subId = String.valueOf(UUID.randomUUID());
                List<String> parentPath = List.of(parentId);

                List<String> subPath = Stream.concat(parentPath.stream(), Stream.of(subId)).toList();
                Category sub = CategoryDataBuilder.withAllFields().id(subId).attributes(null).parentId(parentId).path(subPath).build();

                Attribute attribute = new Attribute("id", "title", "slug", AttributeType.STRING, false, null);
                CreateCategoryRequest request = CreateCategoryRequestDataBuilder.withAllFields()
                        .parentId(sub.getId())
                        .attributes(List.of(attribute.id()))
                        .build();

                ArgumentCaptor<Category> argumentCaptor = ArgumentCaptor.forClass(Category.class);

                when(categoryQueryPort.findById(request.parentId(), "Parent category not found.")).thenReturn(sub);
                when(attributeQueryPort.findAllByIds(request.attributes())).thenReturn(List.of(attribute));
                doNothing().when(commandPort).save(argumentCaptor.capture());

                performCategoryPersist(request).andExpect(status().isOk());

                Category capturedCategory = argumentCaptor.getValue();
                assertThat(capturedCategory).isNotNull();
                assertThat(capturedCategory.getSlug()).isEqualTo(request.slug());
                assertThat(capturedCategory.getParentId()).isEqualTo(request.parentId());
                assertThat(capturedCategory.getPath()).isNotNull();
                assertThat(capturedCategory.getPath().size()).isEqualTo(3);
                assertThat(capturedCategory.getPath()).containsExactly(parentId, subId, capturedCategory.getId());
                assertThat(capturedCategory.getAttributes()).isNotNull();
                assertThat(capturedCategory.getAttributes().size()).isEqualTo(1);
                assertThat(capturedCategory.getAttributes().get(0)).isEqualTo(attribute.id());

                verify(categoryQueryPort, times(2)).findById(request.parentId(), "Parent category not found.");
                verify(attributeQueryPort).findAllByIds(request.attributes());
                verify(commandPort).save(capturedCategory);
            }

            @Test
            void save_shouldReturnBadRequest_whenParentCategoryIsChild() throws Exception {
                Category subCategory = CategoryDataBuilder.withAllFields().build();
                CreateCategoryRequest request = CreateCategoryRequestDataBuilder.withAllFields()
                        .parentId(subCategory.getId())
                        .build();

                when(categoryQueryPort.findById(request.parentId(), "Parent category not found.")).thenReturn(subCategory);

                String content = performCategoryPersist(request)
                        .andExpect(status().isBadRequest())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

                ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
                assertThat(exceptionResponse).isNotNull();
                assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                assertThat(exceptionResponse.getMessage()).isEqualTo("A child category cannot have another child category as its parent.");
                assertThat(exceptionResponse.getPath()).isEqualTo("/categories");

                verify(categoryQueryPort).findById(request.parentId(), "Parent category not found.");
                verifyNoInteractions(commandPort, attributeQueryPort);
            }

            @Test
            void save_shouldReturnNotFound_whenParentCategoryNotFound() throws Exception {
                CreateCategoryRequest categoryRequest = CreateCategoryRequestDataBuilder.withAllFields().build();

                when(categoryQueryPort.findById(categoryRequest.parentId(), "Parent category not found.")).thenThrow(new CategoryNotFoundException("Parent category not found."));

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

                verify(categoryQueryPort).findById(categoryRequest.parentId(), "Parent category not found.");
                verifyNoInteractions(commandPort);
            }

            @Test
            void save_shouldReturnNotFound_whenAttributeNotFound() throws Exception {
                Category parentCategory = CategoryDataBuilder.withAllFields().attributes(null).build();
                CreateCategoryRequest categoryRequest = CreateCategoryRequestDataBuilder.withAllFields().parentId(parentCategory.getId()).build();
                Category childCategory = CategoryDataBuilder.withAllFields().parentId(parentCategory.getId()).build();

                when(categoryQueryPort.findById(categoryRequest.parentId(), "Parent category not found.")).thenReturn(parentCategory);
                when(attributeQueryPort.findAllByIds(childCategory.getAttributes())).thenThrow(new AttributeNotFoundException("Attribute not found by id: %s.".formatted(childCategory.getAttributes().get(0))));

                String content = performCategoryPersist(categoryRequest)
                        .andExpect(status().isNotFound())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

                ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
                assertThat(exceptionResponse).isNotNull();
                assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
                assertThat(exceptionResponse.getMessage()).isEqualTo("Attribute not found by id: %s.".formatted(childCategory.getAttributes().get(0)));
                assertThat(exceptionResponse.getPath()).isEqualTo("/categories");

                verify(categoryQueryPort).findById(categoryRequest.parentId(), "Parent category not found.");
                verify(attributeQueryPort).findAllByIds(childCategory.getAttributes());
                verifyNoInteractions(commandPort);
            }
        }
    }

    @Nested
    class FindCategoriesTests {

        @Test
        void findById_shouldReturnCategory() throws Exception {
            CategoryResponse response = CategoryResponseDataBuilder.withAllFields();
            Category category = CategoryDataBuilder.withAllFields()
                    .id(response.id())
                    .attributes(response.attributes())
                    .title(response.title())
                    .path(response.path())
                    .build();

            when(categoryQueryPort.findById(category.getId(), "Category not found.")).thenReturn(category);

            String content = performCategoryGet(category.getId())
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            CategoryResponse categoryResponse = objectMapper.readValue(content, CategoryResponse.class);

            AssertionUtils.assertFrom(category, categoryResponse, "type");
            assertThat(categoryResponse.type()).isNotNull();
            assertThat(categoryResponse.type()).isEqualTo(CategoryType.CHILD);

            verify(categoryQueryPort).findById(category.getId(), "Category not found.");
        }

        @Test
        void findById_shouldReturnNotFound_whenCategoryNotFound() throws Exception {
            String categoryId = String.valueOf(UUID.randomUUID());

            when(categoryQueryPort.findById(categoryId, "Category not found.")).thenThrow(new CategoryNotFoundException("Category not found."));

            String content = performCategoryGet(categoryId).andExpect(status().isNotFound())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
            assertThat(exceptionResponse).isNotNull();
            assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
            assertThat(exceptionResponse.getMessage()).isEqualTo("Category not found.");
            assertThat(exceptionResponse.getPath()).isEqualTo("/categories/%s".formatted(categoryId));

            verify(categoryQueryPort).findById(categoryId, "Category not found.");
        }
    }

    @Nested
    class TreeCategoriesTests {

        @Test
        void tree_shouldCategoriesTree() throws Exception {
            Attribute attribute1 = AttributeDataBuilder.withAllFields().id("1").build();
            Attribute attribute2 = AttributeDataBuilder.withAllFields().id("2").build();
            String parentId = String.valueOf(UUID.randomUUID()), childId = String.valueOf(UUID.randomUUID());

            Category parent = CategoryDataBuilder.withAllFields().parentId(null).path(List.of(parentId)).attributes(List.of()).build();
            Category child = CategoryDataBuilder.withAllFields()
                    .parentId(parent.getId())
                    .attributes(List.of(attribute1.id(), attribute2.id()))
                    .path(List.of(parentId, childId))
                    .build();

            when(categoryQueryPort.findAll()).thenReturn(List.of(parent, child));
            when(attributeQueryPort.findAllByIds(child.getAttributes())).thenReturn(List.of(attribute1, attribute2));

            String response = performCategoryGetTree()
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            assertThat(response).isNotBlank();
            CategoryTreeResponse treeResponse = objectMapper.readValue(response, CategoryTreeResponse.class);

            assertThat(treeResponse).isNotNull();
            assertThat(treeResponse.getData()).isNotNull();
            assertThat(treeResponse.getData().size()).isEqualTo(1);

            CategoryTreeResponse.CategoryTree tree = treeResponse.getData().get(0);
            AssertionUtils.assertFrom(tree, parent, "children", "parentId", "attributes");

            assertThat(tree.attributes()).isEmpty();
            assertThat(tree.children()).isNotNull();
            assertThat(tree.children().size()).isEqualTo(1);
            assertThat(tree.type()).isNotNull();
            assertThat(tree.type()).isEqualTo(CategoryType.PARENT);

            CategoryTreeResponse.CategoryTree treeChild = tree.children().get(0);
            AssertionUtils.assertFrom(treeChild, child, "children", "parentId", "attributes");
            assertThat(treeChild.type()).isEqualTo(CategoryType.CHILD);

            assertThat(treeChild.attributes().stream().map(Attribute::id).toList()).containsAll(child.getAttributes());
        }

        @Test
        void tree_shouldReturnNotFound_whenOneAttributeIsMissing() throws Exception {
            Attribute attribute1 = AttributeDataBuilder.withAllFields().id("1").build();
            Attribute attribute2 = AttributeDataBuilder.withAllFields().id("2").build();
            Attribute attribute5 = AttributeDataBuilder.withAllFields().id("5").build();
            Attribute attribute8 = AttributeDataBuilder.withAllFields().id("8").build();
            Category category = CategoryDataBuilder.withAllFields().attributes(List.of(attribute1.id(), attribute5.id())).build();
            Category category1 = CategoryDataBuilder.withAllFields().attributes(List.of(attribute2.id(), attribute8.id())).build();

            when(categoryQueryPort.findAll()).thenReturn(List.of(category, category1));
            when(attributeQueryPort.findAllByIds(Stream.concat(category.getAttributes().stream(), category1.getAttributes().stream()).toList()))
                    .thenThrow(new AttributeNotFoundException("Attribute not found by id: %s.".formatted(attribute1.id())));

            String response = performCategoryGetTree()
                    .andExpect(status().isNotFound())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            assertThat(response).isNotBlank();
            ExceptionResponse exceptionResponse = objectMapper.readValue(response, ExceptionResponse.class);
            assertThat(exceptionResponse).isNotNull();
            assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
            assertThat(exceptionResponse.getMessage()).isEqualTo("Attribute not found by id: %s.".formatted(attribute1.id()));
            assertThat(exceptionResponse.getPath()).isEqualTo("/categories/tree");
        }
    }

}
