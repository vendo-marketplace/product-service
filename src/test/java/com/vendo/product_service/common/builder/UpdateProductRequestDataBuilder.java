package com.vendo.product_service.common.builder;

import com.vendo.product_service.web.dto.UpdateProductRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class UpdateProductRequestDataBuilder {

    public static UpdateProductRequest.UpdateProductRequestBuilder buildUpdateProductRequestWithAllFields() {
        return UpdateProductRequest.builder()
                .title("Title")
                .description("Description")
                .quantity(1)
                .price(BigDecimal.ONE)
                .categoryId(String.valueOf(UUID.randomUUID()))
                .attributes(Map.of("attribute_name", List.of("attribute_value")))
                .active(true);
    }

}
