package com.vendo.product_service.application.category.validation.attribute.strategy;

import com.vendo.product_service.application.category.validation.dto.AttributePayload;
import com.vendo.product_service.application.category.validation.dto.ValidationBody;
import com.vendo.product_service.domain.category.model.AttributeType;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EnumAttributeValidator implements AttributeValidatorStrategy {

    @Override
    public ValidationBody validate(AttributePayload payload, List<String> requestAttributes) {
        ValidationBody validationBody = ValidationBody.builder().fieldName(payload.name()).build();

        if (requestAttributes == null || requestAttributes.size() != 1) {
            return validationBody.toBuilder()
                    .errorMessage("Must contain exactly one value.")
                    .build();
        }

        List<String> allowedValues = payload.definition().allowedValues();
        if (!allowedValues.contains(requestAttributes.get(0))) {
            return validationBody.toBuilder()
                    .errorMessage("Invalid value. Allowed values: " + String.join(", ", allowedValues))
                    .build();
        }

        return validationBody.toBuilder().valid(true).build();
    }

    @Override
    public AttributeType getType() {
        return AttributeType.ENUM;
    }
}
