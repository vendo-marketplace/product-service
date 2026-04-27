package com.vendo.product_service.application;

import com.vendo.product_service.application.category.CategoryService;
import com.vendo.product_service.application.category.validation.type.TypeValidationFactory;
import com.vendo.product_service.application.category.validation.type.TypeValidator;
import com.vendo.product_service.domain.category.exception.CategoryAlreadyExistsException;
import com.vendo.product_service.domain.category.exception.CategoryNotFoundException;
import com.vendo.product_service.domain.category.model.Category;
import com.vendo.product_service.port.category.CategoryCommandPort;
import com.vendo.product_service.port.category.CategoryQueryPort;
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
    private TypeValidationFactory typeValidationFactory;

    @Mock
    private TypeValidator typeValidator;

    @Mock
    private CategoryQueryPort queryPort;

    @InjectMocks
    private CategoryService categoryService;

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
        when(typeValidationFactory.getHandler(category.getType())).thenReturn(typeValidator);

        categoryService.save(category);

        verify(commandPort, times(1)).save(category);
    }

    @Test
    void save_shouldThrowCategoryAlreadyExistsException_whenCategoryExists() {
        Category category = buildCategory();

        when(queryPort.existsByCode(category.getCode())).thenReturn(true);

        assertThatThrownBy(() -> categoryService.save(category))
                .isInstanceOf(CategoryAlreadyExistsException.class)
                .hasMessage("Category already exists by code.");

        verify(commandPort, never()).save(any());
    }

    @Test
    void findById_shouldReturnCategory_whenExists() {
        Category category = buildCategory();

        when(queryPort.findById(category.getId(), "Category not found.")).thenReturn(category);

        Category result = categoryService.findById(category.getId());

        assertThat(result).isEqualTo(category);
        verify(queryPort, times(1)).findById(category.getId(), "Category not found.");
    }

    @Test
    void findById_shouldThrowCategoryNotFoundException_whenDoesNotExist() {
        String categoryId = "nonexistent";

        when(queryPort.findById(categoryId, "Category not found."))
                .thenThrow(new CategoryNotFoundException("Category not found."));

        assertThatThrownBy(() -> categoryService.findById(categoryId))
                .isInstanceOf(CategoryNotFoundException.class)
                .hasMessage("Category not found.");

        verify(queryPort, times(1)).findById(categoryId, "Category not found.");
    }
}