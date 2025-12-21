package com.vendo.product_service.domain.category.validation.attribute;

import com.vendo.product_service.domain.category.common.exception.CategoryValidationException;
import com.vendo.product_service.domain.category.db.model.embedded.AttributeDefinition;
import com.vendo.product_service.domain.category.db.cqrs.query.CategoryQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class DefaultCategoryAttributeValidator implements CategoryAttributeValidator {

    private final CategoryQueryService categoryQueryService;

    private final CategoryAttributeValidationFactory categoryAttributeValidationFactory;

    @Override
    public void validateCategoryAttributes(String categoryId, Map<String, List<String>> requestAttributes) {
        Map<String, AttributeDefinition> attributes = categoryQueryService.findByIdOrThrow(categoryId).getAttributes();
        validateAttributes(attributes, requestAttributes);
    }

    private void validateAttributes(Map<String, AttributeDefinition> attributes, Map<String, List<String>> requestAttributes) {
        List<String> invalidAttributes = new ArrayList<>();

        for (Map.Entry<String, AttributeDefinition> attribute : attributes.entrySet()) {
            if (!isAttributeValid(attribute, requestAttributes)) {
                invalidAttributes.add(attribute.getKey());
            }
        }

        if (!invalidAttributes.isEmpty()) {
            throw new CategoryValidationException("Validation failed for category attributes: %s".formatted(invalidAttributes));
        }
    }

    private boolean isAttributeValid(Map.Entry<String, AttributeDefinition> attribute, Map<String, List<String>> requestAttributes) {
        AttributeDefinition attributeDefinition = attribute.getValue();
        String attributeKey = attribute.getKey();

        List<String> requestAttributesValue = requestAttributes.get(attributeKey);
        if (requestAttributesValue == null) {
            return !attributeDefinition.required();
        }

        CategoryAttributeValidationStrategy categoryAttributeValidationFactoryValidator = categoryAttributeValidationFactory.getValidator(attributeDefinition.type());
        return categoryAttributeValidationFactoryValidator.validate(requestAttributesValue);
    }
}
