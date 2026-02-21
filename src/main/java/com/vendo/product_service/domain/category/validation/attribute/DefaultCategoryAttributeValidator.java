package com.vendo.product_service.domain.category.validation.attribute;

import com.vendo.product_service.adapter.model.category.embedded.AttributeDefinition;
import com.vendo.product_service.domain.category.validation.ValidationBody;
import com.vendo.product_service.domain.category.model.CategoryType;
import com.vendo.product_service.domain.category.exception.CategoryTypeException;
import com.vendo.product_service.domain.category.exception.CategoryValidationException;
import com.vendo.product_service.domain.category.model.Category;
import com.vendo.product_service.domain.category.validation.CategoryTypeResolver;
import com.vendo.product_service.domain.category.port.CategoryQueryPort;
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

    private final CategoryTypeResolver categoryTypeResolver;
    
    @Override
    public void validateCategoryAttributes(String requestCategoryId, Map<String, List<String>> requestAttributes) {
        Category category = categoryQueryPort.findById(requestCategoryId, "Parent category not found.");
        throwIfCategoryNotChild(category);
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
        return categoryAttributeValidationFactoryValidator.validate(attributeName, attributeDefinition,  requestAttributes.get(attributeName));
    }

    private void throwIfCategoryNotChild(Category category) {
        CategoryType categoryType = categoryTypeResolver.resolve(category.getParentId(), category.getAttributes());

        if (categoryType != CategoryType.CHILD) {
            throw new CategoryTypeException("Category type should be child.");
        }
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
