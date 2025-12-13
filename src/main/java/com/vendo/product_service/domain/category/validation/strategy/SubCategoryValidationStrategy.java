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

        Category parentRootCategory = categoryQueryService.findById(createCategoryRequest.parentId());
        if (parentRootCategory.getCategoryType() != CategoryType.ROOT) {
            throw new CategoryValidationException("Sub category should have root category as parent.");
        }
    }

    @Override
    public CategoryType getType() {
        return CategoryType.SUB;
    }
}
