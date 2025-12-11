package com.vendo.product_service.domain.category.validation.factory;

import com.vendo.product_service.domain.category.common.exception.CategoryNotFoundException;
import com.vendo.product_service.domain.category.common.type.CategoryType;
import com.vendo.product_service.domain.category.validation.strategy.CategoryValidationStrategy;
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
