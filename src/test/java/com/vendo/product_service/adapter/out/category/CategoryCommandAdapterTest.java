package com.vendo.product_service.adapter.out.category;

import com.vendo.product_service.adapter.model.category.MongoCategory;
import com.vendo.product_service.adapter.out.category.mapper.CategoryEntityMapper;
import com.vendo.product_service.adapter.out.category.repository.CategoryRepository;
import com.vendo.product_service.domain.category.model.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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
        MongoCategory categoryEntity = MongoCategory.builder()
                .id("cat123")
                .code("CODE")
                .title("Title")
                .build();

        when(categoryEntityMapper.toMongoEntity(category)).thenReturn(categoryEntity);

        commandAdapter.save(category);

        verify(categoryEntityMapper, times(1)).toMongoEntity(category);
        verify(categoryRepository, times(1)).save(categoryEntity);
    }
}