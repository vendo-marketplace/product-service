package com.vendo.product_service.validation.factory;

import com.vendo.product_service.common.exception.CategoryValidationException;
import com.vendo.product_service.db.model.embedded.AttributeType;
import com.vendo.product_service.validation.strategy.CategoryAttributeValidationStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CategoryAttributeValidationFactory {

    private final List<CategoryAttributeValidationStrategy> categoryAttributeValidationStrategies;

    public CategoryAttributeValidationStrategy getValidator(AttributeType attributeType) {
        return categoryAttributeValidationStrategies.stream()
                .filter(categoryAttributeValidationStrategy -> categoryAttributeValidationStrategy.getType() == attributeType)
                .findFirst()
                .orElseThrow(() -> new CategoryValidationException("Category attribute type not found."));
    }

}
