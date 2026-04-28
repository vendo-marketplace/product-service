package com.vendo.product_service.test_utils.builder;

import com.vendo.product_service.domain.attribute.model.Attribute;
import com.vendo.product_service.domain.attribute.model.AttributeType;

import java.util.UUID;

public class AttributeDataBuilder {

    public static Attribute withAllFields() {
        return new Attribute(String.valueOf(UUID.randomUUID()), "Title", AttributeType.STRING, false, null);
    }

}
