package com.vendo.product_service.domain.category.validation.attribute;

import com.vendo.product_service.domain.category.db.model.embedded.AttributeDefinition;
import com.vendo.product_service.domain.category.db.model.embedded.AttributeType;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CategoryStringAttributeValidationStrategy implements CategoryAttributeValidationStrategy {

    @Override
    public boolean validate(List<String> attributesValue, AttributeDefinition attributeDefinition) {
        return attributesValue != null && attributesValue.size() == 1;
    }

    @Override
    public AttributeType getType() {
        return AttributeType.STRING;
    }
}
