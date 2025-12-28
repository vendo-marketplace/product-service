package com.vendo.product_service.domain.category.validation.creation;

import com.vendo.product_service.domain.category.common.type.CategoryType;
import com.vendo.product_service.domain.category.web.dto.CreateCategoryRequest;

public interface CategoryCreationValidator {

    void validate(CreateCategoryRequest createCategoryRequest);

    CategoryType getCategoryType();

}
