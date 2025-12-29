package com.vendo.product_service.domain.category.validation.attribute;

import com.vendo.product_service.common.exception.ValidationBody;
import com.vendo.product_service.domain.category.db.model.embedded.AttributeDefinition;
import com.vendo.product_service.domain.category.db.model.embedded.AttributeType;

import java.util.List;

public interface CategoryAttributeValidationStrategy {

    ValidationBody validate(String name, AttributeDefinition definition, List<String> requestAttributes);

    AttributeType getType();

}
