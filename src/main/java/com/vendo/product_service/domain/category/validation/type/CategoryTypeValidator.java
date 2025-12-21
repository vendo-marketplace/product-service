package com.vendo.product_service.domain.category.validation.type;

import com.vendo.product_service.domain.category.common.type.CategoryType;

public interface CategoryTypeValidator {

    void validateCategoryType(String categoryId, CategoryType expectedType);

}
