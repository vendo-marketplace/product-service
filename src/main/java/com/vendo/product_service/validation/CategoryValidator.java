package com.vendo.product_service.validation;

import com.vendo.product_service.common.exception.CategoryValidationException;
import com.vendo.product_service.db.model.embedded.AttributeDefinition;
import com.vendo.product_service.db.query.CategoryQueryService;
import com.vendo.product_service.validation.factory.CategoryAttributeValidationFactory;
import com.vendo.product_service.validation.strategy.CategoryAttributeValidationStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CategoryValidator {

    private final CategoryQueryService categoryQueryService;

    private final CategoryAttributeValidationFactory categoryAttributeValidationFactory;

    public void validateCategoryAttributes(String categoryId, Map<String, List<String>> requestAttributes) {
        Map<String, AttributeDefinition> attributes = categoryQueryService.findByIdOrThrow(categoryId).getAttributes();
        validateRequestedAttributesBySample(attributes, requestAttributes);
    }

    private void validateRequestedAttributesBySample(Map<String, AttributeDefinition> attributes, Map<String, List<String>> requestAttributes) {
        List<String> validationFailedAttributes = new ArrayList<>();

        for (Map.Entry<String, AttributeDefinition> attribute : attributes.entrySet()) {
            String attributeDefinitionKey = attribute.getKey();
            AttributeDefinition attributeDefinition = attribute.getValue();

            if (requestAttributes.containsKey(attributeDefinitionKey)) {
                List<String> requestAttributesValue = requestAttributes.get(attributeDefinitionKey);

                CategoryAttributeValidationStrategy categoryAttributeValidationFactoryValidator = categoryAttributeValidationFactory.getValidator(attributeDefinition.type());
                boolean isValidAttributeValue = categoryAttributeValidationFactoryValidator.validate(requestAttributesValue);
                if (!isValidAttributeValue) {
                    validationFailedAttributes.add(attributeDefinitionKey);
                }
            } else if (attribute.getValue().required()) {
                validationFailedAttributes.add(attributeDefinitionKey);
            }
        }

        if (!validationFailedAttributes.isEmpty()) {
            throw new CategoryValidationException("Validation failed for category attributes: %s".formatted(validationFailedAttributes));
        }
    }
}
