package com.vendo.product_service.adapter.category.in;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vendo.product_service.adapter.category.in.dto.CreateCategoryRequest;
import com.vendo.product_service.adapter.category.in.dto.UpdateCategoryRequest;
import com.vendo.product_service.domain.attribute.exception.AttributeNotFoundException;
import com.vendo.product_service.domain.attribute.model.Attribute;
import com.vendo.product_service.domain.attribute.model.AttributeType;
import com.vendo.product_service.domain.category.exception.CategoryAlreadyExistsException;
import com.vendo.product_service.domain.category.exception.CategoryNotFoundException;
import com.vendo.product_service.domain.category.model.Category;
import com.vendo.product_service.domain.image.model.PresignType;
import com.vendo.product_service.domain.product.pattern.ProductPatterns;
import com.vendo.product_service.domain.user.User;
import com.vendo.product_service.port.attribute.AttributeQueryPort;
import com.vendo.product_service.port.category.CategoryCommandPort;
import com.vendo.product_service.port.category.CategoryQueryPort;
import com.vendo.product_service.port.image.ImageEventSenderPort;
import com.vendo.product_service.port.image.usecase.ImageUseCase;
import com.vendo.product_service.test_utils.builder.CategoryDataBuilder;
import com.vendo.product_service.test_utils.builder.CreateCategoryRequestDataBuilder;
import com.vendo.product_service.test_utils.builder.UserDataBuilder;
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
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import static com.vendo.product_service.domain.category.model.Category.CATEGORY_TYPE_VALIDATION_MESSAGE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CategoryCommandControllerIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private String baseUrl;

    @MockitoBean
    private CategoryCommandPort commandPort;
    @MockitoBean
    private CategoryQueryPort categoryQueryPort;
    @MockitoBean
    private CategoryCommandPort categoryCommandPort;
    @MockitoBean
    private AttributeQueryPort attributeQueryPort;
    @MockitoBean
    private ImageEventSenderPort imageEventSenderPort;
    @MockitoBean
    private ImageUseCase imageUseCase;

    private MockMultipartFile buildImage(String contentType, byte[] content) {
        return new MockMultipartFile("images", "photo.png", contentType, content);
    }

    private ResultActions performCategoryPersist(CreateCategoryRequest categoryRequest) throws Exception {
        return performCategoryPersist(categoryRequest, UserRole.ADMIN);
    }

    private ResultActions performCategoryImageUpload(String categoryId, UserRole role, MultipartFile file) throws Exception {
        User user = new User("id", "email", UserStatus.ACTIVE, Set.of(role), true);

        MockMultipartHttpServletRequestBuilder request = multipart("/categories/image?id=" + categoryId);
        request.with(authentication(SecurityContextService.initializeAuth(user)));
        request.file(new MockMultipartFile(
                "image",
                file.getOriginalFilename(),
                file.getContentType(),
                file.getBytes()));

        return mockMvc.perform(request);
    }

    private ResultActions performCategoryImageRemove(String categoryId, UserRole role) throws Exception {
        User user = new User("id", "email", UserStatus.ACTIVE, Set.of(role), true);
        return mockMvc.perform(delete("/categories/image?id={id}", categoryId)
                .with(authentication(SecurityContextService.initializeAuth(user))));
    }

    private ResultActions performCategoryUpdate(String categoryId, String ownerId, UserRole role, UpdateCategoryRequest request) throws Exception {
        User user = new User(ownerId, "email", UserStatus.ACTIVE, Set.of(role), true);
        return mockMvc.perform(put("/categories?id={id}", categoryId)
                .with(authentication(SecurityContextService.initializeAuth(user)))
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON));
    }

    private ResultActions performCategoryPersist(CreateCategoryRequest categoryRequest, UserRole role) throws Exception {
        User user = new User("id", "email", UserStatus.ACTIVE, Set.of(role), true);
        return mockMvc.perform(post("/categories")
                .with(authentication(SecurityContextService.initializeAuth(user)))
                .content(objectMapper.writeValueAsString(categoryRequest))
                .contentType(MediaType.APPLICATION_JSON));
    }

    @Nested
    class SaveCategoryTests {

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
                Category parent = CategoryDataBuilder.withChild()
                        .id(parentId)
                        .attributes(null)
                        .parentId(null)
                        .path(List.of(parentId))
                        .build();
                Category sub = CategoryDataBuilder.withChild()
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

                Category parent = CategoryDataBuilder.withChild().id(parentId).attributes(null).path(List.of(parentId)).build();
                Category sub = CategoryDataBuilder.withChild().parentId(parent.getId()).attributes(null).build();
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
                Category childCategory = CategoryDataBuilder.withChild().build();
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
                Category sub = CategoryDataBuilder.withChild().id(subId).attributes(null).parentId(parentId).path(subPath).build();

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
                Category subCategory = CategoryDataBuilder.withChild().build();
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
                Category parentCategory = CategoryDataBuilder.withChild().attributes(null).build();
                CreateCategoryRequest categoryRequest = CreateCategoryRequestDataBuilder.withAllFields().parentId(parentCategory.getId()).build();
                Category childCategory = CategoryDataBuilder.withChild().parentId(parentCategory.getId()).build();

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
    class UpdateCategoryTests {

        @Test
        void update_shouldUpdateCategory() throws Exception {
            User user = UserDataBuilder.withAllFields().build();
            String categoryId = "categoryId";
            UpdateCategoryRequest request = new UpdateCategoryRequest("PC", "pc");

            when(categoryQueryPort.existsById(categoryId)).thenReturn(true);

            performCategoryUpdate(categoryId, user.id(), UserRole.ADMIN, request).andExpect(status().isOk());

            verify(categoryQueryPort).existsById(categoryId);
            verify(categoryCommandPort).update(eq(categoryId), any(Category.class));
        }

        @Test
        void update_shouldReturnForbidden_whenNotAdmin() throws Exception {
            User user = UserDataBuilder.withAllFields().build();
            String categoryId = "categoryId";
            UpdateCategoryRequest request = new UpdateCategoryRequest("PC", "pc");

            String content = performCategoryUpdate(categoryId, user.id(), UserRole.USER, request)
                    .andExpect(status().isForbidden())
                    .andReturn().getResponse().getContentAsString();

            assertThat(content).isNotBlank();
            ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
            assertThat(exceptionResponse.getMessage()).isEqualTo("Resource is unreachable.");
            assertThat(exceptionResponse.getErrors()).isNull();
            assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
            assertThat(exceptionResponse.getPath()).isEqualTo("/categories");

            verifyNoInteractions(categoryQueryPort, categoryCommandPort);
        }

        @Test
        void update_shouldReturnBadRequest_whenTitleAndSlugAreInvalid() throws Exception {
            User user = UserDataBuilder.withAllFields().build();
            String categoryId = "categoryId";
            UpdateCategoryRequest request = new UpdateCategoryRequest("_invalid_title", "INVALID_SLUG");

            when(categoryQueryPort.existsById(categoryId)).thenReturn(true);

            String content = performCategoryUpdate(categoryId, user.id(), UserRole.ADMIN, request)
                    .andExpect(status().isBadRequest())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            assertThat(content).isNotBlank();
            ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
            assertThat(exceptionResponse.getMessage()).isEqualTo("Validation failed.");
            assertThat(exceptionResponse.getErrors()).isNotNull();
            assertThat(exceptionResponse.getErrors()).hasSize(2);
            assertThat(exceptionResponse.getErrors().get("title")).isEqualTo("Title must start with a capital letter and may contain only letters, spaces, commas, slashes, hyphens, and apostrophes.");
            assertThat(exceptionResponse.getErrors().get("slug")).isEqualTo("Slug may contain only lowercase letters, numbers, and underscores, and cannot start or end with an underscore.");
            assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            assertThat(exceptionResponse.getPath()).isEqualTo("/categories");

            verifyNoInteractions(categoryQueryPort, categoryCommandPort);
        }

        @Test
        void update_shouldReturnNotFound_whenCategoryNotFound() throws Exception {
            User user = UserDataBuilder.withAllFields().build();
            String categoryId = "categoryId";
            UpdateCategoryRequest request = new UpdateCategoryRequest("PC", "pc");

            when(categoryQueryPort.existsById(categoryId)).thenReturn(false);

            String content = performCategoryUpdate(categoryId, user.id(), UserRole.ADMIN, request)
                    .andExpect(status().isNotFound())
                    .andReturn().getResponse().getContentAsString();

            assertThat(content).isNotBlank();
            ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
            assertThat(exceptionResponse.getMessage()).isEqualTo("Category not found.");
            assertThat(exceptionResponse.getErrors()).isNull();
            assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
            assertThat(exceptionResponse.getPath()).isEqualTo("/categories");

            verify(categoryQueryPort).existsById(categoryId);
            verifyNoInteractions(categoryCommandPort);
        }
    }

    @Nested
    class CategoryRemoveImageTests {

        @Test
        void removeImage_shouldRemoveImageFromCategory() throws Exception {
            Category parent = CategoryDataBuilder.withParent().build();

            when(categoryQueryPort.findById(parent.getId())).thenReturn(parent);

            performCategoryImageRemove(parent.getId(), UserRole.ADMIN)
                    .andExpect(status().isOk());

            verify(categoryQueryPort).findById(parent.getId());
            verify(imageEventSenderPort).delete(parent.getImage().key());
            verify(categoryCommandPort).removeImage(parent.getId());
            verify(imageEventSenderPort).delete(parent.getImage().key());
        }

        @Test
        void removeImage_shouldReturnForbidden_whenNotAdmin() throws Exception {
            Category parent = CategoryDataBuilder.withParent().build();

            String content = performCategoryImageRemove(parent.getId(), UserRole.USER)
                    .andExpect(status().isForbidden())
                    .andReturn().getResponse().getContentAsString();

            assertThat(content).isNotBlank();
            ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
            assertThat(exceptionResponse.getMessage()).isEqualTo("Resource is unreachable.");
            assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
            assertThat(exceptionResponse.getPath()).isEqualTo("/categories/image");

            verifyNoInteractions(categoryQueryPort, imageEventSenderPort, categoryCommandPort);
        }

        @Test
        void removeImage_shouldReturnNotFound_whenCategoryNotFound() throws Exception {
            Category parent = CategoryDataBuilder.withParent().build();

            when(categoryQueryPort.findById(parent.getId())).thenThrow(new CategoryNotFoundException("Category not found."));

            String content = performCategoryImageRemove(parent.getId(), UserRole.ADMIN)
                    .andExpect(status().isNotFound())
                    .andReturn().getResponse().getContentAsString();

            assertThat(content).isNotBlank();
            ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
            assertThat(exceptionResponse.getMessage()).isEqualTo("Category not found.");
            assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
            assertThat(exceptionResponse.getPath()).isEqualTo("/categories/image");

            verify(categoryQueryPort).findById(parent.getId());
            verifyNoInteractions(imageEventSenderPort, categoryCommandPort);
        }

        @Test
        void removeImage_shouldReturnNotFound_whenDeletingFromCategoryWithoutIt() throws Exception {
            Category parent = CategoryDataBuilder.withParent().image(null).build();

            when(categoryQueryPort.findById(parent.getId())).thenReturn(parent);

            String content = performCategoryImageRemove(parent.getId(), UserRole.ADMIN)
                    .andExpect(status().isBadRequest())
                    .andReturn().getResponse().getContentAsString();

            assertThat(content).isNotBlank();
            ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
            assertThat(exceptionResponse.getMessage()).isEqualTo("Category has no image.");
            assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            assertThat(exceptionResponse.getPath()).isEqualTo("/categories/image");

            verify(categoryQueryPort).findById(parent.getId());
            verifyNoInteractions(imageEventSenderPort, categoryCommandPort);
        }
    }

    @Nested
    class UploadImageTests {

        @Test
        void uploadImage_shouldUploadImageForCategory() throws Exception {
            Category category = CategoryDataBuilder.withParent().image(null).build();
            String key = "key";
            MockMultipartFile file = buildImage("image/png", new byte[]{1, 2, 3});
            ArgumentCaptor<Category> captor = ArgumentCaptor.forClass(Category.class);

            when(categoryQueryPort.findById(category.getId())).thenReturn(category);
            when(imageUseCase.upload(eq(PresignType.CATEGORY), anyList())).thenReturn(List.of(key));

            performCategoryImageUpload(category.getId(), UserRole.ADMIN, file).andExpect(status().isOk());

            verify(categoryQueryPort).findById(category.getId());
            verify(imageUseCase).upload(eq(PresignType.CATEGORY), anyList());
            verify(categoryCommandPort).update(eq(category.getId()), captor.capture());
            verifyNoInteractions(imageEventSenderPort);

            Category captorValue = captor.getValue();
            assertThat(captorValue).isNotNull();
            assertThat(captorValue.getImage()).isNotNull();
            assertThat(captorValue.getImage().key()).isEqualTo(key);
            assertThat(captorValue.getImage().url()).isEqualTo(baseUrl + captorValue.getImage().key());
        }

        @Test
        void uploadImage_shouldReturnForbidden_whenNotAdmin() throws Exception {
            Category category = CategoryDataBuilder.withParent().image(null).build();

            String content = performCategoryImageUpload(category.getId(), UserRole.USER, buildImage("image/png", new byte[]{1,2,3}))
                    .andExpect(status().isForbidden()).andReturn().getResponse().getContentAsString();

            assertThat(content).isNotBlank();
            ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
            assertThat(exceptionResponse.getMessage()).isEqualTo("Resource is unreachable.");
            assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
            assertThat(exceptionResponse.getPath()).isEqualTo("/categories/image");

            verifyNoInteractions(categoryQueryPort, imageUseCase, categoryCommandPort, imageEventSenderPort);
        }

        @Test
        void uploadImage_shouldReturnBadRequest_whenFileIsNotImage() throws Exception {
            Category category = CategoryDataBuilder.withParent().image(null).build();
            List<String> keys = List.of("key");

            when(categoryQueryPort.findById(category.getId())).thenReturn(category);
            when(imageUseCase.upload(eq(PresignType.CATEGORY), anyList())).thenReturn(keys);

            String content = performCategoryImageUpload(category.getId(), UserRole.ADMIN, buildImage("video/mp4", new byte[]{1,2,3}))
                    .andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString();

            assertThat(content).isNotBlank();
            ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
            assertThat(exceptionResponse.getMessage()).isEqualTo("Validation failed.");
            assertThat(exceptionResponse.getErrors()).hasSize(1);
            assertThat(exceptionResponse.getErrors().get("image")).isEqualTo("File is not image or empty.");
            assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            assertThat(exceptionResponse.getPath()).isEqualTo("/categories/image");

            verifyNoInteractions(categoryQueryPort, imageUseCase, categoryCommandPort, imageEventSenderPort);
        }

        @Test
        void uploadImage_shouldReturnNotFound_whenCategoryNotFound() throws Exception {
            Category category = CategoryDataBuilder.withParent().image(null).build();
            List<String> keys = List.of("key");

            when(categoryQueryPort.findById(category.getId())).thenThrow(new CategoryNotFoundException("Category not found."));
            when(imageUseCase.upload(eq(PresignType.CATEGORY), anyList())).thenReturn(keys);

            performCategoryImageUpload(category.getId(), UserRole.ADMIN, buildImage("image/png", new byte[]{1,2,3})).andExpect(status().isOk());

            verify(categoryQueryPort).findById(category.getId());
            verify(imageUseCase).upload(eq(PresignType.CATEGORY), anyList());
            verify(categoryCommandPort).update(eq(category.getId()), any());
            verifyNoInteractions(imageEventSenderPort);
        }

        @Test
        void uploadImage_shouldReturnInternalServerError_whenKeysAreEmpty() throws Exception {
            Category category = CategoryDataBuilder.withParent().image(null).build();
            MockMultipartFile file = buildImage("image/png", new byte[]{1, 2, 3});

            when(categoryQueryPort.findById(category.getId())).thenReturn(category);
            when(imageUseCase.upload(eq(PresignType.CATEGORY), anyList())).thenReturn(List.of());

            String content = performCategoryImageUpload(category.getId(), UserRole.ADMIN, file)
                    .andExpect(status().isInternalServerError())
                    .andReturn().getResponse().getContentAsString();

            assertThat(content).isNotBlank();
            ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
            assertThat(exceptionResponse.getMessage()).isEqualTo("Internal server error.");
            assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
            assertThat(exceptionResponse.getPath()).isEqualTo("/categories/image");

            verify(categoryQueryPort).findById(category.getId());
            verify(imageUseCase).upload(eq(PresignType.CATEGORY), anyList());
            verifyNoInteractions(categoryCommandPort, imageEventSenderPort);
        }
    }
}
