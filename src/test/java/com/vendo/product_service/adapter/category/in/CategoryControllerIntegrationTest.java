package com.vendo.product_service.adapter.category.in;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vendo.product_service.adapter.category.in.dto.CategoryResponse;
import com.vendo.product_service.adapter.category.in.dto.CreateCategoryRequest;
import com.vendo.product_service.adapter.security.out.jwt.parser.TokenClaims;
import com.vendo.product_service.adapter.security.out.jwt.parser.TokenClaimsParser;
import com.vendo.product_service.domain.attribute.model.Attribute;
import com.vendo.product_service.domain.attribute.model.AttributeType;
import com.vendo.product_service.domain.category.exception.CategoryAlreadyExistsException;
import com.vendo.product_service.domain.category.exception.CategoryNotFoundException;
import com.vendo.product_service.domain.category.model.Category;
import com.vendo.product_service.port.out.attribute.AttributeQueryPort;
import com.vendo.product_service.port.out.category.CategoryCommandPort;
import com.vendo.product_service.port.out.category.CategoryQueryPort;
import com.vendo.product_service.test_utils.builder.CategoryDataBuilder;
import com.vendo.product_service.test_utils.builder.CreateCategoryRequestDataBuilder;
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
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;
import java.util.UUID;

import static com.vendo.security_lib.constants.AuthConstants.AUTHORIZATION_HEADER;
import static com.vendo.security_lib.constants.AuthConstants.BEARER_PREFIX;
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

    private final Attribute ATTRIBUTE = new Attribute("id", "title", AttributeType.STRING, false, null);
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private CategoryCommandPort commandPort;
    @MockitoBean
    private CategoryQueryPort queryPort;
    @MockitoBean
    private TokenClaimsParser claimsParser;
    @MockitoBean
    private AttributeQueryPort attributeQueryPort;

    @BeforeEach
    public void setUp() {
        SecurityContextHolder.clearContext();
    }

    private ResultActions performCategoryGet(String categoryId) throws Exception {
        TokenClaims claims = new TokenClaims("id", UserStatus.ACTIVE, List.of(UserRole.ADMIN.name()), true);
        return mockMvc.perform(get("/categories/{id}", categoryId)
                .with(authentication(SecurityContextService.initializeAuth(claims))));
    }

    private ResultActions performCategoryPersist(CreateCategoryRequest categoryRequest) throws Exception {
        return performCategoryPersist(categoryRequest, UserRole.ADMIN);
    }

    private ResultActions performCategoryPersist(CreateCategoryRequest categoryRequest, UserRole role) throws Exception {
        TokenClaims claims = new TokenClaims("id", UserStatus.ACTIVE, List.of(role.name()), true);
        return mockMvc.perform(post("/categories")
                .with(authentication(SecurityContextService.initializeAuth(claims)))
                .content(objectMapper.writeValueAsString(categoryRequest))
                .contentType(MediaType.APPLICATION_JSON));
    }

    private ResultActions performCategoryPersist(CreateCategoryRequest categoryRequest, String token) throws Exception {
        return mockMvc.perform(post("/categories")
                .header(AUTHORIZATION_HEADER, BEARER_PREFIX + token)
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
            assertThat(exceptionResponse.getErrors().get("title")).isEqualTo("Title validation failed.");
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
            assertThat(exceptionResponse.getErrors().get("code")).isEqualTo("Code validation failed.");
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
            CreateCategoryRequest request = CreateCategoryRequestDataBuilder.withAllFields()
                    .parentId(null)
                    .attributes(null)
                    .build();
            String token = "token_with_user_role";
            TokenClaims claims = new TokenClaims("id", UserStatus.ACTIVE, List.of(UserRole.USER.name()), true);

            when(claimsParser.extract(token)).thenReturn(claims);

            String content = performCategoryPersist(request, token)
                    .andExpect(status().isForbidden())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
            assertThat(exceptionResponse).isNotNull();
            assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
            assertThat(exceptionResponse.getMessage()).isEqualTo("Resource is unreachable.");
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

            when(queryPort.findById(category.getId(), "Category not found.")).thenReturn(category);

            String content = performCategoryGet(category.getId())
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            CategoryResponse categoryResponse = objectMapper.readValue(content, CategoryResponse.class);

            AssertionUtils.assertFrom(category, categoryResponse, "categoryType");

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
                when(attributeQueryPort.findAllByIdsIn(parentCategory.getAttributes())).thenReturn(List.of(ATTRIBUTE));
                doNothing().when(commandPort).save(argumentCaptor.capture());

                performCategoryPersist(categoryRequest).andExpect(status().isOk());

                Category capturedCategory = argumentCaptor.getValue();
                assertThat(capturedCategory).isNotNull();
                assertThat(capturedCategory.getCode()).isEqualTo(categoryRequest.code());
                assertThat(capturedCategory.getParentId()).isEqualTo(categoryRequest.parentId());

                verify(queryPort).existsByCode(categoryRequest.code());
                verify(queryPort).findById(categoryRequest.parentId(), "Parent category not found.");
                verify(attributeQueryPort).findAllByIdsIn(categoryRequest.attributes());
                verify(commandPort).save(capturedCategory);
            }

            @Test
            void save_shouldReturnBadRequest_whenParentCategoryIsChild() throws Exception {
                Category subCategory = CategoryDataBuilder.withAllFields().build();
                CreateCategoryRequest categoryRequest = CreateCategoryRequestDataBuilder.withAllFields()
                        .parentId(subCategory.getId())
                        .build();

                when(queryPort.existsByCode(categoryRequest.code())).thenReturn(false);
                when(queryPort.findById(categoryRequest.parentId(), "Parent category not found.")).thenReturn(subCategory);
                when(attributeQueryPort.findAllByIdsIn(categoryRequest.attributes())).thenReturn(anyList());

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
                verify(attributeQueryPort).findAllByIdsIn(categoryRequest.attributes());
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
