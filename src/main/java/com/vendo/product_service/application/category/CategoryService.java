package com.vendo.product_service.application.category;

import com.vendo.product_service.application.category.validation.creation.CategoryValidationService;
import com.vendo.product_service.domain.category.exception.CategoryAlreadyExistsException;
import com.vendo.product_service.domain.category.model.Category;
import com.vendo.product_service.port.category.CategoryCommandPort;
import com.vendo.product_service.port.category.CategoryQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryCommandPort categoryCommandPort;
    private final CategoryQueryPort categoryQueryPort;
    private final CategoryValidationService validationService;

    public Category findById(String id) {
        return categoryQueryPort.findById(id, "Category not found.");
    }

    public void save(Category category) {
        System.out.println("Checking if exists by code.");
        throwIfExistsByCode(category.getCode());
        System.out.println("Validating creation.");
        validationService.validateCreation(category);
        categoryCommandPort.save(category);
    }

    private void throwIfExistsByCode(String code) {
        if (categoryQueryPort.existsByCode(code)) {
            throw new CategoryAlreadyExistsException("Category already exists by code.");
        }
    }
}