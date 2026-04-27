package com.vendo.product_service.test_utils.builder;

import com.vendo.product_service.domain.attribute.model.AttributeValue;
import com.vendo.product_service.domain.product.model.Product;

import java.math.BigDecimal;
import java.util.List;

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
                .attributes(List.of(new AttributeValue("attribute_id", List.of("attribute_value"))))
                .active(true);
    }

}
