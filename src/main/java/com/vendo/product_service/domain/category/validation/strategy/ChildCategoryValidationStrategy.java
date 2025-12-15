package com.vendo.product_service.domain.category.validation.strategy;

import com.vendo.product_service.domain.category.common.exception.CategoryValidationException;
import com.vendo.product_service.domain.category.common.type.CategoryType;
import com.vendo.product_service.domain.category.db.query.CategoryQueryService;
import com.vendo.product_service.domain.category.db.model.Category;
import com.vendo.product_service.domain.category.web.dto.CreateCategoryRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChildCategoryValidationStrategy implements CategoryValidationStrategy {

    private final CategoryQueryService categoryQueryService;

    @Override
    public void validate(CreateCategoryRequest createCategoryRequest) {
        if (StringUtils.isEmpty(createCategoryRequest.parentId())) {
            throw new CategoryValidationException("Child category should have parent id.");
        }

        if (createCategoryRequest.attributes() == null || createCategoryRequest.attributes().isEmpty()) {
            throw new CategoryValidationException("Child category should have attributes.");
        }

        Category parentRootCategory = categoryQueryService.findByIdOrThrow(createCategoryRequest.parentId(), "Parent category not found.");
        if (parentRootCategory.getCategoryType() != CategoryType.SUB) {
            throw new CategoryValidationException("Child category should have sub category as parent.");
        }
    }

    @Override
    public CategoryType getType() {
        return CategoryType.CHILD;
    }
}
