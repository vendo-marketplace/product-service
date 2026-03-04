package com.vendo.product_service.adapter.category.out;


import com.vendo.product_service.adapter.category.out.mapper.MongoCategoryMapper;
import com.vendo.product_service.adapter.category.out.persistence.CategoryCommandAdapter;
import com.vendo.product_service.adapter.category.out.persistence.CategoryRepository;
import com.vendo.product_service.adapter.category.out.persistence.MongoCategory;
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
    private MongoCategoryMapper categoryMapper;

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

        when(categoryMapper.toMongoEntity(category)).thenReturn(categoryEntity);

        commandAdapter.save(category);

        verify(categoryMapper, times(1)).toMongoEntity(category);
        verify(categoryRepository, times(1)).save(categoryEntity);
    }
}