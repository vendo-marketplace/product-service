package com.vendo.product_service.application.category.validation.type;

import com.vendo.product_service.domain.category.exception.CategoryValidationException;
import com.vendo.product_service.domain.category.type.CategoryType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ParentTypeValidator implements TypeValidator {

    @Override
    public void validate(String parentId) {
        if (parentId != null) {
            throw new CategoryValidationException("A parent category cannot have a parent.");
        }
    }

    @Override
    public CategoryType getType() {
        return CategoryType.PARENT;
    }

}
