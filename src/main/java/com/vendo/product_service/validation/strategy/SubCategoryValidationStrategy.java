package com.vendo.product_service.validation.strategy;

import com.vendo.product_service.common.exception.CategoryTypeException;
import com.vendo.product_service.common.exception.CategoryValidationException;
import com.vendo.product_service.common.type.CategoryType;
import com.vendo.product_service.db.query.CategoryQueryService;
import com.vendo.product_service.db.model.Category;
import com.vendo.product_service.web.dto.CreateCategoryRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SubCategoryValidationStrategy implements CategoryValidationStrategy {

    private final CategoryQueryService categoryQueryService;

    @Override
    public void validate(CreateCategoryRequest createCategoryRequest) {
        if (StringUtils.isEmpty(createCategoryRequest.parentId())) {
            throw new CategoryValidationException("Sub category should have parent id.");
        }

        if (createCategoryRequest.attributes() != null) {
            throw new CategoryValidationException("Sub category cannot have attributes.");
        }

        Category parentRootCategory = categoryQueryService.findByIdOrThrow(
                createCategoryRequest.parentId(),
                "Parent category not found."
        );
        if (parentRootCategory.getCategoryType() != CategoryType.ROOT) {
            throw new CategoryTypeException("Sub category should have root category as parent.");
        }
    }

    @Override
    public CategoryType getType() {
        return CategoryType.SUB;
    }
}
