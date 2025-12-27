package com.vendo.product_service.domain.category.validation.creation;

import com.vendo.product_service.domain.category.common.exception.CategoryTypeException;
import com.vendo.product_service.domain.category.common.type.CategoryType;
import com.vendo.product_service.domain.category.db.cqrs.query.CategoryQueryService;
import com.vendo.product_service.domain.category.db.model.Category;
import com.vendo.product_service.domain.category.validation.CategoryTypeResolver;
import com.vendo.product_service.domain.category.web.dto.CreateCategoryRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChildCategoryCreationHandler implements CategoryCreationHandler {

    private final CategoryQueryService categoryQueryService;

    private final CategoryTypeResolver categoryTypeResolver;

    @Override
    public void handle(CreateCategoryRequest createCategoryRequest) {
        Category parentCategory = categoryQueryService.findById(createCategoryRequest.parentId(), "Parent category not found.");
        CategoryType parentCategoryType = categoryTypeResolver.resolve(parentCategory.getParentId(), parentCategory.getAttributes());

        if (parentCategoryType == CategoryType.CHILD) {
            throw new CategoryTypeException("Child category shouldn't have child category as parent.");
        }
    }

    @Override
    public CategoryType getCategoryType() {
        return CategoryType.CHILD;
    }

}
