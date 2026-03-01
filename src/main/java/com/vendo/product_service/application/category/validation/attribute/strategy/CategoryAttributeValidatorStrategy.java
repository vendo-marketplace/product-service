package com.vendo.product_service.application.category.validation.attribute.strategy;

import com.vendo.product_service.application.category.validation.dto.ValidationBody;
import com.vendo.product_service.adapter.model.category.embedded.AttributeDefinition;
import com.vendo.product_service.adapter.model.category.embedded.AttributeType;

import java.util.List;

public interface CategoryAttributeValidatorStrategy {

    ValidationBody validate(String name, AttributeDefinition definition, List<String> requestAttributes);

    AttributeType getType();


}
