package com.vendo.product_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vendo.domain.user.common.type.UserRole;
import com.vendo.product_service.common.builder.CreateCategoryRequestDataBuilder;
import com.vendo.product_service.common.dto.JwtPayload;
import com.vendo.product_service.domain.category.db.model.Category;
import com.vendo.product_service.domain.category.db.repository.CategoryRepository;
import com.vendo.product_service.domain.category.web.dto.CreateCategoryRequest;
import com.vendo.product_service.service.JwtService;
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

import java.util.Map;
import java.util.Optional;

import static com.vendo.security.common.constants.AuthConstants.AUTHORIZATION_HEADER;
import static com.vendo.security.common.constants.AuthConstants.BEARER_PREFIX;
import static com.vendo.security.common.type.TokenClaim.ROLES_CLAIM;
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
        void save_shouldSaveRootCategory() throws Exception {
            CreateCategoryRequest categoryRequest = CreateCategoryRequestDataBuilder.buildCreateCategoryRequestWithAllFields().build();
            JwtPayload jwtPayload = JwtPayload.builder()
                    .claims(Map.of(ROLES_CLAIM.getClaim(), UserRole.ADMIN))
                    .build();

            String accessToken = jwtService.generateAccessToken(jwtPayload);
            mockMvc.perform(post("/categories")
                            .header(AUTHORIZATION_HEADER, BEARER_PREFIX + accessToken)
                            .content(objectMapper.writeValueAsString(categoryRequest))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());

            Optional<Category> categoryOptional = categoryRepository.findByTitleIgnoreCase(categoryRequest.title());
            assertThat(categoryOptional).isPresent();
            assertThat(categoryOptional.get().getTitle()).isEqualTo(categoryRequest.title());
        }

        @Test
        void save_shouldReturnConflict_whenCategoryAlreadyExists() {

        }

        @Test
        void save_shouldReturnBadRequest_whenSavingRootCategoryWithParentId() {

        }

        @Test
        void save_shouldReturnBadRequest_whenSavingRootCategoryWithAttributes() {

        }

    }

}
