package com.vendo.product_service.application.category.validation.attribute.strategy;

import com.vendo.core_lib.utils.CollectionUtils;
import com.vendo.product_service.application.category.validation.dto.ValidationBody;
import com.vendo.product_service.domain.attribute.model.Attribute;
import com.vendo.product_service.domain.attribute.model.AttributeType;

import java.util.List;

public interface AttributeValidatorStrategy {

    ValidationBody validate(Attribute originAttribute, List<String> requestAttributes);

    default ValidationBody validateRequirement(Attribute originAttribute, List<String> requestAttributesValue) {
        if ((CollectionUtils.isEmpty(requestAttributesValue)) && originAttribute.required()) {
            return ValidationBody.builder()
                    .fieldName(originAttribute.title())
                    .errorMessage("%s is required.".formatted(originAttribute.title()))
                    .build();
        }

        return ValidationBody.builder().valid(true).build();
    }

    AttributeType getType();

}
