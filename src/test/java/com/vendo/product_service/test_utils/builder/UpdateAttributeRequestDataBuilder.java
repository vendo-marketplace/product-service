package com.vendo.product_service.test_utils.builder;

import com.vendo.product_service.adapter.attribute.in.dto.UpdateAttributeRequest;

public class UpdateAttributeRequestDataBuilder {

    public static UpdateAttributeRequest.UpdateAttributeRequestBuilder withAllFields() {
        return UpdateAttributeRequest.builder()
                .title("Updated title")
                .slug("updated_slug")
                .required(false)
                .allowedValues(null);
    }
}
