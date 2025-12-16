package com.vendo.product_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vendo.common.exception.ExceptionResponse;
import com.vendo.domain.user.common.type.UserRole;
import com.vendo.domain.user.common.type.UserStatus;
import com.vendo.product_service.common.builder.CategoryDataBuilder;
import com.vendo.product_service.common.builder.CreateCategoryRequestDataBuilder;
import com.vendo.product_service.common.builder.JwtPayloadDataBuilder;
import com.vendo.product_service.common.dto.JwtPayload;
import com.vendo.product_service.domain.category.common.type.CategoryType;
import com.vendo.product_service.domain.category.db.model.Category;
import com.vendo.product_service.domain.category.db.model.embedded.AttributeValue;
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

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.vendo.product_service.common.builder.JwtPayloadDataBuilder.buildClaimsWithRole;
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
                    .parentId(null)
                    .attributes(null)
                    .title(null)
                    .build();
            JwtPayload jwtPayload = jwtPayloadDataBuilder.buildValidUserJwtPayload().claims(buildClaimsWithRole(UserRole.ADMIN)).build();

            String accessToken = jwtService.generateAccessToken(jwtPayload);
            String content = mockMvc.perform(post("/categories")
                            .header(AUTHORIZATION_HEADER, BEARER_PREFIX + accessToken)
                            .content(objectMapper.writeValueAsString(categoryRequest))
                            .contentType(MediaType.APPLICATION_JSON))
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
        void save_shouldReturnBadRequest_whenCategoryIsNotPresent() throws Exception {
            CreateCategoryRequest categoryRequest = CreateCategoryRequestDataBuilder.buildCreateCategoryRequestWithAllFields()
                    .parentId(null)
                    .attributes(null)
                    .categoryType(null)
                    .build();
            JwtPayload jwtPayload = jwtPayloadDataBuilder.buildValidUserJwtPayload().claims(buildClaimsWithRole(UserRole.ADMIN)).build();

            String accessToken = jwtService.generateAccessToken(jwtPayload);
            String content = mockMvc.perform(post("/categories")
                            .header(AUTHORIZATION_HEADER, BEARER_PREFIX + accessToken)
                            .content(objectMapper.writeValueAsString(categoryRequest))
                            .contentType(MediaType.APPLICATION_JSON))
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
            assertThat(exceptionResponse.getErrors().get("categoryType")).isEqualTo("Category type is required.");
            assertThat(exceptionResponse.getPath()).isEqualTo("/categories");
        }

        @Test
        void save_shouldReturnConflict_whenCategoryIsAlreadyExistsByTitle() throws Exception {
            CreateCategoryRequest categoryRequest = CreateCategoryRequestDataBuilder.buildCreateCategoryRequestWithAllFields()
                    .parentId(String.valueOf(UUID.randomUUID()))
                    .attributes(null)
                    .build();
            Category category = Category.builder()
                    .title(categoryRequest.title())
                    .categoryType(CategoryType.ROOT)
                    .build();
            categoryRepository.save(category);
            JwtPayload jwtPayload = jwtPayloadDataBuilder.buildValidUserJwtPayload().claims(buildClaimsWithRole(UserRole.ADMIN)).build();

            String accessToken = jwtService.generateAccessToken(jwtPayload);
            String content = mockMvc.perform(post("/categories")
                            .header(AUTHORIZATION_HEADER, BEARER_PREFIX + accessToken)
                            .content(objectMapper.writeValueAsString(categoryRequest))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isConflict())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
            assertThat(exceptionResponse).isNotNull();
            assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.CONFLICT.value());
            assertThat(exceptionResponse.getMessage()).isEqualTo("Category already exists.");
            assertThat(exceptionResponse.getPath()).isEqualTo("/categories");
        }

        @Test
        void save_shouldReturnForbidden_whenAuthenticatedUserIsNotAdmin() throws Exception {
            CreateCategoryRequest categoryRequest = CreateCategoryRequestDataBuilder.buildCreateCategoryRequestWithAllFields()
                    .parentId(null)
                    .attributes(null)
                    .build();
            JwtPayload jwtPayload = jwtPayloadDataBuilder.buildValidUserJwtPayload().claims(buildClaimsWithRole(UserRole.USER)).build();

            String accessToken = jwtService.generateAccessToken(jwtPayload);
            String content = mockMvc.perform(post("/categories")
                            .header(AUTHORIZATION_HEADER, BEARER_PREFIX + accessToken)
                            .content(objectMapper.writeValueAsString(categoryRequest))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isForbidden())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
            assertThat(exceptionResponse).isNotNull();
            assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
            assertThat(exceptionResponse.getMessage()).isEqualTo("You do not have permission to access this resource.");
            assertThat(exceptionResponse.getPath()).isEqualTo("/categories");

            Optional<Category> categoryOptional = categoryRepository.findByTitleIgnoreCase(categoryRequest.title());
            assertThat(categoryOptional).isNotPresent();
        }
    }

    @Nested
    class SaveRootCategoryTests {

        @Test
        void save_shouldSaveRootCategory() throws Exception {
            CreateCategoryRequest categoryRequest = CreateCategoryRequestDataBuilder.buildCreateCategoryRequestWithAllFields()
                    .parentId(null)
                    .attributes(null)
                    .build();
            JwtPayload jwtPayload = jwtPayloadDataBuilder.buildValidUserJwtPayload().claims(buildClaimsWithRole(UserRole.ADMIN)).build();

            String accessToken = jwtService.generateAccessToken(jwtPayload);
            mockMvc.perform(post("/categories")
                            .header(AUTHORIZATION_HEADER, BEARER_PREFIX + accessToken)
                            .content(objectMapper.writeValueAsString(categoryRequest))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());

            Optional<Category> categoryOptional = categoryRepository.findByTitleIgnoreCase(categoryRequest.title());
            assertThat(categoryOptional).isPresent();
            assertThat(categoryOptional.get().getTitle()).isEqualTo(categoryRequest.title());
            assertThat(categoryOptional.get().getCategoryType()).isEqualTo(categoryRequest.categoryType());
            assertThat(categoryOptional.get().getParentId()).isNull();
        }

        @Test
        void save_shouldReturnBadRequest_whenRootCategoryHasParentId() throws Exception {
            CreateCategoryRequest categoryRequest = CreateCategoryRequestDataBuilder.buildCreateCategoryRequestWithAllFields()
                    .parentId(String.valueOf(UUID.randomUUID()))
                    .attributes(null)
                    .build();
            JwtPayload jwtPayload = jwtPayloadDataBuilder.buildValidUserJwtPayload().claims(buildClaimsWithRole(UserRole.ADMIN)).build();

            String accessToken = jwtService.generateAccessToken(jwtPayload);
            String content = mockMvc.perform(post("/categories")
                            .header(AUTHORIZATION_HEADER, BEARER_PREFIX + accessToken)
                            .content(objectMapper.writeValueAsString(categoryRequest))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
            assertThat(exceptionResponse).isNotNull();
            assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            assertThat(exceptionResponse.getMessage()).isEqualTo("Root category cannot have parent id.");
            assertThat(exceptionResponse.getPath()).isEqualTo("/categories");
        }

        @Test
        void save_shouldReturnBadRequest_whenRootCategoryHasAttributes() throws Exception {
            CreateCategoryRequest categoryRequest = CreateCategoryRequestDataBuilder.buildCreateCategoryRequestWithAllFields()
                    .parentId(null)
                    .attributes(Map.of("attribute_name", AttributeValue.builder().type("string").build()))
                    .build();
            JwtPayload jwtPayload = jwtPayloadDataBuilder.buildValidUserJwtPayload().claims(buildClaimsWithRole(UserRole.ADMIN)).build();

            String accessToken = jwtService.generateAccessToken(jwtPayload);
            String content = mockMvc.perform(post("/categories")
                            .header(AUTHORIZATION_HEADER, BEARER_PREFIX + accessToken)
                            .content(objectMapper.writeValueAsString(categoryRequest))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
            assertThat(exceptionResponse).isNotNull();
            assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            assertThat(exceptionResponse.getMessage()).isEqualTo("Root category cannot have attributes.");
            assertThat(exceptionResponse.getPath()).isEqualTo("/categories");
        }
    }

    @Nested
    class SaveSubCategoryTests {

        @Test
        void save_shouldSaveSubCategory() throws Exception {
            Category rootCategory = Category.builder()
                    .title("Root category")
                    .categoryType(CategoryType.ROOT)
                    .build();
            categoryRepository.save(rootCategory);
            CreateCategoryRequest categoryRequest = CreateCategoryRequestDataBuilder.buildCreateCategoryRequestWithAllFields()
                    .parentId(rootCategory.getId())
                    .attributes(null)
                    .categoryType(CategoryType.SUB)
                    .build();
            JwtPayload jwtPayload = jwtPayloadDataBuilder.buildValidUserJwtPayload().claims(buildClaimsWithRole(UserRole.ADMIN)).build();

            String accessToken = jwtService.generateAccessToken(jwtPayload);
            mockMvc.perform(post("/categories")
                            .header(AUTHORIZATION_HEADER, BEARER_PREFIX + accessToken)
                            .content(objectMapper.writeValueAsString(categoryRequest))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());

            Optional<Category> categoryOptional = categoryRepository.findByTitleIgnoreCase(categoryRequest.title());
            assertThat(categoryOptional).isPresent();
            assertThat(categoryOptional.get().getTitle()).isEqualTo(categoryRequest.title());
            assertThat(categoryOptional.get().getCategoryType()).isEqualTo(categoryRequest.categoryType());
            assertThat(categoryOptional.get().getParentId()).isEqualTo(rootCategory.getId());
        }

        @Test
        void save_shouldReturnBadRequest_whenParentIdIsNotPresentInSubCategory() throws Exception {
            Category category = Category.builder()
                    .title("Root category")
                    .categoryType(CategoryType.ROOT)
                    .build();
            categoryRepository.save(category);
            CreateCategoryRequest categoryRequest = CreateCategoryRequestDataBuilder.buildCreateCategoryRequestWithAllFields()
                    .parentId(null)
                    .attributes(null)
                    .categoryType(CategoryType.SUB)
                    .build();
            JwtPayload jwtPayload = jwtPayloadDataBuilder.buildValidUserJwtPayload().claims(buildClaimsWithRole(UserRole.ADMIN)).build();

            String accessToken = jwtService.generateAccessToken(jwtPayload);
            String content = mockMvc.perform(post("/categories")
                            .header(AUTHORIZATION_HEADER, BEARER_PREFIX + accessToken)
                            .content(objectMapper.writeValueAsString(categoryRequest))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
            assertThat(exceptionResponse).isNotNull();
            assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            assertThat(exceptionResponse.getMessage()).isEqualTo("Sub category should have parent id.");
            assertThat(exceptionResponse.getPath()).isEqualTo("/categories");
        }

        @Test
        void save_shouldReturnBadRequest_whenAttributesArePresentInSubCategory() throws Exception {
            Category category = Category.builder()
                    .title("Root category")
                    .categoryType(CategoryType.ROOT)
                    .build();
            categoryRepository.save(category);
            CreateCategoryRequest categoryRequest = CreateCategoryRequestDataBuilder.buildCreateCategoryRequestWithAllFields()
                    .parentId(category.getId())
                    .attributes(Map.of("attribute_name", AttributeValue.builder().type("string").build()))
                    .categoryType(CategoryType.SUB)
                    .build();
            JwtPayload jwtPayload = jwtPayloadDataBuilder.buildValidUserJwtPayload().claims(buildClaimsWithRole(UserRole.ADMIN)).build();

            String accessToken = jwtService.generateAccessToken(jwtPayload);
            String content = mockMvc.perform(post("/categories")
                            .header(AUTHORIZATION_HEADER, BEARER_PREFIX + accessToken)
                            .content(objectMapper.writeValueAsString(categoryRequest))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
            assertThat(exceptionResponse).isNotNull();
            assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            assertThat(exceptionResponse.getMessage()).isEqualTo("Sub category cannot have attributes.");
            assertThat(exceptionResponse.getPath()).isEqualTo("/categories");
        }

        @Test
        void save_shouldReturnBadRequest_whenParentCategoryIsNotRootInSubCategory() throws Exception {
            Category category = Category.builder()
                    .title("Sub category")
                    .categoryType(CategoryType.SUB)
                    .build();
            categoryRepository.save(category);
            CreateCategoryRequest categoryRequest = CreateCategoryRequestDataBuilder.buildCreateCategoryRequestWithAllFields()
                    .parentId(category.getId())
                    .attributes(null)
                    .categoryType(CategoryType.SUB)
                    .build();
            JwtPayload jwtPayload = jwtPayloadDataBuilder.buildValidUserJwtPayload().claims(buildClaimsWithRole(UserRole.ADMIN)).build();

            String accessToken = jwtService.generateAccessToken(jwtPayload);
            String content = mockMvc.perform(post("/categories")
                            .header(AUTHORIZATION_HEADER, BEARER_PREFIX + accessToken)
                            .content(objectMapper.writeValueAsString(categoryRequest))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
            assertThat(exceptionResponse).isNotNull();
            assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            assertThat(exceptionResponse.getMessage()).isEqualTo("Sub category should have root category as parent.");
            assertThat(exceptionResponse.getPath()).isEqualTo("/categories");
        }

        @Test
        void save_shouldReturnNotFound_whenParentCategoryNotFoundInSubCategory() throws Exception {
            CreateCategoryRequest categoryRequest = CreateCategoryRequestDataBuilder.buildCreateCategoryRequestWithAllFields()
                    .parentId(String.valueOf(UUID.randomUUID()))
                    .attributes(null)
                    .categoryType(CategoryType.SUB)
                    .build();
            JwtPayload jwtPayload = jwtPayloadDataBuilder.buildValidUserJwtPayload().claims(buildClaimsWithRole(UserRole.ADMIN)).build();

            String accessToken = jwtService.generateAccessToken(jwtPayload);
            String content = mockMvc.perform(post("/categories")
                            .header(AUTHORIZATION_HEADER, BEARER_PREFIX + accessToken)
                            .content(objectMapper.writeValueAsString(categoryRequest))
                            .contentType(MediaType.APPLICATION_JSON))
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
    class SaveChildCategoryTests {

        @Test
        void save_shouldSaveChildCategory() throws Exception {
            Category subCategory = Category.builder()
                    .title("Sub category")
                    .categoryType(CategoryType.SUB)
                    .build();
            categoryRepository.save(subCategory);
            CreateCategoryRequest categoryRequest = CreateCategoryRequestDataBuilder.buildCreateCategoryRequestWithAllFields()
                    .parentId(subCategory.getId())
                    .attributes(Map.of("attribute_name", AttributeValue.builder().type("string").build()))
                    .categoryType(CategoryType.CHILD)
                    .build();

            JwtPayload jwtPayload = jwtPayloadDataBuilder.buildValidUserJwtPayload().claims(buildClaimsWithRole(UserRole.ADMIN)).build();

            String accessToken = jwtService.generateAccessToken(jwtPayload);
            mockMvc.perform(post("/categories")
                            .header(AUTHORIZATION_HEADER, BEARER_PREFIX + accessToken)
                            .content(objectMapper.writeValueAsString(categoryRequest))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());

            Optional<Category> categoryOptional = categoryRepository.findByTitleIgnoreCase(categoryRequest.title());
            assertThat(categoryOptional).isPresent();
            assertThat(categoryOptional.get().getTitle()).isEqualTo(categoryRequest.title());
            assertThat(categoryOptional.get().getCategoryType()).isEqualTo(categoryRequest.categoryType());
            assertThat(categoryOptional.get().getParentId()).isEqualTo(subCategory.getId());
        }

        @Test
        void save_shouldReturnBadRequest_whenParentIdIsNotPresentInChildCategory() throws Exception {
            Category subCategory = Category.builder()
                    .title("Sub category")
                    .categoryType(CategoryType.SUB)
                    .build();
            categoryRepository.save(subCategory);
            CreateCategoryRequest categoryRequest = CreateCategoryRequestDataBuilder.buildCreateCategoryRequestWithAllFields()
                    .parentId(null)
                    .attributes(Map.of("attribute_name", AttributeValue.builder().type("string").build()))
                    .categoryType(CategoryType.CHILD)
                    .build();

            JwtPayload jwtPayload = jwtPayloadDataBuilder.buildValidUserJwtPayload().claims(buildClaimsWithRole(UserRole.ADMIN)).build();

            String accessToken = jwtService.generateAccessToken(jwtPayload);
            String content = mockMvc.perform(post("/categories")
                            .header(AUTHORIZATION_HEADER, BEARER_PREFIX + accessToken)
                            .content(objectMapper.writeValueAsString(categoryRequest))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
            assertThat(exceptionResponse).isNotNull();
            assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            assertThat(exceptionResponse.getMessage()).isEqualTo("Child category should have parent id.");
            assertThat(exceptionResponse.getPath()).isEqualTo("/categories");
        }

        @Test
        void save_shouldReturnBadRequest_whenAttributesAreNotPresentInChildCategory() throws Exception {
            Category subCategory = Category.builder()
                    .title("Sub category")
                    .categoryType(CategoryType.SUB)
                    .build();
            categoryRepository.save(subCategory);
            CreateCategoryRequest categoryRequest = CreateCategoryRequestDataBuilder.buildCreateCategoryRequestWithAllFields()
                    .parentId(subCategory.getId())
                    .attributes(null)
                    .categoryType(CategoryType.CHILD)
                    .build();

            JwtPayload jwtPayload = jwtPayloadDataBuilder.buildValidUserJwtPayload().claims(buildClaimsWithRole(UserRole.ADMIN)).build();

            String accessToken = jwtService.generateAccessToken(jwtPayload);
            String content = mockMvc.perform(post("/categories")
                            .header(AUTHORIZATION_HEADER, BEARER_PREFIX + accessToken)
                            .content(objectMapper.writeValueAsString(categoryRequest))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
            assertThat(exceptionResponse).isNotNull();
            assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            assertThat(exceptionResponse.getMessage()).isEqualTo("Child category should have attributes.");
            assertThat(exceptionResponse.getPath()).isEqualTo("/categories");
        }

        @Test
        void save_shouldReturnBadRequest_whenParentCategoryIsNotSubInChildCategory() throws Exception {
            Category subCategory = Category.builder()
                    .title("Child category")
                    .categoryType(CategoryType.CHILD)
                    .build();
            categoryRepository.save(subCategory);
            CreateCategoryRequest categoryRequest = CreateCategoryRequestDataBuilder.buildCreateCategoryRequestWithAllFields()
                    .parentId(subCategory.getId())
                    .attributes(Map.of("attribute_name", AttributeValue.builder().type("string").build()))
                    .categoryType(CategoryType.CHILD)
                    .build();

            JwtPayload jwtPayload = jwtPayloadDataBuilder.buildValidUserJwtPayload().claims(buildClaimsWithRole(UserRole.ADMIN)).build();

            String accessToken = jwtService.generateAccessToken(jwtPayload);
            String content = mockMvc.perform(post("/categories")
                            .header(AUTHORIZATION_HEADER, BEARER_PREFIX + accessToken)
                            .content(objectMapper.writeValueAsString(categoryRequest))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
            assertThat(exceptionResponse).isNotNull();
            assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            assertThat(exceptionResponse.getMessage()).isEqualTo("Child category should have sub category as parent.");
            assertThat(exceptionResponse.getPath()).isEqualTo("/categories");
        }

        @Test
        void save_shouldReturnNotFound_whenParentCategoryNotFoundInChildCategory() throws Exception {
            CreateCategoryRequest categoryRequest = CreateCategoryRequestDataBuilder.buildCreateCategoryRequestWithAllFields()
                    .parentId(String.valueOf(UUID.randomUUID()))
                    .attributes(Map.of("attribute_name", AttributeValue.builder().type("string").build()))
                    .categoryType(CategoryType.CHILD)
                    .build();
            JwtPayload jwtPayload = jwtPayloadDataBuilder.buildValidUserJwtPayload().claims(buildClaimsWithRole(UserRole.ADMIN)).build();

            String accessToken = jwtService.generateAccessToken(jwtPayload);
            String content = mockMvc.perform(post("/categories")
                            .header(AUTHORIZATION_HEADER, BEARER_PREFIX + accessToken)
                            .content(objectMapper.writeValueAsString(categoryRequest))
                            .contentType(MediaType.APPLICATION_JSON))
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
            JwtPayload jwtPayload = jwtPayloadDataBuilder.buildValidUserJwtPayload().claims(buildClaimsWithRole(UserRole.ADMIN)).build();

            String accessToken = jwtService.generateAccessToken(jwtPayload);
            String content = mockMvc.perform(get("/categories/{id}", category.getId())
                            .header(AUTHORIZATION_HEADER, BEARER_PREFIX + accessToken))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            CategoryResponse categoryResponse = objectMapper.readValue(content, CategoryResponse.class);
            assertThat(categoryResponse).isNotNull();
            assertThat(categoryResponse.title()).isEqualTo(category.getTitle());
            assertThat(categoryResponse.categoryType()).isEqualTo(category.getCategoryType());
            assertThat(categoryResponse.parentId()).isEqualTo(category.getParentId());
            assertThat(categoryResponse.attributes()).isNotNull();
            assertThat(categoryResponse.attributes().size()).isEqualTo(category.getAttributes().size());
            assertThat(categoryResponse.attributes().get("attribute_name")).isNotNull();

            AttributeValue responseAttributeName = categoryResponse.attributes().get("attribute_name");
            AttributeValue categoryAttributeName = category.getAttributes().get("attribute_name");
            assertThat(responseAttributeName.type()).isEqualTo(categoryAttributeName.type());
            assertThat(responseAttributeName.required()).isEqualTo(categoryAttributeName.required());
            assertThat(responseAttributeName.allowedValues()).isNotNull();
            assertThat(responseAttributeName.allowedValues().size()).isEqualTo(categoryAttributeName.allowedValues().size());
        }

        @Test
        void findById_shouldReturnNotFound_whenCategoryNotFound() throws Exception {
            JwtPayload jwtPayload = jwtPayloadDataBuilder.buildValidUserJwtPayload().claims(buildClaimsWithRole(UserRole.ADMIN)).build();
            String categoryId = String.valueOf(UUID.randomUUID());

            String accessToken = jwtService.generateAccessToken(jwtPayload);
            String content = mockMvc.perform(get("/categories/{id}", categoryId)
                            .header(AUTHORIZATION_HEADER, BEARER_PREFIX + accessToken))
                    .andExpect(status().isNotFound())
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
}
