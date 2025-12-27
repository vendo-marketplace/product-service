package com.vendo.product_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vendo.common.exception.ExceptionResponse;
import com.vendo.domain.user.common.type.UserRole;
import com.vendo.product_service.common.builder.CategoryDataBuilder;
import com.vendo.product_service.common.builder.CreateCategoryRequestDataBuilder;
import com.vendo.product_service.common.builder.JwtPayloadDataBuilder;
import com.vendo.product_service.common.dto.JwtPayload;
import com.vendo.product_service.domain.category.db.model.Category;
import com.vendo.product_service.domain.category.db.model.embedded.AttributeDefinition;
import com.vendo.product_service.domain.category.db.repository.CategoryRepository;
import com.vendo.product_service.domain.category.web.dto.CategoryResponse;
import com.vendo.product_service.domain.category.web.dto.CreateCategoryRequest;
import com.vendo.product_service.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.event.annotation.AfterTestClass;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.vendo.security.common.constants.AuthConstants.AUTHORIZATION_HEADER;
import static com.vendo.security.common.constants.AuthConstants.BEARER_PREFIX;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CategoryControllerIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private JwtPayloadDataBuilder jwtPayloadDataBuilder;

    @Autowired
    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        categoryRepository.deleteAll();
    }

    @AfterTestClass
    void tearDown() {
        categoryRepository.deleteAll();
    }

    @Nested
    class SaveCategoryTests {

        @Test
        void save_shouldReturnBadRequest_whenTitleIsNotPresent() throws Exception {
            CreateCategoryRequest categoryRequest = CreateCategoryRequestDataBuilder.buildCreateCategoryRequestWithAllFields()
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
        }

        @Test
        void save_shouldReturnBadRequest_whenTitleIsBlank() throws Exception {
            CreateCategoryRequest categoryRequest = CreateCategoryRequestDataBuilder.buildCreateCategoryRequestWithAllFields()
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
        }

        @Test
        void save_shouldReturnBadRequest_whenCodeIsNotPresent() throws Exception {
            CreateCategoryRequest categoryRequest = CreateCategoryRequestDataBuilder.buildCreateCategoryRequestWithAllFields()
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
        }

        @Test
        void save_shouldReturnBadRequest_whenCodeIsBlank() throws Exception {
            CreateCategoryRequest categoryRequest = CreateCategoryRequestDataBuilder.buildCreateCategoryRequestWithAllFields()
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
        }

        @Test
        void save_shouldReturnConflict_whenCategoryIsAlreadyExistsByCode() throws Exception {
            CreateCategoryRequest categoryRequest = CreateCategoryRequestDataBuilder.buildCreateCategoryRequestWithAllFields().parentId(null).attributes(null).build();
            Category category = Category.builder()
                    .code(categoryRequest.code())
                    .build();
            categoryRepository.save(category);

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
        }

        @Test
        void save_shouldReturnForbidden_whenAuthenticatedUserIsNotAdmin() throws Exception {
            CreateCategoryRequest categoryRequest = CreateCategoryRequestDataBuilder.buildCreateCategoryRequestWithAllFields()
                    .parentId(null)
                    .attributes(null)
                    .build();

            Map<String, Object> claims = jwtPayloadDataBuilder.buildClaimsWithRole(UserRole.USER);
            JwtPayload jwtPayload = jwtPayloadDataBuilder.buildValidJwtPayload().claims(claims).build();

            String content = performCategoryPersist(categoryRequest, jwtPayload)
                    .andExpect(status().isForbidden())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
            assertThat(exceptionResponse).isNotNull();
            assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
            assertThat(exceptionResponse.getMessage()).isEqualTo("You do not have permission to access this resource.");
            assertThat(exceptionResponse.getPath()).isEqualTo("/categories");
        }

        @Test
        void save_shouldReturnBadRequest_whenAttributesAndNoParent() throws Exception {
            CreateCategoryRequest categoryRequest = CreateCategoryRequestDataBuilder.buildCreateCategoryRequestWithAllFields()
                    .parentId(null)
                    .build();

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
        }
    }

    @Nested
    class SaveParentCategoryTests {

        @Test
        void save_shouldSaveParentCategory_whenNoParentIdAndNotAttributes() throws Exception {
            CreateCategoryRequest categoryRequest = CreateCategoryRequestDataBuilder.buildCreateCategoryRequestWithAllFields()
                    .parentId(null)
                    .attributes(null)
                    .build();

            performCategoryPersist(categoryRequest).andExpect(status().isOk());

            Optional<Category> categoryOptional = categoryRepository.findByCodeIgnoreCase(categoryRequest.code());
            assertThat(categoryOptional).isPresent();
            assertThat(categoryOptional.get().getCode()).isEqualTo(categoryRequest.code());
            assertThat(categoryOptional.get().getParentId()).isNull();
        }
    }

    @Nested
    class SaveSubCategoryTests {

        @Test
        void save_shouldSaveSubCategory_whenParentIdAndNoAttributes() throws Exception {
            Category parentCategory = CategoryDataBuilder.buildCategoryWithAllFields().parentId(null).attributes(null).build();
            categoryRepository.save(parentCategory);
            CreateCategoryRequest categoryRequest = CreateCategoryRequestDataBuilder.buildCreateCategoryRequestWithAllFields()
                    .parentId(parentCategory.getId())
                    .attributes(null)
                    .build();

            performCategoryPersist(categoryRequest).andExpect(status().isOk());

            Optional<Category> categoryOptional = categoryRepository.findByCodeIgnoreCase(categoryRequest.code());
            assertThat(categoryOptional).isPresent();
            assertThat(categoryOptional.get().getCode()).isEqualTo(categoryRequest.code());
            assertThat(categoryOptional.get().getParentId()).isEqualTo(parentCategory.getId());
        }

        @Test
        void save_shouldReturnNotFound_whenParentNotFoundInSubCategory() throws Exception {
            CreateCategoryRequest categoryRequest = CreateCategoryRequestDataBuilder.buildCreateCategoryRequestWithAllFields()
                    .attributes(null)
                    .build();

            String content = performCategoryPersist(categoryRequest)
                    .andExpect(status().isNotFound())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
            assertThat(exceptionResponse).isNotNull();
            assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
            assertThat(exceptionResponse.getMessage()).isEqualTo("Parent category not found by parent.");
            assertThat(exceptionResponse.getPath()).isEqualTo("/categories");
        }

        @Test
        void save_shouldReturnBadRequest_whenSubCategoryHasChildParent() throws Exception {
            Category childCategory = CategoryDataBuilder.buildCategoryWithAllFields().build();
            categoryRepository.save(childCategory);

            CreateCategoryRequest categoryRequest = CreateCategoryRequestDataBuilder.buildCreateCategoryRequestWithAllFields()
                    .parentId(childCategory.getId())
                    .attributes(null)
                    .build();

            String content = performCategoryPersist(categoryRequest)
                    .andExpect(status().isBadRequest())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
            assertThat(exceptionResponse).isNotNull();
            assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            assertThat(exceptionResponse.getMessage()).isEqualTo("Sub category shouldn't have child category as parent.");
            assertThat(exceptionResponse.getPath()).isEqualTo("/categories");
        }
    }

    @Nested
    class SaveChildCategoryTests {

        @Test
        void save_shouldSaveChildCategory_whenParentAndAttributes() throws Exception {
            Category parentCategory = CategoryDataBuilder.buildCategoryWithAllFields().attributes(null).build();
            categoryRepository.save(parentCategory);
            CreateCategoryRequest categoryRequest = CreateCategoryRequestDataBuilder.buildCreateCategoryRequestWithAllFields()
                    .parentId(parentCategory.getId())
                    .build();

            performCategoryPersist(categoryRequest).andExpect(status().isOk());

            Optional<Category> categoryOptional = categoryRepository.findByCodeIgnoreCase(categoryRequest.code());
            assertThat(categoryOptional).isPresent();
            assertThat(categoryOptional.get().getCode()).isEqualTo(categoryRequest.code());
            assertThat(categoryOptional.get().getParentId()).isEqualTo(parentCategory.getId());
        }

        @Test
        void save_shouldReturnBadRequest_whenParentCategoryIsNotSubInChildCategory() throws Exception {
            Category subCategory = CategoryDataBuilder.buildCategoryWithAllFields().build();
            categoryRepository.save(subCategory);
            CreateCategoryRequest categoryRequest = CreateCategoryRequestDataBuilder.buildCreateCategoryRequestWithAllFields()
                    .parentId(subCategory.getId())
                    .build();

            String content = performCategoryPersist(categoryRequest)
                    .andExpect(status().isBadRequest())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
            assertThat(exceptionResponse).isNotNull();
            assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            assertThat(exceptionResponse.getMessage()).isEqualTo("Child category shouldn't have child category as parent.");
            assertThat(exceptionResponse.getPath()).isEqualTo("/categories");
        }

        @Test
        void save_shouldReturnNotFound_whenParentCategoryNotFoundInChildCategory() throws Exception {
            CreateCategoryRequest categoryRequest = CreateCategoryRequestDataBuilder.buildCreateCategoryRequestWithAllFields().build();

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
        }
    }

    @Nested
    class FindCategoriesTests {

        @Test
        void findById_shouldReturnCategory() throws Exception {
            Category category = CategoryDataBuilder.buildCategoryWithAllFields().build();
            categoryRepository.save(category);

            String content = performCategoryGet(category.getId()).andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            CategoryResponse categoryResponse = objectMapper.readValue(content, CategoryResponse.class);
            assertThat(categoryResponse).isNotNull();
            assertThat(categoryResponse.title()).isEqualTo(category.getTitle());
            assertThat(categoryResponse.parentId()).isEqualTo(category.getParentId());
            assertThat(categoryResponse.attributes()).isNotNull();
            assertThat(categoryResponse.attributes().size()).isEqualTo(category.getAttributes().size());
            assertThat(categoryResponse.attributes().get("attribute_name")).isNotNull();

            AttributeDefinition responseAttributeName = categoryResponse.attributes().get("attribute_name");
            AttributeDefinition categoryAttributeName = category.getAttributes().get("attribute_name");
            assertThat(responseAttributeName.type()).isEqualTo(categoryAttributeName.type());
            assertThat(responseAttributeName.required()).isEqualTo(categoryAttributeName.required());
            assertThat(responseAttributeName.allowedValues()).isNotNull();
            assertThat(responseAttributeName.allowedValues().size()).isEqualTo(categoryAttributeName.allowedValues().size());
        }

        @Test
        void findById_shouldReturnNotFound_whenCategoryNotFound() throws Exception {
            String categoryId = String.valueOf(UUID.randomUUID());

            String content = performCategoryGet(categoryId).andExpect(status().isNotFound())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
            assertThat(exceptionResponse).isNotNull();
            assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
            assertThat(exceptionResponse.getMessage()).isEqualTo("Category not found.");
            assertThat(exceptionResponse.getPath()).isEqualTo("/categories/%s".formatted(categoryId));
        }
    }

    private ResultActions performCategoryGet(String categoryId) throws Exception {
        Map<String, Object> claims = jwtPayloadDataBuilder.buildClaimsWithRole(UserRole.ADMIN);
        JwtPayload jwtPayload = jwtPayloadDataBuilder.buildValidJwtPayload().claims(claims).build();

        String accessToken = jwtService.generateAccessToken(jwtPayload);
        return mockMvc.perform(get("/categories/{id}", categoryId)
                        .header(AUTHORIZATION_HEADER, BEARER_PREFIX + accessToken));
    }

    private ResultActions performCategoryPersist(CreateCategoryRequest categoryRequest) throws Exception {
        Map<String, Object> claims = jwtPayloadDataBuilder.buildClaimsWithRole(UserRole.ADMIN);
        JwtPayload jwtPayload = jwtPayloadDataBuilder.buildValidJwtPayload().claims(claims).build();

        return performCategoryPersist(categoryRequest, jwtPayload);
    }

    private ResultActions performCategoryPersist(CreateCategoryRequest categoryRequest, JwtPayload jwtPayload) throws Exception {
        String accessToken = jwtService.generateAccessToken(jwtPayload);
        return mockMvc.perform(post("/categories")
                .header(AUTHORIZATION_HEADER, BEARER_PREFIX + accessToken)
                .content(objectMapper.writeValueAsString(categoryRequest))
                .contentType(MediaType.APPLICATION_JSON));
    }
}
