package com.vendo.product_service.domain.category.validation.attribute;

import com.vendo.product_service.domain.category.db.model.embedded.AttributeDefinition;
import com.vendo.product_service.domain.category.db.model.embedded.AttributeType;

import java.util.List;

public interface CategoryAttributeValidationStrategy {

    boolean validate(List<String> attributesValue, AttributeDefinition attributeDefinition);

    AttributeType getType();

}
