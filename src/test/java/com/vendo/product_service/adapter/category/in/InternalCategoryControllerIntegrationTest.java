package com.vendo.product_service.adapter.category.in;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vendo.core_lib.utils.AssertionUtils;
import com.vendo.product_service.adapter.category.in.dto.CategoryResponse;
import com.vendo.product_service.domain.category.exception.CategoryNotFoundException;
import com.vendo.product_service.domain.category.model.Category;
import com.vendo.product_service.domain.category.type.CategoryType;
import com.vendo.product_service.port.category.CategoryQueryPort;
import com.vendo.product_service.test_utils.builder.CategoryDataBuilder;
import com.vendo.product_service.test_utils.builder.TokenClaimsDataBuilder;
import com.vendo.product_service.test_utils.security.SecurityContextService;
import com.vendo.security_lib.exception.ExceptionResponse;
import com.vendo.security_starter.jwt.parser.TokenClaims;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class InternalCategoryControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private CategoryQueryPort categoryQueryPort;

    private ResultActions performGet(String categoryId) throws Exception {
        TokenClaims claims = TokenClaimsDataBuilder.buildWithAllFields().build();

        return mockMvc.perform(get("/internal/categories/{id}", categoryId)
                .with(authentication(SecurityContextService.initializeAuth(claims))));
    }

    @Test
    void find_shouldReturnCategory() throws Exception {
        Category category = CategoryDataBuilder.withChild().build();

        when(categoryQueryPort.findById(category.getId(), "Category not found.")).thenReturn(category);

        String content = performGet(category.getId())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        CategoryResponse response = objectMapper.readValue(content, CategoryResponse.class);

        AssertionUtils.assertFrom(category, response, "type");
        assertThat(response.type()).isEqualTo(CategoryType.CHILD);

        verify(categoryQueryPort).findById(category.getId(), "Category not found.");
    }

    @Test
    void find_shouldReturnCategory_whenNotChildType() throws Exception {
        Category category = CategoryDataBuilder.withParent().build();

        when(categoryQueryPort.findById(category.getId(), "Category not found.")).thenReturn(category);

        String content = performGet(category.getId())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        CategoryResponse response = objectMapper.readValue(content, CategoryResponse.class);

        assertThat(response.type()).isEqualTo(CategoryType.PARENT);

        verify(categoryQueryPort).findById(category.getId(), "Category not found.");
    }

    @Test
    void find_shouldReturnNotFound_whenCategoryNotFound() throws Exception {
        String categoryId = String.valueOf(UUID.randomUUID());

        when(categoryQueryPort.findById(categoryId, "Category not found.")).thenThrow(new CategoryNotFoundException("Category not found."));

        String content = performGet(categoryId)
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
        assertThat(exceptionResponse).isNotNull();
        assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(exceptionResponse.getMessage()).isEqualTo("Category not found.");
        assertThat(exceptionResponse.getPath()).isEqualTo("/internal/categories/%s".formatted(categoryId));

        verify(categoryQueryPort).findById(categoryId, "Category not found.");
    }

}
