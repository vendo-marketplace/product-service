package com.vendo.product_service.application;

import com.vendo.product_service.domain.category.exception.CategoryAlreadyExistsException;
import com.vendo.product_service.domain.category.model.Category;
import com.vendo.product_service.domain.category.validation.creation.CreateCategoryValidationService;
import com.vendo.product_service.domain.category.port.CategoryCommandPort;
import com.vendo.product_service.domain.category.port.CategoryQueryPort;
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
        if (queryPort.existsByCode(category.getCode())) {
            throw new CategoryAlreadyExistsException("Category already exists by code.");
        }

        commandPort.save(category);
    }

    public Category findById(String id) {
        return queryPort.findById(id, "Category not found.");
    }
}