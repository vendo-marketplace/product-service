package com.vendo.product_service.adapter.product.in;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vendo.product_service.adapter.product.in.dto.CompareAttributeResponse;
import com.vendo.product_service.domain.attribute.exception.AttributeNotFoundException;
import com.vendo.product_service.domain.attribute.model.Attribute;
import com.vendo.product_service.domain.attribute.model.AttributeType;
import com.vendo.product_service.domain.attribute.model.AttributeValue;
import com.vendo.product_service.domain.category.model.Category;
import com.vendo.product_service.domain.product.exception.ProductNotFoundException;
import com.vendo.product_service.domain.product.model.Product;
import com.vendo.product_service.port.attribute.AttributeQueryPort;
import com.vendo.product_service.port.category.CategoryQueryPort;
import com.vendo.product_service.port.product.ProductQueryPort;
import com.vendo.product_service.test_utils.builder.AttributeDataBuilder;
import com.vendo.product_service.test_utils.builder.CategoryDataBuilder;
import com.vendo.product_service.test_utils.builder.ProductDataBuilder;
import com.vendo.security_lib.exception.ExceptionResponse;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CompareProductControllerIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductQueryPort productQueryPort;
    @MockitoBean
    private CategoryQueryPort categoryQueryPort;
    @MockitoBean
    private AttributeQueryPort attributeQueryPort;

    private ResultActions performCompare(String categoryId, List<String> productIds) throws Exception {
        var requestBuilder = get("/products/compare")
                .contentType(MediaType.APPLICATION_JSON)
                .param("categoryId", categoryId);
        for (String productId : productIds) {
            requestBuilder.param("productIds", productId);
        }
        return mockMvc.perform(requestBuilder);
    }

    @Nested
    class CompareProductTests {

        @Test
        void compare_shouldReturnComparedProductAttributes_whenDifferent() throws Exception {
            Attribute attribute = AttributeDataBuilder.withAllFields().id("attr_1").title("Color").build();
            Category category = CategoryDataBuilder.withAllFields().id("ctgr_1").attributes(List.of(attribute.id())).build();

            Product product1 = ProductDataBuilder.withAllFields()
                    .id("prod_1").categoryId(category.getId())
                    .attributes(List.of(new AttributeValue(attribute.id(), List.of("Red"))))
                    .build();
            Product product2 = ProductDataBuilder.withAllFields()
                    .id("prod_2").categoryId(category.getId())
                    .attributes(List.of(new AttributeValue(attribute.id(), List.of("Blue"))))
                    .build();

            when(categoryQueryPort.findById(category.getId())).thenReturn(category);
            when(productQueryPort.requireAllByIds(List.of(product1.getId(), product2.getId()))).thenReturn(List.of(product1, product2));
            when(attributeQueryPort.findAllByIds(List.of(attribute.id()))).thenReturn(List.of(attribute));

            String content = performCompare(category.getId(), List.of(product1.getId(), product2.getId()))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            List<CompareAttributeResponse> responses = objectMapper.readValue(content, new TypeReference<>() {});
            assertThat(responses).isNotNull();
            assertThat(responses.size()).isEqualTo(1);

            CompareAttributeResponse response = responses.get(0);
            assertThat(response.id()).isEqualTo(attribute.id());
            assertThat(response.title()).isEqualTo(attribute.title());
            assertThat(response.same()).isFalse();
            assertThat(response.values().get(0)).isEqualTo(List.of("Red"));
            assertThat(response.values().get(1)).isEqualTo(List.of("Blue"));

            verify(categoryQueryPort).findById(category.getId());
            verify(productQueryPort).requireAllByIds(List.of("prod_1", "prod_2"));
            verify(attributeQueryPort).findAllByIds(List.of(attribute.id()));
        }

        @Test
        void compare_shouldReturnValuesInRequestedProductOrder_regardlessOfRepositoryOrder() throws Exception {
            Attribute attribute = AttributeDataBuilder.withAllFields().id("attr_1").title("Color").build();
            Category category = CategoryDataBuilder.withAllFields().id("cat_1").attributes(List.of(attribute.id())).build();

            Product product1 = ProductDataBuilder.withAllFields()
                    .id("prod_1").categoryId(category.getId())
                    .attributes(List.of(new AttributeValue(attribute.id(), List.of("Red"))))
                    .build();
            Product product2 = ProductDataBuilder.withAllFields()
                    .id("prod_2").categoryId(category.getId())
                    .attributes(List.of(new AttributeValue(attribute.id(), List.of("Green"))))
                    .build();
            Product product3 = ProductDataBuilder.withAllFields()
                    .id("prod_3").categoryId(category.getId())
                    .attributes(List.of(new AttributeValue(attribute.id(), List.of("Blue"))))
                    .build();

            List<String> requestedOrder = List.of("prod_3", "prod_1", "prod_2");

            when(categoryQueryPort.findById(category.getId())).thenReturn(category);
            when(productQueryPort.requireAllByIds(requestedOrder)).thenReturn(List.of(product3, product1, product2));
            when(attributeQueryPort.findAllByIds(List.of(attribute.id()))).thenReturn(List.of(attribute));

            String content = performCompare(category.getId(), requestedOrder)
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            List<CompareAttributeResponse> responses = objectMapper.readValue(content, new TypeReference<>() {});
            List<List<String>> values = responses.get(0).values();

            assertThat(values).isEqualTo(List.of(List.of("Blue"), List.of("Red"), List.of("Green")));
        }

        @Test
        void compare_shouldReturnComparedProductAttributes_whenSame() throws Exception {
            Attribute attribute = AttributeDataBuilder.withAllFields().id("attr_1").title("Color").build();
            Category category = CategoryDataBuilder.withAllFields().id("cat_1").attributes(List.of(attribute.id())).build();

            Product product1 = ProductDataBuilder.withAllFields()
                    .id("prod_1").categoryId(category.getId())
                    .attributes(List.of(new AttributeValue(attribute.id(), List.of("Red"))))
                    .build();
            Product product2 = ProductDataBuilder.withAllFields()
                    .id("prod_2").categoryId(category.getId())
                    .attributes(List.of(new AttributeValue(attribute.id(), List.of("Red"))))
                    .build();

            when(categoryQueryPort.findById(category.getId())).thenReturn(category);
            when(productQueryPort.requireAllByIds(List.of("prod_1", "prod_2"))).thenReturn(List.of(product1, product2));
            when(attributeQueryPort.findAllByIds(List.of(attribute.id()))).thenReturn(List.of(attribute));

            String content = performCompare(category.getId(), List.of("prod_1", "prod_2"))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            List<CompareAttributeResponse> responses = objectMapper.readValue(content, new TypeReference<>() {});
            assertThat(responses.get(0).same()).isTrue();
        }

        @Test
        void compare_shouldReturnEmptyList_whenCategoryHasNotAttributes() throws Exception {
            Category category = CategoryDataBuilder.withAllFields().id("cat_1").attributes(null).build();

            Product product1 = ProductDataBuilder.withAllFields().id("prod_1").categoryId(category.getId()).build();
            Product product2 = ProductDataBuilder.withAllFields().id("prod_2").categoryId(category.getId()).build();

            when(categoryQueryPort.findById(category.getId())).thenReturn(category);
            when(productQueryPort.requireAllByIds(List.of("prod_1", "prod_2"))).thenReturn(List.of(product1, product2));

            String content = performCompare(category.getId(), List.of("prod_1", "prod_2"))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            List<CompareAttributeResponse> responses = objectMapper.readValue(content, new TypeReference<>() {});
            assertThat(responses.size()).isEqualTo(0);

            verifyNoInteractions(attributeQueryPort);
        }

        @Test
        void compare_shouldReturnEmptyValues_whenProductMissingAttribute() throws Exception {
            Attribute attribute = new Attribute("attr_1", "Color", "color", AttributeType.STRING, false, null);
            Category category = CategoryDataBuilder.withAllFields().id("cat_1").attributes(List.of(attribute.id())).build();

            Product product1 = ProductDataBuilder.withAllFields()
                    .id("prod_1").categoryId(category.getId())
                    .attributes(List.of(new AttributeValue(attribute.id(), List.of("Red"))))
                    .build();
            Product product2 = ProductDataBuilder.withAllFields()
                    .id("prod_2").categoryId(category.getId())
                    .attributes(List.of())
                    .build();

            when(categoryQueryPort.findById(category.getId())).thenReturn(category);
            when(productQueryPort.requireAllByIds(List.of("prod_1", "prod_2"))).thenReturn(List.of(product1, product2));
            when(attributeQueryPort.findAllByIds(List.of(attribute.id()))).thenReturn(List.of(attribute));

            String content = performCompare(category.getId(), List.of("prod_1", "prod_2"))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            List<CompareAttributeResponse> responses = objectMapper.readValue(content, new TypeReference<>() {});
            CompareAttributeResponse response = responses.get(0);
            assertThat(response.same()).isFalse();
            assertThat(response.values().get(0)).isEqualTo(List.of("Red"));
            assertThat(response.values().get(1)).isEqualTo(List.of());
        }

        @Test
        void compare_shouldReturnNotFound_whenProductNotFound() throws Exception {
            Category category = CategoryDataBuilder.withAllFields().id("cat_1").build();

            when(categoryQueryPort.findById(category.getId())).thenReturn(category);
            when(productQueryPort.requireAllByIds(List.of("prod_1", "prod_2")))
                    .thenThrow(new ProductNotFoundException("Product not found by id: prod_2."));

            String content = performCompare(category.getId(), List.of("prod_1", "prod_2"))
                    .andExpect(status().isNotFound())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            ExceptionResponse response = objectMapper.readValue(content, ExceptionResponse.class);
            assertThat(response.getCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
            assertThat(response.getMessage()).isEqualTo("Product not found by id: prod_2.");
        }

        @Test
        void compare_shouldReturnNotFound_whenProductDoNotBelongToCategory() throws Exception {
            Attribute attribute = AttributeDataBuilder.withAllFields().id("attr_1").build();
            Category category = CategoryDataBuilder.withAllFields().id("cat_1").attributes(List.of(attribute.id())).build();

            Product product1 = ProductDataBuilder.withAllFields().id("prod_1").categoryId(category.getId()).build();
            Product product2 = ProductDataBuilder.withAllFields().id("prod_2").categoryId("different_category").build();

            when(categoryQueryPort.findById(category.getId())).thenReturn(category);
            when(productQueryPort.requireAllByIds(List.of("prod_1", "prod_2"))).thenReturn(List.of(product1, product2));

            String content = performCompare(category.getId(), List.of("prod_1", "prod_2"))
                    .andExpect(status().isNotFound())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            ExceptionResponse response = objectMapper.readValue(content, ExceptionResponse.class);
            assertThat(response.getCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
            assertThat(response.getMessage()).isEqualTo("Some products do not belong to the specified category.");
        }

        @Test
        void compare_shouldReturnNotFound_whenCategoryNotFound() throws Exception {
            when(categoryQueryPort.findById("cat_1"))
                    .thenThrow(new ProductNotFoundException("Category not found."));

            String content = performCompare("cat_1", List.of("prod_1", "prod_2"))
                    .andExpect(status().isNotFound())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            ExceptionResponse response = objectMapper.readValue(content, ExceptionResponse.class);
            assertThat(response.getCode()).isEqualTo(HttpStatus.NOT_FOUND.value());

            verifyNoInteractions(productQueryPort, attributeQueryPort);
        }

        @Test
        void compare_shouldReturnNotFound_whenAttributeNotFound() throws Exception {
            Category category = CategoryDataBuilder.withAllFields().id("cat_1").attributes(List.of("attr_1")).build();

            Product product1 = ProductDataBuilder.withAllFields().id("prod_1").categoryId(category.getId()).build();
            Product product2 = ProductDataBuilder.withAllFields().id("prod_2").categoryId(category.getId()).build();

            when(categoryQueryPort.findById(category.getId())).thenReturn(category);
            when(productQueryPort.requireAllByIds(List.of("prod_1", "prod_2"))).thenReturn(List.of(product1, product2));
            when(attributeQueryPort.findAllByIds(List.of("attr_1")))
                    .thenThrow(new AttributeNotFoundException("Attribute not found by id: attr_1."));

            String content = performCompare(category.getId(), List.of("prod_1", "prod_2"))
                    .andExpect(status().isNotFound())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            ExceptionResponse response = objectMapper.readValue(content, ExceptionResponse.class);
            assertThat(response.getCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
            assertThat(response.getMessage()).isEqualTo("Attribute not found by id: attr_1.");
        }

        @Test
        void compare_shouldReturnBadRequest_whenCategoryIdIsNull() throws Exception {
            mockMvc.perform(get("/products/compare")
                    .param("productIds", "prod_1", "prod_2")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(categoryQueryPort, productQueryPort, attributeQueryPort);
        }

        @Test
        void compare_shouldReturnBadRequest_whenProductIdsAreEmpty() throws Exception {
            mockMvc.perform(get("/products/compare")
                    .param("categoryId", "cat_1")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(categoryQueryPort, productQueryPort, attributeQueryPort);
        }

        @Test
        void compare_shouldNotRequireAuthentication() throws Exception {
            Category category = CategoryDataBuilder.withAllFields().id("cat_1").attributes(List.of()).build();

            when(categoryQueryPort.findById(category.getId())).thenReturn(category);
            when(productQueryPort.requireAllByIds(List.of("prod_1"))).thenReturn(
                    List.of(ProductDataBuilder.withAllFields().id("prod_1").categoryId(category.getId()).build())
            );

            performCompare(category.getId(), List.of("prod_1"))
                    .andExpect(status().isOk());
        }
    }
}
