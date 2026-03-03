package com.vendo.product_service.application.category.validation.attribute;

import com.vendo.product_service.application.category.validation.attribute.strategy.AttributeValidatorStrategy;
import com.vendo.product_service.application.category.validation.dto.AttributePayload;
import com.vendo.product_service.application.category.validation.dto.ValidationBody;
import com.vendo.product_service.domain.category.exception.CategoryValidationException;
import com.vendo.product_service.domain.category.model.AttributeDefinition;
import com.vendo.product_service.domain.category.model.Category;
import com.vendo.product_service.domain.category.type.CategoryType;
import com.vendo.product_service.domain.port.category.CategoryQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CreateCategoryValidator implements CategoryValidator {

    private final CategoryQueryPort categoryQueryPort;
    private final CategoryValidationFactory categoryValidationFactory;

    @Override
    public void validateAttributes(String requestId, Map<String, List<String>> requestAttributes) {
        Category category = categoryQueryPort.findById(requestId, "Parent category not found.");
        category.throwIfNotDesiredType(CategoryType.CHILD, "Category type should be child.");
        compareAndValidate(category.getAttributes(), requestAttributes);
    }

    private void compareAndValidate(Map<String, AttributeDefinition> attributes, Map<String, List<String>> requestAttributes) {
        List<ValidationBody> invalidAttributes = attributes.entrySet().stream()
                .map(attribute -> new AttributePayload(attribute.getKey(), attribute.getValue()))
                .map(attribute -> isAttributeValid(attribute , requestAttributes))
                .filter(attribute -> !attribute.valid()).toList();

        if (!invalidAttributes.isEmpty()) {
            Map<String, String> validationErrors = invalidAttributes.stream()
                    .collect(Collectors.toMap(ValidationBody::fieldName, ValidationBody::errorMessage));
            throw new CategoryValidationException("Validation failed.", validationErrors);
        }
    }

    private ValidationBody isAttributeValid(AttributePayload payload, Map<String, List<String>> requestAttributes) {
        List<String> attributesValue = requestAttributes.get(payload.name());

        ValidationBody validationBody = validateRequirement(payload, attributesValue);
        if (!validationBody.valid()) return validationBody;

        AttributeValidatorStrategy validationStrategy = categoryValidationFactory.getValidator(payload.definition().type());
        return validationStrategy.validate(payload, attributesValue);
    }

    private ValidationBody validateRequirement(AttributePayload payload, List<String> attributesValue) {
        if ((attributesValue == null || attributesValue.isEmpty()) && payload.definition().required()) {
            return ValidationBody.builder()
                    .fieldName(payload.name())
                    .errorMessage("%s is required.".formatted(payload.name()))
                    .build();
        }

        return ValidationBody.builder().valid(true).build();
    }
}
