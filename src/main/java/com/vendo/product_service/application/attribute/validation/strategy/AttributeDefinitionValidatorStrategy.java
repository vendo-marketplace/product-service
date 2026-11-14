package com.vendo.product_service.application.attribute.validation.strategy;

import com.vendo.product_service.domain.attribute.model.Attribute;
import com.vendo.product_service.domain.attribute.model.AttributeType;

public interface AttributeDefinitionValidatorStrategy {

    void validate(Attribute attribute);

    AttributeType getType();

}
