package com.vendo.product_service.application.category.validation.attribute.strategy;

import com.vendo.product_service.application.category.validation.dto.ValidationBody;
import com.vendo.product_service.domain.attribute.model.Attribute;
import com.vendo.product_service.domain.attribute.model.AttributeType;

import java.util.List;

public interface AttributeValidatorStrategy {

    ValidationBody validate(Attribute originAttribute, List<String> requestAttributes);

    AttributeType getType();

}
