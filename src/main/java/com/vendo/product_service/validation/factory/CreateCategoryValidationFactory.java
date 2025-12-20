package com.vendo.product_service.validation.factory;

import com.vendo.product_service.common.exception.CategoryValidationException;
import com.vendo.product_service.common.type.CategoryType;
import com.vendo.product_service.validation.strategy.CreateCategoryValidationStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CreateCategoryValidationFactory {

    private final List<CreateCategoryValidationStrategy> categoryValidationStrategies;

    public CreateCategoryValidationStrategy getValidator(CategoryType categoryType) {
        return categoryValidationStrategies.stream()
                .filter(categoryValidationStrategy -> categoryValidationStrategy.getType() == categoryType)
                .findFirst()
                .orElseThrow(() -> new CategoryValidationException("Category type not found."));
    }

}
