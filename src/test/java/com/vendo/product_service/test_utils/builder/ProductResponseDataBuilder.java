package com.vendo.product_service.test_utils.builder;

import com.vendo.product_service.adapter.product.in.dto.ProductResponse;
import com.vendo.product_service.domain.attribute.model.AttributeValue;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record ProductResponseDataBuilder() {

    public static ProductResponse.ProductResponseBuilder withAllFields() {
        return ProductResponse.builder()
                .id(String.valueOf(UUID.randomUUID()))
                .title("title")
                .description("description")
                .quantity(1)
                .price(BigDecimal.ONE)
                .ownerId("owner_id")
                .categoryId("category_id")
                .attributes(List.of(new AttributeValue("attribute_id", List.of("attribute_value"))))
                .active(true);
    }
}
