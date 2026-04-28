package com.vendo.product_service.application.category.validation.type;

import com.vendo.product_service.domain.category.model.Category;
import com.vendo.product_service.domain.category.type.CategoryType;

public interface TypeValidator {

    void validate(Category category);

    CategoryType getType();

}
