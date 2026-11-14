package com.vendo.product_service.application.category.validation.attribute;

import com.vendo.core_lib.constants.Delimiters;
import com.vendo.core_lib.utils.CollectionUtils;
import com.vendo.product_service.application.category.validation.attribute.strategy.AttributeValidatorStrategy;
import com.vendo.product_service.application.category.validation.dto.ValidationBody;
import com.vendo.product_service.domain.attribute.exception.InvalidAttributesException;
import com.vendo.product_service.domain.attribute.model.Attribute;
import com.vendo.product_service.domain.attribute.model.AttributeValue;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
class DefaultAttributeValidator implements AttributeValidator {

    private static final String ATTRIBUTES_FIELD = "attributes";

    private final AttributeValidationFactory attributeValidationFactory;

    @Override
    public void validate(List<Attribute> originAttributes, List<AttributeValue> requestAttributes) {
        List<AttributeValue> safeRequestAttributes = CollectionUtils.isEmpty(requestAttributes) ? List.of() : requestAttributes;

        Map<String, String> validationErrors = getRequestStructureErrors(originAttributes, safeRequestAttributes);
        if (validationErrors.isEmpty()) {
            validationErrors = getAttributeValueErrors(originAttributes, safeRequestAttributes);
        }

        if (!validationErrors.isEmpty()) {
            throw new InvalidAttributesException("Attributes validation failed.", validationErrors);
        }
    }

    private Map<String, String> getRequestStructureErrors(List<Attribute> originAttributes, List<AttributeValue> requestAttributes) {
        Map<String, String> slugsById = originAttributes.stream().collect(Collectors.toMap(Attribute::id, Attribute::slug));
        Set<String> seenIds = new HashSet<>();
        Set<String> notAllowedIds = new LinkedHashSet<>();
        Map<String, String> errors = new LinkedHashMap<>();

        for (AttributeValue requestAttribute : requestAttributes) {
            String id = requestAttribute.id();
            if (!slugsById.containsKey(id)) {
                notAllowedIds.add(id);
            } else if (!seenIds.add(id)) {
                errors.putIfAbsent(slugsById.get(id), "Attribute is duplicated.");
            }
        }

        if (!notAllowedIds.isEmpty()) {
            errors.put(ATTRIBUTES_FIELD, "Attributes are not allowed for this category: %s.".formatted(String.join(Delimiters.COMMA_DELIMITER, notAllowedIds)));
        }

        return errors;
    }

    private Map<String, String> getAttributeValueErrors(List<Attribute> originAttributes, List<AttributeValue> requestAttributes) {
        Map<String, String> errors = new LinkedHashMap<>();

        for (Attribute originAttribute : originAttributes) {
            ValidationBody validationBody = validateRequestAttribute(originAttribute, requestAttributes);
            if (!validationBody.valid()) {
                errors.put(originAttribute.slug(), validationBody.errorMessage());
            }
        }

        return errors;
    }

    private ValidationBody validateRequestAttribute(Attribute originAttribute, List<AttributeValue> requestAttributes) {
        List<String> requestValues = getRequestValuesById(originAttribute.id(), requestAttributes);
        final boolean required = originAttribute.required(), empty = CollectionUtils.isEmpty(requestValues);

        if (required && empty) {
            return ValidationBody.from(originAttribute.slug(), "%s is required.".formatted(originAttribute.title()));
        } else if (empty) {
            return ValidationBody.builder().valid(true).build();
        }

        AttributeValidatorStrategy validationStrategy = attributeValidationFactory.getValidator(originAttribute.type());
        return validationStrategy.validate(originAttribute, requestValues);
    }

    private List<String> getRequestValuesById(String originAttributeId, List<AttributeValue> requestAttributes) {
        return requestAttributes.stream()
                .filter(requestAttribute -> requestAttribute.id().equals(originAttributeId))
                .findFirst()
                .map(AttributeValue::values)
                .orElse(List.of());
    }
}
