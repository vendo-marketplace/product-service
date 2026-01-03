package com.vendo.product_service.domain.category.validation.creation;

import com.vendo.product_service.domain.category.common.exception.CategoryTypeException;
import com.vendo.product_service.domain.category.common.type.CategoryType;
import com.vendo.product_service.domain.category.db.cqrs.query.CategoryQueryService;
import com.vendo.product_service.domain.category.db.model.Category;
import com.vendo.product_service.domain.category.validation.CategoryTypeResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChildCategoryCreationValidator implements CategoryCreationValidator {

    private final CategoryQueryService categoryQueryService;

    private final CategoryTypeResolver categoryTypeResolver;

    @Override
    public void validate(String parentId) {
        Category parentCategory = categoryQueryService.findById(parentId, "Parent category not found.");
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
