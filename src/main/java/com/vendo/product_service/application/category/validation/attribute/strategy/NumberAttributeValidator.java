package com.vendo.product_service.application.category.validation.attribute.strategy;

import com.vendo.product_service.application.category.validation.dto.AttributePayload;
import com.vendo.product_service.application.category.validation.dto.ValidationBody;
import com.vendo.product_service.domain.category.model.AttributeType;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NumberAttributeValidator implements AttributeValidatorStrategy {

    @Override
    public ValidationBody validate(AttributePayload payload, List<String> requestAttributes) {
        ValidationBody validationBody = ValidationBody.builder().fieldName(payload.name()).build();

        if (requestAttributes == null || requestAttributes.size() != 1) {
            return validationBody.toBuilder()
                    .errorMessage("Must contain exactly one value.")
                    .build();
        }

        try {
            int attributeValue = Integer.parseInt(requestAttributes.get(0));
            if (attributeValue < 0) {
                return validationBody.toBuilder()
                        .errorMessage("Must be equal or greater than zero.")
                        .build();
            }

        } catch (NumberFormatException e) {
            return validationBody.toBuilder()
                    .errorMessage("Invalid number value.")
                    .build();
        }

        return validationBody.toBuilder().valid(true).build();
    }

    @Override
    public AttributeType getType() {
        return AttributeType.NUMBER;
    }
}
