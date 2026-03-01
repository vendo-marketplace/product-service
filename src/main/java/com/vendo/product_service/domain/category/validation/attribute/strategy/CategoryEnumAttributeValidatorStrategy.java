package com.vendo.product_service.domain.category.validation.attribute.strategy;

import com.vendo.product_service.domain.category.validation.ValidationBody;
import com.vendo.product_service.adapter.model.category.embedded.AttributeDefinition;
import com.vendo.product_service.adapter.model.category.embedded.AttributeType;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CategoryEnumAttributeValidatorStrategy implements CategoryAttributeValidatorStrategy {

    @Override
    public ValidationBody validate(String name, AttributeDefinition definition, List<String> requestAttributes) {
        ValidationBody validationBody = ValidationBody.builder().fieldName(name).build();

        if (requestAttributes == null || requestAttributes.size() != 1) {
            return validationBody.toBuilder()
                    .errorMessage("Must contain exactly one value.")
                    .build();
        }

        if (!definition.allowedValues().contains(requestAttributes.get(0))) {
            return validationBody.toBuilder()
                    .errorMessage("Invalid value. Allowed values: " + String.join(", ", definition.allowedValues()))
                    .build();
        }

        return validationBody.toBuilder().valid(true).build();
    }

    @Override
    public AttributeType getType() {
        return AttributeType.ENUM;
    }
}
