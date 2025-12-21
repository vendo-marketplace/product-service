package com.vendo.product_service.domain.category.validation.type;

import com.vendo.product_service.domain.category.common.type.CategoryType;
import com.vendo.product_service.domain.category.web.dto.CreateCategoryRequest;

public interface CreateCategoryValidationStrategy {

    void validate(CreateCategoryRequest createCategoryRequest);

    CategoryType getType();

}
