package com.vendo.product_service.test_utils.builder;

import com.vendo.product_service.adapter.attribute.in.dto.CreateAttributeRequest;
import com.vendo.product_service.domain.attribute.model.AttributeType;

public class CreateAttributeRequestDataBuilder {

    public static CreateAttributeRequest.CreateAttributeRequestBuilder withAllFields() {
        return CreateAttributeRequest.builder()
                .title("Title")
                .type(AttributeType.STRING)
                .required(false)
                .allowedValues(null);
    }
}
