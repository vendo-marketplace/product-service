package com.vendo.product_service.application.category.validation.attribute;

import com.vendo.product_service.domain.attribute.model.Attribute;
import com.vendo.product_service.domain.attribute.model.AttributeValue;

import java.util.List;

public interface AttributeValidator {

    void validate(List<Attribute> originAttributes, List<AttributeValue> requestAttributes);

}
