package com.vendo.product_service.domain.category.validation.attribute;

import com.vendo.product_service.domain.category.db.model.embedded.AttributeType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class CategoryBooleanAttributeValidationStrategy implements CategoryAttributeValidationStrategy {

    private final Set<String> BOOLEAN_VALUES = Set.of(
            Boolean.FALSE.toString(),
            Boolean.TRUE.toString()
    );

    @Override
    public boolean validate(List<String> attributesValue) {
        if (attributesValue == null || attributesValue.size() != 1) {
            return false;
        }

        return BOOLEAN_VALUES.contains(attributesValue.get(0));
    }

    @Override
    public AttributeType getType() {
        return AttributeType.BOOLEAN;
    }
}
