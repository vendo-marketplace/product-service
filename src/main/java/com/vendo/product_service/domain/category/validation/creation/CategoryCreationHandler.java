package com.vendo.product_service.domain.category.validation.creation;

import com.vendo.product_service.domain.category.common.type.CategoryType;
import com.vendo.product_service.domain.category.web.dto.CreateCategoryRequest;

public interface CategoryCreationHandler {

    void handle(CreateCategoryRequest createCategoryRequest);

    CategoryType getCategoryType();

}
