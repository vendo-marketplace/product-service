package com.vendo.product_service.test_utils.builder;

import com.vendo.product_service.adapter.product.in.dto.CreateProductRequest;
import com.vendo.product_service.domain.attribute.model.AttributeValue;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public record CreateProductRequestDataBuilder() {

    public static CreateProductRequest.CreateProductRequestBuilder withAllFields() {
        return CreateProductRequest.builder()
                .title("title")
                .description("description")
                .price(BigDecimal.ONE)
                .categoryId("category_id")
                .quantity(1)
                .attributes(List.of(new AttributeValue("attribute_id", List.of("attribute_value"))));
    }

}
