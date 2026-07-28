package com.vendo.product_service.adapter.category.out.persistence;


import com.vendo.product_service.adapter.category.out.mapper.MongoCategoryMapper;
import com.vendo.product_service.domain.category.exception.CategoryAlreadyExistsException;
import com.vendo.product_service.domain.category.model.Category;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;

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
        Category category = Category.builder().id("cat123").slug("SLUG").title("Title").build();
        MongoCategory categoryEntity = MongoCategory.builder()
                .id("cat123")
                .slug("SLUG")
                .title("Title")
                .build();

        when(categoryMapper.toEntity(category)).thenReturn(categoryEntity);

        commandAdapter.save(category);

        verify(categoryMapper).toEntity(category);
        verify(categoryRepository).save(categoryEntity);
        verifyNoMoreInteractions(categoryMapper, categoryRepository);
    }

    @Test
    void save_shouldThrowException_whenCategoryAlreadyExistsBySlug() {
        Category category = Category.builder().id("cat123").slug("SLUG").title("Title").build();
        MongoCategory categoryEntity = MongoCategory.builder()
                .id("cat123")
                .slug("SLUG")
                .title("Title")
                .build();

        when(categoryMapper.toEntity(category)).thenReturn(categoryEntity);
        when(categoryRepository.save(categoryEntity)).thenThrow(new DuplicateKeyException("exception message"));

        assertThrows(CategoryAlreadyExistsException.class, () -> commandAdapter.save(category));

        verify(categoryMapper).toEntity(category);
        verify(categoryRepository).save(categoryEntity);

        verifyNoMoreInteractions(categoryMapper, categoryRepository);
    }

}