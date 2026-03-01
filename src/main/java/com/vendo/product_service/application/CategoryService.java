package com.vendo.product_service.application;

import com.vendo.product_service.domain.category.exception.CategoryAlreadyExistsException;
import com.vendo.product_service.domain.category.model.Category;
import com.vendo.product_service.domain.category.validation.creation.CreateCategoryValidationService;
import com.vendo.product_service.port.category.CategoryCommandPort;
import com.vendo.product_service.port.category.CategoryQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryCommandPort commandPort;
    private final CategoryQueryPort queryPort;
    private final CreateCategoryValidationService validationService;

    public void save(Category category) {
        validationService.validateCreation(category);
        throwIfExistsByCode(category.getCode());
        commandPort.save(category);
    }

    public Category findById(String id) {
        return queryPort.findById(id, "Category not found.");
    }

    private void throwIfExistsByCode(String code) {
        if (queryPort.existsByCode(code)) {
            throw new CategoryAlreadyExistsException("Category already exists by code.");
        }
    }
}