package com.vendo.product_service.test_utils.builder;

import com.vendo.product_service.adapter.attribute.out.persistence.MongoAttribute;
import com.vendo.product_service.domain.attribute.model.AttributeType;

import java.util.UUID;

public class MongoAttributeDataBuilder {

    public static MongoAttribute.MongoAttributeBuilder withAllFields() {
        return MongoAttribute.builder()
                .id(String.valueOf(UUID.randomUUID()))
                .title("Title")
                .type(AttributeType.STRING)
                .required(false)
                .allowedValues(null);
    }

}
