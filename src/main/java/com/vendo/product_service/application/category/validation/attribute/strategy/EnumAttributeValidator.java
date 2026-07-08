package com.vendo.product_service.application.category.validation.attribute.strategy;

import com.vendo.core_lib.constants.Delimiters;
import com.vendo.product_service.application.category.validation.dto.ValidationBody;
import com.vendo.product_service.domain.attribute.model.Attribute;
import com.vendo.product_service.domain.attribute.model.AttributeType;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
class EnumAttributeValidator implements AttributeValidatorStrategy {

    @Override
    public ValidationBody validate(Attribute originAttribute, List<String> requestAttributes) {
        ValidationBody validationBody = ValidationBody.builder().fieldName(originAttribute.title()).build();

        if (requestAttributes == null || requestAttributes.size() != 1) {
            return validationBody.toBuilder()
                    .errorMessage("Must contain exactly one value.")
                    .build();
        }

        List<String> allowedValues = originAttribute.allowedValues();
        if (!allowedValues.contains(requestAttributes.get(0))) {
            return validationBody.toBuilder()
                    .errorMessage("Invalid value. Allowed values: " + String.join(Delimiters.COMMA_DELIMITER, allowedValues))
                    .build();
        }

        return validationBody.toBuilder().valid(true).build();
    }

    @Override
    public AttributeType getType() {
        return AttributeType.ENUM;
    }
}
