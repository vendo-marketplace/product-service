package com.vendo.product_service.domain.category.validation.attribute;

import com.vendo.product_service.common.exception.ValidationBody;
import com.vendo.product_service.domain.category.db.model.embedded.AttributeDefinition;
import com.vendo.product_service.domain.category.db.model.embedded.AttributeType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class CategoryBooleanAttributeValidationStrategy implements CategoryAttributeValidationStrategy {

    private final Set<String> BOOLEAN_VALUES = Set.of(
            Boolean.FALSE.toString(),
            Boolean.TRUE.toString()
    );

    @Override
    public ValidationBody validate(String name, AttributeDefinition definition, List<String> requestAttributes) {
        ValidationBody validationBody = ValidationBody.builder().fieldName(name).build();

        if (requestAttributes == null || requestAttributes.size() != 1) {
            return validationBody.toBuilder()
                    .errorMessage("Must contain exactly one value.")
                    .build();
        } else if (!BOOLEAN_VALUES.contains(requestAttributes.get(0))) {
            return validationBody.toBuilder()
                    .errorMessage("Invalid boolean value. Allowed values: true, false.")
                    .build();
        }

        return validationBody.toBuilder().valid(true).build();
    }

    @Override
    public AttributeType getType() {
        return AttributeType.BOOLEAN;
    }
}
