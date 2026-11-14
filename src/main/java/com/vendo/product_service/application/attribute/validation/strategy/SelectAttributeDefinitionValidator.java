package com.vendo.product_service.application.attribute.validation.strategy;

import com.vendo.product_service.domain.attribute.model.AttributeType;
import org.springframework.stereotype.Component;

@Component
class SelectAttributeDefinitionValidator extends AllowedValuesDefinitionValidator {

    @Override
    public AttributeType getType() {
        return AttributeType.SELECT;
    }
}
