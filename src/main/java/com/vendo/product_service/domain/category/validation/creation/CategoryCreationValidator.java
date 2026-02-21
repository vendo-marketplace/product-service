package com.vendo.product_service.domain.category.validation.creation;

import com.vendo.product_service.domain.category.model.CategoryType;

public interface CategoryCreationValidator {

    void validate(String parentId);

    CategoryType getCategoryType();

}
