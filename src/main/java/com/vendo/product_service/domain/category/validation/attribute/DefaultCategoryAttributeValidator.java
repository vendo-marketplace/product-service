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
            ValidationBody validationBody = isAttributeValid(attribute.getKey(), attribute.getValue(), requestAttributes);
            if (!validationBody.isValid()) {
                invalidAttributes.add(validationBody);
            }
        }

        if (!invalidAttributes.isEmpty()) {
            throw new CategoryValidationException("Validation failed.", invalidAttributes.stream().collect(Collectors.toMap(ValidationBody::getFieldName, ValidationBody::getErrorMessage)));
        }
    }

    private ValidationBody isAttributeValid(String attributeName, AttributeDefinition attributeDefinition, Map<String, List<String>> requestAttributes) {
        ValidationBody validationBody = validateAttributeRequirement(attributeName, attributeDefinition, requestAttributes.get(attributeName));
        if (!validationBody.isValid()) {
            return validationBody;
        }

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
