package com.vendo.product_service.application.category.validation.attribute.strategy;

import com.vendo.product_service.application.category.validation.dto.ValidationBody;
import com.vendo.product_service.domain.attribute.model.Attribute;
import com.vendo.product_service.domain.attribute.model.AttributeType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
class BooleanAttributeValidator implements AttributeValidatorStrategy {

    private final Set<String> BOOLEAN_VALUES = Set.of(
            Boolean.FALSE.toString(),
            Boolean.TRUE.toString()
    );

    @Override
    public ValidationBody validate(Attribute originAttribute, List<String> requestAttributes) {
        ValidationBody validationBody = ValidationBody.builder().fieldName(originAttribute.title()).build();

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
