package com.vendo.product_service.application.category.validation.attribute;

import com.vendo.product_service.application.category.validation.attribute.strategy.AttributeValidatorStrategy;
import com.vendo.product_service.domain.category.exception.CategoryValidationException;
import com.vendo.product_service.domain.category.model.AttributeType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AttributesValidationFactory {

    private final List<AttributeValidatorStrategy> categoryAttributeValidationStrategies;

    public AttributeValidatorStrategy getValidator(AttributeType attributeType) {
        return categoryAttributeValidationStrategies.stream()
                .filter(categoryAttributeValidationStrategy -> categoryAttributeValidationStrategy.getType() == attributeType)
                .findFirst()
                .orElseThrow(() -> new CategoryValidationException("Category attribute type not found."));
    }

}
