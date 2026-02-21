package com.vendo.product_service.adapter.out.category;

import com.vendo.product_service.adapter.model.category.MongoCategory;
import com.vendo.product_service.adapter.out.category.mapper.CategoryEntityMapper;
import com.vendo.product_service.adapter.out.category.repository.CategoryRepository;
import com.vendo.product_service.domain.category.exception.CategoryNotFoundException;
import com.vendo.product_service.domain.category.model.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class CategoryQueryAdapterTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryEntityMapper categoryEntityMapper;

    @InjectMocks
    private CategoryQueryAdapter queryAdapter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findById_shouldReturnMappedCategory_whenEntityExists() {
        String id = "cat123";
        MongoCategory categoryEntity = MongoCategory.builder()
                .id("cat123")
                .code("CODE")
                .title("Title")
                .build(); // заглушка
        Category category = Category.builder().id(id).code("CODE").title("Title").build();

        when(categoryRepository.findById(id)).thenReturn(Optional.of(categoryEntity));
        when(categoryEntityMapper.toEntity(categoryEntity)).thenReturn(category);

        Category result = queryAdapter.findById(id, "unused message");

        assertThat(result).isEqualTo(category);
        verify(categoryRepository, times(1)).findById(id);
        verify(categoryEntityMapper, times(1)).toEntity(categoryEntity);
    }

    @Test
    void findById_shouldThrowException_whenEntityNotFound() {
        String id = "cat123";

        when(categoryRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> queryAdapter.findById(id, "unused"))
                .isInstanceOf(CategoryNotFoundException.class)
                .hasMessage("Category not found.");

        verify(categoryRepository, times(1)).findById(id);
        verifyNoInteractions(categoryEntityMapper);
    }

    @Test
    void existsById_shouldReturnTrue_whenExists() {
        String id = "cat123";
        when(categoryRepository.existsById(id)).thenReturn(true);

        boolean exists = queryAdapter.existsById(id);

        assertThat(exists).isTrue();
        verify(categoryRepository, times(1)).existsById(id);
    }

    @Test
    void existsByCode_shouldReturnTrue_whenExists() {
        String code = "CODE123";
        when(categoryRepository.existsByCode(code)).thenReturn(true);

        boolean exists = queryAdapter.existsByCode(code);

        assertThat(exists).isTrue();
        verify(categoryRepository, times(1)).existsByCode(code);
    }
}