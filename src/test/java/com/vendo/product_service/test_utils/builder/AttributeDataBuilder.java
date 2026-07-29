package com.vendo.product_service.test_utils.builder;

import com.vendo.product_service.domain.attribute.model.Attribute;
import com.vendo.product_service.domain.attribute.model.AttributeType;

import java.util.UUID;

public class AttributeDataBuilder {

    public static Attribute.AttributeBuilder withAllFields() {
        return Attribute.builder()
                .id(String.valueOf(UUID.randomUUID()))
                .title("Title")
                .slug("title")
                .type(AttributeType.STRING)
                .required(false)
                .allowedValues(null);
    }

}
