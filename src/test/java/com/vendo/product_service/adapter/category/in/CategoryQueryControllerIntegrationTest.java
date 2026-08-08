package com.vendo.product_service.adapter.category.in;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vendo.core_lib.utils.AssertionUtils;
import com.vendo.product_service.adapter.category.in.dto.CategoryResponse;
import com.vendo.product_service.adapter.category.in.dto.CategoryTreeResponse;
import com.vendo.product_service.domain.attribute.exception.AttributeNotFoundException;
import com.vendo.product_service.domain.attribute.model.Attribute;
import com.vendo.product_service.domain.category.exception.CategoryNotFoundException;
import com.vendo.product_service.domain.category.model.Category;
import com.vendo.product_service.domain.category.type.CategoryType;
import com.vendo.product_service.domain.user.User;
import com.vendo.product_service.port.attribute.AttributeQueryPort;
import com.vendo.product_service.port.category.CategoryCommandPort;
import com.vendo.product_service.port.category.CategoryQueryPort;
import com.vendo.product_service.test_utils.builder.AttributeDataBuilder;
import com.vendo.product_service.test_utils.builder.CategoryDataBuilder;
import com.vendo.product_service.test_utils.builder.CategoryResponseDataBuilder;
import com.vendo.product_service.test_utils.security.SecurityContextService;
import com.vendo.security_lib.exception.ExceptionResponse;
import com.vendo.user_lib.type.UserRole;
import com.vendo.user_lib.type.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CategoryQueryControllerIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private CacheManager cacheManager;

    @MockitoBean
    private CategoryCommandPort commandPort;
    @MockitoBean
    private CategoryQueryPort categoryQueryPort;
    @MockitoBean
    private CategoryCommandPort categoryCommandPort;
    @MockitoBean
    private AttributeQueryPort attributeQueryPort;

    @BeforeEach
    public void setUp() {
        SecurityContextHolder.clearContext();
        cacheManager.getCacheNames().forEach(name -> Objects.requireNonNull(cacheManager.getCache(name)).clear());
    }

    private ResultActions performCategoryGet(String categoryId) throws Exception {
        User user = new User("id", "email", UserStatus.ACTIVE, Set.of(UserRole.ADMIN), true);
        return mockMvc.perform(get("/categories/{id}", categoryId)
                .with(authentication(SecurityContextService.initializeAuth(user))));
    }

    private ResultActions performCategoryGetTree() throws Exception {
        return mockMvc.perform(get("/categories/tree"));
    }

    @Nested
    class FindCategoriesTests {

        @Test
        void findById_shouldReturnCategory() throws Exception {
            CategoryResponse response = CategoryResponseDataBuilder.withAllFields();
            Category category = CategoryDataBuilder.withChild()
                    .id(response.id())
                    .attributes(response.attributes())
                    .title(response.title())
                    .path(response.path())
                    .build();

            when(categoryQueryPort.findById(category.getId(), "Category not found.")).thenReturn(category);

            String content = performCategoryGet(category.getId())
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            CategoryResponse categoryResponse = objectMapper.readValue(content, CategoryResponse.class);

            AssertionUtils.assertFrom(category, categoryResponse, "type");
            assertThat(categoryResponse.type()).isNotNull();
            assertThat(categoryResponse.type()).isEqualTo(CategoryType.CHILD);

            verify(categoryQueryPort).findById(category.getId(), "Category not found.");
        }

        @Test
        void findById_shouldReturnNotFound_whenCategoryNotFound() throws Exception {
            String categoryId = String.valueOf(UUID.randomUUID());

            when(categoryQueryPort.findById(categoryId, "Category not found.")).thenThrow(new CategoryNotFoundException("Category not found."));

            String content = performCategoryGet(categoryId).andExpect(status().isNotFound())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
            assertThat(exceptionResponse).isNotNull();
            assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
            assertThat(exceptionResponse.getMessage()).isEqualTo("Category not found.");
            assertThat(exceptionResponse.getPath()).isEqualTo("/categories/%s".formatted(categoryId));

            verify(categoryQueryPort).findById(categoryId, "Category not found.");
        }
    }

    @Nested
    class TreeCategoriesTests {

        @Test
        void tree_shouldCategoriesTree() throws Exception {
            Attribute attribute1 = AttributeDataBuilder.withAllFields().id("1").build();
            Attribute attribute2 = AttributeDataBuilder.withAllFields().id("2").build();
            String parentId = String.valueOf(UUID.randomUUID()), childId = String.valueOf(UUID.randomUUID());

            Category parent = CategoryDataBuilder.withChild().parentId(null).path(List.of(parentId)).attributes(null).build();
            Category child = CategoryDataBuilder.withChild()
                    .parentId(parent.getId())
                    .attributes(List.of(attribute1.id(), attribute2.id()))
                    .path(List.of(parentId, childId))
                    .build();

            when(categoryQueryPort.findAll()).thenReturn(List.of(parent, child));
            when(attributeQueryPort.findAllByIds(child.getAttributes())).thenReturn(List.of(attribute1, attribute2));

            String response = performCategoryGetTree()
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            assertThat(response).isNotBlank();
            CategoryTreeResponse treeResponse = objectMapper.readValue(response, CategoryTreeResponse.class);

            assertThat(treeResponse).isNotNull();
            assertThat(treeResponse.getData()).isNotNull();
            assertThat(treeResponse.getData().size()).isEqualTo(1);

            CategoryTreeResponse.CategoryTree tree = treeResponse.getData().get(0);
            AssertionUtils.assertFrom(tree, parent, "children", "parentId", "attributes");

            assertThat(tree.attributes()).isEmpty();
            assertThat(tree.children()).isNotNull();
            assertThat(tree.children().size()).isEqualTo(1);
            assertThat(tree.type()).isNotNull();
            assertThat(tree.type()).isEqualTo(CategoryType.PARENT);

            CategoryTreeResponse.CategoryTree treeChild = tree.children().get(0);
            AssertionUtils.assertFrom(treeChild, child, "children", "parentId", "attributes");
            assertThat(treeChild.type()).isEqualTo(CategoryType.CHILD);

            assertThat(treeChild.attributes().stream().map(Attribute::id).toList()).containsAll(child.getAttributes());
        }

        @Test
        void tree_shouldReturnNotFound_whenOneAttributeIsMissing() throws Exception {
            Attribute attribute1 = AttributeDataBuilder.withAllFields().id("1").build();
            Attribute attribute2 = AttributeDataBuilder.withAllFields().id("2").build();
            Attribute attribute5 = AttributeDataBuilder.withAllFields().id("5").build();
            Attribute attribute8 = AttributeDataBuilder.withAllFields().id("8").build();
            Category category = CategoryDataBuilder.withChild().attributes(List.of(attribute1.id(), attribute5.id())).build();
            Category category1 = CategoryDataBuilder.withChild().attributes(List.of(attribute2.id(), attribute8.id())).build();
            List<String> attributes = Stream.concat(category.getAttributes().stream(), category1.getAttributes().stream()).toList();

            when(categoryQueryPort.findAll()).thenReturn(List.of(category, category1));
            when(attributeQueryPort.findAllByIds(attributes))
                    .thenThrow(new AttributeNotFoundException("Attribute not found by id: %s.".formatted(attribute1.id())));

            String response = performCategoryGetTree()
                    .andExpect(status().isNotFound())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            assertThat(response).isNotBlank();
            ExceptionResponse exceptionResponse = objectMapper.readValue(response, ExceptionResponse.class);
            assertThat(exceptionResponse).isNotNull();
            assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
            assertThat(exceptionResponse.getMessage()).isEqualTo("Attribute not found by id: %s.".formatted(attribute1.id()));
            assertThat(exceptionResponse.getPath()).isEqualTo("/categories/tree");

            verify(categoryQueryPort).findAll();
            verify(attributeQueryPort).findAllByIds(attributes);
        }

        @Test
        void tree_shouldReturnTree_whenCategoryHasNullAttributes() throws Exception {
            String parentId = String.valueOf(UUID.randomUUID());

            Category parent = CategoryDataBuilder.withChild().parentId(null).path(List.of(parentId)).attributes(null).build();

            when(categoryQueryPort.findAll()).thenReturn(List.of(parent));

            String response = performCategoryGetTree()
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            assertThat(response).isNotBlank();
            CategoryTreeResponse treeResponse = objectMapper.readValue(response, CategoryTreeResponse.class);

            assertThat(treeResponse).isNotNull();
            assertThat(treeResponse.getData()).isNotNull();
            assertThat(treeResponse.getData().size()).isEqualTo(1);

            CategoryTreeResponse.CategoryTree tree = treeResponse.getData().get(0);
            AssertionUtils.assertFrom(tree, parent, "children", "parentId", "attributes");

            assertThat(tree.attributes()).isNotNull();
            assertThat(tree.attributes()).isEmpty();
            assertThat(tree.children()).isNotNull();
            assertThat(tree.children()).isEmpty();
            assertThat(tree.type()).isNotNull();
            assertThat(tree.type()).isEqualTo(CategoryType.PARENT);

            verify(categoryQueryPort).findAll();
            verifyNoInteractions(attributeQueryPort);
        }
    }
}
