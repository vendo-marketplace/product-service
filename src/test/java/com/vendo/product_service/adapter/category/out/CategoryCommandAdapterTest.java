package com.vendo.product_service.adapter.category.out;


import com.vendo.product_service.adapter.category.out.mapper.MongoCategoryMapper;
import com.vendo.product_service.adapter.category.out.persistence.CategoryCommandAdapter;
import com.vendo.product_service.adapter.category.out.persistence.CategoryRepository;
import com.vendo.product_service.adapter.category.out.persistence.MongoCategory;
import com.vendo.product_service.domain.category.model.Category;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryCommandAdapterTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private MongoCategoryMapper categoryMapper;

    @InjectMocks
    private CategoryCommandAdapter commandAdapter;

    @Test
    void save_shouldMapAndSaveCategory() {
        Category category = Category.builder().id("cat123").code("CODE").title("Title").build();
        MongoCategory categoryEntity = MongoCategory.builder()
                .id("cat123")
                .code("CODE")
                .title("Title")
                .build();

        when(categoryMapper.toEntity(category)).thenReturn(categoryEntity);

        commandAdapter.save(category);

        verify(categoryMapper, times(1)).toEntity(category);
        verify(categoryRepository, times(1)).save(categoryEntity);
        verifyNoMoreInteractions(categoryMapper, categoryRepository);
    }

    @Test
    void save_shouldPropagateException_whenRepositoryFails() {
        Category category = Category.builder().id("cat123").code("CODE").title("Title").build();
        MongoCategory categoryEntity = MongoCategory.builder()
                .id("cat123")
                .code("CODE")
                .title("Title")
                .build();

        RuntimeException dbException = new RuntimeException("Database error.");

        when(categoryMapper.toEntity(category)).thenReturn(categoryEntity);
        when(categoryRepository.save(categoryEntity)).thenThrow(dbException);

        assertThrows(RuntimeException.class, () -> commandAdapter.save(category));

        verify(categoryMapper, times(1)).toEntity(category);
        verify(categoryRepository, times(1)).save(categoryEntity);

        verifyNoMoreInteractions(categoryMapper, categoryRepository);
    }

}