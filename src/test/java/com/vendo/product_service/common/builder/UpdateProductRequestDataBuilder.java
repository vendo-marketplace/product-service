package com.vendo.product_service.common.builder;

import com.vendo.product_service.adapter.in.product.dto.UpdateProductRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class UpdateProductRequestDataBuilder {

    public static UpdateProductRequest.UpdateProductRequestBuilder buildUpdateProductRequestWithAllFields() {
        return UpdateProductRequest.builder()
                .title("New Title")
                .description("New Description")
                .quantity(1)
                .price(BigDecimal.TEN)
                .categoryId(String.valueOf(UUID.randomUUID()))
                .attributes(Map.of("New Attribute", List.of("new_attribute_value")))
                .active(false);
    }

}
