package com.vendo.product_service.validation.strategy;

import com.vendo.product_service.db.model.embedded.AttributeType;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CategoryNumberAttributeValidationStrategy implements CategoryAttributeValidationStrategy {

    @Override
    public boolean validate(List<String> attributesValue) {
        if (attributesValue.size() != 1) {
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
