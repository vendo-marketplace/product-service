package com.vendo.product_service.application.category.validation.creation;

import com.vendo.product_service.domain.category.exception.CategoryTypeException;
import com.vendo.product_service.domain.category.model.Category;
import com.vendo.product_service.domain.category.type.CategoryType;
import com.vendo.product_service.port.category.CategoryQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChildCreationValidator implements CreationValidator {

    private final CategoryQueryPort categoryQueryPort;

    @Override
    public void validate(String parentId) {
        Category parentCategory = categoryQueryPort.findById(parentId, "Parent category not found.");

        if (parentCategory.getType() == CategoryType.CHILD) {
            throw new CategoryTypeException("A child category cannot have another child category as its parent.");
        }
    }

    @Override
    public CategoryType getType() {
        return CategoryType.CHILD;
    }

}
