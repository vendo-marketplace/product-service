package com.vendo.product_service.test_utils.builder;

import com.vendo.product_service.adapter.product.in.dto.AttributeValueRequest;
import com.vendo.product_service.domain.attribute.model.AttributeValue;

import java.util.List;

public class AttributeValueRequestDataBuilder {

    public static AttributeValueRequest.AttributeValueRequestBuilder withAllFields() {
        return AttributeValueRequest.builder()
                .id("attribute_id")
                .values(List.of("attribute_value"));
    }

    public static AttributeValueRequest from(AttributeValue attributeValue) {
        return new AttributeValueRequest(attributeValue.id(), attributeValue.values());
    }

    public static List<AttributeValueRequest> fromAll(List<AttributeValue> attributeValues) {
        return attributeValues.stream().map(AttributeValueRequestDataBuilder::from).toList();
    }

}
