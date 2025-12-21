package com.vendo.product_service.domain.category.validation.type;

import com.vendo.product_service.domain.category.common.exception.CategoryValidationException;
import com.vendo.product_service.domain.category.common.type.CategoryType;
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
