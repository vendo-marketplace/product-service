package com.vendo.product_service.application.attribute.validation.strategy;

import com.vendo.core_lib.utils.CollectionUtils;
import com.vendo.product_service.domain.attribute.exception.InvalidAttributesException;
import com.vendo.product_service.domain.attribute.model.Attribute;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

abstract class AllowedValuesDefinitionValidator implements AttributeDefinitionValidatorStrategy {

    private static final String ALLOWED_VALUES_FIELD = "allowedValues";

    @Override
    public void validate(Attribute attribute) {
        List<String> allowedValues = attribute.allowedValues();

        if (CollectionUtils.isEmpty(allowedValues)) {
            throwInvalid("Allowed values must contain at least one value.");
        }

        if (allowedValues.stream().anyMatch(value -> value == null || value.isBlank())) {
            throwInvalid("Allowed values must not be blank.");
        }

        if (new HashSet<>(allowedValues).size() != allowedValues.size()) {
            throwInvalid("Allowed values must be unique.");
        }
    }

    private void throwInvalid(String message) {
        throw new InvalidAttributesException("Attribute validation failed.", Map.of(ALLOWED_VALUES_FIELD, message));
    }
}
