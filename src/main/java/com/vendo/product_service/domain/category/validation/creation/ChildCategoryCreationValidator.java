package com.vendo.product_service.domain.category.validation.creation;

import com.vendo.product_service.domain.category.model.CategoryType;
import com.vendo.product_service.domain.category.exception.CategoryTypeException;
import com.vendo.product_service.domain.category.model.Category;
import com.vendo.product_service.domain.category.validation.CategoryTypeResolver;
import com.vendo.product_service.domain.category.port.CategoryQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChildCategoryCreationValidator implements CategoryCreationValidator {

    private final CategoryQueryPort categoryQueryPort;
    private final CategoryTypeResolver categoryTypeResolver;

    @Override
    public void validate(String parentId) {
        Category parentCategory = categoryQueryPort.findById(parentId, "Parent category not found.");
        CategoryType parentCategoryType = categoryTypeResolver.resolve(parentCategory.getParentId(), parentCategory.getAttributes());

        if (parentCategoryType == CategoryType.CHILD) {
            throw new CategoryTypeException("A child category cannot have another child category as its parent.");
        }
    }

    @Override
    public CategoryType getCategoryType() {
        return CategoryType.CHILD;
    }

}
