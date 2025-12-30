package com.vendo.product_service.domain.category.validation.attribute;

import com.vendo.product_service.domain.category.common.exception.CategoryTypeException;
import com.vendo.product_service.domain.category.common.exception.CategoryValidationException;
import com.vendo.product_service.common.exception.ValidationBody;
import com.vendo.product_service.domain.category.common.type.CategoryType;
import com.vendo.product_service.domain.category.db.model.Category;
import com.vendo.product_service.domain.category.db.model.embedded.AttributeDefinition;
import com.vendo.product_service.domain.category.db.cqrs.query.CategoryQueryService;
import com.vendo.product_service.domain.category.validation.CategoryTypeResolver;
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

    private final CategoryTypeResolver categoryTypeResolver;
    
    @Override
    public void validateCategoryAttributes(String categoryId, Map<String, List<String>> requestAttributes) {
        Category category = categoryQueryService.findById(categoryId);
        throwIfCategoryNotChild(category);
        validateAttributes(category.getAttributes(), requestAttributes);
    }

    private void validateAttributes(Map<String, AttributeDefinition> attributes, Map<String, List<String>> requestAttributes) {
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
                    .errorMessage("%s is required.".formatted(attribute))
                    .build();
        }

        CategoryAttributeValidationStrategy categoryAttributeValidationFactoryValidator = categoryAttributeValidationFactory.getValidator(attributeDefinition.type());
        return categoryAttributeValidationFactoryValidator.validate(attributeKey, attributeDefinition, requestAttributesValue);
    }

    private void throwIfCategoryNotChild(Category category) {
        CategoryType categoryType = categoryTypeResolver.resolve(category.getParentId(), category.getAttributes());
        if (categoryType != CategoryType.CHILD) {
            throw new CategoryTypeException("Category type should be child.");
        }
    }
}
