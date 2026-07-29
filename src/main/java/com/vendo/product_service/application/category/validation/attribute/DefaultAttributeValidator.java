package com.vendo.product_service.application.category.validation.attribute;

import com.vendo.core_lib.utils.CollectionUtils;
import com.vendo.product_service.application.category.validation.attribute.strategy.AttributeValidatorStrategy;
import com.vendo.product_service.application.category.validation.dto.ValidationBody;
import com.vendo.product_service.domain.attribute.exception.InvalidAttributesException;
import com.vendo.product_service.domain.attribute.model.Attribute;
import com.vendo.product_service.domain.attribute.model.AttributeValue;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
class DefaultAttributeValidator implements AttributeValidator {

    private final AttributeValidationFactory attributeValidationFactory;

    @Override
    public void validate(List<Attribute> originAttributes, List<AttributeValue> requestAttributes) {
        List<ValidationBody> invalidAttributes = getAttributesValidation(originAttributes, requestAttributes);
        if (!invalidAttributes.isEmpty()) throwInvalidAttributes(invalidAttributes);
    }

    private List<ValidationBody> getAttributesValidation(List<Attribute> originAttributes, List<AttributeValue> requestAttributes) {
        return originAttributes.stream()
                .map(originAttribute -> validateRequestAttribute(originAttribute, requestAttributes))
                .filter(attribute -> !attribute.valid())
                .toList();
    }

    private ValidationBody validateRequestAttribute(Attribute originAttribute, List<AttributeValue> requestAttributes) {
        AttributeValue requestAttribute = getRequestAttributeById(originAttribute.id(), requestAttributes);
        return isAttributeValid(originAttribute, requestAttribute.values());
    }

    private ValidationBody isAttributeValid(Attribute originAttribute, List<String> requestAttributeValue) {
        final boolean required = originAttribute.required(), empty = CollectionUtils.isEmpty(requestAttributeValue);
        AttributeValidatorStrategy validationStrategy = attributeValidationFactory.getValidator(originAttribute.type());

        if (required && empty) {
            return ValidationBody.from(originAttribute.slug(), "%s is required.".formatted(originAttribute.title()));
        } else if (!required && empty) {
            return ValidationBody.builder().valid(true).build();
        }

        return validationStrategy.validate(originAttribute, requestAttributeValue);
    }

    private AttributeValue getRequestAttributeById(String originAttributeId, List<AttributeValue> requestAttributes) {
        AttributeValue emptyAttribute = new AttributeValue(originAttributeId, List.of());
        if (CollectionUtils.isEmpty(requestAttributes)) return emptyAttribute;
        return requestAttributes.stream()
                .filter(requestAttribute -> requestAttribute.id().equals(originAttributeId))
                .findAny()
                .orElse(emptyAttribute);
    }

    private void throwInvalidAttributes(List<ValidationBody> invalidAttributes) {
        Map<String, String> validationErrors = invalidAttributes.stream().collect(Collectors.toMap(ValidationBody::fieldName, ValidationBody::errorMessage));
        throw new InvalidAttributesException("Validation failed.", validationErrors);
    }
}
