package com.vendo.product_service.test_utils.builder;

import com.vendo.product_service.adapter.product.in.dto.CreateProductRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public record CreateProductRequestDataBuilder() {

    public static CreateProductRequest.CreateProductRequestBuilder withAllFields() {
        return CreateProductRequest.builder()
                .title("title")
                .description("description")
                .price(BigDecimal.ONE)
                .quantity(1)
                .attributes(Map.of("Attribute", List.of("attribute_value")));
    }

}
