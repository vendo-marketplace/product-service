package com.vendo.product_service.application.category.validation.type;

import com.vendo.product_service.domain.category.type.CategoryType;

public interface TypeValidator {

    void validate(String parentId);

    CategoryType getType();

}
