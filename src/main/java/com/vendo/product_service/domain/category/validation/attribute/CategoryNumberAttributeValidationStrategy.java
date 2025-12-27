package com.vendo.product_service.domain.category.validation.attribute;

import com.vendo.product_service.domain.category.db.model.embedded.AttributeDefinition;
import com.vendo.product_service.domain.category.db.model.embedded.AttributeType;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CategoryNumberAttributeValidationStrategy implements CategoryAttributeValidationStrategy {

    @Override
    public boolean validate(List<String> attributesValue, AttributeDefinition attributeDefinition) {
        if (attributesValue == null || attributesValue.size() != 1) {
            return false;
        }

        try {
            Integer.valueOf(attributesValue.get(0));
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public AttributeType getType() {
        return AttributeType.NUMBER;
    }
}
