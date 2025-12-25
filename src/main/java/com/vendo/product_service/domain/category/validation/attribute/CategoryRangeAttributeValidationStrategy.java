package com.vendo.product_service.domain.category.validation.attribute;

import com.vendo.product_service.domain.category.db.model.embedded.AttributeDefinition;
import com.vendo.product_service.domain.category.db.model.embedded.AttributeType;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CategoryRangeAttributeValidationStrategy implements CategoryAttributeValidationStrategy {

    @Override
    public boolean validate(List<String> attributesValue, AttributeDefinition attributeDefinition) {
        if (attributesValue == null || attributesValue.size() != 2) {
            return false;
        }

        try {
            int from = Integer.parseInt(attributesValue.get(0));
            if (from < 0) {
                return false;
            }

            int to = Integer.parseInt(attributesValue.get(1));
            if (from >= to) {
                return false;
            }
        } catch (NumberFormatException e) {
            return false;
        }

        return true;
    }

    @Override
    public AttributeType getType() {
        return AttributeType.RANGE;
    }
}
