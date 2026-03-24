package com.vendo.product_service.application.category.validation.creation;

import com.vendo.product_service.domain.category.type.CategoryType;

public interface CreationValidator {

    void validate(String parentId);

    CategoryType getType();

}
