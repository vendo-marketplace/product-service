package com.vendo.product_service.domain.category.validation.attribute;

import com.vendo.product_service.domain.category.db.model.embedded.AttributeDefinition;
import com.vendo.product_service.domain.category.db.model.embedded.AttributeType;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CategoryEnumAttributeValidationStrategy implements CategoryAttributeValidationStrategy {

    @Override
    public boolean validate(List<String> requestAttributes, AttributeDefinition attributeDefinition) {
        if (requestAttributes == null || requestAttributes.size() != 1) {
            return false;
        }

        List<String> allowedValues = attributeDefinition.allowedValues();
        if (allowedValues == null || allowedValues.isEmpty()) {
            return false;
        }

        return allowedValues.contains(requestAttributes.get(0));
    }

    @Override
    public AttributeType getType() {
        return AttributeType.ENUM;
    }
}
