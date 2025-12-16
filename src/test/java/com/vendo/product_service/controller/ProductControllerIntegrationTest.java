package com.vendo.product_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vendo.domain.user.common.type.UserRole;
import com.vendo.domain.user.common.type.UserStatus;
import com.vendo.product_service.common.builder.CategoryDataBuilder;
import com.vendo.product_service.common.builder.CreateProductRequestDataBuilder;
import com.vendo.product_service.common.builder.JwtPayloadDataBuilder;
import com.vendo.product_service.common.dto.JwtPayload;
import com.vendo.product_service.db.model.Product;
import com.vendo.product_service.db.repository.ProductRepository;
import com.vendo.product_service.domain.category.common.type.CategoryType;
import com.vendo.product_service.domain.category.db.model.Category;
import com.vendo.product_service.domain.category.db.repository.CategoryRepository;
import com.vendo.product_service.service.JwtService;
import com.vendo.product_service.web.dto.CreateProductRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.event.annotation.AfterTestClass;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.vendo.product_service.common.builder.JwtPayloadDataBuilder.buildClaimsWithRole;
import static com.vendo.security.common.constants.AuthConstants.AUTHORIZATION_HEADER;
import static com.vendo.security.common.constants.AuthConstants.BEARER_PREFIX;
import static com.vendo.security.common.type.TokenClaim.*;
import static com.vendo.security.common.type.TokenClaim.ROLES_CLAIM;
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
            Category category = CategoryDataBuilder.buildCategoryWithAllFields().categoryType(CategoryType.CHILD).build();
            String userId = String.valueOf(UUID.randomUUID());
            Map<String, Object> claims = JwtPayloadDataBuilder.buildUserClaims(userId, true, UserStatus.ACTIVE, UserRole.ADMIN);
            JwtPayload jwtPayload = jwtPayloadDataBuilder.buildValidUserJwtPayload().claims(claims).build();
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
            assertThat(product.getAttributes().size()).isEqualTo(category.getAttributes().size());
            assertThat(product.isActive()).isTrue();
            assertThat(product.getVersion()).isNotNull();
        }

        @Test
        void save_shouldReturnBadRequest_whenValidationFailed() {

        }

        @Test
        void save_shouldReturnForbidden_userNotAuthorized() {

        }

        @Test
        void save_shouldReturnBadRequest_whenCategoryTypeIsNotChild() {

        }
    }

}
