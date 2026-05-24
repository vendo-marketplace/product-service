package com.vendo.product_service.application.category.validation.attribute;

import com.vendo.product_service.application.category.validation.attribute.strategy.AttributeValidatorStrategy;
import com.vendo.product_service.application.category.validation.dto.ValidationBody;
import com.vendo.product_service.domain.attribute.model.Attribute;
import com.vendo.product_service.domain.attribute.model.AttributeValue;
import com.vendo.product_service.domain.category.exception.CategoryValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DefaultAttributesValidator implements AttributesValidator {

    private final AttributesValidationFactory attributesValidationFactory;

    @Override
    public void validate(List<Attribute> originAttributes, List<AttributeValue> requestAttributes) {
        compareAndValidate(originAttributes, requestAttributes);
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
        return isAttributeValid(originAttribute, requestAttribute.values());
    }

    private ValidationBody isAttributeValid(Attribute originAttribute, List<String> requestAttributeValue) {
        AttributeValidatorStrategy validationStrategy = attributesValidationFactory.getValidator(originAttribute.type());

        ValidationBody validatedRequirement = validationStrategy.validateRequirement(originAttribute, requestAttributeValue);
        if (!validatedRequirement.valid()) return validatedRequirement;

        return validationStrategy.validate(originAttribute, requestAttributeValue);
    }

    private AttributeValue getRequestAttributeById(String originAttributeId, List<AttributeValue> requestAttributes) {
        return requestAttributes.stream()
                .filter(requestAttribute -> requestAttribute.id().equals(originAttributeId))
                .findAny()
                .orElse(new AttributeValue(originAttributeId, List.of()));
    }
}
