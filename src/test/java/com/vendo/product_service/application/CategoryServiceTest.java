package com.vendo.product_service.application;

import com.vendo.product_service.application.category.CategoryCommandService;
import com.vendo.product_service.application.category.CategoryQueryService;
import com.vendo.product_service.application.category.validation.type.TypeValidationService;
import com.vendo.product_service.domain.category.exception.CategoryAlreadyExistsException;
import com.vendo.product_service.domain.category.exception.CategoryNotFoundException;
import com.vendo.product_service.domain.category.model.Category;
import com.vendo.product_service.port.out.IdGenerationPort;
import com.vendo.product_service.port.out.category.CategoryCommandPort;
import com.vendo.product_service.port.out.category.CategoryQueryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryCommandPort commandPort;
    @Mock
    private CategoryQueryPort queryPort;
    @Mock
    private IdGenerationPort idGenerationPort;
    @Mock
    private TypeValidationService typeValidationService;

    @InjectMocks
    private CategoryCommandService categoryCommandService;
    @InjectMocks
    private CategoryQueryService categoryQueryService;

    private Category buildCategory() {
        return Category.builder()
                .id("cat123")
                .code("CODE123")
                .title("Category title")
                .build();
    }

    @Test
    void save_shouldCallCommandPort_whenCategoryDoesNotExist() {
        Category category = buildCategory();

        when(queryPort.existsByCode(category.getCode())).thenReturn(false);
        when(idGenerationPort.generate()).thenReturn(category.getId());

        categoryCommandService.save(category);

        verify(commandPort, times(1)).save(category);
        verify(idGenerationPort, times(1)).generate();

        assertThat(category.getPath().contains(("cat123")));
    }

    @Test
    void save_shouldThrowCategoryAlreadyExistsException_whenCategoryExists() {
        Category category = buildCategory();

        when(queryPort.existsByCode(category.getCode())).thenReturn(true);

        assertThatThrownBy(() -> categoryCommandService.save(category))
                .isInstanceOf(CategoryAlreadyExistsException.class)
                .hasMessage("Category already exists by code.");

        verify(commandPort, never()).save(any());
    }

    @Test
    void findById_shouldReturnCategory_whenExists() {
        Category category = buildCategory();

        when(queryPort.findById(category.getId(), "Category not found.")).thenReturn(category);

        Category result = categoryQueryService.findById(category.getId());

        assertThat(result).isEqualTo(category);
        verify(queryPort, times(1)).findById(category.getId(), "Category not found.");
    }

    @Test
    void findById_shouldThrowCategoryNotFoundException_whenDoesNotExist() {
        String categoryId = "nonexistent";

        when(queryPort.findById(categoryId, "Category not found."))
                .thenThrow(new CategoryNotFoundException("Category not found."));

        assertThatThrownBy(() -> categoryQueryService.findById(categoryId))
                .isInstanceOf(CategoryNotFoundException.class)
                .hasMessage("Category not found.");

        verify(queryPort, times(1)).findById(categoryId, "Category not found.");
    }
}