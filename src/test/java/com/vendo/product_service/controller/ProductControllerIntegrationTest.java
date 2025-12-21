package com.vendo.product_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vendo.common.exception.ExceptionResponse;
import com.vendo.domain.user.common.type.UserRole;
import com.vendo.domain.user.common.type.UserStatus;
import com.vendo.product_service.common.builder.*;
import com.vendo.product_service.common.dto.JwtPayload;
import com.vendo.product_service.domain.product.db.model.Product;
import com.vendo.product_service.domain.product.db.repository.ProductRepository;
import com.vendo.product_service.domain.category.common.type.CategoryType;
import com.vendo.product_service.domain.category.db.model.Category;
import com.vendo.product_service.domain.category.db.repository.CategoryRepository;
import com.vendo.product_service.service.JwtService;
import com.vendo.product_service.domain.product.web.dto.CreateProductRequest;
import com.vendo.product_service.domain.product.web.dto.ProductResponse;
import com.vendo.product_service.domain.product.web.dto.UpdateProductRequest;
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

import java.math.BigDecimal;
import java.util.List;
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
public class ProductControllerIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtPayloadDataBuilder jwtPayloadDataBuilder;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
    }

    @AfterTestClass
    void tearDown() {
        productRepository.deleteAll();
    }

    @Nested
    class SaveProductTests {

        @Test
        void save_shouldSaveProduct() throws Exception {
            String userId = String.valueOf(UUID.randomUUID());
            Map<String, Object> claims = jwtPayloadDataBuilder.buildUserClaims(userId, true, UserStatus.ACTIVE, UserRole.ADMIN);
            JwtPayload jwtPayload = jwtPayloadDataBuilder.buildValidJwtPayload().claims(claims).build();
            Category category = CategoryDataBuilder.buildCategoryWithAllFields().categoryType(CategoryType.CHILD).build();
            categoryRepository.save(category);
            CreateProductRequest createProductRequest = CreateProductRequestDataBuilder.buildCreateProductRequestWithRequiredFields()
                    .categoryId(category.getId())
                    .build();

            String accessToken = jwtService.generateAccessToken(jwtPayload);
            mockMvc.perform(post("/products")
                            .header(AUTHORIZATION_HEADER, BEARER_PREFIX + accessToken)
                            .content(objectMapper.writeValueAsString(createProductRequest))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());

            List<Product> products = productRepository.findAll();
            assertThat(products).isNotNull();
            assertThat(products.size()).isEqualTo(1);

            Product product = products.get(0);
            assertThat(product.getTitle()).isEqualTo(createProductRequest.title());
            assertThat(product.getDescription()).isEqualTo(createProductRequest.description());
            assertThat(product.getQuantity()).isEqualTo(createProductRequest.quantity());
            assertThat(product.getPrice()).isEqualTo(createProductRequest.price());
            assertThat(product.getOwnerId()).isEqualTo(userId);
            assertThat(product.getCategoryId()).isEqualTo(category.getId());
            assertThat(product.getAttributes()).isNotNull();
            assertThat(product.getAttributes().size()).isEqualTo(createProductRequest.attributes().size());
            assertThat(product.getAttributes()).isEqualTo(createProductRequest.attributes());
            assertThat(product.isActive()).isTrue();
            assertThat(product.getVersion()).isNotNull();
        }

        @Test
        void save_shouldReturnBadRequest_whenValidationFailed() throws Exception {
            String userId = String.valueOf(UUID.randomUUID());
            Map<String, Object> claims = jwtPayloadDataBuilder.buildUserClaims(userId, true, UserStatus.ACTIVE, UserRole.ADMIN);
            JwtPayload jwtPayload = jwtPayloadDataBuilder.buildValidJwtPayload().claims(claims).build();
            CreateProductRequest createProductRequest = CreateProductRequestDataBuilder.buildCreateProductRequestWithRequiredFields()
                    .title(null)
                    .description(null)
                    .quantity(-1)
                    .price(null)
                    .categoryId(null)
                    .attributes(null)
                    .build();

            String accessToken = jwtService.generateAccessToken(jwtPayload);
            String content = mockMvc.perform(post("/products")
                            .header(AUTHORIZATION_HEADER, BEARER_PREFIX + accessToken)
                            .content(objectMapper.writeValueAsString(createProductRequest))
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
            assertThat(exceptionResponse.getErrors().size()).isEqualTo(6);
            assertThat(exceptionResponse.getPath()).isEqualTo("/products");
        }

        @Test
        void save_shouldReturnNotFound_whenCategoryNotFound() throws Exception {
            String userId = String.valueOf(UUID.randomUUID());
            Map<String, Object> claims = jwtPayloadDataBuilder.buildUserClaims(userId, true, UserStatus.ACTIVE, UserRole.ADMIN);
            JwtPayload jwtPayload = jwtPayloadDataBuilder.buildValidJwtPayload().claims(claims).build();
            CreateProductRequest createProductRequest = CreateProductRequestDataBuilder.buildCreateProductRequestWithRequiredFields()
                    .categoryId(String.valueOf(UUID.randomUUID()))
                    .build();

            String accessToken = jwtService.generateAccessToken(jwtPayload);
            String content = mockMvc.perform(post("/products")
                            .header(AUTHORIZATION_HEADER, BEARER_PREFIX + accessToken)
                            .content(objectMapper.writeValueAsString(createProductRequest))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
            assertThat(exceptionResponse).isNotNull();
            assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
            assertThat(exceptionResponse.getMessage()).isEqualTo("Category not found.");
            assertThat(exceptionResponse.getPath()).isEqualTo("/products");
        }

        @Test
        void save_shouldReturnBadRequest_whenCategoryTypeIsNotChild() throws Exception {
            String userId = String.valueOf(UUID.randomUUID());
            Map<String, Object> claims = jwtPayloadDataBuilder.buildUserClaims(userId, true, UserStatus.ACTIVE, UserRole.ADMIN);
            JwtPayload jwtPayload = jwtPayloadDataBuilder.buildValidJwtPayload().claims(claims).build();
            Category category = CategoryDataBuilder.buildCategoryWithAllFields().categoryType(CategoryType.SUB).build();
            categoryRepository.save(category);
            CreateProductRequest createProductRequest = CreateProductRequestDataBuilder.buildCreateProductRequestWithRequiredFields()
                    .categoryId(category.getId())
                    .build();

            String accessToken = jwtService.generateAccessToken(jwtPayload);
            String content = mockMvc.perform(post("/products")
                            .header(AUTHORIZATION_HEADER, BEARER_PREFIX + accessToken)
                            .content(objectMapper.writeValueAsString(createProductRequest))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString();

            ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
            assertThat(exceptionResponse).isNotNull();
            assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            assertThat(exceptionResponse.getMessage()).isEqualTo("Incorrect category type. Expected CHILD but was SUB.");
            assertThat(exceptionResponse.getPath()).isEqualTo("/products");

            List<Product> products = productRepository.findAll();
            assertThat(products).isNotNull();
            assertThat(products.size()).isEqualTo(0);
        }
    }

    @Nested
    class UpdateProductTests {

        @Test
        void update_shouldUpdateProduct() throws Exception {
            String userId = String.valueOf(UUID.randomUUID());
            Map<String, Object> claims = jwtPayloadDataBuilder.buildUserClaims(userId, true, UserStatus.ACTIVE, UserRole.USER);
            JwtPayload jwtPayload = jwtPayloadDataBuilder.buildValidJwtPayload().claims(claims).build();
            Category category = CategoryDataBuilder.buildCategoryWithAllFields().build();
            categoryRepository.save(category);
            UpdateProductRequest updateProductRequest = UpdateProductRequestDataBuilder.buildUpdateProductRequestWithAllFields()
                    .title("New title")
                    .description("New description")
                    .quantity(0)
                    .price(BigDecimal.ZERO)
                    .categoryId(category.getId())
                    .attributes(Map.of("new_attribute_name", List.of("new_attribute_value")))
                    .active(false)
                    .build();
            Product product = ProductDataBuilder.buildProductWithRequiredFields().ownerId(userId).build();
            productRepository.save(product);

            String accessToken = jwtService.generateAccessToken(jwtPayload);
            mockMvc.perform(put("/products/{id}", product.getId())
                    .header(AUTHORIZATION_HEADER, BEARER_PREFIX + accessToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateProductRequest)))
                    .andExpect(status().isOk());

            Optional<Product> optionalProduct = productRepository.findById(product.getId());
            assertThat(optionalProduct).isPresent();
            Product responseProduct = optionalProduct.get();
            assertThat(responseProduct.getTitle()).isEqualTo(updateProductRequest.title());
            assertThat(responseProduct.getDescription()).isEqualTo(updateProductRequest.description());
            assertThat(responseProduct.getQuantity()).isEqualTo(updateProductRequest.quantity());
            assertThat(responseProduct.getPrice()).isEqualTo(updateProductRequest.price());
            assertThat(responseProduct.getCategoryId()).isEqualTo(updateProductRequest.categoryId());
            assertThat(responseProduct.getAttributes()).isNotNull();
            assertThat(responseProduct.getAttributes().size()).isEqualTo(1);
            assertThat(responseProduct.getAttributes()).isEqualTo(updateProductRequest.attributes());
            assertThat(optionalProduct.get().isActive()).isEqualTo(updateProductRequest.active());
        }

        @Test
        void update_returnNotFound_whenProductNotFound() throws Exception {
            String userId = String.valueOf(UUID.randomUUID());
            String productId = String.valueOf(UUID.randomUUID());
            Map<String, Object> claims = jwtPayloadDataBuilder.buildUserClaims(userId, true, UserStatus.ACTIVE, UserRole.USER);
            JwtPayload jwtPayload = jwtPayloadDataBuilder.buildValidJwtPayload().claims(claims).build();
            Category category = CategoryDataBuilder.buildCategoryWithAllFields().build();
            categoryRepository.save(category);
            UpdateProductRequest updateProductRequest = UpdateProductRequestDataBuilder.buildUpdateProductRequestWithAllFields()
                    .title("New title")
                    .description("New description")
                    .quantity(0)
                    .price(BigDecimal.ZERO)
                    .categoryId(category.getId())
                    .attributes(Map.of("new_attribute_name", List.of("new_attribute_value")))
                    .active(false)
                    .build();

            String accessToken = jwtService.generateAccessToken(jwtPayload);
            String content = mockMvc.perform(put("/products/{id}", productId)
                            .header(AUTHORIZATION_HEADER, BEARER_PREFIX + accessToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateProductRequest)))
                    .andExpect(status().isNotFound())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
            assertThat(exceptionResponse).isNotNull();
            assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
            assertThat(exceptionResponse.getMessage()).isEqualTo("Product not found.");
            assertThat(exceptionResponse.getPath()).isEqualTo("/products/" + productId);
        }

        @Test
        void update_shouldReturnForbidden_whenAuthenticatedUserIsNotOwner() throws Exception {
            String userId = String.valueOf(UUID.randomUUID());
            Map<String, Object> claims = jwtPayloadDataBuilder.buildUserClaims(userId, true, UserStatus.ACTIVE, UserRole.USER);
            JwtPayload jwtPayload = jwtPayloadDataBuilder.buildValidJwtPayload().claims(claims).build();
            Category category = CategoryDataBuilder.buildCategoryWithAllFields().build();
            categoryRepository.save(category);
            UpdateProductRequest updateProductRequest = UpdateProductRequestDataBuilder.buildUpdateProductRequestWithAllFields()
                    .title("New title")
                    .description("New description")
                    .quantity(0)
                    .price(BigDecimal.ZERO)
                    .categoryId(category.getId())
                    .attributes(Map.of("new_attribute_name", List.of("new_attribute_value")))
                    .active(false)
                    .build();
            Product product = ProductDataBuilder.buildProductWithRequiredFields().ownerId(String.valueOf(UUID.randomUUID())).build();
            productRepository.save(product);

            String accessToken = jwtService.generateAccessToken(jwtPayload);
            String content = mockMvc.perform(put("/products/{id}", product.getId())
                            .header(AUTHORIZATION_HEADER, BEARER_PREFIX + accessToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateProductRequest)))
                    .andExpect(status().isForbidden())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
            assertThat(exceptionResponse).isNotNull();
            assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
            assertThat(exceptionResponse.getMessage()).isEqualTo("Only owner can edit its product.");
            assertThat(exceptionResponse.getPath()).isEqualTo("/products/" + product.getId());
        }
    }

    @Nested
    class FindProductTests {

        @Test
        void findById_shouldReturnProduct() throws Exception {
            String userId = String.valueOf(UUID.randomUUID());
            Map<String, Object> claims = jwtPayloadDataBuilder.buildUserClaims(userId, true, UserStatus.ACTIVE, UserRole.USER);
            JwtPayload jwtPayload = jwtPayloadDataBuilder.buildValidJwtPayload().claims(claims).build();
            Product product = ProductDataBuilder.buildProductWithRequiredFields().ownerId(userId).build();
            productRepository.save(product);

            String accessToken = jwtService.generateAccessToken(jwtPayload);
            String content = mockMvc.perform(get("/products/{id}", product.getId())
                            .header(AUTHORIZATION_HEADER, BEARER_PREFIX + accessToken))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            ProductResponse productResponse = objectMapper.readValue(content, ProductResponse.class);
            assertThat(productResponse).isNotNull();
            assertThat(productResponse.getTitle()).isEqualTo(product.getTitle());
            assertThat(productResponse.getDescription()).isEqualTo(product.getDescription());
            assertThat(productResponse.getQuantity()).isEqualTo(product.getQuantity());
            assertThat(productResponse.getPrice()).isEqualTo(product.getPrice());
            assertThat(productResponse.getOwnerId()).isEqualTo(product.getOwnerId());
            assertThat(productResponse.getCategoryId()).isEqualTo(product.getCategoryId());
            assertThat(productResponse.getAttributes()).isNotNull();
            assertThat(productResponse.getAttributes().size()).isEqualTo(1);
            assertThat(productResponse.getAttributes()).isEqualTo(product.getAttributes());
            assertThat(productResponse.isActive()).isTrue();
        }

        @Test
        void findById_shouldReturnNotFound_whenProductNotFound() throws Exception {
            String userId = String.valueOf(UUID.randomUUID());
            String productId = String.valueOf(UUID.randomUUID());
            Map<String, Object> claims = jwtPayloadDataBuilder.buildUserClaims(userId, true, UserStatus.ACTIVE, UserRole.USER);
            JwtPayload jwtPayload = jwtPayloadDataBuilder.buildValidJwtPayload().claims(claims).build();

            String accessToken = jwtService.generateAccessToken(jwtPayload);
            String content = mockMvc.perform(get("/products/{id}", productId)
                            .header(AUTHORIZATION_HEADER, BEARER_PREFIX + accessToken))
                    .andExpect(status().isNotFound())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
            assertThat(exceptionResponse).isNotNull();
            assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
            assertThat(exceptionResponse.getMessage()).isEqualTo("Product not found.");
            assertThat(exceptionResponse.getPath()).isEqualTo("/products/" + productId);
        }
    }
}
