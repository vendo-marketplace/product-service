package com.vendo.product_service.application;

import com.vendo.product_service.application.category.CategoryService;
import com.vendo.product_service.domain.category.exception.CategoryAlreadyExistsException;
import com.vendo.product_service.domain.category.exception.CategoryNotFoundException;
import com.vendo.product_service.domain.category.model.Category;
import com.vendo.product_service.port.category.CategoryCommandPort;
import com.vendo.product_service.port.category.CategoryQueryPort;
import com.vendo.product_service.application.category.validation.creation.CreateCategoryValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class CategoryServiceTest {

    @Mock
    private CategoryCommandPort commandPort;

    @Mock
    private CategoryQueryPort queryPort;

    @Mock
    private CreateCategoryValidationService validationService;

    @InjectMocks
    private CategoryService categoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

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

        categoryService.save(category);

        verify(validationService, times(1)).validateCreation(category);
        verify(commandPort, times(1)).save(category);
    }

    @Test
    void save_shouldThrowCategoryAlreadyExistsException_whenCategoryExists() {
        Category category = buildCategory();

        when(queryPort.existsByCode(category.getCode())).thenReturn(true);

        assertThatThrownBy(() -> categoryService.save(category))
                .isInstanceOf(CategoryAlreadyExistsException.class)
                .hasMessage("Category already exists by code.");

        verify(validationService, times(1)).validateCreation(category);
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