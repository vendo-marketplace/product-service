package com.vendo.product_service.validation;

import com.vendo.product_service.common.type.CategoryType;

public interface CategoryTypeValidator {

    void validateCategoryType(String categoryId, CategoryType expectedType);

}
