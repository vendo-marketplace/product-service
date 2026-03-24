package com.vendo.product_service.application.category.validation.attribute.strategy;

import com.vendo.product_service.application.category.validation.dto.AttributePayload;
import com.vendo.product_service.application.category.validation.dto.ValidationBody;
import com.vendo.product_service.domain.category.model.AttributeType;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RangeAttributeValidator implements AttributeValidatorStrategy {

    @Override
    public ValidationBody validate(AttributePayload payload, List<String> requestAttributes) {
        ValidationBody validationBody = ValidationBody.builder().fieldName(payload.name()).build();

        if (requestAttributes == null || requestAttributes.size() != 2) {
            return validationBody.toBuilder()
                    .errorMessage("Must contain exactly two values.")
                    .build();
        }

        try {
            int from = Integer.parseInt(requestAttributes.get(0));
            if (from < 0) {
                return validationBody.toBuilder()
                        .errorMessage("The first value must be equal or greater than to zero.")
                        .build();
            }

            int to = Integer.parseInt(requestAttributes.get(1));
            if (from >= to) {
                return validationBody.toBuilder()
                        .errorMessage("The first value must be less than the second value.")
                        .build();
            }
        } catch (NumberFormatException e) {
            return validationBody.toBuilder()
                    .errorMessage("Invalid range value format.")
                    .build();
        }

        return validationBody.toBuilder().valid(true).build();
    }

    @Override
    public AttributeType getType() {
        return AttributeType.RANGE;
    }
}
