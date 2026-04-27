package com.vendo.product_service.test_utils.builder;

import com.vendo.product_service.adapter.product.in.dto.UpdateProductRequest;
import com.vendo.product_service.domain.attribute.model.AttributeValue;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class UpdateProductRequestDataBuilder {

    public static UpdateProductRequest.UpdateProductRequestBuilder withAllFields() {
        return UpdateProductRequest.builder()
                .title("New Title")
                .description("New Description")
                .quantity(1)
                .price(BigDecimal.TEN)
                .categoryId(String.valueOf(UUID.randomUUID()))
                .attributes(List.of(new AttributeValue("attribute_id", List.of("attribute_value"))))
                .active(false);
    }

}
