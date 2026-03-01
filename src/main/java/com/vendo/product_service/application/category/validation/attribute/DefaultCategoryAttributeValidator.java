package com.vendo.product_service.application.category.validation.attribute;

import com.vendo.product_service.adapter.model.category.embedded.AttributeDefinition;
import com.vendo.product_service.application.category.validation.attribute.strategy.CategoryAttributeValidatorStrategy;
import com.vendo.product_service.application.category.validation.dto.ValidationBody;
import com.vendo.product_service.domain.category.exception.CategoryValidationException;
import com.vendo.product_service.domain.category.model.Category;
import com.vendo.product_service.domain.category.type.CategoryType;
import com.vendo.product_service.port.category.CategoryQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DefaultCategoryAttributeValidator implements CategoryAttributeValidator {

    private final CategoryQueryPort categoryQueryPort;

    private final CategoryAttributeValidationFactory categoryAttributeValidationFactory;

    @Override
    public void validateCategoryAttributes(String requestCategoryId, Map<String, List<String>> requestAttributes) {
        Category category = categoryQueryPort.findById(requestCategoryId, "Parent category not found.");
        category.throwIfNotDesiredType(CategoryType.CHILD, "Category type should be child.");
        validateAttributes(category.getAttributes(), requestAttributes);
    }

    private void validateAttributes(Map<String, AttributeDefinition> attributes, Map<String, List<String>> requestAttributes) {
        List<ValidationBody> invalidAttributes = attributes.entrySet().stream()
                .map(attribute -> isAttributeValid(attribute.getKey(), attribute.getValue(), requestAttributes))
                .filter(attribute -> !attribute.valid()).toList();

        if (!invalidAttributes.isEmpty()) {
            throw new CategoryValidationException("Validation failed.", invalidAttributes.stream().collect(Collectors.toMap(ValidationBody::fieldName, ValidationBody::errorMessage)));
        }
    }

    private ValidationBody isAttributeValid(String attributeName, AttributeDefinition attributeDefinition, Map<String, List<String>> requestAttributes) {
        ValidationBody validationBody = validateAttributeRequirement(attributeName, attributeDefinition, requestAttributes.get(attributeName));
        if (!validationBody.valid()) return validationBody;

        CategoryAttributeValidatorStrategy categoryAttributeValidationFactoryValidator = categoryAttributeValidationFactory.getValidator(attributeDefinition.type());
        return categoryAttributeValidationFactoryValidator.validate(attributeName, attributeDefinition, requestAttributes.get(attributeName));
    }

    private ValidationBody validateAttributeRequirement(String attributeName, AttributeDefinition attributeDefinition, List<String> attributesValue) {
        if (attributesValue == null && attributeDefinition.required()) {
            return ValidationBody.builder()
                    .fieldName(attributeName)
                    .errorMessage("%s is required.".formatted(attributeName))
                    .build();
        }

        return ValidationBody.builder().valid(true).build();
    }
}
