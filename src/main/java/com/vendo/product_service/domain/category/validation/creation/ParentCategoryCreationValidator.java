package com.vendo.product_service.domain.category.validation.creation;

import com.vendo.product_service.domain.category.exception.CategoryValidationException;
import com.vendo.product_service.domain.category.model.CategoryType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ParentCategoryCreationValidator implements CategoryCreationValidator {

    @Override
    public void validate(String parentId) {

        if (parentId != null) {
            throw new CategoryValidationException("A parent category cannot have a parent.");
        }

    }

    @Override
    public CategoryType getCategoryType() {
        return CategoryType.PARENT;
    }

}
