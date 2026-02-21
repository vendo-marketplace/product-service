package com.vendo.product_service.domain.category.validation.creation;

import com.vendo.product_service.domain.category.exception.CategoryTypeException;
import com.vendo.product_service.domain.category.common.type.CategoryType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CategoryCreationValidationFactory {

    private final List<CategoryCreationValidator> categoryCreationValidators;

    public CategoryCreationValidator getHandler(CategoryType categoryType) {
        return categoryCreationValidators.stream()
                .filter(handler -> handler.getCategoryType() == categoryType)
                .findFirst()
                .orElseThrow(() -> new CategoryTypeException("No category creation handler."));
    }

}
