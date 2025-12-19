package com.vendo.product_service.validation.factory;

import com.vendo.product_service.common.exception.CategoryNotFoundException;
import com.vendo.product_service.common.type.CategoryType;
import com.vendo.product_service.validation.strategy.CategoryValidationStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CategoryValidationFactory {

    private final List<CategoryValidationStrategy> categoryValidationStrategies;

    public CategoryValidationStrategy getValidator(CategoryType categoryType) {
        return categoryValidationStrategies.stream()
                .filter(categoryValidationStrategy -> categoryValidationStrategy.getType() == categoryType)
                .findFirst()
                .orElseThrow(() -> new CategoryNotFoundException("Category type not found."));
    }

}
