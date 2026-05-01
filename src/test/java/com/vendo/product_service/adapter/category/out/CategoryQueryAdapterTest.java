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

import java.util.List;
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
                .code("CODE")
                .title("Title")
                .build();
        Category category = Category.builder().id(id).code("CODE").title("Title").build();

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
        String blankMessage = " ";

        when(categoryRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> queryAdapter.findById(id, blankMessage))
                .isInstanceOf(CategoryNotFoundException.class)
                .hasMessage("Category not found.");

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

    @Test
    void existsByCode_shouldReturnTrue_whenExists() {
        String code = "CODE123";
        when(categoryRepository.existsByCode(code)).thenReturn(true);

        boolean exists = queryAdapter.existsByCode(code);

        assertThat(exists).isTrue();
        verify(categoryRepository, times(1)).existsByCode(code);
        verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    void existsByCode_shouldReturnFalse_whenNotExists() {
        String code = "CODE123";
        when(categoryRepository.existsByCode(code)).thenReturn(false);

        boolean exists = queryAdapter.existsByCode(code);

        assertThat(exists).isFalse();
        verify(categoryRepository, times(1)).existsByCode(code);
        verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    void findByParentId_shouldMapCorrectly() {
        MongoCategory entity = MongoCategory.builder()
                .id("2")
                .parentId("1")
                .build();

        Category domain = Category.builder()
                .id("2")
                .parentId("1")
                .build();

        when(categoryRepository.findByParentId("1")).thenReturn(List.of(entity));
        when(categoryMapper.toCategory(entity)).thenReturn(domain);

        List<Category> result = queryAdapter.findByParentId("1");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getParentId()).isEqualTo("1");
    }
}