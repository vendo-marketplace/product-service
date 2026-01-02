package com.vendo.product_service.domain.category.validation.attribute;

import com.vendo.product_service.common.exception.ValidationBody;
import com.vendo.product_service.domain.category.db.model.embedded.AttributeDefinition;
import com.vendo.product_service.domain.category.db.model.embedded.AttributeType;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CategoryNumberAttributeValidationStrategy implements CategoryAttributeValidationStrategy {

    @Override
    public ValidationBody validate(String name, AttributeDefinition definition, List<String> requestAttributes) {
        ValidationBody validationBody = ValidationBody.builder().fieldName(name).build();

        if (requestAttributes == null || requestAttributes.size() != 1) {
            return validationBody.toBuilder()
                    .errorMessage("Must contain exactly one value.")
                    .build();
        }

        try {
            Integer.parseInt(requestAttributes.get(0));
        } catch (NumberFormatException e) {
            return validationBody.toBuilder()
                    .errorMessage("Invalid numeric value.")
                    .build();
        }

        return validationBody.toBuilder().valid(true).build();
    }

    @Override
    public AttributeType getType() {
        return AttributeType.NUMBER;
    }
}
