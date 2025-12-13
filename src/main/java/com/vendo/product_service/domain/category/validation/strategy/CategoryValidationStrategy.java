package com.vendo.product_service.domain.category.validation.strategy;

import com.vendo.product_service.domain.category.common.type.CategoryType;
import com.vendo.product_service.domain.category.web.dto.CreateCategoryRequest;

public interface CategoryValidationStrategy {

    void validate(CreateCategoryRequest createCategoryRequest);

    CategoryType getType();

}
