package com.vendo.product_service.application.category.validation.attribute.strategy;

import com.vendo.product_service.application.category.validation.dto.AttributePayload;
import com.vendo.product_service.application.category.validation.dto.ValidationBody;
import com.vendo.product_service.adapter.model.category.embedded.AttributeType;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StringAttributeValidatorStrategy implements AttributeValidatorStrategy {

    @Override
    public ValidationBody validate(AttributePayload payload, List<String> requestAttributes) {
        ValidationBody validationBody = ValidationBody.builder().fieldName(payload.name()).build();

        if (requestAttributes == null || requestAttributes.size() != 1) {
            return validationBody.toBuilder()
                    .errorMessage("Must contain exactly one value.")
                    .build();
        }

        if (requestAttributes.get(0).isBlank()) {
            return validationBody.toBuilder()
                    .errorMessage("Must not be blank.")
                    .build();
        }

        return validationBody.toBuilder().valid(true).build();
    }

    @Override
    public AttributeType getType() {
        return AttributeType.STRING;
    }
}
