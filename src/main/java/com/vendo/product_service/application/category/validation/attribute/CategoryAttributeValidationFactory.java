package com.vendo.product_service.application.category.validation.attribute;

import com.vendo.product_service.adapter.model.category.embedded.AttributeType;
import com.vendo.product_service.domain.category.exception.CategoryValidationException;
import com.vendo.product_service.application.category.validation.attribute.strategy.CategoryAttributeValidatorStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CategoryAttributeValidationFactory {

    private final List<CategoryAttributeValidatorStrategy> categoryAttributeValidationStrategies;

    public CategoryAttributeValidatorStrategy getValidator(AttributeType attributeType) {
        return categoryAttributeValidationStrategies.stream()
                .filter(categoryAttributeValidationStrategy -> categoryAttributeValidationStrategy.getType() == attributeType)
                .findFirst()
                .orElseThrow(() -> new CategoryValidationException("Category attribute type not found."));
    }

}
