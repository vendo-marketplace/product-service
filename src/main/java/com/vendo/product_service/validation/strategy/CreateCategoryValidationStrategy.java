package com.vendo.product_service.validation.strategy;

import com.vendo.product_service.common.type.CategoryType;
import com.vendo.product_service.web.dto.CreateCategoryRequest;

public interface CreateCategoryValidationStrategy {

    void validate(CreateCategoryRequest createCategoryRequest);

    CategoryType getType();

}
