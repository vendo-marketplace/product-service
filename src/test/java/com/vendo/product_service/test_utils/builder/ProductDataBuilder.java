package com.vendo.product_service.test_utils.builder;

import com.vendo.product_service.domain.product.model.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record ProductDataBuilder() {

    public static Product.Builder withAllFields() {
        return Product.builder()
                .id("id")
                .title("title")
                .description("description")
                .quantity(1)
                .price(BigDecimal.ONE)
                .ownerId("owner_id")
                .categoryId("category_id")
                .attributes(Map.of("attribute_name", List.of("attribute_value")))
                .active(true);
    }

}
