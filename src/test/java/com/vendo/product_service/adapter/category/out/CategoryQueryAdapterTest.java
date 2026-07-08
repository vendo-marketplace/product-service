package com.vendo.product_service.adapter.category.out;

import com.vendo.product_service.adapter.category.out.mapper.MongoCategoryMapper;
import com.vendo.product_service.adapter.category.out.persistence.CategoryQueryAdapter;
import com.vendo.product_service.adapter.category.out.persistence.CategoryRepository;
import com.vendo.product_service.adapter.category.out.persistence.MongoCategory;
import com.vendo.product_service.domain.category.exception.CategoryNotFoundException;
import com.vendo.product_service.domain.category.model.Category;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryQueryAdapterTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private MongoCategoryMapper categoryMapper;

    @InjectMocks
    private CategoryQueryAdapter queryAdapter;

    @Test
    void findById_shouldReturnMappedCategory_whenEntityExists() {
        String id = "cat123";
        MongoCategory categoryEntity = MongoCategory.builder()
                .id("cat123")
                .slug("SLUG")
                .title("Title")
                .build();
        Category category = Category.builder().id(id).slug("SLUG").title("Title").build();

        when(categoryRepository.findById(id)).thenReturn(Optional.of(categoryEntity));
        when(categoryMapper.toCategory(categoryEntity)).thenReturn(category);

        Category result = queryAdapter.findById(id, "Some error message.");

        assertThat(result).isEqualTo(category);
        verify(categoryRepository, times(1)).findById(id);
        verify(categoryMapper, times(1)).toCategory(categoryEntity);
        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    void findById_shouldThrowExceptionWithCustomMessage_whenEntityNotFound() {
        String id = "cat123";
        String customMessage = "Some error message.";

        when(categoryRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> queryAdapter.findById(id, customMessage))
                .isInstanceOf(CategoryNotFoundException.class)
                .hasMessage(customMessage);

        verify(categoryRepository, times(1)).findById(id);
        verifyNoInteractions(categoryMapper);
        verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    void findById_shouldThrowExceptionWithDefaultMessage_whenEntityNotFound() {
        String id = "cat123";
        String defaultMessage = "Category not found.";

        when(categoryRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> queryAdapter.findById(id))
                .isInstanceOf(CategoryNotFoundException.class)
                .hasMessage(defaultMessage);

        verify(categoryRepository, times(1)).findById(id);
        verifyNoInteractions(categoryMapper);
        verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    void existsById_shouldReturnTrue_whenExists() {
        String id = "cat123";
        when(categoryRepository.existsById(id)).thenReturn(true);

        boolean exists = queryAdapter.existsById(id);

        assertThat(exists).isTrue();
        verify(categoryRepository, times(1)).existsById(id);
        verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    void existsById_shouldReturnFalse_whenNotExists() {
        String id = "cat123";
        when(categoryRepository.existsById(id)).thenReturn(false);

        boolean exists = queryAdapter.existsById(id);

        assertThat(exists).isFalse();
        verify(categoryRepository, times(1)).existsById(id);
        verifyNoMoreInteractions(categoryRepository);
    }
}