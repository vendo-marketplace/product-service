package com.vendo.product_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vendo.common.exception.ExceptionResponse;
import com.vendo.domain.user.common.type.UserRole;
import com.vendo.domain.user.common.type.UserStatus;
import com.vendo.product_service.common.builder.*;
import com.vendo.product_service.common.dto.JwtPayload;
import com.vendo.product_service.domain.category.db.model.embedded.AttributeDefinition;
import com.vendo.product_service.domain.category.db.model.embedded.AttributeType;
import com.vendo.product_service.domain.product.db.model.Product;
import com.vendo.product_service.domain.product.db.repository.ProductRepository;
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
import org.springframework.test.web.servlet.ResultActions;

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

            Category category = CategoryDataBuilder.buildCategoryWithAllFields().build();
            categoryRepository.save(category);
            CreateProductRequest createProductRequest = CreateProductRequestDataBuilder.buildCreateProductRequestWithRequiredFields()
                    .categoryId(category.getId())
                    .build();

            performProductPersist(createProductRequest, jwtPayload).andExpect(status().isOk());

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
            assertThat(product.getCreatedAt()).isNotNull();
            assertThat(product.getUpdatedAt()).isNotNull();
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

            String content = performProductPersist(createProductRequest, jwtPayload)
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

            String content = performProductPersist(createProductRequest, jwtPayload).andExpect(status().isNotFound())
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
        void save_shouldReturnBadRequest_whenCategoryIsNotChild() throws Exception {
            String userId = String.valueOf(UUID.randomUUID());
            Map<String, Object> claims = jwtPayloadDataBuilder.buildUserClaims(userId, true, UserStatus.ACTIVE, UserRole.ADMIN);
            JwtPayload jwtPayload = jwtPayloadDataBuilder.buildValidJwtPayload().claims(claims).build();
            Category category = CategoryDataBuilder.buildCategoryWithAllFields().attributes(null).build();
            categoryRepository.save(category);
            CreateProductRequest createProductRequest = CreateProductRequestDataBuilder.buildCreateProductRequestWithRequiredFields()
                    .categoryId(category.getId())
                    .build();

            String content = performProductPersist(createProductRequest, jwtPayload)
                    .andExpect(status().isBadRequest())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
            assertThat(exceptionResponse).isNotNull();
            assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            assertThat(exceptionResponse.getMessage()).isEqualTo("Category type should be child.");
            assertThat(exceptionResponse.getPath()).isEqualTo("/products");

            List<Product> products = productRepository.findAll();
            assertThat(products).isNotNull();
            assertThat(products.size()).isEqualTo(0);
        }

        @Nested
        class SaveProductWithAttributes {

            @Nested
            class SaveProductWithNumberAttribute {
                @Test
                void save_shouldReturnBadRequest_whenAttributeNameIsNotValid() throws Exception {
                    String userId = String.valueOf(UUID.randomUUID());
                    String validAttributeName = "Number", invalidAttributeName = "number";
                    Map<String, Object> claims = jwtPayloadDataBuilder.buildUserClaims(userId, true, UserStatus.ACTIVE, UserRole.ADMIN);
                    JwtPayload jwtPayload = jwtPayloadDataBuilder.buildValidJwtPayload().claims(claims).build();

                    Category category = CategoryDataBuilder.buildCategoryWithAllFields()
                            .attributes(Map.of(validAttributeName, AttributeDefinition.builder().type(AttributeType.NUMBER).build()))
                            .build();
                    categoryRepository.save(category);
                    CreateProductRequest createProductRequest = CreateProductRequestDataBuilder.buildCreateProductRequestWithRequiredFields()
                            .categoryId(category.getId())
                            .attributes(Map.of(invalidAttributeName, List.of("1")))
                            .build();

                    String content = performProductPersist(createProductRequest, jwtPayload)
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
                }

                @Test
                void save_shouldSaveProductWithNumberAttribute() throws Exception {
                    String userId = String.valueOf(UUID.randomUUID());
                    String validAttributeName = "Number";
                    Map<String, Object> claims = jwtPayloadDataBuilder.buildUserClaims(userId, true, UserStatus.ACTIVE, UserRole.ADMIN);
                    JwtPayload jwtPayload = jwtPayloadDataBuilder.buildValidJwtPayload().claims(claims).build();

                    Category category = CategoryDataBuilder.buildCategoryWithAllFields()
                            .attributes(Map.of(validAttributeName, AttributeDefinition.builder().type(AttributeType.NUMBER).build()))
                            .build();
                    categoryRepository.save(category);
                    CreateProductRequest createProductRequest = CreateProductRequestDataBuilder.buildCreateProductRequestWithRequiredFields()
                            .categoryId(category.getId())
                            .attributes(Map.of(validAttributeName, List.of("1")))
                            .build();

                    performProductPersist(createProductRequest, jwtPayload).andExpect(status().isOk());
                }

                @Test
                void save_shouldReturnBadRequest_whenNumberAttributeIsMoreThanOne() throws Exception {
                    String userId = String.valueOf(UUID.randomUUID());
                    String validAttributeName = "Number";
                    Map<String, Object> claims = jwtPayloadDataBuilder.buildUserClaims(userId, true, UserStatus.ACTIVE, UserRole.ADMIN);
                    JwtPayload jwtPayload = jwtPayloadDataBuilder.buildValidJwtPayload().claims(claims).build();

                    Category category = CategoryDataBuilder.buildCategoryWithAllFields()
                            .attributes(Map.of(validAttributeName, AttributeDefinition.builder().type(AttributeType.NUMBER).build()))
                            .build();
                    categoryRepository.save(category);
                    CreateProductRequest createProductRequest = CreateProductRequestDataBuilder.buildCreateProductRequestWithRequiredFields()
                            .categoryId(category.getId())
                            .attributes(Map.of(validAttributeName, List.of("1", "2")))
                            .build();

                    String content = performProductPersist(createProductRequest, jwtPayload)
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
                    assertThat(exceptionResponse.getErrors().get(validAttributeName)).isEqualTo("Must contain exactly one value.");
                    assertThat(exceptionResponse.getPath()).isEqualTo("/products");
                }

                @Test
                void save_shouldReturnBadRequest_whenNumberAttributeIsNegative() throws Exception {
                    String userId = String.valueOf(UUID.randomUUID());
                    String validAttributeName = "Number";
                    Map<String, Object> claims = jwtPayloadDataBuilder.buildUserClaims(userId, true, UserStatus.ACTIVE, UserRole.ADMIN);
                    JwtPayload jwtPayload = jwtPayloadDataBuilder.buildValidJwtPayload().claims(claims).build();

                    Category category = CategoryDataBuilder.buildCategoryWithAllFields()
                            .attributes(Map.of(validAttributeName, AttributeDefinition.builder().type(AttributeType.NUMBER).build()))
                            .build();
                    categoryRepository.save(category);
                    CreateProductRequest createProductRequest = CreateProductRequestDataBuilder.buildCreateProductRequestWithRequiredFields()
                            .categoryId(category.getId())
                            .attributes(Map.of(validAttributeName, List.of("-1")))
                            .build();

                    String content = performProductPersist(createProductRequest, jwtPayload)
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
                    assertThat(exceptionResponse.getErrors().get(validAttributeName)).isEqualTo("Must be equal or greater than zero.");
                    assertThat(exceptionResponse.getPath()).isEqualTo("/products");
                }

                @Test
                void save_shouldReturnBadRequest_whenNumberAttributeIsNotNumeric() throws Exception {
                    String userId = String.valueOf(UUID.randomUUID());
                    String validAttributeName = "Number";
                    Map<String, Object> claims = jwtPayloadDataBuilder.buildUserClaims(userId, true, UserStatus.ACTIVE, UserRole.ADMIN);
                    JwtPayload jwtPayload = jwtPayloadDataBuilder.buildValidJwtPayload().claims(claims).build();

                    Category category = CategoryDataBuilder.buildCategoryWithAllFields()
                            .attributes(Map.of(validAttributeName, AttributeDefinition.builder().type(AttributeType.NUMBER).build()))
                            .build();
                    categoryRepository.save(category);
                    CreateProductRequest createProductRequest = CreateProductRequestDataBuilder.buildCreateProductRequestWithRequiredFields()
                            .categoryId(category.getId())
                            .attributes(Map.of(validAttributeName, List.of("not_numeric")))
                            .build();

                    String content = performProductPersist(createProductRequest, jwtPayload)
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
                    assertThat(exceptionResponse.getErrors().get(validAttributeName)).isEqualTo("Invalid numeric value.");
                    assertThat(exceptionResponse.getPath()).isEqualTo("/products");
                }

                @Test
                void save_shouldReturnBadRequest_whenNumberAttributeIsHigherThanIntegerMaxValue() throws Exception {
                    String userId = String.valueOf(UUID.randomUUID());
                    String validAttributeName = "Number";
                    Map<String, Object> claims = jwtPayloadDataBuilder.buildUserClaims(userId, true, UserStatus.ACTIVE, UserRole.ADMIN);
                    JwtPayload jwtPayload = jwtPayloadDataBuilder.buildValidJwtPayload().claims(claims).build();

                    Category category = CategoryDataBuilder.buildCategoryWithAllFields()
                            .attributes(Map.of(validAttributeName, AttributeDefinition.builder().type(AttributeType.NUMBER).build()))
                            .build();
                    categoryRepository.save(category);
                    CreateProductRequest createProductRequest = CreateProductRequestDataBuilder.buildCreateProductRequestWithRequiredFields()
                            .categoryId(category.getId())
                            .attributes(Map.of(validAttributeName, List.of("1_000_000_000_000")))
                            .build();

                    String content = performProductPersist(createProductRequest, jwtPayload)
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
                    assertThat(exceptionResponse.getErrors().get(validAttributeName)).isEqualTo("Invalid numeric value.");
                    assertThat(exceptionResponse.getPath()).isEqualTo("/products");
                }
            }

            @Nested
            class SaveProductWithBooleanAttribute {

                @Test
                void save_shouldSaveProductWithBooleanAttribute() throws Exception {
                    String userId = String.valueOf(UUID.randomUUID());

                    Map<String, Object> claims = jwtPayloadDataBuilder.buildUserClaims(userId, true, UserStatus.ACTIVE, UserRole.ADMIN);
                    JwtPayload jwtPayload = jwtPayloadDataBuilder.buildValidJwtPayload().claims(claims).build();

                    Category category = CategoryDataBuilder.buildCategoryWithAllFields()
                            .attributes(Map.of("Boolean", AttributeDefinition.builder().type(AttributeType.BOOLEAN).build()))
                            .build();
                    categoryRepository.save(category);
                    CreateProductRequest createProductRequest = CreateProductRequestDataBuilder.buildCreateProductRequestWithRequiredFields()
                            .categoryId(category.getId())
                            .attributes(Map.of("Boolean", List.of("true")))
                            .build();

                    performProductPersist(createProductRequest, jwtPayload).andExpect(status().isOk());

                    List<Product> products = productRepository.findAll();
                    assertThat(products).isNotNull();
                    assertThat(products.size()).isEqualTo(1);
                    assertThat(products.get(0).getCategoryId()).isEqualTo(category.getId());
                    assertThat(products.get(0).getAttributes()).isNotNull();
                    assertThat(products.get(0).getAttributes().size()).isEqualTo(1);
                    assertThat(products.get(0).getAttributes().get("Boolean")).isNotNull();
                    assertThat(products.get(0).getAttributes().get("Boolean").size()).isEqualTo(1);
                    assertThat(products.get(0).getAttributes().get("Boolean").get(0)).isEqualTo("true");
                }

                @Test
                void save_shouldSaveProduct_whenBooleanAttributeIsMoreThanOne() throws Exception {
                    String userId = String.valueOf(UUID.randomUUID());
                    Map<String, Object> claims = jwtPayloadDataBuilder.buildUserClaims(userId, true, UserStatus.ACTIVE, UserRole.ADMIN);
                    JwtPayload jwtPayload = jwtPayloadDataBuilder.buildValidJwtPayload().claims(claims).build();

                    Category category = CategoryDataBuilder.buildCategoryWithAllFields()
                            .attributes(Map.of("Boolean", AttributeDefinition.builder().type(AttributeType.BOOLEAN).build()))
                            .build();
                    categoryRepository.save(category);
                    CreateProductRequest createProductRequest = CreateProductRequestDataBuilder.buildCreateProductRequestWithRequiredFields()
                            .categoryId(category.getId())
                            .attributes(Map.of("Boolean", List.of("true", "false")))
                            .build();

                    String content = performProductPersist(createProductRequest, jwtPayload)
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
                    assertThat(exceptionResponse.getErrors().get("Boolean")).isEqualTo("Must contain exactly one value.");
                    assertThat(exceptionResponse.getPath()).isEqualTo("/products");
                }

                @Test
                void save_shouldSaveProduct_whenInvalidBooleanAttributeValue() throws Exception {
                    String userId = String.valueOf(UUID.randomUUID());
                    Map<String, Object> claims = jwtPayloadDataBuilder.buildUserClaims(userId, true, UserStatus.ACTIVE, UserRole.ADMIN);
                    JwtPayload jwtPayload = jwtPayloadDataBuilder.buildValidJwtPayload().claims(claims).build();

                    Category category = CategoryDataBuilder.buildCategoryWithAllFields()
                            .attributes(Map.of("Boolean", AttributeDefinition.builder().type(AttributeType.BOOLEAN).build()))
                            .build();
                    categoryRepository.save(category);
                    CreateProductRequest createProductRequest = CreateProductRequestDataBuilder.buildCreateProductRequestWithRequiredFields()
                            .categoryId(category.getId())
                            .attributes(Map.of("Boolean", List.of("not_boolean_value")))
                            .build();

                    String content = performProductPersist(createProductRequest, jwtPayload)
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
                    assertThat(exceptionResponse.getErrors().get("Boolean")).isEqualTo("Invalid boolean value. Allowed values: true, false.");
                    assertThat(exceptionResponse.getPath()).isEqualTo("/products");
                }

            }

            @Nested
            class SaveProductWithEnumAttribute {

                @Test
                void save_shouldSaveProductWithEnumAttribute() throws Exception {
                    String userId = String.valueOf(UUID.randomUUID());
                    String validAttributeName = "Enum";
                    List<String> allowedValues = List.of("TYPE1", "TYPE2");

                    Map<String, Object> claims = jwtPayloadDataBuilder.buildUserClaims(userId, true, UserStatus.ACTIVE, UserRole.ADMIN);
                    JwtPayload jwtPayload = jwtPayloadDataBuilder.buildValidJwtPayload().claims(claims).build();

                    Category category = CategoryDataBuilder.buildCategoryWithAllFields()
                            .attributes(Map.of(validAttributeName, AttributeDefinition.builder().type(AttributeType.ENUM).allowedValues(allowedValues).build()))
                            .build();
                    categoryRepository.save(category);
                    CreateProductRequest createProductRequest = CreateProductRequestDataBuilder.buildCreateProductRequestWithRequiredFields()
                            .categoryId(category.getId())
                            .attributes(Map.of(validAttributeName, allowedValues))
                            .build();

                    String content = performProductPersist(createProductRequest, jwtPayload)
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
                    assertThat(exceptionResponse.getErrors().get(validAttributeName)).isEqualTo("Must contain exactly one value.");
                    assertThat(exceptionResponse.getPath()).isEqualTo("/products");
                }

                @Test
                void save_shouldReturnBadRequest_whenAttributeIsMoreThanOne() throws Exception {
                    String userId = String.valueOf(UUID.randomUUID());
                    String validAttributeName = "Enum";

                    Map<String, Object> claims = jwtPayloadDataBuilder.buildUserClaims(userId, true, UserStatus.ACTIVE, UserRole.ADMIN);
                    JwtPayload jwtPayload = jwtPayloadDataBuilder.buildValidJwtPayload().claims(claims).build();

                    Category category = CategoryDataBuilder.buildCategoryWithAllFields()
                            .attributes(Map.of(validAttributeName, AttributeDefinition.builder().type(AttributeType.ENUM).allowedValues(List.of("TYPE1", "TYPE2")).build()))
                            .build();
                    categoryRepository.save(category);
                    CreateProductRequest createProductRequest = CreateProductRequestDataBuilder.buildCreateProductRequestWithRequiredFields()
                            .categoryId(category.getId())
                            .attributes(Map.of(validAttributeName, List.of("TYPE1", "TYPE2")))
                            .build();

                    String content = performProductPersist(createProductRequest, jwtPayload)
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
                    assertThat(exceptionResponse.getErrors().get(validAttributeName)).isEqualTo("Must contain exactly one value.");
                    assertThat(exceptionResponse.getPath()).isEqualTo("/products");
                }

                @Test
                void save_shouldReturnBadRequest_whenAttributeValueIsNotAllowed() throws Exception {
                    String userId = String.valueOf(UUID.randomUUID());
                    String validAttributeName = "Enum";
                    List<String> allowedValues = List.of("TYPE1", "TYPE2");

                    Map<String, Object> claims = jwtPayloadDataBuilder.buildUserClaims(userId, true, UserStatus.ACTIVE, UserRole.ADMIN);
                    JwtPayload jwtPayload = jwtPayloadDataBuilder.buildValidJwtPayload().claims(claims).build();

                    Category category = CategoryDataBuilder.buildCategoryWithAllFields()
                            .attributes(Map.of(validAttributeName, AttributeDefinition.builder().type(AttributeType.ENUM).allowedValues(allowedValues).build()))
                            .build();
                    categoryRepository.save(category);
                    CreateProductRequest createProductRequest = CreateProductRequestDataBuilder.buildCreateProductRequestWithRequiredFields()
                            .categoryId(category.getId())
                            .attributes(Map.of(validAttributeName, List.of("TYPE3")))
                            .build();

                    String content = performProductPersist(createProductRequest, jwtPayload)
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
                    assertThat(exceptionResponse.getErrors().get(validAttributeName)).isEqualTo("Invalid value. Allowed values: " + String.join(", ", allowedValues));
                    assertThat(exceptionResponse.getPath()).isEqualTo("/products");
                }
            }

            @Nested
            class SaveProductWithRangeAttribute {

                @Test
                void save_shouldSaveProductWithRangeAttribute() throws Exception {
                    String userId = String.valueOf(UUID.randomUUID());
                    String validAttributeName = "Range";

                    Map<String, Object> claims = jwtPayloadDataBuilder.buildUserClaims(userId, true, UserStatus.ACTIVE, UserRole.ADMIN);
                    JwtPayload jwtPayload = jwtPayloadDataBuilder.buildValidJwtPayload().claims(claims).build();

                    Category category = CategoryDataBuilder.buildCategoryWithAllFields()
                            .attributes(Map.of(validAttributeName, AttributeDefinition.builder().type(AttributeType.RANGE).build()))
                            .build();
                    categoryRepository.save(category);
                    CreateProductRequest createProductRequest = CreateProductRequestDataBuilder.buildCreateProductRequestWithRequiredFields()
                            .categoryId(category.getId())
                            .attributes(Map.of(validAttributeName, List.of("0", "100")))
                            .build();

                    performProductPersist(createProductRequest, jwtPayload).andExpect(status().isOk());

                    List<Product> products = productRepository.findAll();
                    assertThat(products).isNotNull();
                    assertThat(products.size()).isEqualTo(1);
                    assertThat(products.get(0).getCategoryId()).isEqualTo(category.getId());
                    assertThat(products.get(0).getAttributes()).isNotNull();
                    assertThat(products.get(0).getAttributes().size()).isEqualTo(1);
                    assertThat(products.get(0).getAttributes().get(validAttributeName)).isNotNull();
                    assertThat(products.get(0).getAttributes().get(validAttributeName).size()).isEqualTo(2);
                    assertThat(products.get(0).getAttributes().get(validAttributeName).get(0)).isEqualTo("0");
                    assertThat(products.get(0).getAttributes().get(validAttributeName).get(1)).isEqualTo("100");
                }

                @Test
                void save_shouldReturnBadRequest_whenAttributesAreMoreThanTwo() throws Exception {
                    String userId = String.valueOf(UUID.randomUUID());
                    String validAttributeName = "Range";

                    Map<String, Object> claims = jwtPayloadDataBuilder.buildUserClaims(userId, true, UserStatus.ACTIVE, UserRole.ADMIN);
                    JwtPayload jwtPayload = jwtPayloadDataBuilder.buildValidJwtPayload().claims(claims).build();

                    Category category = CategoryDataBuilder.buildCategoryWithAllFields()
                            .attributes(Map.of(validAttributeName, AttributeDefinition.builder().type(AttributeType.RANGE).build()))
                            .build();
                    categoryRepository.save(category);
                    CreateProductRequest createProductRequest = CreateProductRequestDataBuilder.buildCreateProductRequestWithRequiredFields()
                            .categoryId(category.getId())
                            .attributes(Map.of(validAttributeName, List.of("0", "100", "200")))
                            .build();

                    String content = performProductPersist(createProductRequest, jwtPayload)
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
                    assertThat(exceptionResponse.getErrors().get(validAttributeName)).isEqualTo("Must contain exactly two values.");
                    assertThat(exceptionResponse.getPath()).isEqualTo("/products");
                }

                @Test
                void save_shouldReturnBadRequest_whenFirstAttributeValueIsLowerThanZero() throws Exception {
                    String userId = String.valueOf(UUID.randomUUID());
                    String validAttributeName = "Range";

                    Map<String, Object> claims = jwtPayloadDataBuilder.buildUserClaims(userId, true, UserStatus.ACTIVE, UserRole.ADMIN);
                    JwtPayload jwtPayload = jwtPayloadDataBuilder.buildValidJwtPayload().claims(claims).build();

                    Category category = CategoryDataBuilder.buildCategoryWithAllFields()
                            .attributes(Map.of(validAttributeName, AttributeDefinition.builder().type(AttributeType.RANGE).build()))
                            .build();
                    categoryRepository.save(category);
                    CreateProductRequest createProductRequest = CreateProductRequestDataBuilder.buildCreateProductRequestWithRequiredFields()
                            .categoryId(category.getId())
                            .attributes(Map.of(validAttributeName, List.of("-1", "100")))
                            .build();

                    String content = performProductPersist(createProductRequest, jwtPayload)
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
                    assertThat(exceptionResponse.getErrors().get(validAttributeName)).isEqualTo("The first value must be greater than or equal to zero.");
                    assertThat(exceptionResponse.getPath()).isEqualTo("/products");
                }

                @Test
                void save_shouldReturnBadRequest_whenFirstAttributeValueIsGreaterThanSecond() throws Exception {
                    String userId = String.valueOf(UUID.randomUUID());
                    String validAttributeName = "Range";

                    Map<String, Object> claims = jwtPayloadDataBuilder.buildUserClaims(userId, true, UserStatus.ACTIVE, UserRole.ADMIN);
                    JwtPayload jwtPayload = jwtPayloadDataBuilder.buildValidJwtPayload().claims(claims).build();

                    Category category = CategoryDataBuilder.buildCategoryWithAllFields()
                            .attributes(Map.of(validAttributeName, AttributeDefinition.builder().type(AttributeType.RANGE).build()))
                            .build();
                    categoryRepository.save(category);
                    CreateProductRequest createProductRequest = CreateProductRequestDataBuilder.buildCreateProductRequestWithRequiredFields()
                            .categoryId(category.getId())
                            .attributes(Map.of(validAttributeName, List.of("100", "100")))
                            .build();

                    String content = performProductPersist(createProductRequest, jwtPayload)
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
                    assertThat(exceptionResponse.getErrors().get(validAttributeName)).isEqualTo("The first value must be less than the second value.");
                    assertThat(exceptionResponse.getPath()).isEqualTo("/products");
                }

                @Test
                void save_shouldReturnBadRequest_whenAttributesAreNotNumericType() throws Exception {
                    String userId = String.valueOf(UUID.randomUUID());
                    String validAttributeName = "Range";

                    Map<String, Object> claims = jwtPayloadDataBuilder.buildUserClaims(userId, true, UserStatus.ACTIVE, UserRole.ADMIN);
                    JwtPayload jwtPayload = jwtPayloadDataBuilder.buildValidJwtPayload().claims(claims).build();

                    Category category = CategoryDataBuilder.buildCategoryWithAllFields()
                            .attributes(Map.of(validAttributeName, AttributeDefinition.builder().type(AttributeType.RANGE).build()))
                            .build();
                    categoryRepository.save(category);
                    CreateProductRequest createProductRequest = CreateProductRequestDataBuilder.buildCreateProductRequestWithRequiredFields()
                            .categoryId(category.getId())
                            .attributes(Map.of(validAttributeName, List.of("not_numeric", "not_numeric")))
                            .build();

                    String content = performProductPersist(createProductRequest, jwtPayload)
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
                    assertThat(exceptionResponse.getErrors().get(validAttributeName)).isEqualTo("Invalid range value format.");
                    assertThat(exceptionResponse.getPath()).isEqualTo("/products");
                }
            }

            @Nested
            class SaveProductWithStringAttribute {

                @Test
                void save_shouldSaveProductWithStringAttribute() throws Exception {
                    String userId = String.valueOf(UUID.randomUUID());
                    String validAttributeName = "String";

                    Map<String, Object> claims = jwtPayloadDataBuilder.buildUserClaims(userId, true, UserStatus.ACTIVE, UserRole.ADMIN);
                    JwtPayload jwtPayload = jwtPayloadDataBuilder.buildValidJwtPayload().claims(claims).build();

                    Category category = CategoryDataBuilder.buildCategoryWithAllFields()
                            .attributes(Map.of(validAttributeName, AttributeDefinition.builder().type(AttributeType.STRING).build()))
                            .build();
                    categoryRepository.save(category);
                    CreateProductRequest createProductRequest = CreateProductRequestDataBuilder.buildCreateProductRequestWithRequiredFields()
                            .categoryId(category.getId())
                            .attributes(Map.of(validAttributeName, List.of("string")))
                            .build();

                    performProductPersist(createProductRequest, jwtPayload).andExpect(status().isOk());

                    List<Product> products = productRepository.findAll();
                    assertThat(products).isNotNull();
                    assertThat(products.size()).isEqualTo(1);
                    assertThat(products.get(0).getCategoryId()).isEqualTo(category.getId());
                    assertThat(products.get(0).getAttributes()).isNotNull();
                    assertThat(products.get(0).getAttributes().size()).isEqualTo(1);
                    assertThat(products.get(0).getAttributes().get(validAttributeName)).isNotNull();
                    assertThat(products.get(0).getAttributes().get(validAttributeName).size()).isEqualTo(1);
                    assertThat(products.get(0).getAttributes().get(validAttributeName).get(0)).isEqualTo("string");
                }

                @Test
                void save_shouldReturnBadRequest_whenAttributeIsBlank() throws Exception {
                    String userId = String.valueOf(UUID.randomUUID());
                    String validAttributeName = "String";

                    Map<String, Object> claims = jwtPayloadDataBuilder.buildUserClaims(userId, true, UserStatus.ACTIVE, UserRole.ADMIN);
                    JwtPayload jwtPayload = jwtPayloadDataBuilder.buildValidJwtPayload().claims(claims).build();

                    Category category = CategoryDataBuilder.buildCategoryWithAllFields()
                            .attributes(Map.of(validAttributeName, AttributeDefinition.builder().type(AttributeType.STRING).build()))
                            .build();
                    categoryRepository.save(category);
                    CreateProductRequest createProductRequest = CreateProductRequestDataBuilder.buildCreateProductRequestWithRequiredFields()
                            .categoryId(category.getId())
                            .attributes(Map.of(validAttributeName, List.of("")))
                            .build();

                    String content = performProductPersist(createProductRequest, jwtPayload)
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
                    assertThat(exceptionResponse.getErrors().get(validAttributeName)).isEqualTo("Must not be empty.");
                    assertThat(exceptionResponse.getPath()).isEqualTo("/products");
                }

            }
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

            performProductUpdate(product.getId(), updateProductRequest, jwtPayload).andExpect(status().isOk());

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

            String content = performProductUpdate(productId, updateProductRequest, jwtPayload)
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

            String content = performProductUpdate(product.getId(), updateProductRequest, jwtPayload)
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

            String content = performProductGet(product.getId(), jwtPayload)
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

            String content = performProductGet(productId, jwtPayload)
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

    private ResultActions performProductPersist(CreateProductRequest createProductRequest, JwtPayload jwtPayload) throws Exception {
        String accessToken = jwtService.generateAccessToken(jwtPayload);
        return mockMvc.perform(post("/products")
                .header(AUTHORIZATION_HEADER, BEARER_PREFIX + accessToken)
                .content(objectMapper.writeValueAsString(createProductRequest))
                .contentType(MediaType.APPLICATION_JSON));
    }

    private ResultActions performProductUpdate(String productId, UpdateProductRequest updateProductRequest, JwtPayload jwtPayload) throws Exception {
        String accessToken = jwtService.generateAccessToken(jwtPayload);
        return mockMvc.perform(put("/products/{id}", productId)
                .header(AUTHORIZATION_HEADER, BEARER_PREFIX + accessToken)
                .content(objectMapper.writeValueAsString(updateProductRequest))
                .contentType(MediaType.APPLICATION_JSON));
    }

    private ResultActions performProductGet(String productId, JwtPayload jwtPayload) throws Exception {
        String accessToken = jwtService.generateAccessToken(jwtPayload);
        return mockMvc.perform(get("/products/{id}", productId)
                .header(AUTHORIZATION_HEADER, BEARER_PREFIX + accessToken)
                .contentType(MediaType.APPLICATION_JSON));
    }

}
