package com.vendo.product_service.adapter.out.category;

import com.vendo.product_service.adapter.model.category.CategoryEntity;
import com.vendo.product_service.adapter.out.category.mapper.CategoryEntityMapper;
import com.vendo.product_service.adapter.out.category.repository.CategoryRepository;
import com.vendo.product_service.domain.category.model.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import static org.mockito.Mockito.*;

class CategoryCommandAdapterTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryEntityMapper categoryEntityMapper;

    @InjectMocks
    private CategoryCommandAdapter commandAdapter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void save_shouldMapAndSaveCategory() {
        Category category = Category.builder().id("cat123").code("CODE").title("Title").build();
        CategoryEntity categoryEntity = CategoryEntity.builder()
                .id("cat123")
                .code("CODE")
                .title("Title")
                .build();

        when(categoryEntityMapper.toCategoryEntityFromCategoryDomain(category)).thenReturn(categoryEntity);

        commandAdapter.save(category);

        verify(categoryEntityMapper, times(1)).toCategoryEntityFromCategoryDomain(category);
        verify(categoryRepository, times(1)).save(categoryEntity);
    }
}