package com.vendo.product_service.application.category.validation.attribute.strategy;

import com.vendo.product_service.domain.category.model.AttributeType;
import com.vendo.product_service.application.category.validation.dto.AttributePayload;
import com.vendo.product_service.application.category.validation.dto.ValidationBody;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class BooleanAttributeValidatorStrategy implements AttributeValidatorStrategy {

    private final Set<String> BOOLEAN_VALUES = Set.of(
            Boolean.FALSE.toString(),
            Boolean.TRUE.toString()
    );

    @Override
    public ValidationBody validate(AttributePayload payload, List<String> requestAttributes) {
        ValidationBody validationBody = ValidationBody.builder().fieldName(payload.name()).build();

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
