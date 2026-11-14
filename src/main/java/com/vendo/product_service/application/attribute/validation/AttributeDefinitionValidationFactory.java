package com.vendo.product_service.application.attribute.validation;

import com.vendo.product_service.application.attribute.validation.strategy.AttributeDefinitionValidatorStrategy;
import com.vendo.product_service.domain.attribute.model.AttributeType;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class AttributeDefinitionValidationFactory {

    private final Map<AttributeType, AttributeDefinitionValidatorStrategy> validatorsByType = new EnumMap<>(AttributeType.class);

    public AttributeDefinitionValidationFactory(List<AttributeDefinitionValidatorStrategy> validators) {
        for (AttributeDefinitionValidatorStrategy validator : validators) {
            AttributeDefinitionValidatorStrategy previous = validatorsByType.put(validator.getType(), validator);
            if (previous != null) {
                throw new IllegalStateException("Duplicate attribute definition validator for type: %s.".formatted(validator.getType()));
            }
        }
    }

    public Optional<AttributeDefinitionValidatorStrategy> findValidator(AttributeType type) {
        return Optional.ofNullable(validatorsByType.get(type));
    }

}
