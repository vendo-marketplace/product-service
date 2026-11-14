package com.vendo.product_service.application.category.validation.attribute.strategy;

import com.vendo.core_lib.constants.Delimiters;
import com.vendo.core_lib.utils.CollectionUtils;
import com.vendo.product_service.application.category.validation.dto.ValidationBody;
import com.vendo.product_service.domain.attribute.model.Attribute;
import com.vendo.product_service.domain.attribute.model.AttributeType;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
class SelectAttributeValidator implements AttributeValidatorStrategy {

    @Override
    public ValidationBody validate(Attribute originAttribute, List<String> requestAttributes) {
        ValidationBody validationBody = ValidationBody.builder().fieldName(originAttribute.title()).build();

        if (CollectionUtils.isEmpty(requestAttributes)) {
            return validationBody.toBuilder()
                    .errorMessage("Must contain at least one value.")
                    .build();
        }

        if (new HashSet<>(requestAttributes).size() != requestAttributes.size()) {
            return validationBody.toBuilder()
                    .errorMessage("Values must be unique.")
                    .build();
        }

        List<String> allowedValues = originAttribute.allowedValues();
        if (CollectionUtils.isEmpty(allowedValues)) {
            return validationBody.toBuilder()
                    .errorMessage("Attribute has no allowed values.")
                    .build();
        }

        Set<String> allowedValuesSet = new HashSet<>(allowedValues);
        List<String> invalidValues = requestAttributes.stream()
                .filter(value -> !allowedValuesSet.contains(value))
                .toList();

        if (!invalidValues.isEmpty()) {
            return validationBody.toBuilder()
                    .errorMessage("Invalid values: %s. Allowed values: %s".formatted(
                            String.join(Delimiters.COMMA_DELIMITER, invalidValues),
                            String.join(Delimiters.COMMA_DELIMITER, allowedValues)
                    ))
                    .build();
        }

        return validationBody.toBuilder().valid(true).build();
    }

    @Override
    public AttributeType getType() {
        return AttributeType.SELECT;
    }
}
