package com.vendo.product_service.test_utils.builder;

import com.vendo.product_service.adapter.product.in.dto.CreateProductRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CreateProductRequestDataBuilder {

    public static CreateProductRequest.CreateProductRequestBuilder buildCreateProductRequestWithRequiredFields() {
        return CreateProductRequest.builder()
                .title("Product title")
                .description("Product description")
                .quantity(1)
                .price(BigDecimal.ONE)
                .categoryId(String.valueOf(UUID.randomUUID()))
                .attributes(Map.of("Attribute", List.of("attribute_value")));
    }

}
