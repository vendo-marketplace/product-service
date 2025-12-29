package com.vendo.product_service.domain.category.validation.attribute;

import com.vendo.product_service.domain.category.common.exception.CategoryValidationException;
import com.vendo.product_service.common.exception.ValidationBody;
import com.vendo.product_service.domain.category.db.model.embedded.AttributeDefinition;
import com.vendo.product_service.domain.category.db.cqrs.query.CategoryQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DefaultCategoryAttributeValidator implements CategoryAttributeValidator {

    private final CategoryQueryService categoryQueryService;

    private final CategoryAttributeValidationFactory categoryAttributeValidationFactory;
    
    @Override
    public void validateCategoryAttributes(String categoryId, Map<String, List<String>> requestAttributes) {
        Map<String, AttributeDefinition> attributes = categoryQueryService.findById(categoryId).getAttributes();
        validateAttributes(attributes, requestAttributes);
    }

    private void validateAttributes(Map<String, AttributeDefinition> attributes, Map<String, List<String>> requestAttributes) {
        if (attributes == null) {
            throw new CategoryValidationException("Child category required.");
        }

        List<ValidationBody> invalidAttributes = new ArrayList<>();

        for (Map.Entry<String, AttributeDefinition> attribute : attributes.entrySet()) {
            ValidationBody validationBody = isAttributeValid(attribute, requestAttributes);
            if (!validationBody.isValid()) {
                invalidAttributes.add(validationBody);
            }
        }

        if (!invalidAttributes.isEmpty()) {
            throw new CategoryValidationException("Validation failed.", invalidAttributes.stream().collect(Collectors.toMap(ValidationBody::getFieldName, ValidationBody::getErrorMessage)));
        }
    }

    private ValidationBody isAttributeValid(Map.Entry<String, AttributeDefinition> attribute, Map<String, List<String>> requestAttributes) {
        AttributeDefinition attributeDefinition = attribute.getValue();
        String attributeKey = attribute.getKey();

        List<String> requestAttributesValue = requestAttributes.get(attributeKey);
        if (requestAttributesValue == null && attributeDefinition.required()) {
            return ValidationBody.builder()
                    .fieldName(attributeKey)
                    .fieldName("%s is required.".formatted(attribute))
                    .build();
        }

        CategoryAttributeValidationStrategy categoryAttributeValidationFactoryValidator = categoryAttributeValidationFactory.getValidator(attributeDefinition.type());
        return categoryAttributeValidationFactoryValidator.validate(attributeKey, attributeDefinition, requestAttributesValue);
    }
}
