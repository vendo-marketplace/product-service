package com.vendo.product_service.application.category.validation.attribute.strategy;

import com.vendo.product_service.application.category.validation.dto.AttributePayload;
import com.vendo.product_service.application.category.validation.dto.ValidationBody;
import com.vendo.product_service.adapter.model.category.embedded.AttributeType;

import java.util.List;

public interface AttributeValidatorStrategy {

    ValidationBody validate(AttributePayload payload, List<String> requestAttributes);

    AttributeType getType();

}
