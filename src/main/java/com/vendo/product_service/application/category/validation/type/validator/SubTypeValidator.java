package com.vendo.product_service.application.category.validation.type.validator;

import com.vendo.product_service.application.category.validation.type.TypeValidator;
import com.vendo.product_service.domain.category.exception.CategoryTypeException;
import com.vendo.product_service.domain.category.model.Category;
import com.vendo.product_service.domain.category.type.CategoryType;
import com.vendo.product_service.port.out.category.CategoryQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SubTypeValidator implements TypeValidator {

    private final CategoryQueryPort categoryQueryPort;

    @Override
    public void validate(Category category) {
        Category parentCategory = categoryQueryPort.findById(category.getParentId(), "Parent category not found.");

        if (parentCategory.getType() == CategoryType.CHILD) {
            throw new CategoryTypeException("A subcategory cannot have a child category as its parent.");
        }
    }

    @Override
    public CategoryType getType() {
        return CategoryType.SUB;
    }
}
