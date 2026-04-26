package com.vendo.product_service.application.category.validation.attribute;

import com.vendo.product_service.application.category.validation.attribute.strategy.AttributeValidatorStrategy;
import com.vendo.product_service.application.category.validation.dto.ValidationBody;
import com.vendo.product_service.domain.attribute.model.Attribute;
import com.vendo.product_service.domain.attribute.model.AttributeValue;
import com.vendo.product_service.domain.category.exception.CategoryValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DefaultAttributesValidator implements AttributesValidator {

    private final AttributesValidationFactory attributesValidationFactory;

    @Override
    public void validate(List<Attribute> originAttributes, List<AttributeValue> requestAttributes) {
        boolean matches = matchAllAttributes(originAttributes, requestAttributes);
        if (!matches) throw new CategoryValidationException("Requesting categories mismatch.");
        compareAndValidate(originAttributes, requestAttributes);
    }

    private boolean matchAllAttributes(List<Attribute> originAttributes, List<AttributeValue> requestAttributes) {
        List<String> originAttributesIds = originAttributes.stream().map(Attribute::id).toList();
        List<String> requestAttributesIds = requestAttributes.stream().map(AttributeValue::id).toList();

        if (originAttributesIds.size() != requestAttributesIds.size()) return false;
        return new HashSet<>(requestAttributesIds).containsAll(originAttributesIds);
    }

    private void compareAndValidate(List<Attribute> originAttributes, List<AttributeValue> requestAttributes) {
        List<ValidationBody> invalidAttributes = originAttributes.stream()
                .map(originAttribute -> validateRequestAttribute(originAttribute, requestAttributes))
                .filter(attribute -> !attribute.valid())
                .toList();

        if (!invalidAttributes.isEmpty()) {
            Map<String, String> validationErrors = invalidAttributes.stream()
                    .collect(Collectors.toMap(ValidationBody::fieldName, ValidationBody::errorMessage));
            throw new CategoryValidationException("Validation failed.", validationErrors);
        }
    }

    private ValidationBody validateRequestAttribute(Attribute originAttribute, List<AttributeValue> requestAttributes) {
        AttributeValue requestAttribute = getRequestAttributeById(originAttribute.id(), requestAttributes);
        return isAttributeValid(originAttribute, requestAttribute.value());
    }

    private ValidationBody isAttributeValid(Attribute originAttribute, List<String> requestAttributesValue) {
        AttributeValidatorStrategy validationStrategy = attributesValidationFactory.getValidator(originAttribute.type());

        ValidationBody validatedRequirement = validationStrategy.validateRequirement(originAttribute, requestAttributesValue);
        if (!validatedRequirement.valid()) return validatedRequirement;

        return validationStrategy.validate(originAttribute, requestAttributesValue);
    }

    private AttributeValue getRequestAttributeById(String originAttributeId, List<AttributeValue> requestAttributes) {
        return requestAttributes.stream()
                .filter(requestAttribute -> requestAttribute.id().equals(originAttributeId))
                .findAny()
                .orElseThrow(() -> new CategoryValidationException("Not found request attribute by origin attribute id: %s.".formatted(originAttributeId)));
    }
}
